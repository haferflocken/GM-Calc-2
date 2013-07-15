package org.gmcalc2.gui;

import org.gmcalc2.item.Player;

import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.ScrollableListFrame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import java.util.ArrayList;
import java.util.TreeMap;

public class PlayerTab extends Tab {
	
	private Font columnFont;
	private Player player;
	private ScrollableListFrame statColumn, equippedColumn, inventoryColumn;

	public PlayerTab(Player player, int x, int y, int width, int height, int tabX, int tabWidth, Font font, Font columnFont) {
		super(player.getName(), x, y, width, height, tabX, tabWidth, font);
		this.columnFont = columnFont;
		setPlayer(player);
	}
	
	public void setPlayer(Player p) {
		//Set the player.
		player = p;
		
		//Add the columns from right to left so the scroll bars line up nicely with the right side.
		int columnWidth = width / 3;
		int columnHeight = height - font.getLineHeight();
		int inventoryColumnX = x2 - columnWidth;
		inventoryColumn = new ScrollableListFrame(inventoryColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(inventoryColumn);
		int equippedColumnX = inventoryColumnX - columnWidth;
		equippedColumn = new ScrollableListFrame(equippedColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(equippedColumn);
		int statColumnX = equippedColumnX - columnWidth;
		statColumn = new ScrollableListFrame(statColumnX, tabY2, columnWidth, columnHeight, 0, 8, Color.white);
		subcontext.addElement(statColumn);
		
		//Fill up the columns.
		fillStatColumn();
		fillItemColumn(equippedColumn, player.getEquipped());
		fillItemColumn(inventoryColumn, player.getInventory());
	}
	
	private void fillStatColumn() {
		statColumn.clearElements();
		
		//TODO: Sort the stats into categories.
		TreeMap<String, String[]> categoryRules = player.getWorld().getPlayerStatCategories();
		TreeMap<String, String[]> categories = new TreeMap<>();
		
		CollapsibleStringGroup statDisplay = new CollapsibleStringGroup(statColumn, "Other", player.getStatMap().toDisplayStrings(), Color.white, 0, 0, statColumn.getWidth(), columnFont, true);
		statColumn.addElement(statDisplay);
	}
	
	private void fillItemColumn(ScrollableListFrame column, ArrayList<Player.QuantityItem> items) {
		column.clearElements();
		
		GUIElement[] elements = new GUIElement[items.size()];
		for (int i = 0; i < items.size(); i++) {
			CollapsibleStringGroup itemDisplay = new CollapsibleStringGroup(column, items.get(i), player.getWorld().getRarityColor(items.get(i).getItem()), 0, 0, column.getWidth(), columnFont, true);
			elements[i] = itemDisplay;
		}
		column.addElements(elements);
	}
	
}
