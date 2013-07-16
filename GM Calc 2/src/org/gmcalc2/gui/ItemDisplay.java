package org.gmcalc2.gui;

import org.gmcalc2.item.Player.QuantityItem;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class ItemDisplay extends CollapsibleStringGroup {
	
	private QuantityItem item;

	//Constructor.
	public ItemDisplay(ScrollableListFrame container, QuantityItem item, Color textColor, int x, int y, int width, Font font, boolean expanded) {
		super(container, item.getItem().getName() + ((item.getAmount() > 1)? " x" + item.getAmount() : ""), item.getItem().getStatMap().toDisplayStrings(), textColor, x, y, width, font, expanded);
		this.item = item;
	}
	
	//Get the item.
	public QuantityItem getItem() {
		return item;
	}
	
}
