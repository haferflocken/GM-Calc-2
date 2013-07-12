//A player is basically a name, a StatMap, some items that go into the stat map, and some items that don't.

package org.gmcalc2.item;

import java.util.ArrayList;
import java.util.TreeMap;

import org.gmcalc2.World;

public class Player {
	
		public class QuantityItem {
			
			private Item item;
			private int amount;
			
			private QuantityItem(Item item, int amount) {
				this.item = item;
				this.amount = amount;
			}
			
			public Item getItem() {
				return item;
			}
			
			public int getAmount() {
				return amount;
			}
		}
	
		public static final String DEFAULT_NAME = "Unnamed";
		public static final String NAME_KEY = "name";
		public static final String EQUIPPED_KEY = "equipped";
		public static final String INVENTORY_KEY = "inventory";

		private World world;
		private String name;
		private StatMap statMap;
		private ArrayList<QuantityItem> equipped;
		private ArrayList<QuantityItem> inventory;
		
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
				Object[] rawItems = (Object[])val;
				for (int i = 0; i < rawItems.length; i++) {
					if (rawItems[i] instanceof Object[]) {
						QuantityItem item = makeItemFromData((Object[])rawItems[i]);
						if (item != null)
							equipped.add(item);
					}
				}
			}
			
			System.out.println("Player " + name + " loaded " + equipped.size() + " equipped items.");
			//Get the inventory items.
			val = values.get(INVENTORY_KEY);
			if (val instanceof Object[]) {
				Object[] rawItems = (Object[])val;
				for (int i = 0; i < rawItems.length; i++) {
					if (rawItems[i] instanceof Object[]) {
						QuantityItem item = makeItemFromData((Object[])rawItems[i]);
						if (item != null)
							inventory.add(item);
					}
				}
			}
			System.out.println("Player " + name + " loaded " + inventory.size() + " inventory items.");
			
			//Recalculate the stats.
			recalculateStats();
		}
		
		//Accessors.
		public World getWorld() {
			return world;
		}
		
		public String getName() {
			return name;
		}
		
		public StatMap getStatMap() {
			return statMap;
		}
		
		public ArrayList<QuantityItem> getEquipped() {
			return equipped;
		}
		
		public ArrayList<QuantityItem> getInventory() {
			return inventory;
		}

		//Recalculate the stats.
		public void recalculateStats() {
			statMap.clear();
			
			for (QuantityItem item : equipped) {
				for (int i = 0; i < item.amount; i++) {
					statMap.mergeMap(item.item.getStatMap());
				}
			}
		}

		//Turn an array of objects into an item.
		public QuantityItem makeItemFromData(Object[] data) {
			//The data should have length 3. The first two elements are arrays and the third element is a string.
			if (data.length != 4 || ! (data[0] instanceof Integer) || !(data[1] instanceof Object[]) || !(data[2] instanceof Object[]) || !(data[3] instanceof String)) {
				System.out.println("Invalid item declaration in player " + name);
				return null;
			}

			//A few casts.
			int amount = (Integer)data[0];
			if (amount < 1)
				return null;
			Object[] rawPrefixes = (Object[])data[1];
			Object[] rawMaterials = (Object[])data[2];
			String rawItemBase = (String)data[3];
			
			//Make the itemBase.
			ItemBase itemBase = world.getItemBase(rawItemBase);
			if (itemBase == null) {
				System.out.println("Could not find itemBase " + rawItemBase + " for player " + name);
				return null;
			}
			
			//Make prefixes.
			Component[] prefixes = new Component[rawPrefixes.length];
			int numNull = 0;
			for (int i = 0; i < prefixes.length; i++) {
				if (rawPrefixes[i] instanceof String)
					prefixes[i] = world.getPrefix((String)rawPrefixes[i]);
				if (prefixes[i] == null)
					numNull++;
			}
			if (numNull > 0) {
				Component[] oldPrefixes = prefixes;
				prefixes = new Component[oldPrefixes.length - numNull];
				for (int q = 0, i = 0; i < oldPrefixes.length; i++) {
					if (oldPrefixes[i] != null)
						prefixes[q++] = oldPrefixes[i];
				}
			}
			
			//Make materials.
			Component[] materials = new Component[rawMaterials.length];
			numNull = 0;
			for (int i = 0; i < materials.length; i++) {
				if (rawMaterials[i] instanceof String)
					materials[i] = world.getMaterial((String)rawMaterials[i]);
				if (materials[i] == null)
					numNull++;
			}
			if (numNull > 0) {
				Component[] oldMaterials = materials;
				materials = new Component[oldMaterials.length - numNull];
				for (int q = 0, i = 0; i < oldMaterials.length; i++) {
					if (oldMaterials[i] != null)
						materials[q++] = oldMaterials[i];
				}
			}
			
			//Return the item.
			Item item = world.makeItem(prefixes, materials, itemBase);
			if (item == null)
				return null;
			return new QuantityItem(item, amount);
		}
}
