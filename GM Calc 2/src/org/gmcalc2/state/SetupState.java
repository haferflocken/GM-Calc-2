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
	
	private static final int BORDER_THICKNESS = 6;
	
	public static final int ID = 0;
	
	private final GMCalc2 gmcalc2;
	private GUIContext ui;
	private TextField pathInput;
	private int frameX, frameY, frameWidth, frameHeight;
	
	private Color elementTextColor, elementBackgroundColor, elementSelectColor, elementFieldColor;
	
	public SetupState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) {
		elementTextColor = Color.white;
		elementBackgroundColor = Color.darkGray;
		elementSelectColor = Color.gray;
		elementFieldColor = Color.gray;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		// Turn on key repeat.
		container.getInput().enableKeyRepeat();
		
		// Create the ui.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		// Set some frame attributes.
		frameX = BORDER_THICKNESS;
		frameY = BORDER_THICKNESS;
		frameWidth = container.getWidth() - BORDER_THICKNESS * 2;
		
		// Add a text display with instructions.
		final String instructions = "Hi! Thank you for helping me test this.\n\n"
				+ "This is a tool that helps a game master run a tabletop roleplaying game. "
				+ "Currently, it's very basic. Items cannot be added to players and items cannot "
				+ "be edited. All you can really do is drag and drop items within a player to "
				+ "equip/unequip them. The only files that can even be opened are players. That "
				+ "said, I'm curious to see if you think this is going in a good direction! Please "
				+ "let me know what you think, and if you find any bugs, please tell me how to "
				+ "reproduce them. Since part of this is to get feedback on how straightforward this "
				+ "is to use, I'll let you figure out the rest on your own.\n\n"
				+ "Enter the path to the folder containing the worlds and then hit enter. This is the "
				+ "folder called 'gmcalc2 worlds' in the zip archive. Extract it somewhere and then "
				+ "type the path to it here, including its name in the path. "
				+ "If this field doesn't contain the worlds folder, nothing will be loaded! However, "
				+ "it won't let you press enter on a path that isn't a directory.\n\n"
				+ "Make sure to click the field to start typing.";
		int iDX = frameX + BORDER_THICKNESS;
		int iDY = frameY + BORDER_THICKNESS;
		int iDWidth = frameWidth - BORDER_THICKNESS * 2;
		TextDisplay instructionsDisplay = new TextDisplay(iDX, iDY, iDWidth, Integer.MAX_VALUE, 0,
				instructions, GMCalc2.BODYFONT, elementTextColor);
		ui.addElement(instructionsDisplay);
		
		// Add a text field.
		String initialText = new File("").getAbsolutePath();
		int tX = frameX + BORDER_THICKNESS;
		int tY = instructionsDisplay.getY() + instructionsDisplay.getHeight() + BORDER_THICKNESS;
		int tW = frameWidth - BORDER_THICKNESS * 2;
		int tH = GMCalc2.BODYFONT_HEIGHT;
		String backgroundMessage = "Enter the path to the worlds folder here.";
		pathInput = new TextField(tX, tY, tW, tH, 0, initialText, backgroundMessage,
				GMCalc2.BODYFONT, elementTextColor, elementSelectColor, elementFieldColor);
		ui.addElement(pathInput);
		
		// Set the frame height.
		int tY2 = pathInput.getY() + pathInput.getHeight();
		frameHeight = tY2 - iDY + BORDER_THICKNESS * 2;
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
		// Draw the frame.
		g.setColor(elementBackgroundColor);
		g.fillRect(frameX, frameY, frameWidth, frameHeight);
		
		// Render the ui.
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return ID;
	}

}
