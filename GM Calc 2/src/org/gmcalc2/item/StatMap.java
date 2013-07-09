//A map of stats.

package org.gmcalc2.item;

import java.util.TreeMap;
import java.util.Map;

public class StatMap {
	
	private TreeMap<String, Stat> stats; //The stats and their names.
	
	//Constructor.
	public StatMap() {
		stats = new TreeMap<>();
	}
	
	//Put a stat in this, overriding the old value if there is one.
	public void put(String key, Stat value) {
		stats.put(key, value);
	}
	
	//Put a stat in this, adding the value to the old value if there is one.
	public void addPut(String key, Stat value) {
		Stat oldValue = stats.get(key);
		if (oldValue == null) {
			stats.put(key, value.copy());
		}
		else {
			oldValue.merge(value);
		}
	}
	
	//Add the like values of a StatMap to this one.
	public void addMap(StatMap other) {
		for (Map.Entry<String, Stat> entry : stats.entrySet()) {
			Stat otherValue = other.stats.get(entry.getKey());
			if (otherValue != null) {
				entry.getValue().merge(otherValue);
			}
		}
	}
	
	//Add all the values of another StatMap to this one.
	public void mergeMap(StatMap other) {
		for (Map.Entry<String, Stat> otherEntry : other.stats.entrySet()) {
			addPut(otherEntry.getKey(), otherEntry.getValue());
		}
	}

}
