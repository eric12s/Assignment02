package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class GadgetAvailableEvent implements Event {
    String gadget;
    private Future<Boolean> returnGadget;

    public GadgetAvailableEvent(String _gadget){
        gadget = _gadget;
        returnGadget = new Future<>();
    }

    public String getGadget() {
        return gadget;
    }

    public Future<Boolean> getReturnGadget() {
        return returnGadget;
    }
}
