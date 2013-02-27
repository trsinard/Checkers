package com.timothysinard.Checkers.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.timothysinard.Checkers.core.BlockOccupant;
import com.timothysinard.Checkers.core.CheckersGame;
import com.timothysinard.Checkers.core.CheckersSettingsManager;
import com.timothysinard.Checkers.core.GameOpponent;
import com.timothysinard.Checkers.core.GameStateListener;
import com.timothysinard.Checkers.core.GameType;
import com.timothysinard.Checkers.utils.FileIOException;
import com.timothysinard.Checkers.utils.InvalidThemeException;
import com.timothysinard.Checkers.utils.Sound;
import com.timothysinard.Checkers.utils.ThemeManager;

/**
 * Main GUI Class to handle the functionality of the Checkers Game
 */
public class CheckersGUI extends Frame implements ActionListener, ItemListener,
		ComponentListener, GameStateListener<CheckersGame> {

	private static Dimension DEFAULT_DIMENSION = new Dimension(1024, 1024);
	private static final long serialVersionUID = 1L;

	// The main container to hold the parts of the GUI
	private final Container GUIContainer;
	// Main display panel that hows the board
	private final DrawPanel boardPanel;
	// Secondary panel which holds the score board
	private final DrawPanel scoreBoardPanel;

	// Reference to handler for the game settings
	private final CheckersSettingsManager settingsManager;
	// Boolean signifying if the intro start screen is displayed
	private boolean showStartPanel;

	// Main canvas for the board panel
	private final GraphicCanvas boardCanvas;
	// Canvas for the score board panel
	private final ScoreBoard scoreBoard;

	// Top menu bar
	private JMenuBar menuBar;
	// Undo button reference
	private JMenuItem undoAction;
	// Move-Guide and Force-Jump toggle references
	private JCheckBox guideButton;
	private JCheckBox forceJumpButton;
	// Game button group for type selection.
	private ButtonGroup gTypeGroup;
	// Reference to background sound
	private final Sound backgroundSound;

	// The scaled ratio compared to original size
	private double guiRatio;

	// Reference to current active game
	private CheckersGame currentGame;

	public CheckersGUI() throws FileIOException {
		// Build basic frame
		super(DEFAULT_DIMENSION, "Checkers");
		this.setResizable(false);
		// Create settings manager
		this.settingsManager = new CheckersSettingsManager();

		// Create main panel to be drawn on
		this.boardPanel = new DrawPanel();
		this.boardCanvas = new GraphicCanvas("boardCanvas");
		this.boardPanel.setDrawable(boardCanvas);
		// Create scoreboard/banner to be drawn on
		this.scoreBoardPanel = new DrawPanel();
		this.scoreBoard = new ScoreBoard("mainScoreBoard");
		GraphicCanvas bannerCanvas = new GraphicCanvas("bannerCanvas");
		this.scoreBoardPanel.setDrawable(bannerCanvas);
		// Prepare listener
		this.addComponentListener(this);
		// Fill the container
		GUIContainer = this.getContentPane();
		GUIContainer.add(boardPanel, BorderLayout.CENTER);
		GUIContainer.add(scoreBoardPanel, BorderLayout.NORTH);
		// Build control menu bar
		buildMenuBar();
		// Start display - set to visible location on screen
		setVisible(true);
		this.setLocation(50, 50);
		// Prepare mouse listener
		new GUIMouseEventListener(this, boardPanel);

		// Introduction screen
		showStartPanel = true;
		BufferedImage image = ThemeManager.getThemeManager().getImage("title");
		boardCanvas.addDrawable("title", new Graphic("title", image, 0, 0,
				new Dimension(image.getWidth(), image.getHeight()), 1));
		image = ThemeManager.getThemeManager().getImage("gloss-board");
		boardCanvas.addDrawable("gloss-board", new Graphic("gloss-board",
				image, 0, 0,
				new Dimension(image.getWidth(), image.getHeight()), 2));
		image = ThemeManager.getThemeManager().getImage("barebanner");
		bannerCanvas.addDrawable("barebanner", new Graphic("barebanner", image,
				0, 0, new Dimension(image.getWidth(), image.getHeight()), 1));
		Thread guiEffectsThread = new Thread(new EffectsRunnable(this));
		guiEffectsThread.start();

		// Scale to half size
		rescale(.50);

		// Start background sound
		backgroundSound = new Sound("/background.wav");
		backgroundSound.loopSound();

	}

	/**
	 * Returns a boolean signifying if the introduction screen is displayed.
	 * 
	 * @return
	 */
	public boolean isShowingStart() {
		return showStartPanel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		// Move-Guide has been toggled
		if (source == guideButton) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				settingsManager.setMoveGuides(false);
				if (currentGame != null) {
					// Clears the highlighted guide
					currentGame.clearMoveHighlights();
				}
			} else if (e.getStateChange() == ItemEvent.SELECTED) {
				settingsManager.setMoveGuides(true);
				if (currentGame != null) {
					// Highlight moves
					currentGame.updateAvailableMoves();
				}
			}
		}
		// Force Jump toggled
		if (source == forceJumpButton) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				settingsManager.setForceJumps(false);
			} else if (e.getStateChange() == ItemEvent.SELECTED) {
				settingsManager.setForceJumps(true);
			}
			if (currentGame != null) {
				// If a game is in progress, start it over.
				newGame(currentGame.getGameOpponent(),
						settingsManager.getGameType());
			}
		}

		// Deals with game-type change
		boolean typeChange = false;
		for (Enumeration<AbstractButton> buttons = gTypeGroup.getElements(); buttons
				.hasMoreElements();) {
			// Loops through all available selectable types
			AbstractButton button = buttons.nextElement();
			if (source == button) {
				if (button.getText() == "Regular"
						&& e.getStateChange() == ItemEvent.SELECTED) {
					// Regular game type and is selected

					settingsManager.setGameType(GameType.REGULAR);
					// Change is true
					typeChange = true;
				} else if (button.getText() == "Reverse"
						&& e.getStateChange() == ItemEvent.SELECTED) {
					// Reverse game type and is selected
					// Turn on Force-Jump, and disable it as optional
					forceJumpButton.setSelected(true);
					forceJumpButton.setEnabled(false);
					settingsManager.setGameType(GameType.REVERSE);
					// Change is true
					typeChange = true;
				}
				if (button.getText() == "Reverse"
						&& e.getStateChange() == ItemEvent.DESELECTED) {
					forceJumpButton.setEnabled(true);
					// Reverse is deselected, Force Jump is optional.
				}
				if (currentGame != null && typeChange) {
					// Type changed while game in progress, reset game.
					newGame(currentGame.getGameOpponent(),
							settingsManager.getGameType());
				}
			}
		}
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		String[] parse = ae.getActionCommand().split(":");

		if (parse[0].equals("TwoPlayers")) {
			newGame(GameOpponent.PLAYER, settingsManager.getGameType());
		}

		// Dead code: For future implementation of AI modes.
		/*
		 * else if (parse[0].equals("aiEasy")) { newGame(GameOpponent.AI_EASY,
		 * settingsManager.getGameType()); } else if
		 * (parse[0].equals("aiMedium")) { newGame(GameOpponent.AI_MEDIUM,
		 * settingsManager.getGameType()); } else if (parse[0].equals("aiHard"))
		 * { newGame(GameOpponent.AI_HARD, settingsManager.getGameType()); }
		 * else
		 */
		// Parse command, selected theme is after the colon ":" split.
		if (parse[0].equals("THEMES")) {
			try {
				updateTheme(parse[1]);
			} catch (InvalidThemeException e) {
				// Invalid Theme
			}
		} else if (parse[0].equals("Undo")) {
			undoMove();
		} else if (parse[0].equals("Exit")) {
			System.exit(0);
		}

		repaint();
	}

	/**
	 * Get and return reference to current game.
	 * 
	 * @return
	 */
	public CheckersGame getCurrentGame() {
		return currentGame;
	}

	/**
	 * Get and return reference to main canvas drawing the board.
	 * 
	 * @return
	 */
	public GraphicCanvas getBoardCanvas() {
		return boardCanvas;
	}

	/**
	 * Get and return reference to canvas drawing the score board banner.
	 * 
	 * @return
	 */
	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}

	/**
	 * Get and return the GUI scale ratio.
	 * 
	 * @return
	 */
	public double getRatio() {
		return guiRatio;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		repaint();
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		repaint();
	}

	/**
	 * Adjust gui scale by given ratio offset.
	 * 
	 * @param offset
	 */
	public void adjustScale(double offset) {
		rescale(guiRatio + offset);
	}

	/**
	 * Rescales the GUI to given ratio. Recursively resizes down until window
	 * fits the screen if ratio is too high.
	 * 
	 * @param ratio
	 */
	public void rescale(double ratio) {
		this.guiRatio = ratio;
		boardPanel.setRatio(ratio);
		scoreBoardPanel.setRatio(ratio);

		int scaledScoreBoardWidth = (int) (Math.round(scoreBoard
				.getOriginalSize().width * scoreBoardPanel.getRatio()));
		int scaledScoreBoardHeight = (int) (Math.round(scoreBoard
				.getOriginalSize().height * scoreBoardPanel.getRatio()));
		scoreBoardPanel.setPreferredSize(new Dimension(scaledScoreBoardWidth,
				scaledScoreBoardHeight));

		Dimension windowBezelOffset = new Dimension(this.getWidth()
				- boardPanel.getSize().width, this.getHeight()
				- boardPanel.getSize().height
				- scoreBoardPanel.getSize().height);

		int newWidth = (int) (Math.round((DEFAULT_DIMENSION.width * boardPanel
				.getRatio()) + windowBezelOffset.getWidth()));
		int newHeight = (int) (Math
				.round((DEFAULT_DIMENSION.height * boardPanel.getRatio())
						+ scaledScoreBoardHeight
						+ windowBezelOffset.getHeight()));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// If the window size is greater than the screen size, recursively
		// rescale down.
		if ((newHeight > dim.getSize().height || newWidth > dim.getSize().width)) {
			if (guiRatio > 0.20) {
				rescale(ratio - 0.05);
			}
		} else if (guiRatio < 0.15) {
			// If ratio is too small, rescale to given minimum.
			rescale(0.15);
		} else {
			// Set the size otherwise
			this.setSize(newWidth, newHeight);
		}
	}

	/**
	 * Calls the current game's undo function, and re-adjust the drawable
	 * reference to the board canvas. Does nothing if game isn't active.
	 */
	public void undoMove() {
		if (currentGame != null) {
			currentGame.undo();
			boardCanvas.addDrawable("board", getCurrentGame().getGameBoard());
		}
		this.repaint();
	}

	/**
	 * Convenience method to begin construction on the menu bar. Creates a new
	 * JMenuBar, adds the Game menu and Option menu to it.
	 */
	private void buildMenuBar() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(buildGameMenu());
		menuBar.add(buildOptionMenu());
	}

	/**
	 * Builds and returns the Game Menu, JMenu item.
	 * 
	 * @return
	 */
	private JMenu buildGameMenu() {
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		// gameMenu.add(buildGameTypeSubMenu());
		// Start: Temporary code for menu construction until AI implementation

		// New Game option
		JMenuItem newGameAction = new JMenuItem("New Game");
		// Allow ctrl+N activation
		newGameAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		newGameAction.addActionListener(this);
		newGameAction.setActionCommand("TwoPlayers");
		newGameAction.setEnabled(true);
		gameMenu.add(newGameAction);

		// Game Mode menu
		gameMenu.add(buildModeMenu());
		gameMenu.addSeparator();
		// Undo option
		undoAction = new JMenuItem("Undo");
		// Allow ctrl+Z activation
		undoAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK));
		undoAction.addActionListener(this);
		undoAction.setActionCommand("Undo");
		undoAction.setEnabled(false);
		undoAction.setForeground(Color.GRAY);
		gameMenu.add(undoAction);

		// Exit option
		JMenuItem exitAction = new JMenuItem("Exit");
		exitAction.addActionListener(this);
		exitAction.setActionCommand("Exit");
		// Allow ctrl+Q activation
		exitAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		gameMenu.addSeparator();
		gameMenu.add(exitAction);

		return gameMenu;
	}

	/*
	 * Dead Code Future AI implementation
	 * 
	 * 
	 * private JMenu buildGameTypeSubMenu() { JMenu newAction = new
	 * JMenu("New"); newAction.setMnemonic(KeyEvent.VK_N); JMenu onePlayer = new
	 * JMenu("One Player"); JMenuItem twoPlayer = new JMenuItem("Two Players");
	 * twoPlayer.addActionListener(this);
	 * twoPlayer.setActionCommand("TwoPlayers");
	 * 
	 * JMenuItem aiEasy = new JMenuItem("Easy"); aiEasy.addActionListener(this);
	 * aiEasy.setActionCommand("aiEasy"); onePlayer.add(aiEasy);
	 * 
	 * JMenuItem aiMedium = new JMenuItem("Moderate");
	 * aiMedium.addActionListener(this); aiMedium.setActionCommand("aiMedium");
	 * onePlayer.add(aiMedium);
	 * 
	 * JMenuItem aiHard = new JMenuItem("Hard"); aiHard.addActionListener(this);
	 * aiHard.setActionCommand("aiHard"); onePlayer.add(aiHard);
	 * 
	 * newAction.add(onePlayer); newAction.add(twoPlayer); return newAction;
	 * 
	 * 
	 * }
	 */

	/**
	 * Convenience method to build and return Options menu.
	 * 
	 * @return
	 */
	private JMenu buildOptionMenu() {
		JMenu optionMenu = new JMenu("Options");
		optionMenu.setMnemonic(KeyEvent.VK_O);

		// Toggable move-guide button.
		guideButton = new JCheckBox("Show Guide");
		guideButton.addItemListener(this);
		guideButton.setMnemonic(KeyEvent.VK_G);
		guideButton.setSelected(false);
		optionMenu.add(guideButton);

		// Toggable Force-jump button
		forceJumpButton = new JCheckBox("Force Jumps");
		forceJumpButton.addItemListener(this);
		forceJumpButton.setMnemonic(KeyEvent.VK_F);
		forceJumpButton
				.setToolTipText("Requires jumps to be made. Restarts game!");
		forceJumpButton.setSelected(false);
		optionMenu.add(forceJumpButton);

		optionMenu.addSeparator();
		optionMenu.add(buildThemeMenu());

		return optionMenu;

	}

	/**
	 * Convenience method to build and return Game Mode menu
	 * 
	 * @return
	 */
	private JMenu buildModeMenu() {
		JMenu modeMenu = new JMenu("Mode");
		modeMenu.setMnemonic(KeyEvent.VK_M);

		// Construct Regular radio-button
		gTypeGroup = new ButtonGroup();
		JRadioButtonMenuItem item = new JRadioButtonMenuItem("Regular");
		item.setSelected(true);
		item.addItemListener(this);
		// Add to game-type-group.
		gTypeGroup.add(item);
		modeMenu.add(item);
		item.setActionCommand("MODE:Regular");

		// Construct Reverse radio-button
		item = new JRadioButtonMenuItem("Reverse");
		// Add to game-type-group.
		gTypeGroup.add(item);
		modeMenu.add(item);
		item.addItemListener(this);
		item.setActionCommand("MODE:Reverse");

		return modeMenu;
	}

	/**
	 * Convenience method to build and return Theme Menu
	 * 
	 * @return
	 */
	private JMenu buildThemeMenu() {
		JMenu themeMenu = new JMenu("Themes");
		themeMenu.setMnemonic(KeyEvent.VK_T);

		Iterator<String> themeIterator = ThemeManager.getThemeManager()
				.iterator();
		// Loops through all available themes, adds each one as options by name.
		while (themeIterator.hasNext()) {
			String theme = themeIterator.next();
			JMenuItem item = new JMenuItem(theme);
			themeMenu.add(item);
			item.addActionListener(this);
			item.setActionCommand("THEME:" + theme);
		}
		try {
			// Sets first available theme, if not available the program will
			// close.
			updateTheme(ThemeManager.getThemeManager().getThemes().get(0));
		} catch (InvalidThemeException e) {
			System.exit(0);
		}
		return themeMenu;
	}

	/**
	 * Creates a new game with given opponent and game type.
	 * 
	 * @param opponent
	 * @param type
	 */
	public void newGame(GameOpponent opponent, GameType type) {

		// If showing start panel, change drawn panel and remove title image
		// from collected drawables.
		if (showStartPanel) {
			this.scoreBoardPanel.setDrawable(scoreBoard);
			boardCanvas.removeDrawable("title");
			showStartPanel = false;
			rescale(guiRatio);
		}
		// Prepare for new game, remove game-over image regardless if a previous
		// game existed.
		boardCanvas.removeDrawable("gameover");
		currentGame = new CheckersGame(opponent, type, settingsManager);
		boardCanvas.addDrawable("board", getCurrentGame().getGameBoard());
		currentGame.addStateListener(this.scoreBoard);
		currentGame.addStateListener(this);
		currentGame.updateAvailableMoves();

	}

	/**
	 * Update the current theme based on given theme ID
	 * 
	 * @param themeID
	 * @throws InvalidThemeException
	 */
	private void updateTheme(String themeID) throws InvalidThemeException {
		String selectedTheme = themeID;
		// If theme is found, set as selected and update the theme manager
		if (selectedTheme != null) {
			ThemeManager.getThemeManager().setTheme(selectedTheme);
			ThemeManager.getThemeManager().updateTheme();
		}
		repaint();
	}

	@Override
	public void boardChange(CheckersGame game) {
		if (currentGame == null) {
			return;
		}

		// If the move-history is empty, disable the Undo option
		if (currentGame.getGameHistory().isEmpty()) {
			undoAction.setEnabled(false);
			undoAction.setForeground(Color.GRAY);
		} else {
			undoAction.setEnabled(true);
			undoAction.setForeground(Color.DARK_GRAY);
		}

	}

	@Override
	public void gameOver(CheckersGame game, BlockOccupant player) {

		// Sets the drawn image for the winning player
		BufferedImage image = null;
		if (player == BlockOccupant.PLAYER) {
			image = ThemeManager.getThemeManager().getImage("gameover-p1");
		} else if (player == BlockOccupant.PLAYER2) {
			image = ThemeManager.getThemeManager().getImage("gameover-p2");
		} else {
			return;
		}
		// Priority level 4, layered above anything from 0 to 3
		boardCanvas.addDrawable("gameover", new Graphic("gameover", image, 0,
				0, new Dimension(image.getWidth(), image.getHeight()), 4));
	}
}
