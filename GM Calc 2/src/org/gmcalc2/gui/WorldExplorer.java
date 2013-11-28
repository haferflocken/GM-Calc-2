// A sidebar that lets the client open files and such.

package org.gmcalc2.gui;

import java.util.HashSet;
import java.util.Map;

import org.gmcalc2.World;
import org.haferslick.gui.Button;
import org.haferslick.gui.CollapsibleListFrame;
import org.haferslick.gui.GUIElement;
import org.haferslick.gui.GUISubcontext;
import org.haferslick.gui.ScrollableListFrame;
import org.haferslick.gui.TextButton;
import org.haferslick.gui.event.GUIEvent;
import org.haferslick.gui.event.GUIEventGenerator;
import org.haferslick.gui.event.GUIEventListener;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class WorldExplorer extends GUISubcontext implements GUIEventGenerator, GUIEventListener {
	
	public static final String TITLE = "World Explorer";

	private int titleY2, bodyHeight;
	private Map<String, World> worlds;
	private Color textColor, titleBackgroundColor, bodyBackgroundColor;
	private Font titleFont, bodyFont;
	private Color buttonHighlightColor;
	private ScrollableListFrame worldFrame;
	private HashSet<GUIEventListener> listeners;
	
	// Constructor.
	public WorldExplorer(int x, int y, int width, int height, int depth, Map<String, World> worlds,
			Color textColor, Color titleBackgroundColor, Color bodyBackgroundColor, Color buttonHighlightColor,
			Font titleFont, Font bodyFont) {
		super(x, y, width, height, depth);
		
		setTitleFont(titleFont);
		setBodyFont(bodyFont);
		
		this.worlds = worlds;
		this.textColor = textColor;
		this.titleBackgroundColor = titleBackgroundColor;
		this.bodyBackgroundColor = bodyBackgroundColor;
		this.buttonHighlightColor = buttonHighlightColor;
		
		listeners = new HashSet<>();
		
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
		CollapsibleListFrame out = new CollapsibleListFrame(frameTitle, textColor, bodyFont, x, y, w, 0, false);
		
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
		int frameHeight = bodyHeight;
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
		g.setFont(titleFont);
		g.drawString(TITLE, x1 + 4, y1);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		titleY2 = y1 + titleFont.getLineHeight();
	}
	
	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		bodyHeight = height - titleFont.getLineHeight();
	}
	
	@Override
	public void guiEvent(GUIEvent<?> event) {
		// Forward events.
		event = new GUIEvent<Object>(this, event.getData());
		
		for (GUIEventListener l : listeners)
			l.guiEvent(event);
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

}
