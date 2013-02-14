package com.timothysinard.Checkers.core;

public interface GameStateListener<E> {

	/**
	 * Method that signifies a change of the game board.
	 * 
	 * @param game
	 */
	public void boardChange(E game);

	/**
	 * Method that signifies game-over and the winning player.
	 * 
	 * @param game
	 * @param player
	 */
	public void gameOver(E game, BlockOccupant player);

}
