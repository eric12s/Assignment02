package bgu.spl.mics;

import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.subscribers.ExampleBroadcastSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {
    public class myEvent implements Event<Integer>{};
    public class myBroadcast implements Broadcast{};

    MessageBroker mb;
    Subscriber sub1;
    Subscriber sub2;
    myEvent ev;
    Message m;
    Message m2;
    myBroadcast br;
    @BeforeEach
    public void setUp(){
        sub1 = new M(1);
        sub2 = new M(2);

        mb = MessageBrokerImpl.getInstance();
        mb.register(sub1);
        mb.register(sub2);
    }

    @Test
    public void subscribeEventTest(){
        mb.subscribeEvent(ev.getClass(), sub1);
        mb.sendEvent(ev);
        m = null;
        try{
            m = mb.awaitMessage(sub1);
        }
        catch (Exception e){
        }
        assertNotNull(m);
        assertEquals(ev, m);
    }

    @Test
    public void subscribeBroadcastTest(){
        mb.subscribeBroadcast(br.getClass(), sub2);
        mb.sendBroadcast(br);
        m2 = null;
        try{
            m2 = mb.awaitMessage(sub2);
        }
        catch (Exception e){
        }
        assertNotNull(m2);
        assertEquals(br, m2);
    }

    @Test
    public void completeTest(){
        Future<Integer> f = mb.sendEvent(ev);
        mb.complete(ev, 0);
        assertTrue(f.isDone());
        assertEquals(0, f.get());
    }

    @Test
    public void registerTest(){
        Subscriber s3 = new M(3);
        mb.register(s3);
        mb.subscribeEvent(ev.getClass(), s3);
        mb.sendEvent(ev);
        m = null;
        try{
           assertEquals(ev, mb.awaitMessage(s3));
        }
        catch (Exception e){
        }
    }

    @Test
    public void sendEventTest(){
        Subscriber s3 = new M(3);
        mb.register(s3);
        mb.subscribeEvent(ev.getClass(), s3);
        mb.sendEvent(ev);
        m = null;
        try{
            assertEquals(ev, mb.awaitMessage(s3));
        }
        catch (Exception e){
        }
    }

    @Test
    public void sendBroadcastTest(){
        Subscriber s3 = new M(3);
        mb.register(s3);
        mb.subscribeBroadcast(br.getClass(), s3);
        mb.sendBroadcast(br);
        m = null;
        try{
            assertEquals(br, mb.awaitMessage(s3));
        }
        catch (Exception e){
        }
    }

    @Test
    public void unregisterTest(){
        mb.unregister(sub1);
        mb.sendEvent(ev);
        try {
            Message m = mb.awaitMessage(sub1);
        }
        catch (Exception e){
            assertEquals(e.toString(),new IllegalStateException().toString());
        }
        mb.register(sub1);
    }

    @Test
    public void awaitMessageTest(){
        mb.subscribeEvent(ev.getClass(), sub1);
        mb.sendEvent(ev);
        m = null;
        try{
            m = mb.awaitMessage(sub1);
        }
        catch (Exception e){
        }
        assertNotNull(m);
        assertEquals(ev, m);
    }
}
