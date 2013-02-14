package com.timothysinard.Checkers.core;

/**
 * Class that stores global data for the active Checkers Game
 */
public class CheckersSettingsManager {

	// Does this game force available jumps
	private boolean forceJumps;
	// Does this game show move-guides
	private boolean moveGuides;
	// Game type of current game
	private GameType gameType;

	public CheckersSettingsManager() {
		forceJumps = false;
		moveGuides = false;
		gameType = GameType.REGULAR;
	}

	/**
	 * Returns if jumps are forced to be made.
	 * 
	 * @return
	 */
	public boolean isForceJumps() {
		return forceJumps;
	}

	/**
	 * Set if jumps are forced to be made.
	 * 
	 * @param forceJumps
	 */
	public void setForceJumps(boolean forceJumps) {
		this.forceJumps = forceJumps;
	}

	/**
	 * Returns if move-guide is on.
	 * 
	 * @return
	 */
	public boolean isMoveGuides() {
		return moveGuides;
	}

	/**
	 * Activate or deactivate move-guide.
	 * 
	 * @param moveGuides
	 */
	public void setMoveGuides(boolean moveGuides) {
		this.moveGuides = moveGuides;
	}

	/**
	 * Returns the selected game type.
	 * 
	 * @return
	 */
	public GameType getGameType() {
		return this.gameType;
	}

	/**
	 * Sets the selected game type.
	 * 
	 * @param type
	 */
	public void setGameType(GameType type) {
		this.gameType = type;
	}

}
