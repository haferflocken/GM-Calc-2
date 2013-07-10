//A world holds:
//	component factories for materials, prefixes, and itemBases
//	a list of players in the world that have been loaded
//	rules for the players in the world
//	colors to color items based on rarity

package org.gmcalc2;

import java.util.ArrayList;

import org.gmcalc2.item.*;

public class World {
	
	private String worldLoc;

	private ComponentFactory prefixFactory;
	private ComponentFactory materialFactory;
	private ItemBaseFactory itemBaseFactory;
		
	private ArrayList<Player> players;
	private Object rules; //TODO: Define an actual rules class.
	private Object rarityColors; //TODO: Define a rarity color class.
	
	//Constructor.
	public World(String worldLoc) {
		this.worldLoc = worldLoc;
		prefixFactory = new ComponentFactory();
		materialFactory = new ComponentFactory();
		itemBaseFactory = new ItemBaseFactory();
		players = new ArrayList<>();
	}
	
	//Get a prefix from the factory.
	public Component getPrefix(String prefixName) {
		return prefixFactory.getComponent(worldLoc + prefixName);
	}
	
	//Get a material from the factory.
	public Component getMaterial(String matName) {
		return materialFactory.getComponent(worldLoc + matName);
	}
	
	//Get an itemBase from the factory.
	public ItemBase getItemBase(String itemBaseName) {
		return itemBaseFactory.getItemBase(worldLoc + itemBaseName);
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
}
