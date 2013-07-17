//A world holds:
//	component factories for materials, prefixes, and itemBases
//	a map of players in the world that have been loaded
//	rules for the players in the world
//	colors to color items based on rarity
//	rules for sorting the stats of players

package org.gmcalc2;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.io.IOException;

import org.newdawn.slick.Color;

import org.gmcalc2.item.*;

import org.haferlib.util.DataReader;

public class World {
	
	//The class for RarityColors.
	public static class RarityColor implements Comparable<RarityColor> {
		
		private Color color;
		private int rarity;
		
		public RarityColor(Color c, int r) {
			color = c;
			rarity = r;
		}
		
		public int compareTo(RarityColor other) {
			//First, compare rarity.
			if (rarity < other.rarity)
				return -1;
			if (rarity > other.rarity)
				return 1;
			//If rarities are equal, compare color values.
			if (color.getRed() < other.color.getRed()) 
				return -1;
			if (color.getRed() > other.color.getRed())
				return 1;
			if (color.getGreen() < other.color.getGreen())
				return -1;
			if (color.getGreen() > other.color.getGreen())
				return 1;
			if (color.getBlue() < other.color.getBlue())
				return -1;
			if (color.getBlue() > other.color.getBlue())
				return 1;
			return 0;
		}
		
		public boolean equals(Object other) {
			if (other instanceof RarityColor) {
				RarityColor o = (RarityColor)other;
				if (color.equals(o.color) && rarity == o.rarity)
					return true;
			}
			return false;
		}
	}
	
	//Keys for loading.
	public static final String NAME_KEY = "name";
	public static final String RARITYCOLORS_KEY = "rarityColors";
	public static final String PLAYERSTATCATEGORIES_KEY = "playerStatCategories";
	
	//Instance fields.
	private String worldLoc;								//The location of the world in the file GMCalc2.
	private DataReader dataReader;							//The data reader that reads files in the world.
	private String name;									//The name of the world.
	private RarityColor[] rarityColors;						//The rarity colors that are displayed in this world.
	private Map<String, String[]> playerStatCategories;		//The categories stats are sorted into in PlayerTabs.
	private ComponentFactory prefixFactory;					//The factory that loads prefixes.
	private ComponentFactory materialFactory;				//The factory that loads materials.
	private ItemBaseFactory itemBaseFactory;				//The factory that loads item bases.
	private TreeMap<String, Player> players;				//The map of players.
	
	//Constructor.
	public World(String worldLoc) {
		GMCalc2.out.println("\nCreating World: " + worldLoc);
		this.worldLoc = worldLoc;
		dataReader = new DataReader();
		
		GMCalc2.out.println("Loading rules...");
		setRulesToDefault();
		try {
			TreeMap<String, Object> rawRules = dataReader.readFile(worldLoc + "rules.txt");
			setRules(rawRules);
			GMCalc2.out.println("Loaded rules.");
		}
		catch (IOException e) {
			GMCalc2.out.println("Failed to load rules.");
		}
		
		GMCalc2.out.println("Caching prefixes...");
		prefixFactory = new ComponentFactory(dataReader);
		prefixFactory.cacheDirectory(worldLoc + "prefixes\\");
		GMCalc2.out.println("Prefixes cached.\nCaching materials...");
		materialFactory = new ComponentFactory(dataReader);
		materialFactory.cacheDirectory(worldLoc + "materials\\");
		GMCalc2.out.println("Materials cached.\nCaching itemBases...");
		itemBaseFactory = new ItemBaseFactory(dataReader);
		itemBaseFactory.cacheDirectory(worldLoc + "itemBases\\");
		players = new TreeMap<>();
		GMCalc2.out.println("ItemBases cached.\n... World created.\n");
	}
	
	//Set the rules to default values.
	public void setRulesToDefault() {
		name = worldLoc;
		rarityColors = new RarityColor[] { new RarityColor(Color.white, Integer.MIN_VALUE) };
		playerStatCategories = new LinkedHashMap<>();
	}
	
