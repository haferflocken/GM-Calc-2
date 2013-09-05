package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class ItemPrefixEditor extends AbstractItemComponentEditor {
	
	private static final String TITLE_STRING = "Edit Item: Prefixes";
	
	private String[] searchStrings;

	// Constructor.
	public ItemPrefixEditor(int x, int y, int width, int height, int depth,
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
		Component[] validPrefixes = world.getPrefixesMatching(item.getItemBase().getPrefixReqs());
			
		searchStrings = new String[validPrefixes.length];
		for (int i = 0; i < searchStrings.length; i++) {
			searchStrings[i] = validPrefixes[i].getName();
		}
	}

	@Override
	protected String[] getSearchStrings(int searchFieldIndex) {
		return searchStrings;
	}

	@Override
	protected Component[] getFieldComponents() {
		return item.getPrefixes();
	}

}
