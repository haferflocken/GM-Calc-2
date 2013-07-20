package org.gmcalc2.state;

import java.util.Map;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.gui.PlayerTab;
import org.gmcalc2.item.Player;
import org.gmcalc2.World;

import org.haferlib.slick.gui.GUIContext;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TabState extends BasicGameState {
	
	public static final int ID = 1;
	
	private GMCalc2 gmcalc2;
	private GUIContext ui; //The UI.
	
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
		
		// Fill the UI with player tabs!
		// TODO
		// For now, just add players from forgotten realms.
		World world = gmcalc2.getWorlds().get("forgottenrealms");
		Map<String, Player> players = world.getPlayerMap();
		for (Map.Entry<String, Player> entry : players.entrySet()) {
			Player player = entry.getValue();
			PlayerTab tab = new PlayerTab(player, 0, 0, container.getWidth(), container.getHeight(), 0, 128, GMCalc2.HEADERFONT, GMCalc2.BODYFONT, Color.gray, Color.darkGray, Color.white, Color.darkGray);
			ui.addElement(tab);
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

}
