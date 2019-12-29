package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.List;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private int id;
	private Squad squad;

	public Moneypenny(int _id) {
		super("Moneypenny " + _id);
		id = _id;
		squad = Squad.getInstance();
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		subscribeEvent(AgentsAvailableEvent.class, e -> {
			List<String> serialNumbers = e.getSerialNumbers();
			boolean b = squad.getAgents(serialNumbers);
			if(isTerminated()) {
				complete(e, null);
				squad.releaseAgents(serialNumbers);
				return;
			}

			Pair<List<String>, Integer> pair = new Pair<>(squad.getAgentsNames(serialNumbers), id);
			complete(e, pair);
			if(b){
				if(!isTerminated() && e.getSendEvent() != null && e.getSendEvent().get()){
					Squad.getInstance().sendAgents(serialNumbers, e.getMissionInfo().getDuration());
				}
				else{
					Squad.getInstance().releaseAgents(serialNumbers);
				}
			}
		});

		subscribeBroadcast(TerminateBroadcast.class, e -> terminate());


	}

}
