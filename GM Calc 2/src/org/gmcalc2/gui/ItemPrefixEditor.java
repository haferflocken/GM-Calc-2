package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.TextButton;
import org.haferlib.slick.gui.event.GUIEvent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;

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
	
	@Override
	protected GUIElement makeBullet(int bulletSize, GUIElement field) {
		TextButton<GUIElement> bullet = new TextButton<>(field, 0, 0, bulletSize, bulletSize, 0, null, fieldColor, Input.KEY_ENTER,
				"|X|", textColor, font, TextButton.CENTER, 0);
		bullet.addListener(this);
		return bullet;
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the event has a GUIElement as the data, remove the element from the fieldFrame.
		if (event.getData() instanceof GUIElement) {
			GUIElement e = (GUIElement)event.getData();
			fieldFrame.removeElement(e);
		}
		// Otherwise, pass this along to the super method.
		else
			super.guiEvent(event);
	}
}
