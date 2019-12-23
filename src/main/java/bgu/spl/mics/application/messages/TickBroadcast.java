package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;

public class TickBroadcast implements Broadcast {
    private int tick;

    public int getTick(){return tick;}
}
