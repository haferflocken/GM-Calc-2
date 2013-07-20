package org.gmcalc2.state;

import org.haferlib.slick.gui.GUIContext;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TabState extends BasicGameState {
	
	public static final int ID = 1;
	
	private GUIContext ui;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
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
