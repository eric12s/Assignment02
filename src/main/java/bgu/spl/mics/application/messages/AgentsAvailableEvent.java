package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class AgentsAvailableEvent implements Event {
    private List<String> serialNumbers;
    private Future<Boolean> sendEvent;
    private MissionInfo missionInfo;

    public AgentsAvailableEvent(List<String> _serialNumbers, MissionInfo _missionInfo){
        serialNumbers = _serialNumbers;
        sendEvent = new Future<>();
        missionInfo = _missionInfo;
    }

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public Future<Boolean> getSendEvent() {
        return sendEvent;
    }
}
