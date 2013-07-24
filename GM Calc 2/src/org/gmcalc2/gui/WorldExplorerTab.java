// The tab of things you can do.

package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.Player;
import org.gmcalc2.state.TabState;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.CollapsibleFrame;
import org.haferlib.slick.gui.CollapsibleListFrame;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class WorldExplorerTab extends Tab implements GUIEventListener {
	
	private static final int BORDER_THICKNESS = 8;

	private TabState tabState;
	private Map<String, World> worlds;
	private Color backgroundColor;
	private Color bodyColor;
	private Font bodyFont;
	private Color buttonHighlightColor;
	private int vBorderX, vBorderY, vBorderHeight;
	private ScrollableListFrame worldExplorer;
	private GUIElement editor;
	
	// Constructor.
	public WorldExplorerTab(String tabName, int x, int y, int width, int height, int tabX, int tabWidth,
			Font tabFont, Color tabEnabledColor, Color tabDisabledColor, Color tabNameColor,
			TabState tabState, Map<String, World> worlds, Color backgroundColor, Color bodyColor, Font bodyFont, Color buttonHighlightColor) {
		super(tabName, x, y, width, height, tabX, tabWidth, tabFont, tabEnabledColor, tabDisabledColor, tabNameColor);
		
		// Assign fields.
		this.tabState = tabState;
		this.worlds = worlds;
		this.backgroundColor = backgroundColor;
		this.bodyColor = bodyColor;
		this.bodyFont = bodyFont;
		this.buttonHighlightColor = buttonHighlightColor;
		
		initWorldExplorer(); // Create the world explorer.
	}
	
	// Make a frame of a world for the world explorer.
	private CollapsibleListFrame makeWorldFrame(World world, int x, int y, int w) {
		// Make the world frame.
		CollapsibleListFrame worldFrame = new CollapsibleListFrame(world.getName(), bodyColor, bodyFont, x, y, w, true);
		
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
		CollapsibleListFrame out = new CollapsibleListFrame(frameTitle, bodyColor, bodyFont, x, y, w, true);
		
		// Add buttons to the frame.
		GUIElement[] buttons = new GUIElement[map.size()];
		int i = 0;
		int buttonWidth = out.getListWidth(), buttonHeight = bodyFont.getLineHeight();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			Button<Object> b = new Button<Object>(entry.getKey(), entry.getValue(), bodyColor, bodyFont, Button.LEFT, 0, 0, 0, buttonWidth, buttonHeight, 0, null, buttonHighlightColor, Input.KEY_ENTER);
			b.addListener(this);
			buttons[i++] = b;
		}
		out.addElements(buttons);
		
		// Return.
		return out;
	}
	
	// Create the world explorer.
	private void initWorldExplorer() {
		// Get some shape data.
		int frameX = x1;
		int frameY = tabY2 + BORDER_THICKNESS;
		int frameWidth = width / 4;
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
		
		// Create the world explorer.
		worldExplorer = new ScrollableListFrame(explorerElements, frameX, frameY, frameWidth, interiorHeight - BORDER_THICKNESS, 0, frameScrollBarWidth, Color.white);
		subcontext.addElement(worldExplorer);
		
		// Set the vertical border fields appropriately.
		vBorderX = worldExplorer.getX() + worldExplorer.getWidth();
		vBorderY = worldExplorer.getY();
		vBorderHeight = worldExplorer.getHeight();
	}
	
	@Override
	public void renderInterior(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, tabY2, width, interiorHeight);
		
		// Draw the area borders.
		g.setColor(tabEnabledColor);
		g.fillRect(x1, tabY2, width, BORDER_THICKNESS);
		g.fillRect(vBorderX, vBorderY, BORDER_THICKNESS, vBorderHeight);
						
		// Render the subcontext.
		renderSubcontext(g, x1, tabY2, x2, y2);
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
			if (!tabState.setEnabledTabByName(p.getName()))
				tabState.setEnabledTab(this);
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
