package bgu.spl.mics;


//import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.concurrent.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<Subscriber>> typeAndQS;
	private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> subAndQM;
	private ConcurrentHashMap<Event, Future> EvAndFut;

	private static class MessageBrokerHolder {
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}

	private MessageBrokerImpl() {
		typeAndQS = new ConcurrentHashMap<>();
		subAndQM = new ConcurrentHashMap<>();
		EvAndFut = new ConcurrentHashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return MessageBrokerHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (type) {
			if (subAndQM.get(m) != null) {
				BlockingQueue<Subscriber> t = typeAndQS.putIfAbsent(type, new LinkedBlockingQueue<>());
				if (t!=null) {
					typeAndQS.get(t).add(m);
				}
				typeAndQS.get(type).add(m);
			} else {
				System.out.println("No Such Subscriber");
			}
		}
	}
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if(subAndQM.get(m)!=null) {
			if (typeAndQS.contains(type)) {
				typeAndQS.get(type).add(m);
			} else {
				BlockingQueue<Subscriber> tmp = new LinkedBlockingQueue<>();
				typeAndQS.put(type, tmp);
				tmp.add(m);
			}
		}
		else{
			System.out.println("No Such Subscriber");
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		EvAndFut.get(e).resolve(result);
		EvAndFut.get(e).notify();
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Class type = b.getClass();
		int size = typeAndQS.get(type).size();
		try {
			for(int i = 0; i < size; i++) {
				Subscriber s = typeAndQS.get(type).take();
				subAndQM.get(s).add(b);
				typeAndQS.get(type).add(s);
			}
		}
		catch (InterruptedException e){}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class type = e.getClass();
		Future<T> fut = new Future<>();
		EvAndFut.put(e, fut);
		if(typeAndQS.get(e.getClass()).isEmpty())
			return null;
		synchronized (e.getClass()) {
			try {
				Subscriber tmp = typeAndQS.get(e.getClass()).take();
				subAndQM.get(tmp).put(e);
				typeAndQS.get(e.getClass()).put(tmp);
			} catch (InterruptedException ex) {}
		}

		while(!fut.isDone()) {
			try {
				fut.wait();
			} catch (InterruptedException ex) {}
		}
		return fut;
	}


	@Override
	public void register(Subscriber m) {
		subAndQM.put(m, new LinkedBlockingDeque<>());
	}

	@Override
	public void unregister(Subscriber m) {
		try {
			while (!subAndQM.get(m).isEmpty()) {
				Message tmp = subAndQM.get(m).take();

				if (tmp.getClass().isAssignableFrom(Event.class)) {
					EvAndFut.get(tmp).resolve(null);
				}
			}
		}catch(InterruptedException ex){}
		subAndQM.remove(m);

		for(Class<? extends Message> iter : typeAndQS.keySet()){
			if(typeAndQS.get(iter).contains(m)){
				typeAndQS.get(iter).remove(m);
			}
		}
	}


	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
			return subAndQM.get(m).take();
	}

	

}
