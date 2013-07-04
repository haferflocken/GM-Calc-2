package org.gmcalc2;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import org.gmcalc2.gui.*;

import org.haferlib.slick.gui.*;

public class GMCalc2 extends BasicGame {
	
	public static Font FONT;
	public static int FONT_HEIGHT;
	
	private GUIContext ui;
	
	public GMCalc2() {
		super("GM Calc 2");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		//Create the font.
		FONT = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18), false);
		FONT_HEIGHT = FONT.getLineHeight();
		
		//Create the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		Tab tab = new Tab("Tab Test 1", 0, 0, container.getWidth(), container.getHeight(), 0, 128, FONT);
		tab.disable();
		ui.addElement(tab);
		tab = new Tab("Tab Test 2", 0, 0, container.getWidth(), container.getHeight(), 128, 128, FONT);
		tab.disable();
		ui.addElement(tab);
		ui.addElement(new Tab("Tab Test 3", 0, 0, container.getWidth(), container.getHeight(), 256, 128, FONT));		
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
		AppGameContainer gc = new AppGameContainer(new GMCalc2(), 1280, 768, false);
		gc.start();
	}

}
