//A scrollable frame that arranges its elements into a list and can be told to rearrange them if one changes size.

package org.gmcalc2.gui;

import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableFrame;

import org.newdawn.slick.Color;

import java.util.ArrayList;

public class ScrollableCSGFrame extends ScrollableFrame {
	
	private int ySpacing;
	
	public ScrollableCSGFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, int ySpacing) {
		super(x, y, width, height, depth, scrollBarWidth, scrollBarColor);
		this.ySpacing = ySpacing;
	}

	public ScrollableCSGFrame(GUIElement[] elements, int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, int ySpacing) {
		this(x, y, width, height, depth, scrollBarWidth, scrollBarColor, ySpacing);
		addElements(elements);
	}
	
	/*private int getBottomY() {
		//We can assume the last element is on bottom.
		ArrayList<GUIElement> elements = subcontext.getElements();
		if (elements.size() > 0) {
			GUIElement e = elements.get(elements.size() - 1);
			return e.getY() + e.getHeight();
		}
		return 0;
	}
	
	public void addElement(GUIElement e) {
		//Find the bottom y to align to. 
		int yAlign = getBottomY() + ySpacing;
		
		//Align the element.
		e.setX(x1);
		e.setY(yAlign);
		
		//Add the element.
		super.addElement(e);
	}
	
	public void addElements(GUIElement[] es) {
		//Find the bottom y to align to. 
		int yAlign = getBottomY() + ySpacing;
		
		//Align the elements.
		for (int i = 0; i < es.length; i++) {
			es[i].setX(x1);
			es[i].setY(yAlign);
			yAlign += es[i].getHeight() + ySpacing;
		}
		
		//Add the elements.
		super.addElements(es);
	}*/
	
	public void realignFromElement(GUIElement e) {
		//Get the index of the element.
		ArrayList<GUIElement> elements = subcontext.getElements();
		int i;
		for (i = 0; i < elements.size(); i++) {
			if (elements.get(i).equals(e))
				break;
		}
		//If we don't find the element, return.
		if (i == elements.size())
			return;
		
		//Reposition the elements below the index.
		int yAlign = e.getY() + e.getHeight();
		for (i += 1; i < elements.size(); i++) {
			e = elements.get(i);
			e.setY(yAlign);
			yAlign += e.getHeight() + ySpacing;
		}
		
		//Recalculate the scrolling fields.
		recalculateScrollingFields();
	}
}
