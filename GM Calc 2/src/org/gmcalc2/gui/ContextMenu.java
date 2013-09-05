package org.gmcalc2.gui;

import java.util.HashSet;

import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.TextButton;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventGenerator;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * A context menu that holds buttons.
 * 
 * @author John
 *
 */

public class ContextMenu extends GUISubcontext implements GUIEventGenerator, GUIEventListener {
	
	private ListFrame buttonList;
	private Font font;
	private Color textColor, backgroundColor, highlightColor, borderColor;
	private HashSet<GUIEventListener> listeners;

	// Constructor.
	public ContextMenu(int x, int y, int width, int depth,
			Font font, String[] buttonNames, Color textColor, Color backgroundColor, Color highlightColor, Color borderColor) {
		super(x, y, width, 0, depth);
		
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.highlightColor = highlightColor;
		this.borderColor = borderColor;
		
		listeners = new HashSet<>();
		
		// Make the buttons.
		TextButton<?>[] buttons = new TextButton<?>[buttonNames.length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new TextButton<String>(buttonNames[i], 0, 0, width, font.getLineHeight(), 0,
					backgroundColor, highlightColor, Input.KEY_ENTER,
					buttonNames[i], textColor, font, TextButton.LEFT, 0);
			buttons[i].addListener(this);
		}
		
		// Fill a list frame with them.
		buttonList = new ListFrame(buttons, x1, y1, width, 0);
		subcontext.addElement(buttonList);
		
		// Set the height to the button list's height.
		setHeight(buttonList.getHeight());
	}

	@Override
	public void render(Graphics g) {
		renderSubcontext(g, x1, y1, x2, y2);
		
		// Draw the border.
		g.setColor(borderColor);
		g.drawRect(x1, y1, width, height);
	}
	
	@Override
	public void clickedElsewhere(GUIElement target, int button) {
		super.clickedElsewhere(target, button);
		dead = true;
	}
	
	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
		super.mouseDownElsewhere(target, button);
		dead = true;
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If we are receiving an event from one of the buttons,
		// pass the data along to the listeners and then die.
		GUIEventGenerator generator = event.getGenerator();
		if (generator instanceof TextButton) {
			TextButton<?> button = (TextButton<?>)generator;
			if (buttonList.contains(button)) {
				String data = (String)event.getData();
				GUIEvent<String> out = new GUIEvent<>(this, data);
				for (GUIEventListener l : listeners)
					l.guiEvent(out);
				dead = true;
			}
		}
	}

}
