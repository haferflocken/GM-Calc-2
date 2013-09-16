package org.gmcalc2.gui;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.item.Player;
import org.gmcalc2.item.Stat;
import org.gmcalc2.item.Item;
import org.haferlib.slick.gui.CollapsibleListFrame;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableListFrame;
import org.haferlib.slick.gui.TextDisplay;
import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.haferlib.util.ListBag;
import org.haferlib.util.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

public class PlayerTab extends Tab implements GUIEventListener {
	
	private static final String CONTEXT_MENU_EDIT_PREFIXES = "Edit Prefixes";
	private static final String CONTEXT_MENU_EDIT_MATERIALS = "Edit Materials";
	private static final String CONTEXT_MENU_EDIT_ITEMBASE = "Edit Item Base";
	private static final String CONTEXT_MENU_ADJUST_QUANTITY = "Adjust Quantity";
	private static final String CONTEXT_MENU_DELETE_STACK = "Delete Stack";
	private static final String[] CONTEXT_MENU_OPTIONS_ALL =
		{ CONTEXT_MENU_EDIT_PREFIXES, CONTEXT_MENU_EDIT_MATERIALS, CONTEXT_MENU_EDIT_ITEMBASE, 
		CONTEXT_MENU_ADJUST_QUANTITY, CONTEXT_MENU_DELETE_STACK };
	private static final String[] CONTEXT_MENU_OPTIONS_NOMATERIALS =
		{ CONTEXT_MENU_EDIT_PREFIXES, CONTEXT_MENU_EDIT_ITEMBASE, CONTEXT_MENU_ADJUST_QUANTITY,
		CONTEXT_MENU_DELETE_STACK };
	
	private Font columnFont;
	private Color backgroundColor, itemDisplayHighlightColor;
	private Player player;
	private ScrollableListFrame statColumn, equippedColumn, inventoryColumn;
	private ItemDisplay selectedItemDisplay;
	private String dragString;
	private Color dragStringColor;
	private int dragStringXOffset, dragStringYOffset;
	private boolean dragging;
	private ContextMenu contextMenu;
	private GUIElement itemEditor;

	// Constructor.
	public PlayerTab(Player player, int x, int y, int width, int height, int tabX, Font font, Font columnFont,
			Color tabEnabledColor, Color tabDisabledColor, Color tabNameColor, Color backgroundColor, Color itemDisplayHighlightColor) {
		super(player.getId(), player.getName(), x, y, width, height, tabX, font.getWidth(player.getName()) * 10 / 9, font, tabEnabledColor, tabDisabledColor, tabNameColor);
		this.columnFont = columnFont;
		this.backgroundColor = backgroundColor;
		this.itemDisplayHighlightColor = itemDisplayHighlightColor;
		setPlayer(player);
	}
	
