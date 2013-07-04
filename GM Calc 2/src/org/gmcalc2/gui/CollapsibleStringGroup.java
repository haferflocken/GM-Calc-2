package org.gmcalc2.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import org.haferlib.slick.gui.GUIElement;

public class CollapsibleStringGroup implements GUIElement {
	
	private String title;
	private String[] strings;
	private Color textColor;
	private Font font;
	private int x1, y1, x2, y2;
	private int width, height;
	private boolean expanded;
	private Image predrawnImage;
	
	public CollapsibleStringGroup(String title, String[] strings, Color textColor, int x, int y, int width, Font font, boolean expanded) {
		this.title = title;
		this.strings = strings;
		this.textColor = textColor;
		this.font = font;
		this.expanded = expanded;
		setX(x);
		setY(y);
		setWidth(width);
		recalcHeight();
		try {
			redraw();
		}
		catch (SlickException e) {
		}
	}
	
	@Override
	public void update(int delta) {
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(predrawnImage, x1, y1);
	}
	
	//Draw the predrawn image.
	public void redraw() throws SlickException {
		//Create the offscreen image to draw to.
		predrawnImage = Image.createOffscreenImage(width, height);
		Graphics g = predrawnImage.getGraphics();
		
		//Give the image a transparent background.
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, width, height);
		g.setDrawMode(Graphics.MODE_NORMAL);
		
		//Draw the title.
		g.setColor(textColor);
		g.setFont(font);
		g.drawString(title, 0, 0);
		
		//If expanded, draw the strings.
		if (expanded) {
			for (int i = 0; i < strings.length; i++)
				g.drawString(strings[i], 0, font.getLineHeight() + font.getLineHeight() * i);
		}
		
		//Flush the graphics to the image and destroy the graphics context.
		g.flush();
		g.destroy();
	}
	
	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x1 + width;
	}
	
	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		y2 = y1 + height;
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
		height = h;
		y2 = y1 + height;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	public void recalcHeight() {
		height = font.getLineHeight();
		if (expanded)
			height += font.getLineHeight() * strings.length;
	}

	@Override
	public void click(int x, int y, int button) {
		//Collapse or expand if we click the button.
		//TODO
	}
	
	@Override
	public void mouseDown(int x, int y, int button) {
		//Drag this around.
		//TODO
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