	//Set the rules using loaded data.
	public void setRules(TreeMap<String, Object> rawRules) {
		Object val;
		//Get the name.
		val = rawRules.get(NAME_KEY);
		if (val instanceof String)
			name = (String)val;
		
		//Get the rarity colors.
		val = rawRules.get(RARITYCOLORS_KEY);
		if (val instanceof Map<?, ?>) {
			//Get the map and make an ArrayList to put the parsed colors into.
			Map<?, ?> rarityMap = (Map<?, ?>)val;
			ArrayList<RarityColor> rarityColorList = new ArrayList<>();
			
			//Look through the map, parsing out rarity colors.
			for (Map.Entry<?, ?> entry : rarityMap.entrySet()) {
				if (entry.getKey() instanceof Integer && entry.getValue() instanceof Object[]) {
					Integer rarityVal = (Integer)entry.getKey();
					Object[] rawColor = (Object[])entry.getValue();
					if (rawColor.length == 3 && rawColor[0] instanceof Integer && rawColor[1] instanceof Integer && rawColor[2] instanceof Integer) {
						int red = (Integer)rawColor[0];
						int green = (Integer)rawColor[1];
						int blue = (Integer)rawColor[2];
						Color rarityColor = new Color(red, green, blue);
						rarityColorList.add(new RarityColor(rarityColor, rarityVal));
					}
				}
			}
			
			//If we successfully parsed any, set rarityColors to the contents of rarityColorList and then sort.
			if (rarityColorList.size() > 0) {
				rarityColors = rarityColorList.toArray(new RarityColor[rarityColorList.size()]);
				Arrays.sort(rarityColors);
			}
		}
		
		//Get the player stat categories.
		val = rawRules.get(PLAYERSTATCATEGORIES_KEY);
		if (val instanceof Map<?, ?>) {
			Map<?, ?> catMap = (Map<?, ?>)val;
			ArrayList<String> catValBuilder = new ArrayList<>();
			for (Map.Entry<?, ?> entry : catMap.entrySet()) {
				if (entry.getKey() instanceof String && entry.getValue() instanceof Object[]) {
					String catKey = (String)entry.getKey();
					Object[] rawCatVal = (Object[])entry.getValue();
					for (Object o : rawCatVal) {
						if (o instanceof String)
							catValBuilder.add((String)o);
					}
					String[] catVal = catValBuilder.toArray(new String[catValBuilder.size()]);
					catValBuilder.clear();
					playerStatCategories.put(catKey, catVal);
				}
			}
		}
	}
	
	//Get the name.
	public String getName() {
		return name;
	}
	
	//Get the player stat categories.
	public Map<String, String[]> getPlayerStatCategories() {
		return playerStatCategories;
	}
	
	//Get a prefix from the factory.
	public Component getPrefix(String prefixName) {
		return prefixFactory.getComponent(worldLoc + "prefixes\\" + prefixName);
	}
	
	//Get a material from the factory.
	public Component getMaterial(String matName) {
		return materialFactory.getComponent(worldLoc + "materials\\" + matName);
	}
	
	//Get an itemBase from the factory.
	public ItemBase getItemBase(String itemBaseName) {
		return itemBaseFactory.getItemBase(worldLoc + "itemBases\\" + itemBaseName);
	}
	
	//Make an item with no prefixes and with default materials.
	public Item makeItem(ItemBase itemBase) {
		//Get the default materials.
		String[] defMatNames = itemBase.getDefaultMaterials();
		
		//If there are no default materials, make a materialless item.
		if (defMatNames == null)
			return makeItem(new Component[0], itemBase);
		
		//Otherwise, find the materials and make the item.
		ArrayList<Component> materialList = new ArrayList<>();
		for (int i = 0; i < defMatNames.length; i++) {
			Component mat = materialFactory.getComponent(defMatNames[i]);
			if (mat != null)
				materialList.add(mat);
		}
		
		//Make and return the item.
		Component[] materials = materialList.toArray(new Component[materialList.size()]);
		return makeItem(materials, itemBase);
	}
	
	//Make an item with no prefixes and some materials.
	public Item makeItem(Component[] materials, ItemBase itemBase) {
		return makeItem(new Component[0], materials, itemBase);
	}
	
	//Make an item with some prefixes and some materials.
	public Item makeItem(Component[] prefixes, Component[] materials, ItemBase itemBase) {
		return new Item(prefixes, materials, itemBase);
	}
	
	//Get a player.
	public Player getPlayer(String playerFile) {
		//Return a player if we find one.
		Player player = players.get(playerFile);
		if (player != null)
			return player;
		
		//Otherwise, if the path points to a file, load it, place it in the tree, and return it.
		try {
			TreeMap<String, Object> playerValues = dataReader.readFile(worldLoc + "players\\" + playerFile);
			player = new Player(this, playerValues);
			players.put(playerFile, player);
			return player;
		}
		catch (IOException e) {
		}
		return null;
	}

	//Get the rarity color of an item.
	public Color getRarityColor(Item item) {
		int rarity = item.getRarity();
		for (int i = rarityColors.length - 1; i > -1; i--) {
			if (rarity >= rarityColors[i].rarity)
				return rarityColors[i].color;
		}
		return Color.black;
	}
}
