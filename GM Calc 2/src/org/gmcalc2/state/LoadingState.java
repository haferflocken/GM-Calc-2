package org.gmcalc2.state;

import java.io.IOException;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.factory.WorldFactory;

import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.ImageFrame;
import org.haferlib.slick.gui.OutputFrame;
import org.haferlib.util.DataReader;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LoadingState extends BasicGameState {
	
	public static final int ID = 0;
	
	// Instance fields.
	private GMCalc2 gmcalc2;
	private String worldsFolder;
	private GUIContext ui;
	private OutputFrame out;
	private WorldFactory worldFactory;
	
	// Constructors.
	public LoadingState(GMCalc2 gmcalc2, String worldsFolder) {
		this.gmcalc2 = gmcalc2;
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
		
		// Make the banner.
		Image bannerImage = Image.createOffscreenImage(container.getWidth(), GMCalc2.HEADERFONT_HEIGHT * 2);
		Graphics bannerG = bannerImage.getGraphics();
		bannerG.setFont(GMCalc2.HEADERFONT);
		bannerG.setColor(Color.white);
		String bannerText = "GMCalc2: Loading worlds...";
		int drawX = container.getWidth() / 2 - GMCalc2.HEADERFONT.getWidth(bannerText) / 2;
		int drawY = GMCalc2.HEADERFONT_HEIGHT / 2;
		bannerG.drawString(bannerText, drawX, drawY);
		bannerG.flush();
		bannerG.destroy();
		ImageFrame banner = new ImageFrame(bannerImage, 0, 0, bannerImage.getWidth(), bannerImage.getHeight(), 0);
		ui.addElement(banner);
		
		// Make the output frame.
		int outY = banner.getHeight();
		int outHeight = container.getHeight() - outY;
		out = new OutputFrame(0, outY, container.getWidth(), outHeight, Integer.MIN_VALUE, GMCalc2.BODYFONT, Color.white, 10, Color.white);
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
	public void leave(GameContainer container, StateBasedGame game) {
		// Destroy the UI when we leave. We never come back so it isn't really needed.
		ui.destroy();
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		// Load the next thing.
		if (!worldFactory.isFinished())
			worldFactory.loadNext();
		// If we are done loading, tell the game to go to the tab state.
		else {
			gmcalc2.setWorlds(worldFactory.getLoadedValues());
			game.enterState(TabState.ID);
		}
		
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
