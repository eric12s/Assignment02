package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    Future<Integer> fut;
    @BeforeEach
    public void setUp(){
        fut = new Future<>();
    }

    @Test
    public void getTest(){
        assertNull(fut.get());
        fut.resolve(5);
        assertEquals(1, fut.get());
    }

    @Test
    public void isDone(){
        assertFalse(fut.isDone());
        fut.resolve(5);
        assertTrue(fut.isDone());
    }

    @Test
    public void resolveTest(){
        fut.resolve(5);
        assertTrue(fut.isDone());
    }

    @Test
    public void getTimeTest(){
        if(fut.isDone()){
            Integer i = fut.get();
            assertEquals(i, fut.get(1000, TimeUnit.MILLISECONDS));
        }
        else{
            assertNull(fut.get(1, TimeUnit.SECONDS));
        }
    }
}
