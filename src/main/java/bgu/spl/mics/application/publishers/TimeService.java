
package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;


/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class TimeService extends Publisher {
	private int currentTick;
	private int lastTick;

	public TimeService(int _lastTick) {
		super("Time Service");
		lastTick = _lastTick;
	}

	@Override
	protected void initialize() {
		currentTick = 0;
	}

	@Override
	public void run() {
		while(currentTick != lastTick){
			increaseTick();
			System.out.println(currentTick);
			getSimplePublisher().sendBroadcast(new TickBroadcast(currentTick));
		}
		getSimplePublisher().sendBroadcast(new TerminateBroadcast());
	}

	public void increaseTick(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		currentTick++;
	}

}
