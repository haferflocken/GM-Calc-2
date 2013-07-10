package org.gmcalc2.item;

import java.util.ArrayList;
import java.util.TreeMap;

public class Player {
	
		public static final String DEFAULT_NAME = "Unnamed";

		private String name;
		private StatMap statMap;
		private ArrayList<Item> equipped;
		private ArrayList<Item> inventory;
		
		//Constructors.
		public Player() {
			name = DEFAULT_NAME;
			statMap = new StatMap();
			equipped = new ArrayList<>();
			inventory = new ArrayList<>();
		}
		
		public Player(TreeMap<String, Object> values) {
			this();
		}
		
		//Accessors.
		public String getName() {
			return name;
		}
		
		public StatMap getStatMap() {
			return statMap;
		}
		
		public ArrayList<Item> getEquipped() {
			return equipped;
		}
		
		public ArrayList<Item> getInventory() {
			return inventory;
		}

		//Recalculate the stats.
		public void recalculateStats() {
			statMap.clear();
			
			for (Item i : equipped) {
				statMap.mergeMap(i.getStatMap());
			}
		}
}
