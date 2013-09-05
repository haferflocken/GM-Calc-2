package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.SearchField;
import org.haferlib.slick.gui.TextDisplay;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class ItemPrefixEditor extends GUISubcontext {

	private Font font;
	private Color textColor, backgroundColor, fieldColor, borderColor, searchColor;
	private Item item;
	private World world;
	private TextDisplay titleDisplay;
	private String[] searchStrings;
	private ListFrame fieldFrame;
	private SearchField[] searchFields;
	
	// Constructor.
	public ItemPrefixEditor(int x, int y, int width, int height, int depth,
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
		makeFieldFrame();
		
		subcontext.addAndRemoveElements();
	}
	
	// Rethink the search strings and update them in the fields.
	public void rethinkSearchStrings() {
		Component[] validPrefixes = world.getPrefixesMatching(item.getItemBase().getPrefixReqs());
		
		searchStrings = new String[validPrefixes.length];
		for (int i = 0; i < searchStrings.length; i++) {
			searchStrings[i] = validPrefixes[i].getName();
		}
		
		if (searchFields != null) {
			for (SearchField field : searchFields) {
				field.setSearchStrings(searchStrings);
			}
		}
	}
	
	// Make the title display.
	public void makeTitleDisplay() {
		subcontext.removeElement(titleDisplay);
		titleDisplay = new TextDisplay(x1, y1, width, font.getLineHeight(), 0,
				"Edit Item: Prefixes", font, textColor);
		subcontext.addElement(titleDisplay);
	}
	
	// Make the field frame and put in fields for the item's current prefixes.
	public void makeFieldFrame() {
		// Remove the old field frame.
		if (fieldFrame != null) {
			subcontext.removeElement(fieldFrame);
			fieldFrame.destroy();
		}
		
		// Make the field frame.
		int fFX = x1;
		int fFY = y1 + titleDisplay.getHeight();
		int fFXOffset = 0;
		int fFWidth = width - fFXOffset;
		int fFYSpacing = 2;
		fieldFrame = new ListFrame(fFX, fFY, fFWidth, 0, ListFrame.XALIGN_LEFT, fFXOffset, fFYSpacing);
		subcontext.addElement(fieldFrame);
		
		// Add fields to it for the current prefixes.
		Component[] prefixes = item.getPrefixes();
		searchFields = new SearchField[prefixes.length];
		for (int i = 0; i < prefixes.length; i++) {
			searchFields[i] = new SearchField(0, 0, fieldFrame.getWidth(), font.getLineHeight(), 0,
					prefixes[i].getName(), "Type here to search.", searchStrings, font,
					textColor, textColor, searchColor, fieldColor);
		}
		fieldFrame.addElements(searchFields);
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
