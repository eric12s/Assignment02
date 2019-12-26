package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;


import java.util.List;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int tick;
	private int id;

	public M(int _id) {
		super("M " + _id);
		id = _id;
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, e -> tick = e.getTick());

		subscribeEvent(MissionReceivedEvent.class,
				e ->
				{
					MissionInfo missionInfo = e.getMissionInfo();
					AgentsAvailableEvent agAvail = new AgentsAvailableEvent(missionInfo.getSerialAgentsNumbers(), missionInfo);
					Future<Pair<List<String>, Integer>> futAgent = getSimplePublisher().sendEvent(agAvail);

					GadgetAvailableEvent GadgetAvail = new GadgetAvailableEvent(missionInfo.getGadget());
					Future<Pair<Integer, Boolean>> futGadget = getSimplePublisher().sendEvent(GadgetAvail);
					if(futAgent != null &&futAgent.get() != null && futGadget != null &&  futGadget.get() != null && futGadget.get().getElement1()) {
						if (tick < missionInfo.getTimeExpired()) {
							agAvail.getSendEvent().resolve(true);
							GadgetAvail.getReturnGadget().resolve(false);
							Report r = new Report();
							r.setM(id);
							r.setMissionName(missionInfo.getMissionName());
							r.setAgentsNames(futAgent.get().getElement0());
							r.setAgentsSerialNumbers(missionInfo.getSerialAgentsNumbers());
							r.setGadgetName(missionInfo.getGadget());
							r.setTimeIssued(missionInfo.getTimeIssued());
							r.setMoneypenny(futAgent.get().getElement1());
							r.setQTime(futGadget.get().getElement0());
							r.setTimeCreated(tick);
							System.out.println(r.getMissionName());
							Diary.getInstance().addReport(r);
							complete(e, true);
						} else {
							complete(e, false);
							agAvail.getSendEvent().resolve(false);
							GadgetAvail.getReturnGadget().resolve(true);
						}
					}else{
						complete(e, false);
						agAvail.getSendEvent().resolve(false);
						GadgetAvail.getReturnGadget().resolve(true);
					}
					Diary.getInstance().incrementTotal();
				});

		subscribeBroadcast(TerminateBroadcast.class, e -> this.terminate());

	}

}