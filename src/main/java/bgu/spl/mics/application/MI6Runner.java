package bgu.spl.mics.application;

import bgu.spl.mics.application.json.JsonParser;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

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
    private static Squad squad;

    public static void main(String[] args) {
        initialize();

        Thread _intelligence = new Thread(intelligence[0]);
        Thread _m = new Thread(m[0]);
        Thread _monneypenny = new Thread(monneypenny[0]);
        Thread _q = new Thread(new Q());

        _intelligence.start();
        _m.start();
        _monneypenny.start();
        _q.start();
    }

    public static void initialize() {
        Gson gson = new Gson();
        JsonParser json = null;

        try {
            json = gson.fromJson(new FileReader("/users/studs/bsc/2020/ericsa/workspace/assignment2/input.json"), JsonParser.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Creating Inventory
        inventory = Inventory.getInstance();
        inventory.load(json.inventory);

        // Creating M
        m = new M[json.services.M];
        for (int i = 0; i < json.services.M; i = i + 1)
            m[i] = new M(); // TODO: Add M.id field

        // Creating Monneypenny
        monneypenny = new Moneypenny[json.services.Moneypenny];
        for (int i = 0; i < json.services.Moneypenny; i = i + 1)
            monneypenny[i] = new Moneypenny();

        // Creating intelligence with Missions
        intelligence = new Intelligence[json.services.intelligence.length];
        int counter = 0;
        LinkedList<MissionInfo> missions = new LinkedList<>();
        for (JsonParser.Intelligence _intelligence : json.services.intelligence) {
            //intelligence[counter] = new Intelligence();
            for (JsonParser.Mession mission : _intelligence.missions) {
                MissionInfo missionInfo = new MissionInfo();
                missionInfo.setSerialAgentsNumbers(mission.serialAgentsNumbers);
                missionInfo.setDuration(mission.duration);
                missionInfo.setGadget(mission.gadget);
                missionInfo.setMissionName(mission.missionName);
                missionInfo.setTimeExpired(mission.timeExpired);
                missionInfo.setTimeIssued(mission.timeIssued);
                missions.add(missionInfo);
                //intelligence[counter].loadMission(missionInfo);
            }
            intelligence[counter] = new Intelligence(missions);
            counter = counter + 1;
        }


        // Creating Squad
        Agent[] agents = new Agent[json.squad.length];
        for (int i = 0; i < json.squad.length; i = i + 1) {
            agents[i] = new Agent();
            agents[i].setName(json.squad[i].name);
            agents[i].setSerialNumber(json.squad[i].serialNumber);
        }
        squad = Squad.getInstance();
        squad.load(agents);
    }
}