	// Get the player.
	public Player getPlayer() {
		return player;
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
	public void fillStatColumn() {
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
		
		// Make the stat displays from them.
		CollapsibleListFrame[] statDisplays = new CollapsibleListFrame[categoryNames.size()];
		int groupWidth = statColumn.getWidth() - statColumn.getScrollBarWidth();
		int groupSubwidth = groupWidth - columnFont.getLineHeight();
		for (int q, i = 0; i < statDisplays.length; i++) {
			statDisplays[i] = new CollapsibleListFrame(categoryNames.get(i), Color.white, columnFont, 0, 0, groupWidth, 0, true);
			String[] strings = categoryContents.get(i);
			TextDisplay[] stringDisplays = new TextDisplay[strings.length];
			for (q = 0; q < strings.length; q++) {
				stringDisplays[q] = new TextDisplay(0, 0, groupSubwidth, Integer.MAX_VALUE, 0, strings[q], columnFont, Color.white);
			}
			statDisplays[i].addElements(stringDisplays);
		}
		statColumn.addElements(statDisplays);
	}
	
	// Fill a column with collapsible string groups representing a list of items.
	public void fillItemColumn(ScrollableListFrame column, ListBag<Item> bag, boolean expanded) {
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

	// Select an item display.
	// REQUIRES: group is not null
	private void selectItemDisplay(ItemDisplay group) {
		Log.getDefaultLog().info("Selecting an item display.");
		
		selectedItemDisplay = group;
		dragString = selectedItemDisplay.getItem().getName();
		dragStringColor = selectedItemDisplay.getTextColor();
		int dragStringWidth = columnFont.getWidth(dragString);
		int dragStringHeight = columnFont.getLineHeight();
		dragStringXOffset = -dragStringWidth / 2;
		dragStringYOffset = -dragStringHeight / 2;
	}
	
	// Clear the selected item display.
	private void clearSelectedItemDisplay() {
		selectedItemDisplay = null;
		dragString = null;
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
			
			// Move the item display.
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
			
			// If itemDisplay is the selected item display, select toDisplay.
			if (itemDisplay == selectedItemDisplay)
				selectItemDisplay(toDisplay);
			
			player.recalculateStats(); // Recalc the stats.
			fillStatColumn(); // Update the stat display.
		}
	}
	
	@Override
	public void renderInterior(Graphics g) {
		// Draw the top labels.
		g.setColor(tabEnabledColor);
		g.fillRect(x1, tabY2, width, font.getLineHeight());
		
		g.setColor(tabNameColor);
		g.setFont(font);
		g.drawString("Stats", statColumn.getX(), tabY2);
		g.drawString("Equipped", equippedColumn.getX(), tabY2);
		g.drawString("Inventory", inventoryColumn.getX(), tabY2);
		
		// Draw the background of the subcontext.
		g.setColor(backgroundColor);
		g.fillRect(x1, statColumn.getY(), width, statColumn.getHeight());
		
		// Draw the selection highlight.
		if (selectedItemDisplay != null) {
			g.setColor(itemDisplayHighlightColor);
			g.fillRect(selectedItemDisplay.getX(), selectedItemDisplay.getY(), selectedItemDisplay.getWidth(), selectedItemDisplay.getHeight());
		}
		
		// Draw the subcontext.
		renderSubcontext(g, x1, tabY2, x2, y2);
		
		// Draw the dragImage if we are dragging and the mouse isn't inside of the selected item display.
		if (dragging && !selectedItemDisplay.pointIsWithin(mouseX, mouseY)) {
			g.setColor(dragStringColor);
			g.drawString(dragString, mouseX + dragStringXOffset, mouseY + dragStringYOffset);
		}
	}

	// Override click so that we can select collapsible groups in the columns.
	@Override
	public void click(int x, int y, int button) {
		super.click(x, y, button);
		
		// If the click is the left or right button...
		if (button == Input.MOUSE_LEFT_BUTTON || button == Input.MOUSE_RIGHT_BUTTON) {
			// If the click was within the context menu, ignore it.
			if (contextMenu != null && !contextMenu.dead() && contextMenu.pointIsWithin(x, y)) {
				return;
			}
			
			// If the click is within the item editor, ignore the click.
			if (itemEditor != null && !itemEditor.dead() && itemEditor.pointIsWithin(x, y)) {
				return;
			}
			
			// If the click was within the equipped column, see if there is a new group to select and select it if there is.
			if (equippedColumn.pointIsWithin(x, y)) {
				Log.getDefaultLog().info("Clicked equipped column");
				GUIElement e = equippedColumn.getElementAtPoint(x, y);
				if (!(e instanceof ItemDisplay)) {
					Log.getDefaultLog().info("Null? " + e);
					clearSelectedItemDisplay();
				}
				else if (selectedItemDisplay == null || !selectedItemDisplay.equals(e)) {
					clearSelectedItemDisplay();
					selectItemDisplay((ItemDisplay)e);
				}
			}
			
			// If the click was within the equipped column, see if there is a new group to select and select it if there is.
			else if (inventoryColumn.pointIsWithin(x, y)) {
				Log.getDefaultLog().info("Clicked inventory column");
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
				// First, get the string array for the needed options.
				String[] options;
				if (selectedItemDisplay.getItem().getMaterials().length > 0) {
					options = CONTEXT_MENU_OPTIONS_ALL;
				}
				else {
					options = CONTEXT_MENU_OPTIONS_NOMATERIALS;
				}
				
				int contextMenuWidth = 0;
				for (String s : options) {
					int sWidth = GMCalc2.BODYFONT.getWidth(s);
					if (sWidth > contextMenuWidth)
						contextMenuWidth = sWidth;
				}
				
				contextMenu = new ContextMenu(x, y, contextMenuWidth, Integer.MAX_VALUE,
						columnFont, options, tabNameColor, itemDisplayHighlightColor, tabEnabledColor, tabNameColor);
				contextMenu.addListener(this);
				subcontext.addElement(contextMenu);
			}
		}
	}
	
	// Override mouseDown so we can drag collapsible groups in the columns.
	@Override
	public void mouseDown(int x, int y, int button) {
		super.mouseDown(x, y, button);
		
		// Drag if we are holding the left button, have a drag image and we are on top of selectedItemDisplay.
		if (button == Input.MOUSE_LEFT_BUTTON && dragString != null && selectedItemDisplay.pointIsWithin(x, y))
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
		if (dragString != null) {
			dragging = false;
			clearSelectedItemDisplay();
		}
	}
	
	// Override mouseDownElsewhere so we can stop dragging collapsible groups in the columns.
	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
		super.mouseDownElsewhere(target, button);
		
		// Don't drag if we're doing stuff elsewhere.
		if (dragString != null) {
			dragging = false;
			clearSelectedItemDisplay();
		}
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// Events that come from the context menu are a notification to stop listening to it.
		// They also tell this what to do.
		if (event.getGenerator().equals(contextMenu)) {
			// Stop listening to the context menu.
			contextMenu.removeListener(this);
			contextMenu = null;
			
			Object eventData = event.getData();
			// If the player wants to edit something about an item...
			if (eventData.equals(CONTEXT_MENU_EDIT_PREFIXES) ||
					eventData.equals(CONTEXT_MENU_EDIT_MATERIALS) ||
					eventData.equals(CONTEXT_MENU_EDIT_ITEMBASE)) {
				
				// Remove the old item editor.
				if (itemEditor != null && !itemEditor.dead()) {
					subcontext.removeElement(itemEditor);
				}
				
				// Get the dimensions for the new editor.
				int iEW = width / 2;
				int iEH = height / 2;
				int iEX = x1 + width / 2 - iEW / 2; 
				int iEY = y1 + height / 2 - iEH / 2;
				
				// Make the appropriate editor.
				if (eventData.equals(CONTEXT_MENU_EDIT_PREFIXES)) {
					itemEditor = new ItemPrefixEditor(iEX, iEY, iEW, iEH, Integer.MAX_VALUE, selectedItemDisplay, this,
							columnFont, tabNameColor, backgroundColor, tabEnabledColor, tabNameColor, itemDisplayHighlightColor);
				}
				else if (eventData.equals(CONTEXT_MENU_EDIT_MATERIALS)) {
					itemEditor = new ItemMaterialEditor(iEX, iEY, iEW, iEH, Integer.MAX_VALUE, selectedItemDisplay, this,
							columnFont, tabNameColor, backgroundColor, tabEnabledColor, tabNameColor, itemDisplayHighlightColor);
				}
				else {
					itemEditor = new ItemItemBaseEditor(iEX, iEY, iEW, iEH, Integer.MAX_VALUE, selectedItemDisplay, this,
							columnFont, tabNameColor, backgroundColor, tabEnabledColor, tabNameColor, itemDisplayHighlightColor);
				}
				subcontext.addElement(itemEditor);
			}
			// If the player wants to adjust the quantity...
			else if (event.getData().equals(CONTEXT_MENU_ADJUST_QUANTITY)) {
				// TODO
			}
			// If the player wants to delete the stack, delete it.
			else if (event.getData().equals(CONTEXT_MENU_DELETE_STACK)) {
				selectedItemDisplay.decreaseQuantity(selectedItemDisplay.getQuantity());
				clearSelectedItemDisplay();
				player.recalculateStats();
				fillStatColumn();
			}
		}
	}
	
}
