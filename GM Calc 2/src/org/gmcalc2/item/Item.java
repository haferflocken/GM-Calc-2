package org.gmcalc2.item;

import java.util.Arrays;

import org.gmcalc2.World;
import org.haferutil.Log;

/**
 * An item contains an ItemBase, an array of Components representing the materials
 * applied to the ItemBase, and an array of Components representing the prefixes
 * applied to the ItemBase. It also accumulates the stats of these into a StatMap
 * and accumulates their rarity and names.
 * 
 * @author John Werner
 */

public class Item {
	
	private final World world;		// The world this item exists in. It's too integral to the other fields to allow it to change.
	private Component[] prefixes;	// The prefixes.
	private Component[] materials;	// The materials.
	private ItemBase itemBase;		// The item base.
	private StatMap statMap;		// The stats.
	private String name;			// The name. Is a combination of the prefixes, materials, and item base names.
	private int rarity;				// The rarity. This is the sum of the rarities of the prefixes, materials, and item base.
	
	// Constructor.
	public Item(World world, Component[] prefixes, Component[] materials, ItemBase itemBase) {
		this.world = world;
		this.itemBase = itemBase;
		setPrefixesFilter(prefixes);
		setMaterialsFilter(materials);
		statMap = new StatMap();
		recalculateStats();
		recalculateName();
	}
	
	// Accessors.
	public World getWorld() {
		return world;
	}
	
	public Component[] getPrefixes() {
		return prefixes;
	}
	
	public Component[] getMaterials() {
		return materials;
	}
	
	public ItemBase getItemBase() {
		return itemBase;
	}
	
	public StatMap getStatMap() {
		return statMap;
	}
		
	public String getName() {
		return name;
	}
		
	public int getRarity() {
		return rarity;
	}
	
	// Recalculate the stats and rarity of this item.
	public void recalculateStats() {
		statMap.clear(); // Clear the stat map before proceeding.
		rarity = itemBase.getRarity(); // The rarity is initially the item base's rarity.
		statMap.mergeMap(itemBase.getStatMap()); // Merge the item base into the stats.
		
		// Add the materials into the stats. This is done before the prefixes so that the prefixes don't merge in any changes that allow materials to modify more than they should.
		// At the same time, add the materials' rarities to the rarity.
		for (int i = 0; i < materials.length; i++) {
			statMap.addMap(materials[i].getStatMap());
			rarity += materials[i].getRarity();
		}
		
		// Merge the prefixes into the stats.
		// At the same time, add the prefixes' rarities to the rarity.
		for (int i = 0; i < prefixes.length; i++) {
			statMap.mergeMap(prefixes[i].getStatMap());
			rarity += prefixes[i].getRarity();
		}
	}
	
	// Recalculate the name.
	public void recalculateName() {
		StringBuilder nameBuilder = new StringBuilder();
		
		// Add the prefixes.
		for (int i = 0; i < prefixes.length; i++) {
			nameBuilder.append(prefixes[i].getName());
			nameBuilder.append(' ');
		}
		
		// Add the item base.
		nameBuilder.append(itemBase.getName());
		
		// Add the materials.
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
		
		// Assign the name.
		name = nameBuilder.toString();
	}
	
	// Set the prefixes to the valid prefixes from a given array.
	private void setPrefixesFilter(Component[] newPrefixes) {
		// Filter out the invalid prefixes.
		int numValid = 0;
		boolean[] validity = new boolean[newPrefixes.length];
		for (int i = 0; i < newPrefixes.length; i++) {
			validity[i] = itemBase.getPrefixReqs().passes(newPrefixes[i]);
			if (validity[i])
				numValid++;
		}
				
		// Assign our prefixes to the valid ones.
		prefixes = new Component[numValid];
		for (int j = 0, i = 0; i < newPrefixes.length; i++) {
			if (validity[i]) {
				prefixes[j] = newPrefixes[i];
				j++;
			}
		}
	}
	
	// Set the prefixes to something.
	public void setPrefixes(Component[] newPrefixes) {
		Log.getDefaultLog().info("Changing prefixes from "
				+ Arrays.toString(prefixes) + " to " + Arrays.toString(newPrefixes));
		
		setPrefixesFilter(newPrefixes);
		recalculateName();
		recalculateStats();
	}
	
	// Set the materials to the valid materials in a given array,
	// and the invalid ones to default.
	private void setMaterialsFilter(Component[] newMaterials) {
		// Get the default materials.
		String[] defaultMaterials = itemBase.getDefaultMaterials();

		// Figure out how many times we'll have to loop.
		int numChecks = (defaultMaterials.length < newMaterials.length) ?
				defaultMaterials.length : newMaterials.length;

		// Assign the valid materials from newMaterials and use the defaults otherwise.
		materials = new Component[defaultMaterials.length];
		for (int i = 0; i < numChecks; i++) {
			boolean valid = itemBase.getMaterialReqs()[i].passes(newMaterials[i]);
			if (valid)
				materials[i] = newMaterials[i];
			else
				materials[i] = world.getMaterial(defaultMaterials[i]);
		}

		// If numChecks is less than materials.length, fill in the remainder
		// of materials with the defaults.
		for (int i = numChecks; i < materials.length; i++) {
			materials[i] = world.getMaterial(defaultMaterials[i]);
		}
	}
	
	// Set the materials to something.
	public void setMaterials(Component[] newMaterials) {
		Log.getDefaultLog().info("Changing materials from "
				+ Arrays.toString(materials) + " to " + Arrays.toString(newMaterials));
		
		setMaterialsFilter(newMaterials);
		
		// Recalculate our name and stats.
		recalculateName();
		recalculateStats();
	}
	
	// Set the item base to something.
	public void setItemBase(ItemBase newItemBase) {
		Log.getDefaultLog().info("Changing item base from "
				+ itemBase.getName() + " to " + newItemBase.getName());
		
		// Assign the item base.
		itemBase = newItemBase;
		
		// Reassign the materials and prefixes to ensure they are valid.
		setMaterialsFilter(materials);
		setPrefixesFilter(prefixes);
		
		// Recalculate the name and stats.
		recalculateName();
		recalculateStats();
	}

}
