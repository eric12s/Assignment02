package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    private Squad sq;
    private Agent[] ag;
    @BeforeEach
    public void setUp(){
        sq = Squad.getInstance();
        ag = new Agent[3];
        ag[0].setSerialNumber("000");
        ag[1].setSerialNumber("007");
        ag[2].setSerialNumber("006");
        ag[0].setName("Agent1");
        ag[1].setName("Agent2");
        ag[2].setName("Agent3");
    }

    @Test
    public void getInstanceTest(){
        assertEquals(sq, Squad.getInstance(), "getInstance failed");
    }

    @Test
    public void loadTest(){
        sq.load(ag);
        List<String> serials = new ArrayList<>();
        for(int i = 0; i <= 2; i++){
            serials.add(ag[i].getSerialNumber());
        }
        assertTrue(sq.getAgents(serials));
    }

    @Test
    public void releaseAgentsTest(){
        List<String> serials = new ArrayList<>();
        serials.add(ag[0].getSerialNumber());
        ag[0].acquire();
        sq.releaseAgents(serials);
        assertTrue(ag[0].isAvailable());
    }

    @Test
    public void sendAgentsTest(){
        List<String> serials = new ArrayList<>();
        serials.add(ag[0].getSerialNumber());
        ag[0].release();
        sq.sendAgents(serials, 4000);
        assertFalse(ag[0].isAvailable());
        try {
            TimeUnit.MILLISECONDS.sleep(4100);
        }
        catch(Exception e){
        }
        assertTrue(ag[0].isAvailable());
    }

    @Test
    public void getAgentsTest(){
        List<String> serials = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            serials.add(ag[i].getSerialNumber());
        }
        ag[0].acquire();
        ag[1].acquire();
        assertFalse(sq.getAgents(serials));
        ag[0].release();
        ag[1].release();
        assertTrue(sq.getAgents(serials));
    }

    @Test
    public void getAgentsNamesTest(){
        List<String> serials = new ArrayList<>();
        for(int i = 0; i <= 2; i++){
            serials.add(ag[i].getSerialNumber());
        }
        List<String> validNames= new ArrayList<>();
        validNames.add("Agent1");
        validNames.add("Agent2");
        validNames.add("Agent3");
        assertEquals(validNames, sq.getAgentsNames(serials));
    }
}
