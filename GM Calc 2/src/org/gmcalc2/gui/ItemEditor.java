package org.gmcalc2.gui;

import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.TagRequirement;
import org.haferlib.slick.gui.ArrayEditor;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListEditor;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class ItemEditor extends GUISubcontext {
	
	private final ItemDisplay itemDisplay;
	private final Item item;
	private final ArrayEditor itemBaseEditor;
	private ArrayEditor materialEditor;
	private ListEditor prefixEditor;
	private final Font font;
	private final Color textColor, fieldColor, backgroundColor, searchColor;

	public ItemEditor(int x, int y, int width, int height, int depth, ItemDisplay itemDisplay,
			Font font, Color textColor, Color fieldColor, Color backgroundColor, Color searchColor) {
		super(x, y, width, height, depth);
		
		// Assign fields.
		this.itemDisplay = itemDisplay;
		this.item = itemDisplay.getItem();
		this.font = font;
		this.textColor = textColor;
		this.fieldColor = fieldColor;
		this.backgroundColor = backgroundColor;
		this.searchColor = searchColor;
		
		// Create the item base editor.
		ItemBase itemBase = item.getItemBase();
		
		String[] fieldContents = new String[] { itemBase.getFilePath() };
		String[] fieldMessages = new String[] { "Type an item base filepath here." };
		
		int numItemBases = item.getWorld().getNumItemBases();
		String[][] fieldSearchStrings = new String[][] {
				item.getWorld().getItemBaseMap().keySet().toArray(new String[numItemBases])
			};
		
		int editorX = x1;
		int editorY = y1;
		int editorWidth = width;
		int editorHeight = font.getLineHeight() * 2;
		itemBaseEditor = new ArrayEditor(editorX, editorY, editorWidth, editorHeight, 0, 0,
				"Item Base", font, textColor, fieldColor, null, searchColor,
				1, fieldContents, fieldMessages, fieldSearchStrings);
		
		subcontext.addElement(itemBaseEditor);
		
		createMaterialEditor();
		createPrefixEditor();
		
		subcontext.addAndRemoveElements();
	}
	
	private void refreshMaterialEditor() {
		destroyMaterialEditor();
		createMaterialEditor();
	}
	
	private void refreshPrefixEditor() {
		destroyPrefixEditor();
		createPrefixEditor();
	}
	
	private void destroyMaterialEditor() {
		subcontext.removeElement(materialEditor);
		materialEditor.destroy();
	}
	
	private void destroyPrefixEditor() {
		subcontext.removeElement(prefixEditor);
		prefixEditor.destroy();
	}
	
	private void createMaterialEditor() {
		Component[] materials = item.getMaterials();
		if (materials.length <= 0)
			return;
		int editorX = x1;
		int editorY = itemBaseEditor.getY() + itemBaseEditor.getHeight();
		int editorWidth = width;
		int editorYSpacing = 2;
		int editorHeight = font.getLineHeight() * (1 + materials.length) + editorYSpacing * (materials.length - 1);
		
		String[] fieldContents = new String[materials.length];
		String[] fieldMessages = new String[materials.length];
		String[][] fieldSearchStrings = new String[materials.length][0];
		
		for (int i = 0; i < materials.length; i++) {
			// Assign the easy stuff.
			fieldContents[i] = materials[i].getFilePath();
			fieldMessages[i] = "Type a material filepath here.";
			
			// Make the search strings.
			TagRequirement requirement = item.getItemBase().getMaterialReqs()[i];
			Component[] possibleMaterials = item.getWorld().getMaterialsMatching(requirement);
			fieldSearchStrings[i] = new String[possibleMaterials.length];
			for (int q = 0; q < fieldSearchStrings[i].length; q++) {
				fieldSearchStrings[i][q] = possibleMaterials[q].getFilePath();
			}
		}
		
		materialEditor = new ArrayEditor(editorX, editorY, editorWidth, editorHeight, 0, editorYSpacing,
				"Materials", font, textColor, fieldColor, null, searchColor,
				materials.length, fieldContents, fieldMessages, fieldSearchStrings);
		
		subcontext.addElement(materialEditor);
	}
	
	private void createPrefixEditor() {
		Component[] prefixes = item.getPrefixes();
		int editorX = x1;
		int editorY;
		if (materialEditor == null)
			editorY = itemBaseEditor.getY() + itemBaseEditor.getHeight();
		else
			editorY = materialEditor.getY() + materialEditor.getHeight();
		int editorWidth = width;
		int editorYSpacing = 2;
		int editorHeight = y2 - editorY;
		
		String fieldMessage = "Type a prefix filepath here.";
		
		TagRequirement requirement = item.getItemBase().getPrefixReqs();
		Component[] possiblePrefixes = item.getWorld().getPrefixesMatching(requirement);
		String[] fieldSearchStrings = new String[possiblePrefixes.length];
		for (int i = 0; i < fieldSearchStrings.length; i++) {
			fieldSearchStrings[i] = possiblePrefixes[i].getFilePath();
		}
		
		prefixEditor = new ListEditor(editorX, editorY, editorWidth, editorHeight, 0, editorYSpacing,
				"Prefixes", font, textColor, fieldColor, null, searchColor);
		prefixEditor.setDefaultFieldMessage(fieldMessage);
		prefixEditor.setDefaultFieldSearchStrings(fieldSearchStrings);
		for (int i = 0; i < prefixes.length; i++) {
			prefixEditor.addField(prefixes[i].getFilePath(), fieldMessage, fieldSearchStrings);
		}
		
		subcontext.addElement(prefixEditor);
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
		
		// Draw the border.
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}

}
