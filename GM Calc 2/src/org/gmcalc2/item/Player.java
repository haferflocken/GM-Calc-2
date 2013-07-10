//A player is basically a name, a StatMap, some items that go into the stat map, and some items that don't.

package org.gmcalc2.item;

import java.util.ArrayList;
import java.util.TreeMap;

import org.gmcalc2.World;

public class Player {
	
		public static final String DEFAULT_NAME = "Unnamed";
		public static final String NAME_KEY = "name";
		public static final String EQUIPPED_KEY = "equipped";
		public static final String INVENTORY_KEY = "inventory";

		private World world;
		private String name;
		private StatMap statMap;
		private ArrayList<Item> equipped;
		private ArrayList<Item> inventory;
		
		//Constructors.
		public Player(World world) {
			this.world = world;
			name = DEFAULT_NAME;
			statMap = new StatMap();
			equipped = new ArrayList<>();
			inventory = new ArrayList<>();
		}
		
		public Player(World world, TreeMap<String, Object> values) {
			this(world);
			
			Object val;
			//Get the name.
			val = values.get(NAME_KEY);
			if (val instanceof String)
				name = (String)val;
			//Get the equipped items.
			val = values.get(EQUIPPED_KEY);
			if (val instanceof Object[]) {
				//TODO
			}
			//Get the inventory items.
			val = values.get(INVENTORY_KEY);
			if (val instanceof Object[]) {
				//TODO
			}
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

		//Turn an array of objects into an item.
		public Item makeItemFromData(Object[] data) {
			//The data should have length 3.
			if (data.length != 3)
				return null;
			
		}
}
