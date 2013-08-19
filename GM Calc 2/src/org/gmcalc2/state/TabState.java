package org.gmcalc2.state;

import org.gmcalc2.GMCalc2;
import org.gmcalc2.World;
import org.gmcalc2.gui.LogDisplay;
import org.gmcalc2.gui.TabContainer;
import org.gmcalc2.gui.WorldExplorer;
import org.gmcalc2.item.Component;
import org.gmcalc2.item.ItemBase;
import org.gmcalc2.item.Player;
import org.haferlib.slick.gui.Button;
import org.haferlib.slick.gui.GUIContext;
import org.haferlib.slick.gui.GUIElement;
import org.haferlib.slick.gui.GUIEvent;
import org.haferlib.slick.gui.GUIEventListener;
import org.haferlib.slick.gui.ImageButton;
import org.haferlib.slick.gui.ListFrame;
import org.haferlib.util.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class TabState extends BasicGameState implements GUIEventListener {
	
	private static final int BORDER_THICKNESS = 6;		// The thickness of the border around elements.
	
	public static final int ID = 1;
	
	private GMCalc2 gmcalc2;
	private GUIContext ui;							// The UI.
	private int workbenchX, workbenchY;				// The position of the workbench.
	private int workbenchWidth, workbenchHeight;	// The size of the workbench.
	private GUIElement[] workbenchViews;			// Each element is a different view of the workbench.
	private int currentView;						// The index of the current view.
	private int worldExplorerView;					// The index of the world explorer view.
	private int tabContainerView;					// The index of the tab container view.
	private ListFrame toolbar;						// The toolbar on the left side of the window.
	
	// Color scheme.
	private Color elementTextColor, elementHighlightColor, elementBackgroundColor, elementSelectColor;
	
	// Constructor.
	public TabState(GMCalc2 gmcalc2) {
		this.gmcalc2 = gmcalc2;
	}
	
	// Switch the workbench to a particular view.
	private void switchWorkbench(int view) {
		if (view != currentView) {
			ui.removeElement(workbenchViews[currentView]);
			currentView = view;
			ui.addElement(workbenchViews[currentView]);
		}
	}
	
	// Add a workbench view and make a button for it in the toolbar.
	private void addWorkbenchView(GUIElement view, int index, String buttonIconPath, int toolbarButtonSize) throws SlickException {
		// Place the view in workbenchViews.
		workbenchViews[index] = view;

		// Add a button for the view to the toolbar.
		Image buttonImage = new Image(buttonIconPath);
		Button<Integer> button = new ImageButton<>(index, 0, 0,
				toolbarButtonSize, toolbarButtonSize, 0, null,
				elementHighlightColor, Input.KEY_ENTER, buttonImage,
				Button.CENTER, 0);
		button.addListener(this);
		toolbar.addElement(button);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// Make colors.
		elementTextColor = Color.white;
		elementHighlightColor = Color.gray;
		elementBackgroundColor = Color.darkGray;
		elementSelectColor = new Color(95, 95, 95);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {		
		// Find some dimensions.
		int toolbarX = BORDER_THICKNESS;
		int toolbarY = BORDER_THICKNESS;
		int toolbarWidth = 32;
		int toolbarButtonBorder = 2;
		int toolbarButtonSize = toolbarWidth - toolbarButtonBorder * 2;
		workbenchX = toolbarX + toolbarWidth + BORDER_THICKNESS;
		workbenchY = BORDER_THICKNESS;
		workbenchWidth = container.getWidth() - workbenchX - BORDER_THICKNESS;
		workbenchHeight = container.getHeight() - BORDER_THICKNESS * 2;
		
		// Create the UI.
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		// Create the toolbar.
		toolbar = new ListFrame(toolbarX, toolbarY, toolbarWidth, 100, ListFrame.XALIGN_CENTER, 0, toolbarButtonBorder);
		ui.addElement(toolbar);
		
		// Initialize workbenchViews and an index to place views at.
		workbenchViews = new GUIElement[3];
		currentView = 0;
		int viewIndex = 0;
		
		// Create the world explorer view.
		WorldExplorer worldExplorer = new WorldExplorer(workbenchX, workbenchY, workbenchWidth, workbenchHeight, 0, gmcalc2.getWorlds(),
				elementTextColor, elementHighlightColor, elementBackgroundColor, elementSelectColor, GMCalc2.HEADERFONT, GMCalc2.BODYFONT);
		worldExplorer.addListener(this);
		addWorkbenchView(worldExplorer, viewIndex, "resources\\worldExplorerIcon.png", toolbarButtonSize);
		worldExplorerView = viewIndex;
		viewIndex++;
		
		// Create the tab container view.
		TabContainer tabContainer = new TabContainer(workbenchX, workbenchY, workbenchWidth, workbenchHeight, 0,
				elementTextColor, elementHighlightColor, elementBackgroundColor, elementSelectColor);
		addWorkbenchView(tabContainer, viewIndex, "resources\\editIcon.png", toolbarButtonSize);
		tabContainerView = viewIndex;
		viewIndex++;
		
		// Create the log display and place it in workbenchViews.
		LogDisplay logDisplay = new LogDisplay(workbenchX, workbenchY, workbenchWidth, workbenchHeight, 0, 10,
				GMCalc2.HEADERFONT, GMCalc2.BODYFONT, elementTextColor, elementHighlightColor, elementBackgroundColor);
		addWorkbenchView(logDisplay, viewIndex, "resources\\consoleIcon.png", toolbarButtonSize);
		viewIndex++;
		
		// Add the first workbench view to the ui context.
		ui.addElement(workbenchViews[currentView]);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Draw the toolbar background.
		g.setColor(elementBackgroundColor);
		g.fillRect(toolbar.getX(), toolbar.getY(), toolbar.getWidth(), workbenchHeight);
		
		// Draw the ui.
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the generator is a button, then we know it is a toolbar button.
		// Switch to the appropriate workbench view.
		if (event.getGenerator() instanceof Button) {
			int view = (Integer)event.getData();
			switchWorkbench(view);
		}
		// If the generator is the world explorer, we need to switch the
		// workbench to view a tab for the data.
		else if (event.getGenerator() instanceof WorldExplorer) {
			Object eventData = event.getData();
			
			// Switch to the tab container view.
			switchWorkbench(tabContainerView);
			
			// Open a tab.
			if (eventData instanceof Player) {
				Log.getDefaultLog().info("Clicked player!");
				
				TabContainer tabContainer = (TabContainer)workbenchViews[tabContainerView];
				Player player = (Player)eventData;
				
				boolean success = tabContainer.setEnabledTabById(player.getId());
				if (!success) {
					tabContainer.addTabForPlayer(player);
					tabContainer.setEnabledTabById(player.getId());
				}
			}
			else if (eventData instanceof ItemBase) {
				Log.getDefaultLog().info("Clicked item base!");
				// TODO
			}
			else if (eventData instanceof Component) {
				Log.getDefaultLog().info("Clicked component!");
				// TODO
			}
			else if (eventData instanceof World) {
				Log.getDefaultLog().info("Clicked world!");
				// TODO
			}
		}
	}

}
