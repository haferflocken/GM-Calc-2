package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.SearchField;
import org.haferlib.slick.gui.TextDisplay;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class ItemItemBaseEditor extends GUISubcontext {
	
	private Font font;
	private Color textColor, backgroundColor, fieldColor, borderColor, searchColor;
	private Item item;
	private World world;
	private TextDisplay titleDisplay;
	private String[] searchStrings;
	private SearchField searchField;

	// Constructor.
	public ItemItemBaseEditor(int x, int y, int width, int height, int depth,
			Item item, World world, Font font, Color textColor,
			Color backgroundColor, Color fieldColor, Color borderColor, Color searchColor) {
		super(x, y, width, height, depth);
		
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldColor = fieldColor;
		this.borderColor = borderColor;
		this.searchColor = searchColor;
		
		this.item = item;
		this.world = world;
		
		rethinkSearchStrings();
		makeTitleDisplay();
		
		// Add a search field.
		searchField = new SearchField(x1, y1 + titleDisplay.getHeight(), width, font.getLineHeight(), 0,
				item.getItemBase().getName(), "Type here to search.", searchStrings, font,
				textColor, textColor, searchColor, fieldColor);
		subcontext.addElement(searchField);
		
		subcontext.addAndRemoveElements();
	}
	
	// Rethink the search strings and let the search field know.
	public void rethinkSearchStrings() {
		// Make the search strings.
		searchStrings = new String[world.getNumItemBases()];
		int i = 0;
		for (Map.Entry<String, ItemBase> entry : world.getItemBaseMap().entrySet()) {
			searchStrings[i] = entry.getValue().getName();
			i++;
		}
		
		// Set the field to use the search strings.
		if (searchField != null)
			searchField.setSearchStrings(searchStrings);
	}
	
	// Make the title display.
	public void makeTitleDisplay() {
		subcontext.removeElement(titleDisplay);
		titleDisplay = new TextDisplay(x1, y1, width, font.getLineHeight(), 0,
				"Edit Item: Item Base", font, textColor);
		subcontext.addElement(titleDisplay);
	}

	@Override
	public void render(Graphics g) {
		// Draw the backgroound.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
				
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
		
		// Draw the border.
		g.setColor(borderColor);
		g.drawRect(x1, y1, width, height);
	}

}
