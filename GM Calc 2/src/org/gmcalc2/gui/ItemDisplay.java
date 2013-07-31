package org.gmcalc2.gui;

import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.CollapsibleStringGroup;
import org.haferlib.util.ListBag;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;

public class ItemDisplay extends CollapsibleStringGroup {

	private Item item; // The item.
	private ListBag<Item> bag; // The bag the item is in.
	private boolean dead; //Is this dead?

	// Constructor.
	public ItemDisplay(Item item, ListBag<Item> bag, Color textColor,
			int x, int y, int width, int depth, Font font, boolean expanded) {
		super(item.getName() + (bag.getCount(item) > 1 ? " x" + bag.getCount(item) : ""),
				item.getStatMap().toDisplayStrings(), textColor, x, y, width, depth, font, expanded);
		this.item = item;
		this.bag = bag;
		dead = false;
		recalcTitle();
	}

	// Get the item.
	public Item getItem() {
		return item;
	}

	// Get the bag the item is in.
	public ListBag<Item> getBag() {
		return bag;
	}
	
	// Add to the count.
	public void increaseQuantity(int amount) {
		bag.add(item, amount);
		recalcTitle();
	}
	
	// Lower the count.
	public void decreaseQuantity(int amount) {
		bag.remove(item, amount);
		recalcTitle();
	}
	
	// Does this have a certain item?
	public boolean hasItem(Item other) {
		return item.equals(other);
	}
	
	// Recalc this, making it for dead if its count is less than 1 and changing the title as appropriate.
	public void recalcTitle() {
		int count = bag.getCount(item);
		if (count < 1) {
			dead = true;
			return;
		}
		String newTitle = item.getName() + (count > 1 ? " x" + count : "");
		if (!newTitle.equals(title)) {
			title = newTitle;
			try {
				redraw();
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Override dead() to return dead.
	@Override
	public boolean dead() {
		return dead;
	}

}
