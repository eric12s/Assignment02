package bgu.spl.mics.application.passiveObjects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;

	private static class SquadHolder{
	    private static Squad instance = new Squad();
    }

    private Squad(){
	    agents = new HashMap<>();
    }
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SquadHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public synchronized void load (Agent[] agents) {
		for(int i = 0; i < agents.length; i++){
		    this.agents.put(agents[i].getSerialNumber(), agents[i]);
        }
	}

	/**
	 * Releases agents.
	 */
	public synchronized void releaseAgents(List<String> serials){
		for(String serial : serials){
		    if(agents.containsKey(serial))
		         agents.get(serial).release();
        }
        notifyAll();
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public synchronized void sendAgents(List<String> serials, int time){
        try {
            Thread.sleep(time * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        releaseAgents(serials);
    }

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public synchronized boolean getAgents(List<String> serials) {
	    serials.sort(String::compareTo);
        for (String serial : serials) {
            if (!agents.containsKey(serial))
                return false;
        }
        for (String serial : serials) {
            while (!agents.get(serial).isAvailable()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String serial : serials) {
            agents.get(serial).acquire();
        }
        return true;
    }




    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public synchronized List<String> getAgentsNames(List<String> serials){
        List<String> names = new LinkedList<>();
        for(String serial : serials){
            if(agents.containsKey(serial)){
                names.add(agents.get(serial).getName());
            }
        }
        if(names.size() == 0)
            return null;
        return names;
    }

}
