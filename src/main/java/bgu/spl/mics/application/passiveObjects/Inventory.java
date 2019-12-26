package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MessageBrokerImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;

	public void addGadget(String gadget) {
		gadgets.add(gadget);
	}

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	private Inventory(){
		gadgets = new LinkedList<>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
		for(int i = 0; i  < inventory.length; i++)
			gadgets.add(inventory[i]);
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget){
		if(gadgets.contains(gadget)){
			gadgets.remove(gadget);
			return true;
		}
		return false;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgeds.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonOutput = gson.toJson(gadgets);

			try {
				FileWriter fileWriter = new FileWriter(filename);
				fileWriter.write(jsonOutput);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public List<String> getGadgets() {
		return gadgets;
	}
}
