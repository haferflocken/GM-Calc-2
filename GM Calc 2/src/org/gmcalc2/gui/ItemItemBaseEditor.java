package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class ItemItemBaseEditor extends AbstractItemComponentEditor {
	
	private static final String TITLE_STRING = "Edit Item: Item Base";
	
	private String[] searchStrings;

	// Constructor.
	public ItemItemBaseEditor(int x, int y, int width, int height, int depth,
			Item item, World world, Font font, Color textColor,
			Color backgroundColor, Color fieldColor, Color borderColor, Color searchColor) {
		super(x, y, width, height, depth, item, world, font,
				textColor, backgroundColor, fieldColor, borderColor, searchColor);
	}
	
	@Override
	protected String getTitleString() {
		return TITLE_STRING;
	}
	
	@Override
	protected void rethinkSearchStrings() {
		// Make the search strings.
		searchStrings = new String[world.getNumItemBases()];
		int i = 0;
		for (Map.Entry<String, ItemBase> entry : world.getItemBaseMap().entrySet()) {
			searchStrings[i] = entry.getValue().getName();
			i++;
		}
	}
	
	@Override
	protected String[] getSearchStrings(int searchFieldIndex) {
		return searchStrings;
	}

	@Override
	protected Component[] getFieldComponents() {
		return new Component[] { item.getItemBase() };
	}

}
