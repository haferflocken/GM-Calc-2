package org.gmcalc2;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.gmcalc2.gui.*;
import org.gmcalc2.item.Player;
import org.haferlib.slick.gui.*;

import java.io.File;
import java.util.ArrayList;

public class GMCalc2 extends BasicGame {
	
	//Static resources.
	public static Font HEADERFONT;
	public static int HEADERFONT_HEIGHT;
	public static Font BODYFONT;
	public static int BODYFONT_HEIGHT;
	
	public static Color TAB_ENABLED_COLOR, TAB_DISABLED_COLOR, TAB_NAME_COLOR, PLAYERTAB_BACKGROUND_COLOR;
	public static OutputFrame out;
	
	private static boolean STATICS_CREATED = false;
	
	//Instance fields.
	private GUIContext ui;
	private ArrayList<World> worlds;
	
	//Constructor.
	public GMCalc2() {
		super("GM Calc 2");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
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
		
		//Create the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		out = new OutputFrame(0, 0, container.getWidth(), container.getHeight(), Integer.MIN_VALUE, BODYFONT, Color.white, 10, Color.white);
		ui.addElement(out);
		
		//Load all the worlds.
		loadWorlds("E:\\John\\Google Drive\\gmcalc2 worlds\\");

		//Make a player tab from the forgotten realms world.
		for (World w : worlds) {
			if (w.getName().equals("Forgotten Realms")) {
				Player playerTest = w.getPlayer("playerTest.txt");
				PlayerTab tab = new PlayerTab(playerTest, 0, 0, container.getWidth(), container.getHeight(), 0, 128, HEADERFONT, BODYFONT, TAB_ENABLED_COLOR, TAB_DISABLED_COLOR, TAB_NAME_COLOR, PLAYERTAB_BACKGROUND_COLOR);
				ui.addElement(tab);
				break;
			}
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}
	
	//Load the worlds in a directory.
	private void loadWorlds(String worldDirPath) {
		worlds = new ArrayList<>(); //Create a new world list.
		
		//Make sure the given path is a directory.
		File worldsFolder = new File(worldDirPath);
		if (!worldsFolder.isDirectory())
			return;
		
		//Look at the files in the worlds folder and load the ones that are folders as worlds.
		File[] worldFolders = worldsFolder.listFiles();
		for (File f : worldFolders) {
			if (f.isDirectory()) {
				String worldPath = f.getAbsolutePath();
				if (worldPath.charAt(worldPath.length() - 1) != '\\')
					worldPath += '\\';
				World world = new World(worldPath);
				worlds.add(world);
			}
		}
	}
	
	//The main method.
	public static void main(String[] args) throws SlickException {
		AppGameContainer gc = new AppGameContainer(new GMCalc2(), 1280, 600, false);
		gc.setShowFPS(false);
		gc.start();
	}

}
