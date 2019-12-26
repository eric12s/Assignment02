package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GadgetAvailableEvent implements Event {
    String gadget;

    public GadgetAvailableEvent(String _gadget){
        gadget = _gadget;
    }

    public String getGadget() {
        return gadget;
    }
}
