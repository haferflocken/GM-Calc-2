package org.gmcalc2.state;

import java.io.IOException;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.World;
import org.gmcalc2.factory.WorldFactory;

import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.OutputFrame;
import org.haferlib.util.DataReader;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LoadingState extends BasicGameState {
	
	public static final int ID = 0;
	
	// Instance fields.
	private String worldsFolder;
	private GUIContext ui;
	private OutputFrame out;
	private WorldFactory worldFactory;
	
	// Constructors.
	public LoadingState(String worldsFolder) {
		this.worldsFolder = worldsFolder;
	}
	
	// Check if we are done loading.
	public boolean isFinished() {
		return worldFactory.isFinished();
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// Initialize the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		out = new OutputFrame(0, 0, container.getWidth(), container.getHeight(), Integer.MIN_VALUE, GMCalc2.BODYFONT, Color.white, 10, Color.white);
		ui.addElement(out);
		
		// Create the world factory.
		try {
			worldFactory = new WorldFactory(new DataReader());
			worldFactory.setOutputFrame(out);
			worldFactory.setDirectory(worldsFolder);
		}
		catch (IOException e) {
			ui.destroy();
			throw new SlickException("Failed to create world factory.");
		}
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		// Load the next thing.
		if (!worldFactory.isFinished())
			worldFactory.loadNext();
		// If we are done loading, tell the game to go to the tab state.
		else 
			game.enterState(TabState.ID);
		
		// Update the ui.
		ui.update(container.getInput(), delta);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		// Render the ui.
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return ID;
	}

}
