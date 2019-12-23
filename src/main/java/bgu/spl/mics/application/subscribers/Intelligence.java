package bgu.spl.mics.application.subscribers;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;


import java.util.LinkedList;
import java.util.List;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private List<MissionInfo> infos;
	private int tick;


	public Intelligence(LinkedList<MissionInfo> _infos) {
		super("Intelligence");
		infos = _infos;
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(
				TickBroadcast.class,
				e -> {
					tick = e.getTick();
					for(MissionInfo m : infos){
						int tmp = m.getTimeIssued();
						if(tmp <= tick){
							this.getSimplePublisher().sendEvent(new MissionReceivedEvent(m));
							infos.remove(m);
						}
					}
				}
		);

		subscribeBroadcast(TerminateBroadcast.class,
				e->{this.terminate();}
				);
	}

}