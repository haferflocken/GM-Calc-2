package org.gmcalc2.state;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.gui.PlayerTab;
import org.gmcalc2.gui.Tab;
import org.gmcalc2.gui.WorldExplorerTab;
import org.gmcalc2.item.Player;
import org.gmcalc2.World;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventListener;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TabState extends BasicGameState implements GUIEventListener {
	
	public static final int ID = 1;
	
	private GMCalc2 gmcalc2;
	private GUIContext ui; // The UI.
	private Button<?> behindTabButton; // The button that detects off-tab clicks.
	private WorldExplorerTab worldExplorer; // The world explorer.
	private LinkedHashSet<Tab> tabs; // The tabs in this state.
	
	// Constructor.
	public TabState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}
	
	// EFFECTS:  Sets the currently enabled tab by name.
	//			 Returns true if successful, false if not.
	public boolean setEnabledTabByName(String tabName) {
		boolean result = false; // Did we find and enable a tab?
		
		// Loop through the tabs, looking for one with the given name.
		for (Tab t : tabs) {
			if (t.getTabName().equals(tabName)) {
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
		int lastTabX = -128;
		for (Tab t : tabs) {
			if (t.getTabX() > lastTabX)
				lastTabX = t.getTabX();
		}
		
		tab.setTabX(lastTabX + 128);
		tabs.add(tab);
		ui.addElement(tab);
		return true;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		// Create the UI.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		tabs = new LinkedHashSet<>();
		
		// Create the hidden button that opens the world explorer.
		behindTabButton = new Button<Object>("", Color.black, GMCalc2.BODYFONT, 0, 0, container.getWidth(), container.getHeight(), Integer.MIN_VALUE, Color.black, Color.black, Input.KEY_ESCAPE);
		behindTabButton.addListener(this);
		ui.addElement(behindTabButton);
		
		// Create the world explorer tab. 
		String wETitle = "World Explorer";
		worldExplorer = new WorldExplorerTab(wETitle, 0, 0, container.getWidth(), container.getHeight(), 0, 128,
				GMCalc2.HEADERFONT, Color.gray, Color.darkGray, Color.white,
				this, gmcalc2.getWorlds(), Color.darkGray, Color.white, GMCalc2.BODYFONT, Color.gray);
		addTab(worldExplorer);
		
		// Fill the UI with player tabs!
		// For now, just add players from forgotten realms.
		World world = gmcalc2.getWorlds().get("forgottenrealms");
		Map<String, Player> players = world.getPlayerMap();
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player player = entry.getValue();
			PlayerTab tab = new PlayerTab(player, 0, 0, container.getWidth(), container.getHeight(), 0, 128, GMCalc2.HEADERFONT, GMCalc2.BODYFONT, Color.gray, Color.darkGray, Color.white, Color.darkGray);
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
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the behind tab button was clicked, go to the world explorer.
		if (event.getGenerator().equals(behindTabButton)) {
			setEnabledTab(worldExplorer);
		}
	}

}
