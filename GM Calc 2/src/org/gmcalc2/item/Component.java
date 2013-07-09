//A component of an item.

package org.gmcalc2.item;

import java.util.TreeSet;

public class Component {

	private String name;
	private StatMap statMap;
	private int rarity;
	private TreeSet<String> tags;
	
	//Constructor.
	public Component() {
		statMap = new StatMap();
		rarity = 0;
		tags = new TreeSet<>();
	}
	
	//Accessors.
	public String getName() {
		return name;
	}
	
	public StatMap getStatMap() {
		return statMap;
	}
	
	public int getRarity() {
		return rarity;
	}
	
	public TreeSet<String> getTags() {
		return tags;
	}
	
}
