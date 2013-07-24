// The tab of things you can do.

package org.gmcalc2.gui;

import java.util.Map;

import org.gmcalc2.World;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.CollapsibleFrame;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class WorldExplorerTab extends Tab {
	
	private static final int BORDER_THICKNESS = 8;

	private Map<String, World> worlds;
	private Color backgroundColor;
	private Font bodyFont;
	private int vBorderX, vBorderY, vBorderHeight;
	private ScrollableListFrame worldExplorer;
	private GUIElement editor;
	
	// Constructor.
	public WorldExplorerTab(String tabName, Map<String, World> worlds, int x, int y, int width, int height, int tabX, int tabWidth,
			Font font, Font bodyFont, Color tabEnabledColor, Color tabDisabledColor, Color tabNameColor, Color backgroundColor) {
		super(tabName, x, y, width, height, tabX, tabWidth, font, tabEnabledColor, tabDisabledColor, tabNameColor);
		
		this.worlds = worlds; // Assign the worlds.
		this.bodyFont = bodyFont; // Assign the body font.
		this.backgroundColor = backgroundColor; // Assign the background color.
		
		initWorldExplorer(); // Create the world explorer.
	}
	
	// Make a frame of a world for the world explorer.
	private CollapsibleFrame makeWorldFrame(World world, int x, int y, int w) {
		// Make the frames that will go into the world frame.
		CollapsibleFrame playersFrame, prefixesFrame, materialsFrame, itemBasesFrame;
		int subFrameX = x + bodyFont.getLineHeight();
		int subFrameY = y + bodyFont.getLineHeight();
		int subFrameWidth = x + w - subFrameX;
		playersFrame = makeMapFrame("Players", world.getPlayerMap(), subFrameX, subFrameY, subFrameWidth);
		subFrameY += playersFrame.getHeight();
		prefixesFrame = makeMapFrame("Prefixes", world.getPrefixMap(), subFrameX, subFrameY, subFrameWidth);
		subFrameY += prefixesFrame.getHeight();
		materialsFrame = makeMapFrame("Materials", world.getMaterialMap(), subFrameX, subFrameY, subFrameWidth);
		subFrameY += materialsFrame.getHeight();
		itemBasesFrame = makeMapFrame("Item Bases", world.getItemBaseMap(), subFrameX, subFrameY, subFrameWidth);
		subFrameY += itemBasesFrame.getHeight();
		
		// Make the world frame.
		int frameHeight = subFrameY - y;
		CollapsibleFrame worldFrame = new CollapsibleFrame(world.getName(), tabNameColor, bodyFont, x, y, w, frameHeight, true);
		worldFrame.addElement(playersFrame);
		worldFrame.addElement(prefixesFrame);
		worldFrame.addElement(materialsFrame);
		worldFrame.addElement(itemBasesFrame);
		
		return worldFrame;
	}
	
	// Make a frame from a map.
	private CollapsibleFrame makeMapFrame(String frameTitle, Map<String, ?> map, int x, int y, int w) {
		// Make the buttons to go in the frame.
		GUIElement[] buttons = new GUIElement[map.size()];
		int i = 0;
		int buttonX = x, buttonY = y + bodyFont.getLineHeight();
		int buttonWidth = w, buttonHeight = bodyFont.getLineHeight();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			buttons[i++] = new Button<Object>(entry.getKey(), tabNameColor, bodyFont, Button.LEFT, 0, buttonX, buttonY, buttonWidth, buttonHeight, 0, backgroundColor, tabNameColor, Input.KEY_ENTER);
			buttonY += buttonHeight;
		}
		
		// Make the frame.
		int frameHeight = bodyFont.getLineHeight() + buttons.length * buttonHeight;
		CollapsibleFrame out = new CollapsibleFrame(frameTitle, tabNameColor, bodyFont, x, y, w, frameHeight, true);
		out.addElements(buttons);
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

}
