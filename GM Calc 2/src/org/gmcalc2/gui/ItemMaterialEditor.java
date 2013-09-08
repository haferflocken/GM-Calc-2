package org.gmcalc2.gui;

import org.gmcalc2.item.Component;
import org.gmcalc2.item.TagRequirement;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class ItemMaterialEditor extends AbstractItemComponentEditor {
	
	private static final String TITLE_STRING = "Edit Item: Materials";
	
	private String[][] searchStrings;
	
	// Constructor.
	public ItemMaterialEditor(int x, int y, int width, int height, int depth,
			ItemDisplay itemDisplay, PlayerTab playerTab, Font font, Color textColor,
			Color backgroundColor, Color fieldColor, Color borderColor, Color searchColor) {
		super(x, y, width, height, depth, itemDisplay, playerTab, font,
				textColor, backgroundColor, fieldColor, borderColor, searchColor);
	}
	
	@Override
	protected String getTitleString() {
		return TITLE_STRING;
	}
	
	@Override
	protected void rethinkSearchStrings() {
		Component[] materials = item.getMaterials();
		searchStrings = new String[materials.length][0];
		TagRequirement[] materialReqs = item.getItemBase().getMaterialReqs();
		for (int i = 0; i < materials.length; i++) {
			Component[] validMaterials = world.getMaterialsMatching(materialReqs[i]);
			searchStrings[i] = new String[validMaterials.length];
			for (int q = 0; q < validMaterials.length; q++) {
				searchStrings[i][q] = validMaterials[q].getFilePath();
			}
		}
	}

	@Override
	protected String[] getSearchStrings(int searchFieldIndex) {
		return searchStrings[searchFieldIndex];
	}

	@Override
	protected Component[] getFieldComponents() {
		return item.getMaterials();
	}
	
	@Override
	protected Component getComponentFromWorld(String path) {
		return world.getMaterial(path);
	}
	
	@Override
	protected Component getCurrentComponent(int index) {
		return item.getMaterials()[index];
	}
	
	@Override
	protected void assignComponents(Component[] components) {
		item.setMaterials(components);
	}
}
