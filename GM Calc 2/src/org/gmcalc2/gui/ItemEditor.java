package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.TagRequirement;
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
	private Color textColor, backgroundColor, fieldMessageColor, fieldColor;
	
	// Constructor.
	public ItemEditor(int x, int y, int width, int height, int depth,
			World world, Item item, Font font, Color textColor, Color backgroundColor, Color fieldMessageColor, Color fieldColor) {
		super(x, y, width, height, depth);
		
		this.item = item;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldMessageColor = fieldMessageColor;
		this.fieldColor = fieldColor;
		
		makeScrollableList();
		
		ItemBase itemBase = item.getItemBase();
		
		// Get the names of the prefixes.
		Component[] prefixes = item.getPrefixes();
		String[] prefixNames = new String[prefixes.length];
		for (int i = 0; i < prefixes.length; i++)
			prefixNames[i] = prefixes[i].getName();
		
		// Get the prefix requirements as the background messages.
		String prefixReq = itemBase.getPrefixReqs().toString();
		String[] prefixRequirements = new String[prefixes.length];
		for (int i = 0; i < prefixes.length; i++)
			prefixRequirements[i] = prefixReq;
		
		// Get the prefix search strings.
		Component[] compPrefixSearch = world.getPrefixesMatching(itemBase.getPrefixReqs());
		String[] basePrefixSearch = new String[compPrefixSearch.length];
		for (int i = 0; i < compPrefixSearch.length; i++) {
			basePrefixSearch[i] = compPrefixSearch[i].getName();
		}
		String[][] prefixSearch = new String[prefixes.length][0];
		for (int i = 0; i < prefixes.length; i++) {
			prefixSearch[i] = basePrefixSearch;
		}

		// Create the prefix editor.
		ListEditor prefixEditor = new ListEditor(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Prefixes", prefixNames, prefixRequirements, prefixSearch, font, textColor, null, fieldMessageColor, fieldColor);
		scrollListFrame.addElement(prefixEditor);
		
		// Get the names of the materials.
		Component[] materials = item.getMaterials();
		String[] materialNames = new String[materials.length];
		for (int i = 0; i < materials.length; i++)
			materialNames[i] = materials[i].getName();
		
		// Get the material requirements as the background messages.
		TagRequirement[] materialReqs = itemBase.getMaterialReqs();
		String[] materialRequirements = new String[materials.length];
		for (int i = 0; i < materials.length; i++)
			materialRequirements[i] = materialReqs[i].toString();
		
		// Get the material search strings.
		String[][] materialSearch = new String[materials.length][0];
		for (int q, i = 0; i < materials.length; i++) {
			Component[] compMatSearch = world.getMaterialsMatching(materialReqs[i]);
			materialSearch[i] = new String[compMatSearch.length];
			for (q = 0; q < compMatSearch.length; q++) {
				materialSearch[i][q] = compMatSearch[q].getName();
			}
		}
		
		// Make the material editor.
		TextFieldGroup materialEditor = new TextFieldGroup(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Materials", materialNames, materialRequirements, materialSearch, font, textColor, null, fieldMessageColor, fieldColor);
		scrollListFrame.addElement(materialEditor);
		
		// Get the item base name and some dummy values to fill the item base editor.
		String[] itemBaseName = new String[] { itemBase.getName() };
		String[] itemBaseBackground = new String[1];
		String[][] itemBaseSearch = new String[1][0];
		
		// Make the item base editor.
		TextFieldGroup itemBaseEditor = new TextFieldGroup(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Item Base", itemBaseName, itemBaseBackground, itemBaseSearch, font, textColor, null, fieldMessageColor, fieldColor);
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
