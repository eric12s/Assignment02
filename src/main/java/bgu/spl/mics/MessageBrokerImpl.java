package bgu.spl.mics;


//import com.sun.org.apache.xpath.internal.operations.Bool;

import bgu.spl.mics.application.messages.TerminateBroadcast;

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
	private boolean terminated;

	private static class MessageBrokerHolder {
		private static MessageBrokerImpl instance = new MessageBrokerImpl();
	}

	private MessageBrokerImpl() {
		typeAndQS = new ConcurrentHashMap<>();
		subAndQM = new ConcurrentHashMap<>();
		EvAndFut = new ConcurrentHashMap<>();
		terminated = false;
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
				typeAndQS.putIfAbsent(type, new LinkedBlockingQueue<>());
				typeAndQS.get(type).add(m);
			} else {
				System.out.println("No Such Subscriber");
			}
		}
	}
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if(subAndQM.get(m)!=null) {
			typeAndQS.putIfAbsent(type, new LinkedBlockingQueue<>());
			typeAndQS.get(type).add(m);
		}
		else{
			System.out.println("No Such Subscriber");
		}
	}

	@Override
	public synchronized  <T> void complete(Event<T> e, T result) {
			if (!terminated)
				EvAndFut.get(e).resolve(result);
			else
				EvAndFut.get(e).resolve(null);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(terminated)
			return;

		Class type = b.getClass();
		typeAndQS.putIfAbsent(type, new LinkedBlockingQueue<>());
		int size = typeAndQS.get(type).size();
		try {
			if (type == TerminateBroadcast.class) {
				terminated = true;

				for (BlockingQueue<Message> tmp : subAndQM.values())
					tmp.clear();
				for (Future future : EvAndFut.values())
					future.resolve(null);
			}

			for(int i = 0; i < size; i++) {
				Subscriber s = typeAndQS.get(type).take();
				subAndQM.get(s).add(b);
				typeAndQS.get(type).add(s);
			}

			if(terminated) {
				for (Subscriber sub : subAndQM.keySet())
					sub.terminate();
			}
		}
		catch (InterruptedException e){}
	}

	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if(terminated)
			return null;

		Class type = e.getClass();
		typeAndQS.putIfAbsent(e.getClass(), new LinkedBlockingQueue<>());
		if(typeAndQS.get(type).isEmpty()) {
			return null;
		}
		if(terminated)
			return null;

		try {
			Subscriber tmp = typeAndQS.get(e.getClass()).take();
			subAndQM.get(tmp).put(e);
			typeAndQS.get(e.getClass()).put(tmp);
		} catch (InterruptedException ex) {}
		Future<T> fut = new Future<>();
		EvAndFut.put(e, fut);
		return fut;
	}


	@Override
	public void register(Subscriber m) {
		subAndQM.put(m, new LinkedBlockingQueue<>());
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

	@Override
	public void clear() {//TODO: Remove it
		typeAndQS.clear();
		subAndQM.clear();
		EvAndFut.clear();
		terminated = false;
	}


}