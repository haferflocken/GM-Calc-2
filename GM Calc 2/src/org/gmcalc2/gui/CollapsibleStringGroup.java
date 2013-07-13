package org.gmcalc2.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.slick.GraphicsUtils;
import org.gmcalc2.item.Player;

public class CollapsibleStringGroup implements GUIElement {
	
	private ScrollableListFrame container;
	private String title;
	private String[] strings;
	private Color textColor;
	private Font font;
	private int x1, y1, x2, y2;
	private int width, expandedHeight, collapsedHeight;
	private int toggleButtonX1, toggleButtonY1, toggleButtonX2, toggleButtonY2;
	private int toggleButtonPos, toggleButtonSize;
	private boolean expanded;
	private Image displayImage;
	private Image expandedImage;
	private Image collapsedImage;
	
	//Constructors.
	public CollapsibleStringGroup(ScrollableListFrame container, String title, String[] strings, Color textColor, int x, int y, int width, Font font, boolean expanded) {
		this.container = container;
		this.title = title;
		this.strings = strings;
		this.textColor = textColor;
		this.font = font;
		setWidth(width);
		try {
			redraw();
		}
		catch (SlickException e) {
		}
		setExpanded(expanded);
		setX(x);
		setY(y);
	}
	
	public CollapsibleStringGroup(ScrollableListFrame container, Player.QuantityItem item, Color textColor, int x, int y, int width, Font font, boolean expanded) {
		this(container, item.getItem().getName() + ((item.getAmount() > 1)? " x" + item.getAmount() : ""), item.getItem().getStatMap().toDisplayStrings(), textColor, x, y, width, font, expanded);
	}
	
	@Override
	public void update(int delta) {
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(displayImage, x1, y1);
	}
	
	//Draw the predrawn images.
	public void redraw() throws SlickException {
		//Make a GraphicsUtil to help out.
		GraphicsUtils gUtil = new GraphicsUtils();
		
		//Figure out the height.
		int titleX = font.getLineHeight();
		int titleWidth = width - titleX;
		int titleHeight = gUtil.getWrappedStringHeight(font, title, titleWidth);
		int stringsX = titleX + 8;
		int stringsWidth = width - stringsX;
		int stringsHeight = 0;
		for (String s : strings) {
			stringsHeight += gUtil.getWrappedStringHeight(font, s, stringsWidth);
		}
		expandedHeight = titleHeight + stringsHeight;
		collapsedHeight = titleHeight;
		
		//Create the offscreen images to draw to.
		expandedImage = Image.createOffscreenImage(width, expandedHeight);
		collapsedImage = Image.createOffscreenImage(width, collapsedHeight);
		Graphics expandedG = expandedImage.getGraphics();
		Graphics collapsedG = collapsedImage.getGraphics();
		
		//Give the images a transparent background.
		Color transparency = new Color(0, 0, 0, 0);
		expandedG.setDrawMode(Graphics.MODE_ALPHA_MAP);
		expandedG.setColor(transparency);
		expandedG.fillRect(0, 0, width, expandedHeight);
		expandedG.setDrawMode(Graphics.MODE_NORMAL);
		collapsedG.setDrawMode(Graphics.MODE_ALPHA_MAP);
		collapsedG.setColor(transparency);
		collapsedG.fillRect(0, 0, width, collapsedHeight);
		collapsedG.setDrawMode(Graphics.MODE_NORMAL);
		
		//Figure out the size of the toggle button and draw it.
		toggleButtonSize = font.getLineHeight() * 2 / 3;
		int toggleButtonCenter = font.getLineHeight() / 2;
		toggleButtonPos = font.getLineHeight() / 6;
		expandedG.setColor(textColor);
		expandedG.setFont(font);
		expandedG.drawRect(toggleButtonPos, toggleButtonPos, toggleButtonSize, toggleButtonSize); //Button outline.
		expandedG.fillRect(toggleButtonPos, toggleButtonCenter - 1, toggleButtonSize, 2); //Horizontal bar.
		collapsedG.setColor(textColor);
		collapsedG.setFont(font);
		collapsedG.drawRect(toggleButtonPos, toggleButtonPos, toggleButtonSize, toggleButtonSize); //Button outline.
		collapsedG.fillRect(toggleButtonPos, toggleButtonCenter - 1, toggleButtonSize, 2); //Horizontal bar.
		collapsedG.fillRect(toggleButtonCenter - 1, toggleButtonPos, 2, toggleButtonSize); //Vertical bar.
		
		//Draw the title on both.
		int stringsY = gUtil.drawStringWrapped(expandedG, title, titleX, 0, titleWidth);
		gUtil.drawStringWrapped(collapsedG, title, titleX, 0, titleWidth);
		
		//Draw the strings on the expanded one.
		for (String s : strings)
			stringsY = gUtil.drawStringWrapped(expandedG, s, stringsX, stringsY, stringsWidth);
		
		//Flush the graphics to the image and destroy the graphics context.
		expandedG.flush();
		expandedG.destroy();
		collapsedG.flush();
		collapsedG.destroy();
	}
	
	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x1 + width;
		toggleButtonX1 = x1 + toggleButtonPos;
		toggleButtonX2 = toggleButtonX1 + toggleButtonSize;
	}
	
	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		if (expanded)
			y2 = y1 + expandedHeight;
		else
			y2 = y1 + collapsedHeight;
		toggleButtonY1 = y1 + toggleButtonPos;
		toggleButtonY2 = toggleButtonY1 + toggleButtonSize;
	}

	@Override
	public int getY() {
		return y1;
	}
	
	@Override
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setHeight(int h) {
	}

	@Override
	public int getHeight() {
		if (expanded)
			return expandedHeight;
		return collapsedHeight;
	}
	
	public void setExpanded(boolean e) {
		expanded = e;
		if (expanded) {
			y2 = y1 + expandedHeight;
			displayImage = expandedImage;
		}
		else {
			y2 = y1 + collapsedHeight;
			displayImage = collapsedImage;
		}
		container.realignFromElement(this);
	}

	@Override
	public void click(int x, int y, int button) {
		//Collapse or expand if we click the button.
		if (x >= toggleButtonX1 && y >= toggleButtonY1 && x <= toggleButtonX2 && y <= toggleButtonY2)
			setExpanded(!expanded);
	}
	
	@Override
	public void mouseDown(int x, int y, int button) {
		//TODO: Drag this around.
	}
	
	@Override
	public void hover(int x, int y) {
	}

	@Override
	public void clickedElsewhere(int button) {
	}
	
	@Override
	public void mouseDownElsewhere(int button) {
	}
	
	@Override
	public void hoveredElsewhere() {
	}
	
	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public void keyPressed(int key, char c) {
	}
	
	@Override
	public void keyInputDone() {
	}
	
	@Override
	public boolean dead() {
		return false;
	}
}
