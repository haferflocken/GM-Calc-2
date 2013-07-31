package org.gmcalc2.state;

import java.util.LinkedHashSet;
import java.util.Map;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.gui.PlayerTab;
import org.gmcalc2.gui.Tab;
import org.gmcalc2.gui.WorldExplorer;
import org.gmcalc2.item.Player;
import org.gmcalc2.World;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.ListFrame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TabState extends BasicGameState implements GUIEventListener {
	
	private static final int BORDER_THICKNESS = 6;		// The thickness of the border around elements.
	
	public static final int ID = 1;
	
	private GMCalc2 gmcalc2;
	private GUIContext ui;								// The UI.
	private ListFrame toolbar;							// The toolbar on the left side of the window.
	private Button<WorldExplorer> worldExplorerButton;	// The button in the toolbar that opens the world explorer.
	private WorldExplorer worldExplorer;				// The world explorer.
	private LinkedHashSet<Tab> tabs;					// The tabs in this state.
	private int tabAreaLeftX;							// The left X of the tab area.
	private int borderedHeight;							// The height of the window minus BORDER_THICKNESS * 2.
	
	// Color scheme.
	private Color elementTextColor, elementHighlightColor, elementBackgroundColor;
	
	// Constructor.
	public TabState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
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
		int newTabX = tabAreaLeftX;
		for (Tab t : tabs) {
			int tabX2 = t.getTabX() + t.getTabWidth();
			if (tabX2 > newTabX)
				newTabX = tabX2;
		}
		
		tab.setTabX(newTabX);
		tabs.add(tab);
		ui.addElement(tab);
		return true;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// Make colors.
		elementTextColor = Color.white;
		elementHighlightColor = Color.gray;
		elementBackgroundColor = Color.darkGray;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {		
		// Create the UI.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		tabs = new LinkedHashSet<>();
		borderedHeight = container.getHeight() - BORDER_THICKNESS * 2;
		
		// Create the world explorer. 
		worldExplorer = new WorldExplorer(0, 0, 256, container.getHeight(), Integer.MAX_VALUE,
				this, gmcalc2.getWorlds(), elementBackgroundColor, elementTextColor, GMCalc2.BODYFONT, elementHighlightColor);
		
		// Create the toolbar.
		int toolbarX = BORDER_THICKNESS;
		int toolbarY = BORDER_THICKNESS;
		int toolbarWidth = 24;
		toolbar = new ListFrame(toolbarX, toolbarY, toolbarWidth, 100);
		Button<WorldExplorer> worldExplorerButton = new Button<>("WE", worldExplorer, elementTextColor, GMCalc2.BODYFONT, Button.CENTER, 0, toolbarX, toolbarY, toolbarWidth, toolbarWidth,
				0, null, elementHighlightColor, Input.KEY_ENTER);
		worldExplorerButton.addListener(this);
		toolbar.addElement(worldExplorerButton);
		ui.addElement(toolbar);
		
		// Fill the UI with player tabs!
		// For now, just add players from forgotten realms.
		World world = gmcalc2.getWorlds().get("forgottenrealms");
		Map<String, Player> players = world.getPlayerMap();
		tabAreaLeftX = toolbarX + toolbarWidth + BORDER_THICKNESS;
		int tabY = BORDER_THICKNESS;
		int tabWidth = container.getWidth() - tabAreaLeftX - BORDER_THICKNESS;
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player player = entry.getValue();
			PlayerTab tab = new PlayerTab(player, tabAreaLeftX, tabY, tabWidth, borderedHeight, 0, 128,
					GMCalc2.HEADERFONT, GMCalc2.BODYFONT, elementHighlightColor, elementBackgroundColor, elementTextColor, elementBackgroundColor);
			tab.disable();
			addTab(tab);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Draw the toolbar background.
		g.setColor(elementBackgroundColor);
		g.fillRect(toolbar.getX(), toolbar.getY(), toolbar.getWidth(), borderedHeight);
		
		// Draw the ui.
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the world explorer button was pushed, toggle the world explorer.
		if (event.getGenerator() == worldExplorerButton) {
			if (ui.contains(worldExplorer))
				ui.removeElement(worldExplorer);
			else
				ui.addElement(worldExplorer);
		}
	}

}
