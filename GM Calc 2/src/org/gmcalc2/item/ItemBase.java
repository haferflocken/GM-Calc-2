package org.gmcalc2.item;

public class ItemBase extends Component {
	
	private TagRequirement prefixTags;		//The tag requirements for all prefixes.
	private TagRequirement[] materialTags;	//The tag requirements for each material.
	private Component[] defaultMaterials;	//The default materials, if none are specified.
	
	//Constructor.
	public ItemBase() {
		super();
	}
}
