package org.gmcalc2.gui;

import org.gmcalc2.item.Player;
import org.gmcalc2.item.Stat;
import org.gmcalc2.item.Item;

import org.haferlib.slick.gui.CollapsibleStringGroup;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.util.ListBag;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

public class PlayerTab extends Tab {
	
	private Font columnFont;
	private Color backgroundColor;
	private Image labelsImage;
	private Player player;
	private ScrollableListFrame statColumn, equippedColumn, inventoryColumn;
	private ItemDisplay selectedItemDisplay;
	private Image dragImage;
	private boolean dragging;
	private int dragImageXOffset, dragImageYOffset;

	// Constructor.
	public PlayerTab(Player player, int x, int y, int width, int height, int tabX, int tabWidth, Font font, Font columnFont, Color tabEnabledColor, Color tabDisabledColor, Color tabNameColor, Color backgroundColor) {
		super(player.getId(), player.getName(), x, y, width, height, tabX, tabWidth, font, tabEnabledColor, tabDisabledColor, tabNameColor);
		this.columnFont = columnFont;
		this.backgroundColor = backgroundColor;
		setPlayer(player);
		redrawLabels();
	}
	
	// Set the player and recreate the columns.
	public void setPlayer(Player p) {
		// Set the player.
		player = p;
		
		// Get rid of the old columns (if they exist).
		if (statColumn != null)
			subcontext.removeElement(statColumn);
		if (equippedColumn != null)
			subcontext.removeElement(equippedColumn);
		if (inventoryColumn != null)
			subcontext.removeElement(inventoryColumn);
		
		// Clear the selection.
		clearSelectedItemDisplay();
		
		// Add the new columns from right to left so the scroll bars line up nicely with the right side.
		int columnY = tabY2 + font.getLineHeight();
		int columnWidth = width / 3;
		int columnHeight = y2 - columnY;
		int inventoryColumnX = x2 - columnWidth;
		inventoryColumn = new ScrollableListFrame(inventoryColumnX, columnY, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(inventoryColumn);
		int equippedColumnX = inventoryColumnX - columnWidth;
		equippedColumn = new ScrollableListFrame(equippedColumnX, columnY, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(equippedColumn);
		int statColumnX = equippedColumnX - columnWidth;
		statColumn = new ScrollableListFrame(statColumnX, columnY, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(statColumn);
		
		// Fill up the columns.
		fillStatColumn();
		fillItemColumn(equippedColumn, player.getEquipped(), true);
		fillItemColumn(inventoryColumn, player.getInventory(), false);
	}
	
	// Fill the stat column with the sorted stats of the player.
	private void fillStatColumn() {
		statColumn.reinitSubcontext();
		
		// Some fields that will help sort the stats.
		Map<String, String[]> categoryRules = player.getWorld().getPlayerStatCategories();
		TreeMap<String, Stat> playerStats = player.getStatMap().copyTree();
		ArrayList<String> categoryNames = new ArrayList<>();
		ArrayList<String[]> categoryContents = new ArrayList<>();

		// Sort the stats.
		ArrayList<String> catBuilder = new ArrayList<>();
		for (Map.Entry<String, String[]> ruleEntry : categoryRules.entrySet()) {
			String[] ruleKeys = ruleEntry.getValue();
			for (String s : ruleKeys) {
				Stat stat = playerStats.remove(s);
				if (stat != null)
					catBuilder.add(s + ": " + stat.toString());
			}
			if (catBuilder.size() > 0) {
				String[] catStats = catBuilder.toArray(new String[catBuilder.size()]);
				catBuilder.clear();
				categoryNames.add(ruleEntry.getKey());
				categoryContents.add(catStats);
			}
		}
		
		// Handle the stats that weren't able to be sorted.
		for (Map.Entry<String, Stat> entry : playerStats.entrySet()) {
			catBuilder.add(entry.getKey() + ": " + entry.getValue().toString());
		}
		if (catBuilder.size() > 0) {
			String[] catStats = catBuilder.toArray(new String[catBuilder.size()]);
			catBuilder.clear();
			categoryNames.add("Other");
			categoryContents.add(catStats);
		}
		
		// Make the ItemDisplays from them.
		GUIElement[] statDisplays = new GUIElement[categoryNames.size()];
		int groupWidth = statColumn.getWidth() - statColumn.getScrollBarWidth();
		for (int i = 0; i < statDisplays.length; i++) {
			statDisplays[i] = new CollapsibleStringGroup(categoryNames.get(i), categoryContents.get(i), Color.white, 0, 0, groupWidth, 0, columnFont, true);
		}
		statColumn.addElements(statDisplays);
	}
	
	// Fill a column with collapsible string groups representing a list of items.
	private void fillItemColumn(ScrollableListFrame column, ListBag<Item> bag, boolean expanded) {
		column.reinitSubcontext();
		
		int displayWidth = column.getWidth() - column.getScrollBarWidth();
		GUIElement[] elements = new GUIElement[bag.size()];
		for (int i = 0; i < bag.size(); i++) {
			Item item = bag.get(i);
			ItemDisplay itemDisplay = makeItemDisplay(item, bag, displayWidth, expanded);
			elements[i] = itemDisplay;
		}
		column.addElements(elements);
	}
	
	// Make an item display.
	private ItemDisplay makeItemDisplay(Item item, ListBag<Item> bag, int displayWidth, boolean expanded) {
		ItemDisplay itemDisplay = new ItemDisplay(item, bag, player.getWorld().getRarityColor(item), 0, 0, displayWidth, 0, columnFont, expanded);
		return itemDisplay;
	}

	// Redraw the labels that are displayed above the columns.
	private void redrawLabels() {
		try {
			labelsImage = Image.createOffscreenImage(width, font.getLineHeight());
			Graphics g = labelsImage.getGraphics();
			g.setColor(tabEnabledColor);
			g.fillRect(0, 0, width, font.getLineHeight());
			
			g.setColor(tabNameColor);
			g.setFont(font);
			g.drawString("Stats", statColumn.getX() - x1, 0);
			g.drawString("Equipped", equippedColumn.getX() - x1, 0);
			g.drawString("Inventory", inventoryColumn.getX() - x1, 0);
			
			g.flush();
			g.destroy();
		}
		catch (SlickException e) {
			System.out.println("Failed to draw labels for tab.");
		}
	}
	
	// Select an item display.
	// REQUIRES: group is not null
	private void selectItemDisplay(ItemDisplay group) {
		System.out.println("Selecting an item display.");
		
		selectedItemDisplay = group;
		int dragImageWidth = columnFont.getWidth(selectedItemDisplay.getTitle());
		int dragImageHeight = columnFont.getLineHeight();
		dragImageXOffset = -dragImageWidth / 2;
		dragImageYOffset = -dragImageHeight / 2;
		// Draw the new drag image.
		try {
			// Create the drag image and a graphics to draw to it.
			dragImage = Image.createOffscreenImage(dragImageWidth, dragImageHeight);
			Graphics g = dragImage.getGraphics();
			
			// Fill the background with transparency.
			Color transparency = new Color(0, 0, 0, 0);
			g.setDrawMode(Graphics.MODE_ALPHA_MAP);
			g.setColor(transparency);
			g.fillRect(0, 0, dragImageWidth, dragImageHeight);
			g.setDrawMode(Graphics.MODE_NORMAL);
			
			// Draw the string.
			g.setFont(columnFont);
			g.setColor(selectedItemDisplay.getTextColor());
			g.drawString(selectedItemDisplay.getTitle(), 0, 0);
			
			// Flush the graphics and destroy it.
			g.flush();
			g.destroy();
		}
		catch (SlickException e) {
			e.printStackTrace();
			clearSelectedItemDisplay();
			return;
		}
	}
	
	// Clear the selected item display.
	private void clearSelectedItemDisplay() {
		selectedItemDisplay = null;
		// Destroy the old drag image.
		if (dragImage != null) {
			try {
				dragImage.destroy();
				dragImage = null;
			}
			catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Stop dragging the selected item display and, if needed, move it to its new position.
	// REQUIRES: An item display is selected.
	private void dropSelectedItemDisplay() {
		// Stop dragging.
		dragging = false;
		
		// See if it needs a new position. First check is that we aren't just placing it where it already is.
		if (!selectedItemDisplay.pointIsWithin(mouseX, mouseY)) {
			// See which one we're moving it from.
			ScrollableListFrame fromColumn;
			ListBag<Item> fromBag;
			if (equippedColumn.contains(selectedItemDisplay)) {
				fromColumn = equippedColumn;
				fromBag = player.getEquipped();
			}
			else if (inventoryColumn.contains(selectedItemDisplay)) {
				fromColumn = inventoryColumn;
				fromBag = player.getInventory();
			}
			else
				return;
			
			// See where it is being placed.
			ScrollableListFrame toColumn;
			ListBag<Item> toBag;
			if (equippedColumn.pointIsWithin(mouseX, mouseY)) {
				toColumn = equippedColumn;
				toBag = player.getEquipped();
			}
			else if (inventoryColumn.pointIsWithin(mouseX, mouseY)) {
				toColumn = inventoryColumn;
				toBag = player.getInventory();
			}
			else
				return;
			
			// Otherwise, move the item display.
			moveItemDisplay(fromColumn, toColumn, fromBag, toBag, selectedItemDisplay, mouseY, 1);
		}
	}
	
	// Move an item display from one column to another.
	private void moveItemDisplay(ScrollableListFrame fromColumn, ScrollableListFrame toColumn,
			ListBag<Item> fromBag, ListBag<Item> toBag,
			ItemDisplay itemDisplay, int placeAtY, int quantity) {

		// If we are moving within a column, just use the column's moveElement method.
		if (fromColumn.equals(toColumn)) {
			fromColumn.moveElement(itemDisplay, placeAtY);
		}

		// If we are moving column to column, remove the item from one and add it to the other
		// and then fill the stat column up again.
		else {
			Item item = itemDisplay.getItem(); // Get the item.
			itemDisplay.decreaseQuantity(quantity); // Decrease the quantity of the item display.
			
			// See if the item is already in the to column. If so, increase its quantity.
			ItemDisplay toDisplay = null;
			for (GUIElement e : toColumn.getElements()) {
				if (e instanceof ItemDisplay) {
					ItemDisplay eD = (ItemDisplay)e;
					if (eD.getItem().equals(item)) {
						toDisplay = eD;
						toDisplay.increaseQuantity(quantity);
						break;
					}
				}
			}
			// If not, make a display.
			if (toDisplay == null) {
				toBag.add(itemDisplay.getItem(), quantity); // Add to the quantity in the to bag.
				toDisplay = makeItemDisplay(item, toBag, toColumn.getWidth() - toColumn.getScrollBarWidth(), true);
				toColumn.addElement(toDisplay, placeAtY); // Add to the to column at the given y.
			}
			
			player.recalculateStats(); // Recalc the stats.
			fillStatColumn(); // Update the stat display.
		}
	}
	
	@Override
	public void renderInterior(Graphics g) {
		// Draw the top labels.
		g.drawImage(labelsImage, x1, tabY2);
		
		// Draw the subcontext and its background.
		g.setColor(backgroundColor);
		g.fillRect(x1, statColumn.getY(), width, statColumn.getHeight());
		renderSubcontext(g, x1, tabY2, x2, y2);
		
		// Draw the selection box.
		if (selectedItemDisplay != null) {
			g.setColor(Color.red);
			g.drawRect(selectedItemDisplay.getX(), selectedItemDisplay.getY(), selectedItemDisplay.getWidth(), selectedItemDisplay.getHeight());
		}
		
		// Draw the dragImage if we are dragging and the mouse isn't inside of the selected item display.
		if (dragging && !selectedItemDisplay.pointIsWithin(mouseX, mouseY)) {
			g.drawImage(dragImage, mouseX + dragImageXOffset, mouseY + dragImageYOffset);
		}
	}

	// Override click so that we can select collapsible groups in the columns.
	@Override
	public void click(int x, int y, int button) {
		super.click(x, y, button);
		
		// If the click is the left or right button...
		if (button == Input.MOUSE_LEFT_BUTTON || button == Input.MOUSE_RIGHT_BUTTON) {
			// If the click was within the equipped column, see if there is a new group to select and select it if there is.
			if (equippedColumn.pointIsWithin(x, y)) {
				System.out.println("Clicked equipped column");
				GUIElement e = equippedColumn.getElementAtPoint(x, y);
				if (!(e instanceof ItemDisplay)) {
					System.out.println("Null? " + e);
					clearSelectedItemDisplay();
				}
				else if (selectedItemDisplay == null || !selectedItemDisplay.equals(e)) {
					clearSelectedItemDisplay();
					selectItemDisplay((ItemDisplay)e);
				}
			}
			
			// If the click was within the equipped column, see if there is a new group to select and select it if there is.
			else if (inventoryColumn.pointIsWithin(x, y)) {
				System.out.println("Clicked inventory column");
				GUIElement e = inventoryColumn.getElementAtPoint(x, y);
				if (!(e instanceof ItemDisplay))
					clearSelectedItemDisplay();
				else if (selectedItemDisplay == null || !selectedItemDisplay.equals(e)) {
					clearSelectedItemDisplay();
					selectItemDisplay((ItemDisplay)e);
				}
			}
			
			// If the click was anywhere else, clear the selection.
			else
				clearSelectedItemDisplay();
			
			// If the click was the right mouse button and an item display was selected, open the context menu.
			if (button == Input.MOUSE_RIGHT_BUTTON && selectedItemDisplay != null) {
				// TODO
			}
		}
	}
	
	// Override mouseDown so we can drag collapsible groups in the columns.
	@Override
	public void mouseDown(int x, int y, int button) {
		super.mouseDown(x, y, button);
		
		// Drag if we are holding the left button, have a drag image and we are on top of selectedItemDisplay.
		if (button == Input.MOUSE_LEFT_BUTTON && dragImage != null && selectedItemDisplay.pointIsWithin(x, y))
			dragging = true;
	}
	
	// Override hover so we can stop dragging collapsible groups in the columns.
	@Override
	public void hover(int x, int y) {
		super.hover(x, y);
		
		// If we are dragging and suddenly we go to hover, we damn well better relocate the selected item display.
		if (dragging)
			dropSelectedItemDisplay();
	}
	
	// Override clickedElsewhere so we can stop dragging collapsible groups in the columns.
	@Override
	public void clickedElsewhere(GUIElement target, int button) {
		super.clickedElsewhere(target, button);
		
		// Don't drag if we're doing stuff elsewhere.
		if (dragImage != null) {
			dragging = false;
			clearSelectedItemDisplay();
		}
	}
	
	// Override mouseDownElsewhere so we can stop dragging collapsible groups in the columns.
	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
		super.mouseDownElsewhere(target, button);
		
		// Don't drag if we're doing stuff elsewhere.
		if (dragImage != null) {
			dragging = false;
			clearSelectedItemDisplay();
		}
	}
	
}
