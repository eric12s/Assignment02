package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	private int tick;
	private Inventory inv;

	public Q() {
		super("Q");
		inv = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, e -> tick = e.getTick());

		subscribeEvent(GadgetAvailableEvent.class, e ->{
			System.out.println("Q received a GadgetAvailableEvent");
			boolean isAvailable = inv.getItem(e.getGadget());
			Pair<Integer, Boolean> pair = new Pair<>(tick, isAvailable);
            complete(e,  pair);
            System.out.println("Q completed the event with "+ pair.toString());
            if(isAvailable && e.getReturnGadget().get())
			    inv.addGadget(e.getGadget());
		});

		subscribeBroadcast(TerminateBroadcast.class, e -> terminate());
	}

}
