package org.gmcalc2.gui;

import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.TextButton;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;

public class ListEditor extends TextFieldGroup {
	
	private static final Object EVENT_DATA_INCREASE = "increase";
	
	// Constructor.
	public ListEditor(int x, int y, int width, int maxHeight, int depth,
			String title, String[] list, String[] backgroundList, Font font,
			Color textColor, Color backgroundColor, Color fieldMessageColor, Color fieldColor) {
		super(x, y, width, maxHeight, depth, title, list, backgroundList, font, textColor, backgroundColor, fieldMessageColor, fieldColor);
		
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

	@Override
	public void guiEvent(GUIEvent<?> event) {
		super.guiEvent(event);
		
		// If we get an event with data of EVENT_DATA_INCREASE, increase the size of the list.
		if (EVENT_DATA_INCREASE.equals(event.getData())) {
			addField();
		}
	}

}
