//A component of an item.

package org.gmcalc2.item;

import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;

public class Component {
	
	public static final String NAME_KEY = "name";
	public static final String STATMAP_KEY = "stats";
	public static final String RARITY_KEY = "rarity";
	public static final String TAGS_KEY = "tags";

	private String name;
	private StatMap statMap;
	private int rarity;
	private TreeSet<String> tags;
	
	//Constructors.
	public Component(String name, StatMap statMap, int rarity, TreeSet<String> tags) {
		this.name = name;
		this.statMap = statMap;
		this.rarity = rarity;
		this.tags = tags;
	}
	
	public Component() {
		this("Untitled Item", new StatMap(), 0, new TreeSet<String>());
	}
	
	@SuppressWarnings("unchecked")
	public Component(TreeMap<String, Object> values) {
		this();
		Object val;
		//Get the name.
		val = values.get(NAME_KEY);
		if (val instanceof String)
			name = (String)val;
		//Get the stats.
		val = values.get(STATMAP_KEY);
		if (val instanceof Map<?, ?>) 
			statMap = new StatMap((Map<Object, Object>)val);
		//Get the rarity.
		val = values.get(RARITY_KEY);
		if (val instanceof Integer)
			rarity = (Integer)val;
		//Get the tags.
		val = values.get(TAGS_KEY);
		if (val instanceof Object[]) {
			Object[] rawTags = (Object[])val;
			for (int i = 0; i < rawTags.length; i++) {
				if (rawTags[i] instanceof String) {
					tags.add((String)rawTags[i]);
				}
			}
		}
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
