package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.MissionInfo;


import java.util.LinkedList;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private LinkedList<MissionInfo> _MissionMap = new LinkedList<>();
//    private LinkedList<Event> _Events = new LinkedList<>();

	public Intelligence() {
		super("need to change");
	}


	@Override
	protected void initialize() {
		// TODO Implement this
		MessageBroker messageBroker = MessageBrokerImpl.getInstance();
		messageBroker.register(this);
//		messageBroker.subscribeBroadcast(TickBroadcast.class, this);




		//exectue events
//        for(MissionInfo misson : _MissionMap){
//            MissionReceivedEvent event = new MissionReceivedEvent(misson);
//            MessageBrokerImpl.getInstance().sendEvent(event);
//        }

	}

	public void loadMission(MissionInfo missionInfo) {
		_MissionMap.add(missionInfo);
	}
}