package org.gmcalc2.gui;

import java.util.ArrayList;

import org.gmcalc2.item.Component;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.SearchField;
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
		Component[] validPrefixes = world.getPrefixesMatching(item.getItemBase().getPrefixReqs());
			
		searchStrings = new String[validPrefixes.length];
		for (int i = 0; i < searchStrings.length; i++) {
			searchStrings[i] = validPrefixes[i].getFilePath();
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
	protected GUIElement makeBullet(int bulletSize, GUISubcontext field) {
		TextButton<GUISubcontext> bullet = new TextButton<>(field, 0, 0, bulletSize, bulletSize, 0, null, fieldColor, Input.KEY_ENTER,
				"|X|", textColor, font, TextButton.CENTER, 0);
		bullet.addListener(this);
		return bullet;
	}
	
	@Override
	protected Component getComponentFromWorld(String path) {
		return world.getPrefix(path);
	}
	
	@Override
	protected Component getCurrentComponent(int index) {
		return item.getPrefixes()[index];
	}
	
	@Override
	protected void assignComponents(Component[] components) {
		item.setPrefixes(components);
	}
	
	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the event has a GUISubcontext as the data, remove the subcontext from the fieldFrame
		// and discard its corresponding search field.
		if (event.getData() instanceof GUISubcontext) {
			GUISubcontext e = (GUISubcontext)event.getData();
			fieldFrame.removeElement(e);
			ArrayList<GUIElement> subElements = e.getElements();
			for (GUIElement elem : subElements) {
				if (elem instanceof SearchField) {
					SearchField[] oldFields = searchFields;
					searchFields = new SearchField[oldFields.length - 1];
					for (int j = 0, i = 0; i < oldFields.length; i++) {
						if (oldFields[i] != elem)
							searchFields[j++] = oldFields[i];
					}
					break;
				}
			}
		}
		// Otherwise, pass this along to the super method.
		else
			super.guiEvent(event);
	}
}
