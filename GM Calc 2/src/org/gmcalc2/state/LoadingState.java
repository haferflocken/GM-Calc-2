package org.gmcalc2.state;

import java.io.IOException;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.factory.WorldFactory;
import org.haferslick.gui.GUIContext;
import org.haferslick.gui.ImageFrame;
import org.haferslick.gui.OutputFrame;
import org.haferutil.DataReader;
import org.haferutil.Log;
import org.hafermath.expression.ExpressionBuilder;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state loads the worlds and then switches to the TabState.
 * 
 * @author John Werner
 *
 */

public class LoadingState extends BasicGameState {
	
	public static final int ID = 1;
	
	private static final int BORDER_THICKNESS = 6; // The thickness of the border around elements.
	
	// Instance fields.
	private final GMCalc2 gmcalc2;				// The main app, kept track of to tell it to switch states.
	private GUIContext ui;						// The UI.
	private OutputFrame out;					// The output frame.
	private WorldFactory worldFactory;			// The factory that makes the worlds.
	private int loadCount;						// The number of times loadNext() has been called.
	private double loadProgress;				// The progress of loading as a percentage.
	private int loadBarX, loadBarY;				// The position of the load bar.
	private int loadBarMaxWidth, loadBarHeight;	// The dimensions of the load bar.
	private int loadBarProgressWidth;			// The width of the load bar's progress.
	private int loadBarPendingX;				// The x of the pending part of the load bar.
	private int loadBarPendingWidth;			// The width of the pending part of the load bar.
	
	// Color scheme.
	private Color elementTextColor, elementBackgroundColor, loadBarColor;
	
	// Constructors.
	public LoadingState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		elementTextColor = Color.white;
		elementBackgroundColor = Color.darkGray;
		loadBarColor = Color.cyan;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {		
		// Initialize the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		// Calculate a width with borders.
		int borderedWidth = container.getWidth() - BORDER_THICKNESS * 2;
				
		// Make the banner image and get its graphics.
		Image bannerImage = Image.createOffscreenImage(borderedWidth, GMCalc2.HEADERFONT_HEIGHT * 2);
		Graphics bannerG = bannerImage.getGraphics();
		
		// Fill the banner's background with elementBackgroundColor.
		// bannerG.setColor(elementBackgroundColor);
		// bannerG.fillRect(0, 0, bannerImage.getWidth(), bannerImage.getHeight());
		
		// Draw the banner's text.
		bannerG.setFont(GMCalc2.HEADERFONT);
		bannerG.setColor(elementTextColor);
		String bannerText = "GMCalc2: Loading worlds...";
		int drawX = bannerImage.getWidth() / 2 - GMCalc2.HEADERFONT.getWidth(bannerText) / 2;
		int drawY = bannerImage.getHeight() / 4;
		bannerG.drawString(bannerText, drawX, drawY);
		bannerG.flush();
		bannerG.destroy();
		
		// Make the ImageFrame for the banner.
		int bannerX = BORDER_THICKNESS;
		int bannerY = 0;
		int bannerWidth = borderedWidth;
		int bannerHeight = bannerImage.getHeight();
		ImageFrame banner = new ImageFrame(bannerImage, bannerX, bannerY, bannerWidth, bannerHeight, 0);
		ui.addElement(banner);

		// Make the load bar.
		loadBarX = BORDER_THICKNESS;
		loadBarY = banner.getY() + banner.getHeight();
		loadBarMaxWidth = borderedWidth;
		loadBarHeight = 12;
		
		// Make the output frame.
		int outX = BORDER_THICKNESS;
		int outY = loadBarY + loadBarHeight + BORDER_THICKNESS;
		int outWidth = borderedWidth;
		int outHeight = container.getHeight() - outY - BORDER_THICKNESS;
		out = new OutputFrame(outX, outY, outWidth, outHeight, Integer.MIN_VALUE, GMCalc2.BODYFONT,
				elementTextColor, 10, elementTextColor);
		ui.addElement(out);
		
		// Dump the existing log contents to out.
		out.append(Log.getDefaultLog().getContents());
		
		// Make out observe the log.
		Log.getDefaultLog().addObserver(out);
		
		// Get the worlds folder from gmcalc2.
		String worldsFolder = gmcalc2.getWorldsFolder();

		// Create the world factory.
		loadCount = 0;
		try {
			worldFactory = new WorldFactory(new DataReader(), new ExpressionBuilder());
			worldFactory.setDirectory(worldsFolder);
		}
		// If the world factory fails, return to the setup state.
		catch (IOException e) {
			worldFactory = null;
			gmcalc2.enterState(SetupState.ID);
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		// Make the output frame stop observing the log.
		Log.getDefaultLog().removeObserver(out);
		
		// Destroy the UI when we leave. We never come back so it isn't really needed.
		ui.destroy();
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		// Load the next thing.
		if (!worldFactory.isFinished()) {
			worldFactory.loadNext();
			loadCount++;
			loadProgress = ((double)loadCount) / ((double)worldFactory.getDirSize());
			loadBarProgressWidth = (int)(loadProgress * loadBarMaxWidth);
			loadBarPendingX = loadBarX + loadBarProgressWidth;
			loadBarPendingWidth = loadBarMaxWidth - loadBarProgressWidth;
		}
		
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
		// Draw the background of the output frame.
		g.setColor(elementBackgroundColor);
		g.fillRect(out.getX(), out.getY(), out.getWidth(), out.getHeight());
		
		// Render the ui.
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
		
		// Render the loading progress bar.
		g.setColor(elementBackgroundColor);
		g.fillRect(loadBarPendingX, loadBarY, loadBarPendingWidth, loadBarHeight);
		g.setColor(loadBarColor);
		g.fillRect(loadBarX, loadBarY, loadBarProgressWidth, loadBarHeight);
	}

	@Override
	public int getID() {
		return ID;
	}

}
