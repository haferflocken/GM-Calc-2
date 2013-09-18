package org.gmcalc2.item;

import java.util.Map;

import org.haferlib.util.expression.ExpressionBuilder;

public class ItemBase extends Component {
	
	public static final String PREFIXREQS_KEY = "prefixRequirements";
	public static final String MATERIALREQS_KEY = "materialRequirements";
	public static final String DEFAULTMATERIALS_KEY = "defaultMaterials";
	
	private TagRequirement prefixReqs;		// The tag requirements for all prefixes.
	private TagRequirement[] materialReqs;	// The tag requirements for each material.
	private String[] defaultMaterials;		// The default materials that Item uses if no materials are passed to item.
	
	// Constructors.
	public ItemBase(String filePath, Map<String, Object> values, ExpressionBuilder expBuilder) {
		super(filePath, values, expBuilder);
		
		Object val;
		// Get the prefix tag requirements.
		val = values.get(PREFIXREQS_KEY);
		if (val instanceof Object[]) {
			String[] rawPrefixReqs = getStringsFromArray((Object[])val);
			prefixReqs = new TagRequirement(rawPrefixReqs);
		}
		else {
			prefixReqs = new TagRequirement();
		}
		// Get the material tag requirements.
		val = values.get(MATERIALREQS_KEY);
		if (val instanceof Object[]) {
			Object[] allMaterialReqs = (Object[])val;
			materialReqs = new TagRequirement[allMaterialReqs.length];
			for (int i = 0; i < allMaterialReqs.length; i++) {
				if (allMaterialReqs[i] instanceof Object[]) {
					String[] rawMatReqs = getStringsFromArray((Object[])allMaterialReqs[i]);
					materialReqs[i] = new TagRequirement(rawMatReqs);
				}
			}
		}
		// Get the default materials.
		val = values.get(DEFAULTMATERIALS_KEY);
		if (val instanceof Object[])
			defaultMaterials = getStringsFromArray((Object[])val);
	}
	
	// A helper method for loading. TODO: Put this somewhere more appropriate.
	protected String[] getStringsFromArray(Object[] array) {
		// Count the number of strings.
		int numStrings = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] instanceof String) {
				numStrings++;
			}
		}
		
		// Make an output array, fill it up, and return it.
		String[] out = new String[numStrings];
		for (int q = 0, i = 0; i < array.length; i++) {
			if (array[i] instanceof String) {
				out[q] = (String)array[i];
				q++;
			}
		}
		return out;
	}

	// Accessors.
	public TagRequirement getPrefixReqs() {
		return prefixReqs;
	}
	
	public TagRequirement[] getMaterialReqs() {
		return materialReqs;
	}
	
	public String[] getDefaultMaterials() {
		return defaultMaterials;
	}
}
