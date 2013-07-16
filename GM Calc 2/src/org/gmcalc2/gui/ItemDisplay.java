package org.gmcalc2.gui;

import org.gmcalc2.item.Player.QuantityItem;

import org.haferlib.slick.gui.CollapsibleStringGroup;
import org.haferlib.slick.gui.ScrollableListFrame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;

public class ItemDisplay extends CollapsibleStringGroup {
	
	private QuantityItem item; //The item.

	//Constructor.
	public ItemDisplay(ScrollableListFrame container, QuantityItem item, Color textColor, int x, int y, int width, Font font, boolean expanded) {
		super(container, item.toString(), item.getItem().getStatMap().toDisplayStrings(), textColor, x, y, width, font, expanded);
		this.item = item;
	}
	
	//Get the item.
	public QuantityItem getItem() {
		return item;
	}
	
	//Recalc the title.
	public void recalcTitle() {
		String newTitle = item.toString();
		if (!newTitle.equals(title)) {
			title = newTitle;
			try {
				redraw();
			}
			catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
	
}
