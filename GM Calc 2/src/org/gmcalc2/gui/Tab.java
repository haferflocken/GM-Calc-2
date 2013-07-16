//A tab. The x1, y1, x2, y2, width, and height all follow GUIElement conventions, which is basically to say they define the bounding box of the tab including the little bit that sticks out at the top.

package org.gmcalc2.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Polygon;

import org.haferlib.slick.gui.GUISubcontext;

public class Tab extends GUISubcontext {
	
	protected String tabName;
	protected Font font;
	protected Color tabEnabledColor, tabDisabledColor, tabNameColor;
	protected Polygon tabShape;
	protected int tabY2;	//The tab shape. No need for tabY1 because it is the same as y1.
	protected int x2, y2, width, height;
	protected int depth;
	protected boolean dead, enabled;
	
	public Tab(String tabName, int x, int y, int width, int height, int tabX, int tabWidth, Font font, Color tabEnabledColor, Color tabDisabledColor, Color tabNameColor) {
		//Set the parameters.
		this.tabName = tabName;
		this.tabEnabledColor = tabEnabledColor;
		this.tabDisabledColor = tabDisabledColor;
		this.tabNameColor = tabNameColor;
		createTab(tabX, tabWidth, font);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		enable();
	}
	
	@Override
	public void render(Graphics g) {
		//Draw the tab.
		if (enabled)
			g.setColor(tabEnabledColor);
		else
			g.setColor(tabDisabledColor);
		g.fill(tabShape);
		g.setColor(tabNameColor);
		g.setFont(font);
		g.drawString(tabName, tabShape.getX() + tabShape.getWidth() / 2 - (font.getWidth(tabName) / 2), y1);
		
		//Render the interior if enabled.
		if (enabled)
			renderInterior(g);
	}
	
	public void renderInterior(Graphics g) {
		//Draw the background.
		g.setColor(tabEnabledColor);
		g.fillRect(x1, tabY2, width, height);
				
		//Render the subcontext.
		subcontext.render(g, x1, tabY2, x2, y2);
	}
	
	public int getTabX() {
		return (int)tabShape.getX();
	}
	
	public void setTabX(int x) {
		tabShape.setX(x);
	}
	
	public int getTabWidth() {
		return (int)tabShape.getWidth();
	}
	
	public void createTab(int tX, int tW, Font f) {
		font = f;
		tabY2 = y1 + font.getLineHeight();
		tabShape = new Polygon(new float[] {
			tX - 4, tabY2, //Bottom left
			tX + 4, y1, //Top left
			tX + tW - 4, y1, //Top right
			tX + tW + 4, tabY2, //Bottom right
		});
	}
	
	public void setX(int x) {
		int dX = x - x1;
		super.setX(x);
		x2 = x1 + width;
		setTabX((int)tabShape.getX() + dX);
	}
	
	public void setY(int y) {
		super.setY(y);
		y2 = y1 + height;
		tabShape.setY(y);
		tabY2 = y1 + font.getLineHeight();
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
	
	public Font getFont() {
		return font;
	}

	@Override
	public boolean pointIsWithin(int x, int y) {
		if (enabled)
			return (x >= x1 && y >= tabY2 && x <= x2 && y <= y2) | tabShape.contains(x, y);
		return tabShape.contains(x, y);
	}
	
	@Override
	public void click(int x, int y, int button) {
		super.click(x, y, button);
		if (!enabled) {
			if (button == Input.MOUSE_LEFT_BUTTON) {
				enable();
			}
		}
	}
	
	@Override
	public void clickedElsewhere(int button) {
		super.clickedElsewhere(button);
		if (enabled && button == Input.MOUSE_LEFT_BUTTON) {
			disable();
		}
	}
	
	@Override
	public int getDepth() {
		return depth;
	}
	
	public void enable() {
		enabled = true;
		depth = 10;
		subcontext.enable();
	}
	
	public void disable() {
		enabled = false;
		depth = 0;
		subcontext.disable();
	}
	
	@Override
	public boolean dead() {
		return dead;
	}
}
