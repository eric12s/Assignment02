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
		//if(EvAndFut.get(e) != null) { //TODO: why not null??
			if (!terminated)
				EvAndFut.get(e).resolve(result);
			else
				EvAndFut.get(e).resolve(null);
		//}
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
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if(terminated)
			return null;

		Class type = e.getClass();
		typeAndQS.putIfAbsent(e.getClass(), new LinkedBlockingQueue<>());
		synchronized (e.getClass()) {
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
		}
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


/*

package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


*/
/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *//*


/*public class MessageBrokerImpl implements MessageBroker {
    private static class MessageBrokerImplHolder {
        private static MessageBrokerImpl instance = new MessageBrokerImpl();
    }

    private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<Subscriber>> typeMessageQueue;
    private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> subscribersQueue;
    private ConcurrentHashMap<Event, Future> promises;
    private BlockingQueue<Future> allFutures;
    private Boolean terminated;

    private MessageBrokerImpl() {
        typeMessageQueue = new ConcurrentHashMap<>();
        subscribersQueue = new ConcurrentHashMap<>();
        promises = new ConcurrentHashMap<>();
        allFutures = new LinkedBlockingQueue<>();
        terminated = false;
    }


*//**
     * Retrieves the single instance of this class.
     *//*

    public static MessageBroker getInstance() {
        return MessageBrokerImplHolder.instance;
    }

    @Override

    public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
        if (subscribersQueue.containsKey(m)) {
            typeMessageQueue.putIfAbsent(type, new LinkedBlockingQueue<>());
            if (!typeMessageQueue.get(type).contains(m))
                typeMessageQueue.get(type).add(m);
            this.notifyAll();
        }
    }

    @Override

    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
        if (subscribersQueue.containsKey(m)) {
            typeMessageQueue.putIfAbsent(type, new LinkedBlockingQueue<>());
            if (!typeMessageQueue.get(type).contains(m))
                typeMessageQueue.get(type).add(m);
        }
        this.notifyAll();
    }

    @Override
    public synchronized <T> void complete(Event<T> e, T result) {
        if (result != null && result.getClass().equals(Future.class)) {
            allFutures.add((Future) result);
            if (terminated)
                ((Future) result).resolve(null);
        }
        promises.get(e).resolve(result);
    }

    @Override
    //Here we need the synchronized it wont
    public synchronized void sendBroadcast(Broadcast b) {
        if (terminated)
            return;

        if (b.getClass().equals(TerminateBroadcast.class)) {
            terminated = true;
            for (BlockingQueue<Message> subscriberQueue : subscribersQueue.values())
                subscriberQueue.clear();
            for (Future future : allFutures)
                if (future != null)
                    future.resolve(null);
        }

        if (typeMessageQueue.containsKey(b.getClass()))
            for (Subscriber s : typeMessageQueue.get(b.getClass()))
                subscribersQueue.get(s).add(b);

        this.notifyAll();
    }


    @Override
    public synchronized <T> Future<T> sendEvent(Event<T> e) {
        if (terminated)
            return null;

        while (
                !typeMessageQueue.containsKey(e.getClass()) &&
                        typeMessageQueue.containsKey(e.getClass()) &&
                        typeMessageQueue.get(e.getClass()).isEmpty()
        ) {
            try {
                this.wait();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        if (terminated)
            return null;

        Subscriber subscriber = typeMessageQueue.get(e.getClass()).peek();
        typeMessageQueue.get(e.getClass()).remove(subscriber);
        typeMessageQueue.get(e.getClass()).add(subscriber);
        Future<T> future = new Future<>();
        promises.put(e, future);
        allFutures.add(future);

        try {
            subscribersQueue.get(subscriber).put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        this.notifyAll();
        return future;
    }

    @Override
    public void register(Subscriber m) {
        subscribersQueue.put(m, new LinkedBlockingQueue<>());
    }


    @Override
    //TODO: no need synchronized here
    public void unregister(Subscriber m) {
        subscribersQueue.remove(m);
        for (BlockingQueue<Subscriber> queue : typeMessageQueue.values())
            if (queue.contains(m))
                queue.remove(m);
    }

    @Override
    public synchronized Message awaitMessage(Subscriber m) throws InterruptedException {
        while (subscribersQueue.get(m).isEmpty())
            this.wait();
        return subscribersQueue.get(m).remove();
    }

    public synchronized void clear() {
        typeMessageQueue.clear();
        subscribersQueue.clear();
        promises.clear();
        allFutures.clear();
        terminated = false;
    }
}*/

