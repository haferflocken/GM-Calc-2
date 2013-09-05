package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.TagRequirement;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.ScrollableFrame;
import org.haferlib.slick.gui.SearchField;
import org.haferlib.slick.gui.TextDisplay;
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
		
		// Get the prefixes and their requirements.
		Component[] prefixes = item.getPrefixes();
		String prefixReqString = itemBase.getPrefixReqs().toString();
		
		// Get the prefix search strings.
		Component[] compPrefixSearch = world.getPrefixesMatching(itemBase.getPrefixReqs());
		String[] prefixSearch = new String[compPrefixSearch.length];
		for (int i = 0; i < compPrefixSearch.length; i++) {
			prefixSearch[i] = compPrefixSearch[i].getName();
		}

		// Create the prefix editor.
		TextDisplay prefixEditorTitle = new TextDisplay(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
				"Prefixes", font, textColor);
		SearchField[] prefixEditorFields = new SearchField[prefixes.length];
		for (int i = 0; i < prefixes.length; i++) {
			prefixEditorFields[i] = new SearchField(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
					prefixes[i].getName(), prefixReqString, prefixSearch, font,
					textColor, fieldMessageColor, Color.red, fieldColor);
		}
		
		// Get the materials and their requirements.
		Component[] materials = item.getMaterials();
		TagRequirement[] materialReqs = itemBase.getMaterialReqs();
		
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
		TextDisplay materialEditorTitle = new TextDisplay(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
				"Materials", font, textColor);
		SearchField[] materialEditorFields = new SearchField[materials.length];
		for (int i = 0; i < materials.length; i++) {
			materialEditorFields[i] = new SearchField(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
					materials[i].getName(), materialReqs[i].toString(), materialSearch[i], font,
					textColor, fieldMessageColor, Color.red, fieldColor);
		}
		
		/*TextFieldGroup materialEditor = new TextFieldGroup(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Materials", materialNames, materialRequirements, materialSearch, font, textColor, null, fieldMessageColor, fieldColor);
		scrollListFrame.addElement(materialEditor);*/
		
		// Make the item base search strings.
		String[] itemBaseSearchStrings = new String[world.getItemBaseMap().size()];
		int i = 0;
		for (Map.Entry<String, ItemBase> entry : world.getItemBaseMap().entrySet()) {
			itemBaseSearchStrings[i] = entry.getValue().getName();
			i++;
		}
		
		// Make the item base editor.
		TextDisplay itemBaseEditorTitle = new TextDisplay(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
				"Item Base", font, textColor);
		SearchField itemBaseEditorField = new SearchField(0, 0, scrollListFrame.getWidth(), font.getLineHeight(), 0,
				itemBase.getName(), null, itemBaseSearchStrings, font,
				textColor, fieldMessageColor, Color.red, fieldColor);
		
		/*TextFieldGroup itemBaseEditor = new TextFieldGroup(0, 0, scrollListFrame.getWidth(), Integer.MAX_VALUE, 0,
				"Item Base", itemBaseName, itemBaseBackground, itemBaseSearch, font, textColor, null, fieldMessageColor, fieldColor);
		scrollListFrame.addElement(itemBaseEditor);*/
		
		// Add the elements to the scroll list frame.
		scrollListFrame.addElement(itemBaseEditorTitle);
		scrollListFrame.addElement(itemBaseEditorField);
		
		scrollListFrame.addElement(materialEditorTitle);
		scrollListFrame.addElements(materialEditorFields);
		
		scrollListFrame.addElement(prefixEditorTitle);
		scrollListFrame.addElements(prefixEditorFields);
		
		// Update the subcontext.
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
