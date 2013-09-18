package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.slick.gui.SearchField;
import org.haferlib.slick.gui.TextButton;
import org.haferlib.slick.gui.TextDisplay;
import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public abstract class AbstractItemComponentEditor extends GUISubcontext implements GUIEventListener {
	
	protected Font font;
	protected Color textColor, backgroundColor, fieldColor, borderColor, searchColor;
	protected Item item;
	protected World world;
	protected ItemDisplay itemDisplay;
	protected PlayerTab playerTab;
	protected TextDisplay titleDisplay;
	protected Button<?> closeButton;
	protected ScrollableListFrame fieldFrame;
	protected SearchField[] searchFields;

	/**
	 * Constructor.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param width The width.
	 * @param height The height.
	 * @param depth The depth to place this in the context.
	 * @param item The item this edits.
	 * @param world The world this is editing in.
	 * @param font The font to display text with.
	 * @param textColor The color to display text with.
	 * @param backgroundColor The background color.
	 * @param fieldColor The background color of the fields.
	 * @param borderColor The color of the border.
	 * @param searchColor The color of the search function of the fields.
	 */
	protected AbstractItemComponentEditor(int x, int y, int width, int height, int depth,
			ItemDisplay itemDisplay, PlayerTab playerTab, Font font, Color textColor,
			Color backgroundColor, Color fieldColor, Color borderColor, Color searchColor) {
		super(x, y, width, height, depth);
		
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldColor = fieldColor;
		this.borderColor = borderColor;
		this.searchColor = searchColor;
		
		item = itemDisplay.getItem();
		world = item.getWorld();
		this.itemDisplay = itemDisplay;
		this.playerTab = playerTab;
		
		rethinkSearchStrings();
		makeTitleBar();
		makeFieldFrame();
		
		subcontext.addAndRemoveElements();
	}
	
	/**
	 * Get the title string.
	 * 
	 * @return The string to be displayed in the title bar.
	 */
	protected abstract String getTitleString();
	
	/**
	 * Called during the constructor. After this is called,
	 * getSearchStrings(int) must return the appropriate values.
	 */
	protected abstract void rethinkSearchStrings();
	
	/**
	 * Get the search strings for a field of the given index.
	 * 
	 * @param searchFieldIndex The index of the field in searchFields.
	 * @return The strings the given fields is to search through.
	 */
	protected abstract String[] getSearchStrings(int searchFieldIndex);
	
	/**
	 * Get the components the fields are supposed to represent.
	 * 
	 * @return The fields the components will have their contents set to.
	 */
	protected abstract Component[] getFieldComponents();
	
	/**
	 * Make the title bar, which is a TextDisplay of the name of this element
	 * and a button to close it, and add it to the subcontext.
	 */
	private void makeTitleBar() {
		int cBWidth = font.getLineHeight();
		int cBHeight = font.getLineHeight();
		int cBX = x2 - cBWidth;
		int cBY = y1;
		
		closeButton = new TextButton<Object>(null, cBX, cBY, cBWidth, cBHeight, 1,
				null, fieldColor, Input.KEY_ENTER,
				"[X]", textColor, font, TextButton.CENTER, 0);
		closeButton.addListener(this);
		subcontext.addElement(closeButton);
		
		int tDX = x1;
		int tDY = y1;
		int tDWidth = width - cBWidth;
		int tDHeight = font.getLineHeight();
		
		titleDisplay = new TextDisplay(tDX, tDY, tDWidth, tDHeight, 0,
				getTitleString(), font, textColor);
		subcontext.addElement(titleDisplay);
	}
	
	/**
	 * Make the field frame and add fields to it.
	 */
	private void makeFieldFrame() {
		// Make the field frame.
		int fFX = x1;
		int fFY = y1 + titleDisplay.getHeight();
		int fFWidth = width;
		int fFHeight = height - titleDisplay.getHeight();
		int fFScrollBarWidth = 10;
		int fFXOffset = fFScrollBarWidth;
		int fFYSpacing = 2;
		fieldFrame = new ScrollableListFrame(fFX, fFY, fFWidth, fFHeight, 0,
				fFScrollBarWidth, textColor, ListFrame.XALIGN_LEFT, fFXOffset, fFYSpacing);
		subcontext.addElement(fieldFrame);
			
		// Add fields to it for the current prefixes.
		// They are within horizontal frames so they can have bullet points.
		int fieldWidth = calcFieldWidth();
		int fieldHeight = font.getLineHeight();
		Component[] fieldComponents = getFieldComponents();
		searchFields = new SearchField[fieldComponents.length];
		for (int i = 0; i < fieldComponents.length; i++) {
			GUIElement field = makeField(fieldWidth, fieldHeight, i, fieldComponents);
			fieldFrame.addElement(field);
		}
	}
	
	protected int calcFieldWidth() {
		return fieldFrame.getWidth() - fieldFrame.getScrollBarWidth() - fieldFrame.getXAlignOffset();
	}
	
	protected GUIElement makeField(int fieldWidth, int fieldHeight, int index, Component[] fieldComponents) {
		searchFields[index] =  new SearchField(0, 0, fieldWidth, fieldHeight, 0,
				fieldComponents[index].getFilePath(), "Type here to search.", getSearchStrings(index), font,
				textColor, textColor, searchColor, fieldColor);
		return searchFields[index];
	}
	
	/**
	 * Make components from the search fields.
	 * 
	 * @return Components made from the contents of the search fields.
	 */
	private Component[] makeComponentsFromSearchFields() {
		// Get the components.
		Component[] out = new Component[searchFields.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = getComponentFromWorld(searchFields[i].toString());
			
			// If a component isn't found, just use the old one.
			if (out[i] == null)
				out[i] = getCurrentComponent(i);
		}
		
		// Return them.
		return out;
	}
	
	/**
	 * An abstract method to let subclasses tell makeComponentsFromSearchFields()
	 * where to look for components.
	 * 
	 * @param path The path to the component.
	 * @return A component corresponding to the given path.
	 */
	protected abstract Component getComponentFromWorld(String path);
	
	/**
	 * An abstract method to let subclasses tell makeComponentsFromSearchFields()
	 * what component is currently in the item at a given index.
	 * 
	 * @param index The index of the component.
	 * @return The component at the index in the item.
	 */
	protected abstract Component getCurrentComponent(int index);
	
	/**
	 * An abstract method to let subclasses assign the result of
	 * makeComponentsFromSearchFields() appropriately.
	 *
	 * @param components The components from the search fields.
	 */
	protected abstract void assignComponents(Component[] components);
	
	/**
	 * Make the item display rethink its appearance.
	 */
	private void refreshItemDisplay() {
		itemDisplay.recalcColor();
		itemDisplay.recalcTitle();
		itemDisplay.recalcStrings();
	}
	
	/**
	 * Update the item with the contents of the search fields.
	 */
	private void updateItem() {
		Component[] newComponents = makeComponentsFromSearchFields();
		assignComponents(newComponents);
		playerTab.getPlayer().recalculateStats();
		playerTab.fillStatColumn();
		refreshItemDisplay();
	}
	
	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
				
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
				
		// Draw the border.
		g.setColor(borderColor);
		g.drawRect(x1, y1, width, height);
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the event is from the close button, update the item and set dead to true.
		if (closeButton.equals(event.getGenerator())) {
			updateItem();
			dead = true;
		}
	}
}
