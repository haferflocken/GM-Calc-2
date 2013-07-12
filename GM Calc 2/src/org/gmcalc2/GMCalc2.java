package org.gmcalc2;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import org.gmcalc2.gui.*;
import org.gmcalc2.item.Player;

import org.haferlib.slick.gui.*;

public class GMCalc2 extends BasicGame {
	
	public static Font HEADERFONT;
	public static int HEADERFONT_HEIGHT;
	public static Font BODYFONT;
	public static int BODYFONT_HEIGHT;
	
	private GUIContext ui;
	
	public GMCalc2() {
		super("GM Calc 2");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		//Create the fonts.
		HEADERFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18), false);
		HEADERFONT_HEIGHT = HEADERFONT.getLineHeight();
		BODYFONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14), false);
		BODYFONT_HEIGHT = BODYFONT.getLineHeight();
		
		//Create the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
	
		//Create the world.
		World worldTest = new World("E:\\John\\Google Drive\\gmcalc2 worlds\\forgottenrealms\\");
		Player playerTest = worldTest.getPlayer("playerTest.txt");
		PlayerTab tab = new PlayerTab(playerTest, 0, 0, container.getWidth(), container.getHeight(), 0, 128, HEADERFONT, BODYFONT);
		ui.addElement(tab);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer gc = new AppGameContainer(new GMCalc2(), 1280, 600, false);
		gc.setShowFPS(false);
		gc.start();
	}

}
