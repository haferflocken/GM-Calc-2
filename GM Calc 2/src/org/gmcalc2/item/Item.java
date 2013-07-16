//An item is made of several components and pools their stats into its stat map.

package org.gmcalc2.item;

public class Item {
	
	private Component[] prefixes;	//The prefixes.
	private Component[] materials;	//The materials.
	private ItemBase itemBase;		//The item base.
	private StatMap statMap;		//The stats.
	private String name;			//The name. Is a combination of the prefixes, materials, and item base names.
	private int rarity;				//The rarity. This is the sum of the rarities of the prefixes, materials, and item base.
	
	//Constructor.
	public Item(Component[] prefixes, Component[] materials, ItemBase itemBase) {
		this.prefixes = prefixes;
		this.materials = materials;
		this.itemBase = itemBase;
		statMap = new StatMap();
		recalculateStats();
		recalculateName();
	}
	
	//Recalculate the stats and rarity of this item.
	public void recalculateStats() {
		statMap.clear(); //Clear the stat map before proceeding.
		rarity = itemBase.getRarity(); //The rarity is initially the item base's rarity.
		statMap.mergeMap(itemBase.getStatMap()); //Merge the item base into the stats.
		
		//Add the materials into the stats. This is done before the prefixes so that the prefixes don't merge in any changes that allow materials to modify more than they should.
		//At the same time, add the materials' rarities to the rarity.
		for (int i = 0; i < materials.length; i++) {
			statMap.addMap(materials[i].getStatMap());
			rarity += materials[i].getRarity();
		}
		
		//Merge the prefixes into the stats.
		//At the same time, add the prefixes' rarities to the rarity.
		for (int i = 0; i < prefixes.length; i++) {
			statMap.mergeMap(prefixes[i].getStatMap());
			rarity += prefixes[i].getRarity();
		}
	}
	
	//Recalculate the name.
	public void recalculateName() {
		StringBuilder nameBuilder = new StringBuilder();
		
		//Add the prefixes.
		for (int i = 0; i < prefixes.length; i++) {
			nameBuilder.append(prefixes[i].getName());
			nameBuilder.append(' ');
		}
		
		//Add the item base.
		nameBuilder.append(itemBase.getName());
		
		//Add the materials.
		if (materials.length > 0) {
			nameBuilder.append(" (");
			for (int i = 0; i < materials.length - 2; i++) {
				nameBuilder.append(materials[i].getName());
				nameBuilder.append(", ");
			}
			if (materials.length > 1) {
				nameBuilder.append(materials[materials.length - 2].getName());
				nameBuilder.append(" and ");
			}
			if (materials.length > 0) {
				nameBuilder.append(materials[materials.length - 1].getName());
			}
			nameBuilder.append(')');
		}
		
		//Assign the name.
		name = nameBuilder.toString();
	}

	//Accessors.
	public StatMap getStatMap() {
		return statMap;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRarity() {
		return rarity;
	}

}
