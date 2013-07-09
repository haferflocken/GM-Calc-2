//A component of an item.

package org.gmcalc2.item;

import java.util.TreeSet;

public class Component {

	private StatMap statMap;
	private int rarity;
	private TreeSet<String> tags;
	
	//Constructor.
	public Component() {
		statMap = new StatMap();
		rarity = 0;
		tags = new TreeSet<>();
	}
}
