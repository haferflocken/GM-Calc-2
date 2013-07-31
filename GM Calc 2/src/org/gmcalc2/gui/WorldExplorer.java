// A sidebar that lets the client open files and such.

package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.Player;
import org.gmcalc2.state.TabState;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.CollapsibleListFrame;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.slick.gui.TextButton;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class WorldExplorer extends GUISubcontext implements GUIEventListener {
	
	public static final String TITLE = "World Explorer";

	private int x2, y2;
	private int width, height;
	private int depth;
	private int titleY2, bodyHeight;
	private TabState tabState;
	private Map<String, World> worlds;
	private Color textColor, titleBackgroundColor, bodyBackgroundColor;
	private Font titleFont, bodyFont;
	private Color buttonHighlightColor;
	private ScrollableListFrame worldFrame;
	
	// Constructor.
	public WorldExplorer(int x, int y, int width, int height, int depth,
			TabState tabState, Map<String, World> worlds,
			Color textColor, Color titleBackgroundColor, Color bodyBackgroundColor,
			Font titleFont, Font bodyFont, Color buttonHighlightColor) {
		super(x, y);
		
		setTitleFont(titleFont);
		setBodyFont(bodyFont);
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setDepth(depth);
		
		this.tabState = tabState;
		this.worlds = worlds;
		this.textColor = textColor;
		this.titleBackgroundColor = titleBackgroundColor;
		this.bodyBackgroundColor = bodyBackgroundColor;
		this.buttonHighlightColor = buttonHighlightColor;
		
		initWorldFrame(); // Create the world explorer.
	}
	
	// Make a frame of a world for the world explorer.
	private CollapsibleListFrame makeWorldFrame(World world, int x, int y, int w) {
		// Make the world frame.
		CollapsibleListFrame worldFrame = new CollapsibleListFrame(world.getName(), textColor, bodyFont, x, y, w, 0, true);
		
		// Add frames to the world frame.
		CollapsibleListFrame playersFrame, prefixesFrame, materialsFrame, itemBasesFrame;
		int subFrameWidth = worldFrame.getListWidth();
		playersFrame = makeMapFrame("Players", world.getPlayerMap(), 0, 0, subFrameWidth);
		prefixesFrame = makeMapFrame("Prefixes", world.getPrefixMap(), 0, 0, subFrameWidth);
		materialsFrame = makeMapFrame("Materials", world.getMaterialMap(), 0, 0, subFrameWidth);
		itemBasesFrame = makeMapFrame("Item Bases", world.getItemBaseMap(), 0, 0, subFrameWidth);
		
		worldFrame.addElements(new GUIElement[] { playersFrame, prefixesFrame, materialsFrame, itemBasesFrame });
		
		// Return.
		return worldFrame;
	}
	
	// Make a frame from a map.
	private CollapsibleListFrame makeMapFrame(String frameTitle, Map<String, ?> map, int x, int y, int w) {
		// Make the frame.
		CollapsibleListFrame out = new CollapsibleListFrame(frameTitle, textColor, bodyFont, x, y, w, 0, true);
		
		// Add buttons to the frame.
		GUIElement[] buttons = new GUIElement[map.size()];
		int i = 0;
		int buttonWidth = out.getListWidth(), buttonHeight = bodyFont.getLineHeight();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			Button<Object> b = new TextButton<Object>(entry.getValue(), 0, 0, buttonWidth, buttonHeight,
					0, null, buttonHighlightColor, Input.KEY_ENTER,
					entry.getKey(), textColor, bodyFont, Button.LEFT, 0);
			b.addListener(this);
			buttons[i++] = b;
		}
		out.addElements(buttons);
		
		// Return.
		return out;
	}
	
	// Create the world frame.
	private void initWorldFrame() {
		// Get some shape data.
		int frameX = x1;
		int frameY = titleY2;
		int frameWidth = width;
		int frameHeight = height;
		int frameScrollBarWidth = 10;
		
		// Create the elements that hold the worlds.
		GUIElement[] explorerElements = new GUIElement[worlds.size()];
		int i = 0;
		int elementY = frameY;
		int elementWidth = frameWidth - frameScrollBarWidth;
		for (Map.Entry<String, World> entry : worlds.entrySet()) {
			explorerElements[i] = makeWorldFrame(entry.getValue(), x1, elementY, elementWidth);
			elementY += explorerElements[i].getHeight();
			i++;
		}
		
		// Create the world frame.
		worldFrame = new ScrollableListFrame(explorerElements, frameX, frameY, frameWidth, frameHeight, 0, frameScrollBarWidth, Color.white);
		subcontext.addElement(worldFrame);
	}
	
	// Set the title font.
	public void setTitleFont(Font f) {
		titleFont = f;
		titleY2 = y1 + titleFont.getLineHeight();
		bodyHeight = height - titleFont.getLineHeight();
	}
	
	// Set the body font.
	public void setBodyFont(Font f) {
		bodyFont = f;
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(titleBackgroundColor);
		g.fillRect(x1, y1, width, titleFont.getLineHeight());
		g.setColor(bodyBackgroundColor);
		g.fillRect(x1, titleY2, width, bodyHeight);
		
		// Draw the title.
		g.setColor(textColor);
		g.drawString(TITLE, x1 + 4, y1);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		x2 = x1 + width;
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		y2 = y1 + height;
		titleY2 = y1 + titleFont.getLineHeight();
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
		bodyHeight = height - titleFont.getLineHeight();
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}
	
	@Override
	public void setDepth(int d) {
		depth = d;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean dead() {
		return false;
	}
	
	@Override
	// This will get button presses. It needs to switch to a player tab if clicking a player,
	// a component editor if clicking a prefix or material, an item base editor if clicking
	// an item base, and a rule editor if clicking a world.
	public void guiEvent(GUIEvent<?> event) {
		Object eventData = event.getData();
		
		// Clicking a player.
		if (eventData instanceof Player) {
			Player p = (Player)eventData;
			// See if there already is a tab.
			boolean success = tabState.setEnabledTabById(p.getId());
			// If there isn't, add a new tab.
			if (!success) {
				// TODO
				// PlayerTab tab = new PlayerTab();
			}
		}
		
		// Clicking an item base.
		else if (eventData instanceof ItemBase) {
			System.out.println("Clicked item base!");
		}
		
		// Clicking a material or prefix.
		else if (eventData instanceof Component) {
			System.out.println("Clicked prefix or material!");
		}
		
		// Clicking a world.
		else if (eventData instanceof World) {
			System.out.println("Clicked world!");
		}
	}

}
