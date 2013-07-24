package org.gmcalc2.state;

import java.util.Map;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.gui.PlayerTab;
import org.gmcalc2.gui.WorldExplorerTab;
import org.gmcalc2.item.Player;
import org.gmcalc2.World;

import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIContext;
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
	
	// Constructor.
	public TabState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		// Create the UI.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		// Create the hidden button that opens the world explorer.
		behindTabButton = new Button<Object>("", Color.black, GMCalc2.BODYFONT, 0, 0, container.getWidth(), container.getHeight(), Integer.MIN_VALUE, Color.black, Color.black, Input.KEY_ESCAPE);
		behindTabButton.addListener(this);
		ui.addElement(behindTabButton);
		
		// Create the world explorer tab. 
		String wETitle = "World Explorer";
		worldExplorer = new WorldExplorerTab(wETitle, gmcalc2.getWorlds(), 0, 0, container.getWidth(), container.getHeight(), 0, 128,
				GMCalc2.HEADERFONT, GMCalc2.BODYFONT, Color.gray, Color.darkGray, Color.white, Color.darkGray);
		ui.addElement(worldExplorer);
		
		// Fill the UI with player tabs!
		// For now, just add players from forgotten realms.
		/*World world = gmcalc2.getWorlds().get("forgottenrealms");
		Map<String, Player> players = world.getPlayerMap();
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player player = entry.getValue();
			PlayerTab tab = new PlayerTab(player, 0, 0, container.getWidth(), container.getHeight(), 0, 128, GMCalc2.HEADERFONT, GMCalc2.BODYFONT, Color.gray, Color.darkGray, Color.white, Color.darkGray);
			ui.addElement(tab);
		}*/
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
			// TODO
			System.out.println("Click detected!");
		}
	}

}
