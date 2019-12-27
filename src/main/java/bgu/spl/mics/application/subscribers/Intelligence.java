package bgu.spl.mics.application.subscribers;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import java.util.concurrent.BlockingQueue;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private BlockingQueue<MissionInfo> infos;
	private int tick;
	private int id;

	public Intelligence(BlockingQueue<MissionInfo> _infos, int _id) {
		super("Intelligence " + _id);
		infos = _infos;
		id = _id;
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(
				TickBroadcast.class,
				e -> {
					tick = e.getTick();
					for(MissionInfo m : infos){
						int start = m.getTimeIssued();
						int end = m.getTimeExpired();
						if(start <= tick && end >= tick){
							this.getSimplePublisher().sendEvent(new MissionReceivedEvent(m));
							System.out.println("Intelligence send a mission");
							infos.remove(m);
						}
					}
				}
		);

		subscribeBroadcast(TerminateBroadcast.class,
				e->this.terminate()
            );
	}

}