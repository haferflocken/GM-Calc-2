// A world holds:
//	component factories for materials, prefixes, and itemBases
//	a map of players in the world that have been loaded
//	rules for the players in the world
//	colors to color items based on rarity
//	rules for sorting the stats of players

package org.gmcalc2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;

import org.newdawn.slick.Color;
import org.gmcalc2.item.*;
import org.haferlib.util.expression.ExpressionBuilder;

public class World {
	
	// The class for RarityColors.
	public static class RarityColor implements Comparable<RarityColor> {
		
		private Color color;
		private int rarity;
		
		public RarityColor(Color c, int r) {
			color = c;
			rarity = r;
		}
		
		public int compareTo(RarityColor other) {
			// First, compare rarity.
			if (rarity < other.rarity)
				return -1;
			if (rarity > other.rarity)
				return 1;
			// If rarities are equal, compare color values.
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
	
	// Keys for loading.
	public static final String NAME_KEY = "name";
	public static final String RARITYCOLORS_KEY = "rarityColors";
	public static final String PLAYERSTATCATEGORIES_KEY = "playerStatCategories";
	public static final String PLAYERBASESTATS_KEY = "playerBase";
	
	// Instance fields.
	private String worldLoc;										// The location of the world in the file GMCalc2.
	private String name;											// The name of the world.
	private RarityColor[] rarityColors;								// The rarity colors that are displayed in this world.
	private LinkedHashMap<String, String[]> playerStatCategories;	// The categories stats are sorted into in PlayerTabs.
	private StatMap playerBaseStats;								// The player's base stats.
	private Map<String, Component> prefixes;						// The prefixes.
	private Map<String, Component> materials;						// The materials.
	private Map<String, ItemBase> itemBases;						// The itemBases.
	private Map<String, Player> players;							// The players.
	
	// Constructor.
	public World(Map<String, Object> ruleValues, ExpressionBuilder expBuilder, Map<String, Component> prefixes,
			Map<String, Component> materials, Map<String, ItemBase> itemBases) {
		setRulesToDefault();
		setRules(ruleValues, expBuilder);
		this.prefixes = prefixes;
		this.materials = materials;
		this.itemBases = itemBases;
		players = null;
	}
	
	// Set the rules to default values.
	public void setRulesToDefault() {
		name = worldLoc;
		rarityColors = new RarityColor[] { new RarityColor(Color.white, Integer.MIN_VALUE) };
		playerStatCategories = new LinkedHashMap<>();
		playerBaseStats = null;
	}
	
	// Set the rules using loaded data.
	public void setRules(Map<String, Object> rawRules, ExpressionBuilder expBuilder) {
		Object val;
		// Get the name.
		val = rawRules.get(NAME_KEY);
		if (val instanceof String)
			name = (String)val;
		
		// Get the rarity colors.
		val = rawRules.get(RARITYCOLORS_KEY);
		if (val instanceof Map<?, ?>) {
			//Get the map and make an ArrayList to put the parsed colors into.
			Map<?, ?> rarityMap = (Map<?, ?>)val;
			ArrayList<RarityColor> rarityColorList = new ArrayList<>();
			
			// Look through the map, parsing out rarity colors.
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
			
			// If we successfully parsed any, set rarityColors to the contents of rarityColorList and then sort.
			if (rarityColorList.size() > 0) {
				rarityColors = rarityColorList.toArray(new RarityColor[rarityColorList.size()]);
				Arrays.sort(rarityColors);
			}
		}
		
		// Get the player stat categories.
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
		
		// Get the player base stats.
		val = rawRules.get(PLAYERBASESTATS_KEY);
		if (val instanceof Map<?, ?>) {
			playerBaseStats = new StatMap((Map<?, ?>)val, expBuilder);
		}
	}
	
	// Get the name.
	public String getName() {
		return name;
	}
	
	// Get the player stat categories.
	public Map<String, String[]> getPlayerStatCategories() {
		return playerStatCategories;
	}
	
	// Get the player base stats.
	public StatMap getPlayerBaseStats() {
		return playerBaseStats;
	}
	
	// Get a prefix.
	public Component getPrefix(String prefixName) {
		return prefixes.get(prefixName);
	}
	
	// Get the prefix map.
	public Map<String, Component> getPrefixMap() {
		return prefixes;
	}
	
	// Get a material.
	public Component getMaterial(String matName) {
		return materials.get(matName);
	}
	
	// Get the material map.
	public Map<String, Component> getMaterialMap() {
		return materials;
	}
	
	// Get an itemBase.
	public ItemBase getItemBase(String itemBaseName) {
		return itemBases.get(itemBaseName);
	}
	
	// Get the item base map.
	public Map<String, ItemBase> getItemBaseMap() {
		return itemBases;
	}
	
	
	// Make an item with no prefixes and with default materials.
	public Item makeItem(ItemBase itemBase) {
		// Get the default materials.
		String[] defMatNames = itemBase.getDefaultMaterials();
		
		// If there are no default materials, make a materialless item.
		if (defMatNames == null)
			return makeItem(new Component[0], itemBase);
		
		// Otherwise, find the materials and make the item.
		ArrayList<Component> materialList = new ArrayList<>();
		for (int i = 0; i < defMatNames.length; i++) {
			Component mat = materials.get(defMatNames[i]);
			if (mat != null)
				materialList.add(mat);
		}
		
		// Make and return the item.
		Component[] materials = materialList.toArray(new Component[materialList.size()]);
		return makeItem(materials, itemBase);
	}
	
	// Make an item with no prefixes and some materials.
	public Item makeItem(Component[] materials, ItemBase itemBase) {
		return makeItem(new Component[0], materials, itemBase);
	}
	
	// Make an item with some prefixes and some materials.
	public Item makeItem(Component[] prefixes, Component[] materials, ItemBase itemBase) {
		return new Item(prefixes, materials, itemBase);
	}
	
	// Get a player.
	public Player getPlayer(String playerFile) {
		return players.get(playerFile);
	}

	// Get the player map.
	public Map<String, Player> getPlayerMap() {
		return players;
	}
	
	// Set the player map.
	public void setPlayerMap(Map<String, Player> p) {
		players = p;
	}
	
	// Get the rarity color of an item.
	public Color getRarityColor(Item item) {
		int rarity = item.getRarity();
		for (int i = rarityColors.length - 1; i > -1; i--) {
			if (rarity >= rarityColors[i].rarity)
				return rarityColors[i].color;
		}
		return Color.black;
	}
	
	// Helper method to get components matching from a map.
	private Component[] getComponentsMatching(TagRequirement requirement, Map<String, Component> map) {
		// Count the number of prefixes.
		int numOut = 0;
		for (Map.Entry<String, Component> entry : map.entrySet()) {
			Component c = entry.getValue();
			if (requirement.passes(c))
				numOut++;
		}
				
		// Fill and return an output array.
		Component[] out = new Component[numOut];
		int i = 0;
		for (Map.Entry<String, Component> entry : map.entrySet()) {
			Component c = entry.getValue();
			if (requirement.passes(c))
				out[i++] = c;
		}
		return out;
	}
	
	// Get the prefixes matching a tag requirement.
	public Component[] getPrefixesMatching(TagRequirement requirement) {
		return getComponentsMatching(requirement, prefixes);
	}
	
	// Get the materials matching a tag requirement.
	public Component[] getMaterialsMatching(TagRequirement requirement) {
		return getComponentsMatching(requirement, materials);
	}
}
