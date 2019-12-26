package bgu.spl.mics.application;

import bgu.spl.mics.application.json.JsonParser;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    private static Inventory inventory;
    private static M[] m;
    private static Moneypenny[] monneypenny;
    private static Intelligence[] intelligence;
    private static Q q;
    private static Squad squad;
    private static List<Thread> threadPool;
    private static Diary diary;
    private static TimeService timeService;

    public static void main(String[] args) {
        initialize(args);

        // start all threads
        for (Thread thread : threadPool)
            thread.start();

        // join all threads to the current thread
        for (Thread thread : threadPool) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // after the threads finish their tasks
        inventory.printToFile(args[1]);
        diary.printToFile(args[2]);
    }

    public static void initialize(String args[]) {
        Gson gson = new Gson();
        JsonParser json = null;

        try {
            json = gson.fromJson(new FileReader(args[0]), JsonParser.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        threadPool = new LinkedList<>();

        // Creating Inventory
        inventory = Inventory.getInstance();
        inventory.load(json.inventory);

        diary = Diary.getInstance();
        //diary.setTotal(0);

        // Creating M
        m = new M[json.services.M];
        for (int i = 0; i < json.services.M; i = i + 1) {
            m[i] = new M(i);
            threadPool.add(new Thread(m[i]));
        }

        // Creating Monneypenny
        monneypenny = new Moneypenny[json.services.Moneypenny];
        for (int i = 0; i < json.services.Moneypenny; i = i + 1) {
            monneypenny[i] = new Moneypenny(i);
            threadPool.add(new Thread(monneypenny[i]));
        }


        // Creating intelligence with Missions
        intelligence = new Intelligence[json.services.intelligence.length];
        int counter = 0;
        BlockingQueue<MissionInfo> missions = new LinkedBlockingQueue<>();
        for (JsonParser.Intelligence _intelligence : json.services.intelligence) {
         //   MissionInfo[] missionInfos = gson.fromJson(json.services.intelligence ,MissionInfo[].class);
            for (JsonParser.Mession mission : _intelligence.missions) {
                MissionInfo missionInfo = new MissionInfo();
                missionInfo.setSerialAgentsNumbers(mission.serialAgentsNumbers);
                missionInfo.setDuration(mission.duration);
                missionInfo.setGadget(mission.gadget);
                missionInfo.setMissionName(mission.missionName);
                missionInfo.setTimeExpired(mission.timeExpired);
                missionInfo.setTimeIssued(mission.timeIssued);
                missions.add(missionInfo);
            }
            intelligence[counter] = new Intelligence(missions, counter);
            threadPool.add(new Thread(intelligence[counter]));
            counter++;
        }

        //Creating TimeService
        timeService = new TimeService(json.services.time);
        threadPool.add(new Thread(timeService));

        //creating q
        q = new Q();
        threadPool.add(new Thread(q));

        // Creating Squad
        squad = Squad.getInstance();
        Agent[] agents = new Agent[json.squad.length];
        for (int i = 0; i < json.squad.length; i = i + 1) {
            String name = json.squad[i].name;
            String serialNumber = json.squad[i].serialNumber;
            agents[i] = new Agent(serialNumber, name);
        }
        squad.load(agents);
    }
}