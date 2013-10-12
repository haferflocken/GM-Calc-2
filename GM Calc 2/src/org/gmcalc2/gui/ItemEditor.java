package org.gmcalc2.gui;

import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.TagRequirement;
import org.haferlib.slick.gui.ArrayEditor;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListEditor;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.slick.gui.TextButton;
import org.haferlib.slick.gui.TextDisplay;
import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ItemEditor extends GUISubcontext implements GUIEventListener {
	
	private static final String TITLE = "Edit Item";
	
	private final ItemDisplay itemDisplay;
	private final Item item;
	private final TextDisplay titleDisplay;
	private final TextButton<?> closeButton;
	private final ScrollableListFrame editorList;
	private final ArrayEditor itemBaseEditor;
	private ArrayEditor materialEditor;
	private ListEditor prefixEditor;
	private final Font font;
	private int titleBorderY;
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
		
		// Create the title display.
		titleDisplay = new TextDisplay(x1, y1, width, font.getLineHeight(), 0, TITLE, font, textColor, TextDisplay.WIDTH_STATIC_HEIGHT_STATIC, TextDisplay.TEXT_ALIGN_CENTER);
		titleBorderY = titleDisplay.getY() + titleDisplay.getHeight();
		subcontext.addElement(titleDisplay);
		
		// Create the close button.
		int closeButtonSize = font.getLineHeight();
		int closeButtonX = x2 - closeButtonSize;
		int closeButtonY = y1;
		closeButton = new TextButton<Object>(null, closeButtonX, closeButtonY, closeButtonSize, closeButtonSize, 0,
				fieldColor, searchColor, Input.KEY_ESCAPE, "[X]", textColor, font);
		closeButton.addListener(this);
		subcontext.addElement(closeButton);
		
		// Create the editor list.
		int editorListX = x1;
		int editorListY = titleBorderY;
		int editorListWidth = width;
		int editorListHeight = y2 - titleBorderY;
		int editorListScrollBarWidth = 10;
		int editorListXOffset = editorListScrollBarWidth;
		editorList = new ScrollableListFrame(editorListX, editorListY, editorListWidth, editorListHeight, 0,
				editorListScrollBarWidth, textColor, ListFrame.XALIGN_CENTER, editorListXOffset, 0);
		subcontext.addElement(editorList);
		
		// Create the item base editor.
		ItemBase itemBase = item.getItemBase();
		
		String[] fieldContents = new String[] { itemBase.getFilePath() };
		String[] fieldMessages = new String[] { "Type an item base filepath here." };
		
		int numItemBases = item.getWorld().getNumItemBases();
		String[][] fieldSearchStrings = new String[][] {
				item.getWorld().getItemBaseMap().keySet().toArray(new String[numItemBases])
			};
		
		int editorWidth = editorList.getWidth() - editorList.getXAlignOffset() - editorList.getScrollBarWidth();
		int editorHeight = font.getLineHeight() * 2;
		itemBaseEditor = new ArrayEditor(0, 0, editorWidth, editorHeight, 0, 0,
				"Item Base", font, textColor, fieldColor, null, searchColor,
				1, fieldContents, fieldMessages, fieldSearchStrings);
		
		editorList.addElement(itemBaseEditor);
		
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
		editorList.removeElement(materialEditor);
		materialEditor.destroy();
	}
	
	private void destroyPrefixEditor() {
		editorList.removeElement(prefixEditor);
		prefixEditor.destroy();
	}
	
	private void createMaterialEditor() {
		Component[] materials = item.getMaterials();
		if (materials.length <= 0)
			return;
		int editorWidth = editorList.getWidth() - editorList.getXAlignOffset() - editorList.getScrollBarWidth();
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
		
		materialEditor = new ArrayEditor(0, 0, editorWidth, editorHeight, 0, editorYSpacing,
				"Materials", font, textColor, fieldColor, null, searchColor,
				materials.length, fieldContents, fieldMessages, fieldSearchStrings);
		
		editorList.addElement(materialEditor);
	}
	
	private void createPrefixEditor() {
		Component[] prefixes = item.getPrefixes();
		int editorWidth = editorList.getWidth() - editorList.getXAlignOffset() - editorList.getScrollBarWidth();
		int editorYSpacing = 2;
		int editorHeight = Integer.MAX_VALUE;
		
		String fieldMessage = "Type a prefix filepath here.";
		
		TagRequirement requirement = item.getItemBase().getPrefixReqs();
		Component[] possiblePrefixes = item.getWorld().getPrefixesMatching(requirement);
		String[] fieldSearchStrings = new String[possiblePrefixes.length];
		for (int i = 0; i < fieldSearchStrings.length; i++) {
			fieldSearchStrings[i] = possiblePrefixes[i].getFilePath();
		}
		
		prefixEditor = new ListEditor(0, 0, editorWidth, editorHeight, 0, editorYSpacing,
				"Prefixes", font, textColor, fieldColor, null, searchColor);
		prefixEditor.setDefaultFieldMessage(fieldMessage);
		prefixEditor.setDefaultFieldSearchStrings(fieldSearchStrings);
		for (int i = 0; i < prefixes.length; i++) {
			prefixEditor.addField(prefixes[i].getFilePath(), fieldMessage, fieldSearchStrings);
		}
		
		editorList.addElement(prefixEditor);
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		titleBorderY = y1 + font.getLineHeight();
	}
	
	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		closeButton.setX(x2 - closeButton.getWidth());
		titleDisplay.setWidth(width);
		editorList.setWidth(width);
		int edWidth = editorList.getWidth() - editorList.getScrollBarWidth() - editorList.getXAlignOffset();
		itemBaseEditor.setWidth(edWidth);
		if (materialEditor != null)
			materialEditor.setWidth(edWidth);
		prefixEditor.setWidth(edWidth);
	}
	
	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		editorList.setHeight(y2 - titleBorderY);
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
		
		// Draw the border.
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);
		g.drawLine(x1, titleBorderY, x2, titleBorderY);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If we receive an event from the close button, set dead to true.
		if (event.getGenerator() == closeButton) {
			dead = true;
		}
	}

}
