package org.gmcalc2.gui;

import java.util.HashSet;

import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventGenerator;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.slick.gui.SearchField;
import org.haferlib.slick.gui.TextField;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class TextFieldGroup extends GUISubcontext implements GUIEventGenerator, GUIEventListener {

	private int maxHeight;
	private String title;
	private ListFrame listFrame;
	private Font font;
	private Color textColor, backgroundColor, fieldMessageColor, fieldColor;
	private HashSet<GUIEventListener> listeners;
	
	// Constructor.
	public TextFieldGroup(int x, int y, int width, int maxHeight, int depth,
			String title, String[] list, String[] backgroundList, String[][] listSearchStrings, Font font,
			Color textColor, Color backgroundColor, Color fieldMessageColor, Color fieldColor) {
		super(x, y, width, font.getLineHeight(), depth);
		listeners = new HashSet<>();
		subcontext = new GUIContext();
		
		this.maxHeight = maxHeight;
		this.title = title;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.fieldMessageColor = fieldMessageColor;
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
			addField(list[i], backgroundList[i], listSearchStrings[i]);
		}
		
		// Add and remove so that the elements will be moved by any setX/setY operations
		// if they are called before the first update.
		subcontext.addAndRemoveElements();
	}
	
	/**
	 * Add a field with the given contents.
	 * 
	 * @param fieldContents The contents of the new field. Can be null.
	 * @param fieldBackgroundMessage The background message of the field. Can be null.
	 */
	public void addField(String fContents, String fBackgroundMessage, String[] fSearchStrings) {
		int fieldWidth = listFrame.getWidth() - listFrame.getXAlignOffset();
		TextField field = new SearchField(0, 0, fieldWidth, font.getLineHeight(), 0,
				fContents, fBackgroundMessage, fSearchStrings,
				font, textColor, fieldMessageColor, textColor, fieldColor);
		listFrame.addElement(field);
	}
	
	/**
	 * Add a new empty field.
	 */
	public void addField() {
		addField(null, null, new String[] {});
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
