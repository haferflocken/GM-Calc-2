package org.gmcalc2.state;

import java.io.File;

import org.gmcalc2.GMCalc2;
import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.TextDisplay;
import org.haferlib.slick.gui.TextField;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The setup state is where the user tells the program where the worlds are.
 * 
 * @author John Werner
 *
 */

public class SetupState extends BasicGameState {
	
	public static final int ID = 0;
	
	private final GMCalc2 gmcalc2;
	private GUIContext ui;
	private TextField pathInput;
	
	private Color elementTextColor, elementBackgroundColor, elementSelectColor;
	
	public SetupState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) {
		elementTextColor = Color.white;
		elementBackgroundColor = Color.black;
		elementSelectColor = Color.gray;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		// Create the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		// Add a text display with instructions.
		final String instructions = "Enter the path to the folder containing the worlds and then hit enter.\n"
				+ "If this doesn't contain the worlds, nothing will be loaded!\n";
		TextDisplay instructionsDisplay = new TextDisplay(0, 0, container.getWidth(), container.getHeight(),
				0, instructions, GMCalc2.BODYFONT, elementTextColor);
		ui.addElement(instructionsDisplay);
		
		// Add a text field.
		String initialText = new File("").getAbsolutePath();
		int tX = 0;
		int tY = instructionsDisplay.getY() + instructionsDisplay.getHeight();
		int tW = container.getWidth();
		int tH = container.getHeight();
		String backgroundMessage = "Enter the path to the worlds folder here.";
		pathInput = new TextField(tX, tY, tW, tH, 0, initialText, backgroundMessage,
				GMCalc2.BODYFONT, elementTextColor, elementSelectColor, elementBackgroundColor);
		ui.addElement(pathInput);
		
		// Turn on key repeat.
		container.getInput().enableKeyRepeat();
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		// Destroy the ui.
		ui.destroy();
		pathInput = null;
		
		// Disable key repeat.
		container.getInput().disableKeyRepeat();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
		
		// If enter is pressed, make sure the given string is a directory path,
		// and if it is, set the worlds folder and switch to the loading state.
		if (container.getInput().isKeyPressed(Input.KEY_ENTER)) {
			String pathString = pathInput.toString();
			File pathFile = new File(pathString);
			if (pathFile.exists() && pathFile.isDirectory()) {
				gmcalc2.setWorldsFolder(pathInput.toString());
				gmcalc2.enterState(LoadingState.ID);
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return ID;
	}

}
