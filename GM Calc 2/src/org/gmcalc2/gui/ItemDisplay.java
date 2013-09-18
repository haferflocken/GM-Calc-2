package org.gmcalc2.gui;

import org.gmcalc2.item.Item;
import org.gmcalc2.item.StatMap;
import org.haferlib.slick.gui.CollapsibleListFrame;
import org.haferlib.slick.gui.TextDisplay;
import org.haferlib.util.ListBag;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class ItemDisplay extends CollapsibleListFrame {

	private Item item;					// The item.
	private ListBag<Item> bag;			// The bag the item is in.

	// Constructor.
	public ItemDisplay(Item item, ListBag<Item> bag, Color textColor,
			int x, int y, int width, int depth, Font font, boolean expanded) {
		super("", textColor, font,
				x, y, width, depth, expanded);
		dead = false;
		setItem(item, bag);
	}

	// Get the item.
	public Item getItem() {
		return item;
	}

	// Get the bag the item is in.
	public ListBag<Item> getBag() {
		return bag;
	}
	
	// Does this have a certain item?
	public boolean hasItem(Item other) {
		return item.equals(other);
	}
	
	// Get the quantity.
	public int getQuantity() {
		return bag.getCount(item);
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
		
		if (bag.getCount(item) < 1) {
			dead = true;
		}
	}
	
	// Set the item.
	public void setItem(Item i, ListBag<Item> b) {
		item = i;
		bag = b;
		recalcColor();
		recalcTitle();
		recalcStrings();
	}
	
	// Recalc the color.
	public void recalcColor() {
		textColor = item.getWorld().getRarityColor(item);
	}
	
	// Recalc the title.
	public void recalcTitle() {
		String newTitle = item.getName();
		int count = bag.getCount(item);
		if (count > 1)
			newTitle += " x" + count;
		setTitle(newTitle, font);
	}
	
	// Recalc the strings.
	public void recalcStrings() {
		StatMap itemStats = item.getStatMap();
		String[] statStrings = itemStats.toDisplayStrings();
		
		clearElements();
		
		int statDisplayOffset = font.getLineHeight();
		int statDisplayWidth = width - statDisplayOffset;
		TextDisplay[] statDisplays = new TextDisplay[statStrings.length];
		for (int i = 0; i < statStrings.length; i++) {
			statDisplays[i] = new TextDisplay(0, 0, statDisplayWidth, Integer.MAX_VALUE, 0, statStrings[i], font, textColor);
		}
		
		addElements(statDisplays);
	}

}
