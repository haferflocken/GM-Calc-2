package org.gmcalc2.gui;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.HorizontalListFrame;
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
				"|X|", textColor, font, TextButton.CENTER, 0);
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
		int fFXOffset = 0;
		int fFYSpacing = 2;
		fieldFrame = new ScrollableListFrame(fFX, fFY, fFWidth, fFHeight, 0,
				fFScrollBarWidth, textColor, ListFrame.XALIGN_LEFT, fFXOffset, fFYSpacing);
		subcontext.addElement(fieldFrame);
			
		// Add fields to it for the current prefixes.
		// They are within horizontal frames so they can have bullet points.
		int bulletSize = font.getLineHeight();
		int fieldWidth = fieldFrame.getWidth() - fieldFrame.getScrollBarWidth() - fieldFrame.getXAlignOffset() - bulletSize;
		int fieldHeight = font.getLineHeight();
		Component[] fieldComponents = getFieldComponents();
		HorizontalListFrame[] fieldBars = new HorizontalListFrame[fieldComponents.length];
		searchFields = new SearchField[fieldComponents.length];
		for (int i = 0; i < fieldComponents.length; i++) {
			searchFields[i] = new SearchField(0, 0, fieldWidth, fieldHeight, 0,
					fieldComponents[i].getFilePath(), "Type here to search.", getSearchStrings(i), font,
					textColor, textColor, searchColor, fieldColor);
			
			fieldBars[i] = new HorizontalListFrame(0, 0, fieldHeight, 0);
			fieldBars[i].addElement(makeBullet(bulletSize, fieldBars[i]));
			fieldBars[i].addElement(searchFields[i]);
		}
		fieldFrame.addElements(fieldBars);
	}
	
	protected GUIElement makeBullet(int bulletSize, GUIElement fieldBar) {
		return new TextDisplay(0, 0, bulletSize, bulletSize, 0,
				"o", font, textColor, TextDisplay.WIDTH_STATIC_HEIGHT_STATIC, TextDisplay.TEXT_ALIGN_CENTER);
	}
	
	// Modify the item, and refresh the item display.
	private void updateItem() {
		
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
