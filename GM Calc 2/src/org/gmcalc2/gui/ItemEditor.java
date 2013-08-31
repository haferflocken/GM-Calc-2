package org.gmcalc2.gui;

import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.ScrollableFrame;
import org.haferlib.slick.gui.TextField;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * An editor that visually edits an item.
 * 
 * @author John Werner
 *
 */

public class ItemEditor extends GUISubcontext {
	
	private static final String TITLE = "Edit item";
	
	private Item item;
	private ScrollableFrame scrollFrame;
	private ListFrame scrollListFrame;
	private Font font;
	private Color textColor, backgroundColor, fieldColor;
	
	// Constructor.
	public ItemEditor(int x, int y, int width, int height, int depth,
			Item item, Font font, Color textColor, Color backgroundColor, Color fieldColor) {
		super(x, y, width, height, depth);
		
		this.item = item;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldColor = fieldColor;
		
		makeScrollableList();
		
		Component[] prefixes = item.getPrefixes();
		String[] prefixNames = new String[prefixes.length];
		for (int i = 0; i < prefixes.length; i++)
			prefixNames[i] = prefixes[i].getName();
		ListEditor prefixEditor = new ListEditor(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Prefixes", prefixNames, font, textColor, null, fieldColor);
		scrollListFrame.addElement(prefixEditor);
		
		Component[] materials = item.getMaterials();
		String[] materialNames = new String[materials.length];
		for (int i = 0; i < materials.length; i++)
			materialNames[i] = materials[i].getName();
		ListEditor materialEditor = new ListEditor(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Materials", materialNames, font, textColor, null, fieldColor);
		scrollListFrame.addElement(materialEditor);
		
		int iBEHeight = font.getLineHeight();
		TextField itemBaseEditor = new TextField(0, 0, scrollListFrame.getWidth(), iBEHeight, 0,
				item.getItemBase().getName(), null, font, textColor, null, fieldColor);
		scrollListFrame.addElement(itemBaseEditor);
		
		subcontext.addAndRemoveElements();
	}
	
	// Make the scroll frame and the list frame within it.
	private void makeScrollableList() {
		int sFX = x1;
		int sFY = y1 + font.getLineHeight();
		int sFW = width;
		int sFH = height - font.getLineHeight();
		int sFSBW = 10;
		scrollFrame = new ScrollableFrame(sFX, sFY, sFW, sFH, 0, sFSBW, textColor);
		subcontext.addElement(scrollFrame);
		
		int sLFXOffset = 2;
		int sLFX = sFX + sLFXOffset;
		int sLFW = sFW - sFSBW - sLFXOffset;
		int sLFSpacing = 2;
		scrollListFrame = new ListFrame(sLFX, sFY, sLFW, 0, ListFrame.XALIGN_LEFT, 0, sLFSpacing);
		
		scrollFrame.addElement(scrollListFrame);
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
		
		// Draw the title.
		g.setColor(textColor);
		g.drawString(TITLE, x1, y1);
	}

}
