package org.gmcalc2;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;
import org.gmcalc2.state.*;
import org.haferlib.slick.gui.GUIContext;

import java.util.Map;

public class GMCalc2 extends StateBasedGame {
	
	// Static resources.
	public static Font HEADERFONT;
	public static int HEADERFONT_HEIGHT;
	public static Font BODYFONT;
	public static int BODYFONT_HEIGHT;
	
	private static boolean STATICS_CREATED = false;
	
	// Instance fields.
	private String worldsFolder;
	private Map<String, World> worlds;
	
	// Constructor.
	public GMCalc2() {
		super("GM Calc 2");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// Load statics if necessary.
		if (!STATICS_CREATED) {
			// Create the fonts.
			HEADERFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16), false);
			HEADERFONT_HEIGHT = HEADERFONT.getLineHeight();
			BODYFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14), false);
			BODYFONT_HEIGHT = BODYFONT.getLineHeight();
			
			// Mark the statics as created.
			STATICS_CREATED = true;
		}
		
		// Make the states.
		LoadingState loadingState = new LoadingState(this);
		SetupState setupState = new SetupState(this);
		TabState tabState = new TabState(this);
		
		// Add the states.
		addState(setupState);
		addState(loadingState);
		addState(tabState);
	}
	
	// Set the worlds folder.
	public void setWorldsFolder(String wF) {
		worldsFolder = wF;
	}
	
	// Set the worlds.
	public void setWorlds(Map<String, World> w) {
		worlds = w;
	}
	
	// Get the worlds folder.
	public String getWorldsFolder() {
		return worldsFolder;
	}
	
	// Get the worlds.
	public Map<String, World> getWorlds() {
		return worlds;
	}
	
	// Override the main update so that GUIContext.debugMode can be toggled at any time.
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		
		if (container.getInput().isKeyPressed(Input.KEY_GRAVE)) {
			GUIContext.debugMode++;
			GUIContext.debugMode %= GUIContext.NUM_DEBUG_MODES;
		}
	}
	
	// The main method.
	public static void main(String[] args) throws SlickException {
		AppGameContainer gc = new AppGameContainer(new GMCalc2(), 1280, 600, false);
		gc.setShowFPS(false);
		gc.start();
	}

}
