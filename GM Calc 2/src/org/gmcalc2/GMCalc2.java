package org.gmcalc2;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;
import org.gmcalc2.state.*;

import java.util.Map;

public class GMCalc2 extends StateBasedGame {
	
	//Static resources.
	public static Font HEADERFONT;
	public static int HEADERFONT_HEIGHT;
	public static Font BODYFONT;
	public static int BODYFONT_HEIGHT;
	
	public static Color TAB_ENABLED_COLOR, TAB_DISABLED_COLOR, TAB_NAME_COLOR, PLAYERTAB_BACKGROUND_COLOR;
	
	private static boolean STATICS_CREATED = false;
	
	//Instance fields.
	private Map<String, World> worlds;
	
	//Constructor.
	public GMCalc2() {
		super("GM Calc 2");
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		//Load statics if necessary.
		if (!STATICS_CREATED) {
			//Create the fonts.
			HEADERFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16), false);
			HEADERFONT_HEIGHT = HEADERFONT.getLineHeight();
			BODYFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14), false);
			BODYFONT_HEIGHT = BODYFONT.getLineHeight();
			
			//Define the colors.
			TAB_ENABLED_COLOR = Color.gray;
			TAB_DISABLED_COLOR = Color.darkGray;
			TAB_NAME_COLOR = Color.white;
			PLAYERTAB_BACKGROUND_COLOR = Color.darkGray;
			
			//Mark the statics as created.
			STATICS_CREATED = true;
		}
		
		// Make the loading state.
		LoadingState loadingState = new LoadingState("C:\\Users\\John\\Google Drive\\gmcalc2 worlds\\");
		addState(loadingState);
		
		// Make the tab state.
		addState(new TabState());
		
		//Make a player tab from the forgotten realms world.
		/*for (World w : worlds) {
			if (w.getName().equals("Forgotten Realms")) {
				Player playerTest = w.getPlayer("playerTest.txt");
				PlayerTab tab = new PlayerTab(playerTest, 0, 0, container.getWidth(), container.getHeight(), 0, 128, HEADERFONT, BODYFONT, TAB_ENABLED_COLOR, TAB_DISABLED_COLOR, TAB_NAME_COLOR, PLAYERTAB_BACKGROUND_COLOR);
				ui.addElement(tab);
				break;
			}
		}*/
	}
	
	//The main method.
	public static void main(String[] args) throws SlickException {
		AppGameContainer gc = new AppGameContainer(new GMCalc2(), 1280, 600, false);
		gc.setShowFPS(false);
		gc.start();
	}

}
