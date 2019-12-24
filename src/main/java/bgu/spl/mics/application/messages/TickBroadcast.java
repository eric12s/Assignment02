package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {
    private int tick;

    public TickBroadcast(int _tick){tick = _tick;}
    public int getTick(){return tick;}
}
