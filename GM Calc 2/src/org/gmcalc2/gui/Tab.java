//A tab. The x1, y1, x2, y2, width, and height all follow GUIElement conventions, which is basically to say they define the bounding box of the tab including the little bit that sticks out at the top.

package org.gmcalc2.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Polygon;

import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ScrollableFrame;
import org.haferlib.slick.gui.GUIElement;

import org.gmcalc2.GMCalc2;

public class Tab extends GUISubcontext {
	
	private String tabName;
	private Font font;
	private Polygon tabShape;
	private int tabY2;	//The tab shape. No need for tabY1 because it is the same as y1.
	private int x2, y2, width, height;
	private int depth;
	private boolean dead, enabled;
	
	public Tab(String tabName, int x, int y, int width, int height, int tabX, int tabWidth, Font font) {
		//Set the parameters.
		this.tabName = tabName;
		createTab(tabX, tabWidth, font);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		enable();
		
		//Add the columns from right to left so the scroll bars line up nicely with the right side.
		int columnWidth = width / 3;
		int columnHeight = height - font.getLineHeight();
		int inventoryColumnX = x2 - columnWidth;
		ScrollableFrame inventoryColumn = new ScrollableFrame(inventoryColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(inventoryColumn);
		int equippedColumnX = inventoryColumnX - columnWidth;
		ScrollableFrame equippedColumn = new ScrollableFrame(equippedColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(equippedColumn);
		int statColumnX = equippedColumnX - columnWidth;
		ScrollableFrame statColumn = new ScrollableFrame(statColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(statColumn);
		
		//Fill the columns up.
		GUIElement[] statsTest = new GUIElement[15];
		for (int i = 0; i < statsTest.length; i++) {
			statsTest[i] = new CollapsibleStringGroup("Group " + i, new String[] {"a", "b", "c"}, Color.white, statColumnX, tabY2 + i * GMCalc2.FONT_HEIGHT * 4, columnWidth, GMCalc2.FONT, true);
		}
		statColumn.addElements(statsTest);
	}
	
	@Override
	public void render(Graphics g) {
		//Draw the tab.
		if (enabled)
			g.setColor(Color.lightGray);
		else
			g.setColor(Color.gray);
		g.fill(tabShape);
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(tabName, tabShape.getX(), y1);
		
		//The following is only rendered if the tab is enabled.
		if (!enabled)
			return;
		
		//Draw the background.
		g.setColor(Color.lightGray);
		g.fillRect(x1, tabY2, width, height);
		
		//Render the subcontext.
		subcontext.render(g, x1, tabY2, x2, y2);
	}
	
	public void setTabX(int x) {
		tabShape.setX(x);
	}
	
	public void createTab(int tX, int tW, Font f) {
		font = f;
		tabY2 = y1 + font.getLineHeight();
		tabShape = new Polygon(new float[] {
			tX, tabY2, //Bottom left
			tX + 8, y1, //Top left
			tX + tW - 8, y1, //Top right
			tX + tW, tabY2, //Bottom right
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
			if (button == Input.MOUSE_LEFT_BUTTON)
				enable();
		}
	}
	
	@Override
	public void mouseDown(int x, int y, int button) {
		super.mouseDown(x, y, button);
		if (!enabled) {
			if (button == Input.MOUSE_LEFT_BUTTON)
				enable();
		}
	}
	
	@Override
	public void clickedElsewhere(int button) {
		super.clickedElsewhere(button);
		if (button == Input.MOUSE_LEFT_BUTTON)
			disable();
	}
	
	@Override
	public void mouseDownElsewhere(int button) {
		super.mouseDownElsewhere(button);
		if (button == Input.MOUSE_LEFT_BUTTON)
			disable();
	}
	
	@Override
	public int getDepth() {
		return depth;
	}
	
	public void enable() {
		enabled = true;
		subcontext.enable();
	}
	
	public void disable() {
		enabled = false;
		subcontext.disable();
	}
	
	@Override
	public boolean dead() {
		return dead;
	}
}
