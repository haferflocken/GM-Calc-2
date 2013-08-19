package org.gmcalc2.gui;

import java.util.LinkedHashSet;
import java.util.Set;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.Player;
import org.haferlib.slick.gui.GUISubcontext;
import org.haferlib.util.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TabContainer extends GUISubcontext {

	private Set<Tab> tabs;	// The tabs in this container.
	
	// The color scheme.
	private Color textColor, highlightColor, backgroundColor, selectColor;
	
	// Constructor.
	public TabContainer(int x, int y, int width, int height, int depth,
			Color textColor, Color highlightColor, Color backgroundColor, Color selectColor) {
		super(x, y, width, height, depth);
		
		this.textColor = textColor;
		this.highlightColor = highlightColor;
		this.backgroundColor = backgroundColor;
		this.selectColor = selectColor;
		
		tabs = new LinkedHashSet<>();
	}
	

	// EFFECTS:  Sets the currently enabled tab by name.
	//			 Returns true if successful, false if not.
	public boolean setEnabledTabById(String tabId) {
		boolean result = false; // Did we find and enable a tab?
		
		// Loop through the tabs, looking for one with the given name.
		for (Tab t : tabs) {
			if (t.getId().equals(tabId)) {
				t.enable();
				result = true;
			}
			else {
				t.disable();
			}
		}
		
		// Return the result.
		return result;
	}
	
	// EFFECTS:  Sets the currently enabled tab, adding it
	//			 to the tabs if it is not already there.
	//			 Returns true if successful, false if not.
	public boolean setEnabledTab(Tab tab) {
		// Can't enable a null tab.
		if (tab == null)
			return false;
		
		// Disable all tabs besides the given one.
		for (Tab t : tabs) {
			if (!tab.equals(t)) {
				t.disable();
			}
		}
		
		// Try and add the tab. If we already have it, nothing will happen.
		addTab(tab);
		
		// Enable the given tab.
		tab.enable();
		
		// Return true.
		return true;
	}
	
	// EFFECTS:  Add a tab to this.
	//			 Return true if successful, false otherwise.
	public boolean addTab(Tab tab) {
		// Return false if the tab is null or we already have it.
		if (tab == null)
			return false;
		if (tabs.contains(tab))
			return false;
		
		// Otherwise, add the tab and set its tabX appropriately.
		int newTabX = x1;
		for (Tab t : tabs) {
			int tabX2 = t.getTabX() + t.getTabWidth();
			if (tabX2 > newTabX)
				newTabX = tabX2;
		}
		
		tab.setTabX(newTabX);
		tabs.add(tab);
		subcontext.addElement(tab);
		return true;
	}
	
	// Make and add a player tab.
	public void addTabForPlayer(Player player) {
		PlayerTab tab = new PlayerTab(player, x1, y1, width, height, 0, GMCalc2.HEADERFONT, GMCalc2.BODYFONT,
				highlightColor, backgroundColor, textColor, backgroundColor, selectColor);
		tab.disable();
		addTab(tab);
	}
	
	// Make and add a component tab.
	public void addTabForComponent(Component component) {
		Log.getDefaultLog().debug("TabContainer.addTabForComponent(Component) not yet implemented.");
	}
	
	// Make and add a item base tab.
	public void addTabForItemBase(ItemBase itemBase) {
		Log.getDefaultLog().debug("TabContainer.addTabForItemBase(ItemBase) not yet implemented.");
	}

	@Override
	public void render(Graphics g) {
		renderSubcontext(g, x1, y1, x2, y2);
	}

}
