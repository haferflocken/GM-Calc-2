package org.gmcalc2.gui;

import java.util.HashSet;

import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventGenerator;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.TextButton;
import org.haferlib.slick.gui.TextField;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ListEditor extends GUISubcontext implements GUIEventGenerator, GUIEventListener {
	
	private static final Object EVENT_DATA_INCREASE = "increase";

	private int maxHeight;
	private String title;
	private ListFrame listFrame;
	private Font font;
	private Color textColor, backgroundColor, fieldColor;
	private HashSet<GUIEventListener> listeners;
	
	// Constructor.
	public ListEditor(int x, int y, int width, int maxHeight, int depth,
			String title, String[] list, Font font, Color textColor, Color backgroundColor, Color fieldColor) {
		super(x, y, width, font.getLineHeight(), depth);
		listeners = new HashSet<>();
		subcontext = new GUIContext();
		
		this.maxHeight = maxHeight;
		this.title = title;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldColor = fieldColor;
		
		// Create the list frame.
		int lFX = x1;
		int lFY = y1 + font.getLineHeight();
		int lFW = width;
		int lFXOffset = font.getLineHeight();
		int lFSpacing = 2;
		listFrame = new ListFrame(lFX, lFY, lFW, 0, ListFrame.XALIGN_LEFT, lFXOffset, lFSpacing);
		listFrame.addListener(this);
		subcontext.addElement(listFrame);
		
		// Add fields for the given list.
		for (int i = 0; i < list.length; i++) {
			addField(list[i]);
		}
		
		// Figure out where to put the buttons.
		int buttonSize = font.getLineHeight();
		int buttonY = y1;
		int addButtonX = x1 + font.getWidth(title);
		
		// Add a button to add a field.
		Button<?> addButton = new TextButton<Object>(EVENT_DATA_INCREASE,
				addButtonX, buttonY, buttonSize, buttonSize, 0,
				fieldColor, null, Input.KEY_ENTER, "+", textColor, font, TextButton.CENTER, 0);
		addButton.addListener(this);
		subcontext.addElement(addButton);
		
		// Add and remove so that the elements will be moved by any setX/setY operations
		// if they are called before the first update.
		subcontext.addAndRemoveElements();
	}
	
	public void addField(String fieldContents) {
		int fieldWidth = listFrame.getWidth() - listFrame.getXAlignOffset();
		TextField field =  new TextField(0, 0, fieldWidth, font.getLineHeight(), 0,
				fieldContents, null, font, textColor, null, fieldColor);
		listFrame.addElement(field);
	}
	
	public void addField() {
		addField("");
	}
	
	@Override
	public void setHeight(int h) {
		maxHeight = h;
		if (height > maxHeight)
			super.setHeight(maxHeight);
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		if (backgroundColor != null) {
			g.setColor(backgroundColor);
			g.fillRect(x1, y1, width, height);
		}
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
		
		// Draw the title.
		g.setColor(textColor);
		g.drawString(title, x1, y1);
		
		// Draw the border.
		//g.drawRect(x1, y1, width - 1, height - 1);
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If we get a resize event from the component frame, set our height
		// to its height plus font.getLineHeight(), then notify our listeners.
		if (GUIEvent.RESIZE_EVENT.equals(event.getData())) {
			int newHeight = font.getLineHeight() + listFrame.getHeight();
			if (newHeight < maxHeight)
				super.setHeight(newHeight);
			else
				super.setHeight(maxHeight);
			
			GUIEvent<?> out = new GUIEvent<Object>(this, GUIEvent.RESIZE_EVENT);
			for (GUIEventListener l : listeners)
				l.guiEvent(out);
		}
		
		// If we get an event with data of EVENT_DATA_INCREASE, increase the size of the list.
		if (EVENT_DATA_INCREASE.equals(event.getData())) {
			addField();
		}
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
