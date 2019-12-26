package bgu.spl.mics.application.json;

import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class JsonParser {
    public String[] inventory;
    public Services services;
    public Squad[] squad;

    public class Squad {
        public String name;
        public String serialNumber;
    }

    public class Mission {
        public List<String> serialAgentsNumbers;
        public int duration;
        public String gadget;
        public String name;
        public int timeExpired;
        public int timeIssued;
    }

    public class Intelligence {
        public Mission[] missions;
    }

    public class Services {
        public int M;
        public int Moneypenny;
        public Intelligence[] intelligence;
        public int time;
    }


}