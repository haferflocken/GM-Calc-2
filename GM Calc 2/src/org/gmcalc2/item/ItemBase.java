package org.gmcalc2.item;

import java.util.TreeMap;

public class ItemBase extends Component {
	
	private TagRequirement prefixTags;		//The tag requirements for all prefixes.
	private TagRequirement[] materialTags;	//The tag requirements for each material.
	private Component[] defaultMaterials;	//The default materials, if none are specified.
	
	//Constructors.
	public ItemBase() {
		super();
	}
	
	public ItemBase(TreeMap<String, Object> values) {
		super(values);
	}
}
