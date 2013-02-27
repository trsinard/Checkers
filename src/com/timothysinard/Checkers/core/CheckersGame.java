package com.timothysinard.Checkers.core;

import java.util.ArrayList;

public class CheckersGame {

	// Reference to game board
	private CheckersBoard gameBoard;
	// Manager to store settings across games
	private final CheckersSettingsManager settingsManager;
	// Collection of previous game states.
	private final ArrayList<CheckersGame> gameHistory;
	// Collection of available moves
	private final ArrayList<MoveData> availableMoves;
	// Current player turn
	private int playerTurn;
	// Is the game over?
	private boolean gameOver;
	// Current piece count for each player
	private int pieceCount_P1;
	private int pieceCount_P2;
	// Gameblock that must continue a jump-series.
	private GameBlock continueJumpPiece;
	// Current active, selected, piece.
	private GameBlock activePiece;
	// Reference to a collection of game state listeners
	private final ArrayList<GameStateListener<CheckersGame>> gameStateListeners;
	// Two-player, ai-easy, ai-medium, ai-hard opponent
	private final GameOpponent gameOpponent;
	// Game Type to play
	private final GameType gameType;

	public CheckersGame(GameOpponent gameOpponent, GameType gameType,
			CheckersSettingsManager settingsManager) {

		this.settingsManager = settingsManager;
		this.gameHistory = new ArrayList<CheckersGame>();
		this.availableMoves = new ArrayList<MoveData>();
		this.gameStateListeners = new ArrayList<GameStateListener<CheckersGame>>();
		this.gameBoard = new CheckersBoard();
		this.gameOver = false;
		this.continueJumpPiece = null;
		this.playerTurn = 1;
		this.pieceCount_P1 = this.pieceCount_P2 = 12;
		this.gameOpponent = gameOpponent;
		this.gameType = gameType;
		updateAvailableMoves();
		// Dead code: For future implementation of AI single player
		if (gameOpponent != GameOpponent.PLAYER) {
			// Create new AI
		}
	}

	/**
	 * Undo method which reverts the board to the previous state of game play
	 * prior to recent move. Sets each related piece of board data to that which
	 * is stored, then removes the entry from history.
	 */
	public void undo() {
		// Only if previous moves have been made
		if (!gameHistory.isEmpty()) {
			int undoIndex = gameHistory.size() - 1;
			CheckersGame prev = gameHistory.get(undoIndex);
			this.gameBoard = prev.getGameBoard();
			this.setActive(prev.getActivePiece());
			this.setPieceCountP1(prev.getPieceCount(BlockOccupant.PLAYER));
			this.setPieceCountP2(prev.getPieceCount(BlockOccupant.PLAYER2));
			this.gameOver = prev.isGameOver();
			this.playerTurn = prev.getPlayerTurn();
			gameHistory.remove(undoIndex);
			checkGameState(false);

		}
	}

	/**
	 * Get and return a copy of the game state history.
	 * 
	 * @return
	 */
	public ArrayList<CheckersGame> getGameHistory() {
		return new ArrayList<CheckersGame>(gameHistory);
	}

	/**
	 * Get and return if the current game is over.
	 * 
	 * @return
	 */
	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * Get and return the current game opponent type.
	 * 
	 * @return
	 */
	public GameOpponent getGameOpponent() {
		return gameOpponent;
	}

	/**
	 * Get and return the current game type
	 * 
	 * @return
	 */
	public GameType getGameType() {
		return gameType;
	}

	/**
	 * Get and return the piece count for the given BlockOccupant piece. This
	 * assumes the board is standard checkers size, NULL count and empty count
	 * based on 32.
	 * 
	 * @param piece
	 * @return
	 */
	public int getPieceCount(BlockOccupant piece) {
		if (piece == BlockOccupant.PLAYER) {
			return pieceCount_P1;
		} else if (piece == BlockOccupant.PLAYER2) {
			return pieceCount_P2;
		} else if (piece == BlockOccupant.NULL) {
			return 32;
		} else {
			return (32 - pieceCount_P1 - pieceCount_P2);
		}
	}

	/**
	 * Get and return current player turn
	 * 
	 * @return
	 */
	public int getPlayerTurn() {
		return playerTurn;
	}

	/**
	 * Get and return reference to current game board
	 * 
	 * @return
	 */
	public CheckersBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * Get and return the current active, selected, piece
	 * 
	 * @return
	 */
	public GameBlock getActivePiece() {
		return activePiece;
	}

	/**
	 * Set current active, selected, piece to given GameBlock block
	 * 
	 * @param block
	 */
	public void setActive(GameBlock block) {
		if (activePiece != null) {
			// Current active piece is not null, and is an available movable
			// piece
			// Will not set as a highlighted target piece
			for (MoveData md : availableMoves) {
				if (activePiece.equals(md.getSrcPiece())) {
					md.getDestPiece().setHighlightAsTarget(false);
				}
			}
			// Sets the current selected piece to false
			activePiece.setSelected(false);
			// If the current active piece isn't the given new active block,
			// piece is moved to block location.
			if (activePiece != block) {
				move(activePiece, block);
			}
			// Current active piece is assumed to require a continued-jump until
			// proven otherwise.
			activePiece = continueJumpPiece;
			if (continueJumpPiece != null) {
				activePiece.setSelected(true);
			}
		} else if (block.getOccupant() == BlockOccupant.EMPTY
				|| block.getOccupant() == BlockOccupant.NULL) {
			// Cannot set Empty or Null blocks as active
			activePiece = null;
		} else {
			// If nothing is currently selected, then received block is the new
			// active piece.
			activePiece = block;
			activePiece.setSelected(true);
		}
		// Check if settings set to display Move Guides
		if (settingsManager.isMoveGuides()) {
			if (activePiece == null) {
				for (MoveData md : availableMoves) {
					// If no piece is active, show available moves, not
					// available targets
					md.getSrcPiece().setHighlightAsMovable(true);
				}
			} else {
				for (MoveData md : availableMoves) {
					md.getSrcPiece().setHighlightAsMovable(false);
					if (activePiece.equals(md.getSrcPiece())) {
						// If active piece exists, show available targets, not
						// available movable pieces.
						md.getDestPiece().setHighlightAsTarget(true);
					}
				}
			}
		}
	}

	public void addStateListener(GameStateListener<CheckersGame> listener) {
		gameStateListeners.add(listener);
		for (GameStateListener<CheckersGame> gsl : gameStateListeners) {
			gsl.boardChange(this);
		}
	}

	/**
	 * Checks if the given source piece GameBlock and destination piece
	 * GameBlock have one block between them. Checks if the separating block is
	 * occupied by an opposing piece of the given source piece, and the
	 * destination GameBlock is empty. These qualities make up a checkers jump,
	 * which if they are valid, it will return true.
	 * 
	 * @param srcPiece
	 * @param destPiece
	 * @return
	 */
	private boolean isJump(GameBlock srcPiece, GameBlock destPiece) {
		GameBlock[][] grid = gameBoard.getBoard();
		// Checks the method-described conditions for each corner direction.
		if (((srcPiece.getGridX() + 2 == destPiece.getGridX()
				&& srcPiece.getGridY() + 2 == destPiece.getGridY() && srcPiece
				.getOccupant() != grid[srcPiece.getGridX() + 1][srcPiece
				.getGridY() + 1].getOccupant())
				|| (srcPiece.getGridX() - 2 == destPiece.getGridX()
						&& srcPiece.getGridY() + 2 == destPiece.getGridY()
						&& srcPiece.getOccupant() != grid[srcPiece.getGridX() - 1][srcPiece
								.getGridY() + 1].getOccupant() && destPiece
						.getOccupant() == BlockOccupant.EMPTY)
				|| (srcPiece.getGridX() + 2 == destPiece.getGridX()
						&& srcPiece.getGridY() - 2 == destPiece.getGridY()
						&& srcPiece.getOccupant() != grid[srcPiece.getGridX() + 1][srcPiece
								.getGridY() - 1].getOccupant() && destPiece
						.getOccupant() == BlockOccupant.EMPTY) || (srcPiece
				.getGridX() - 2 == destPiece.getGridX()
				&& srcPiece.getGridY() - 2 == destPiece.getGridY()
				&& srcPiece.getOccupant() != grid[srcPiece.getGridX() - 1][srcPiece
						.getGridY() - 1].getOccupant() && destPiece
				.getOccupant() == BlockOccupant.EMPTY))
				&& destPiece.getOccupant() == BlockOccupant.EMPTY) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a jump will allow additional jumps directly after for
	 * multi-jumps.
	 * 
	 * @param srcPiece
	 * @param destPiece
	 * @return
	 */
	private boolean isContJump(GameBlock srcPiece, GameBlock destPiece) {
		// Verify the given GameBlocks construct a valid Checkers Jump
		boolean jumped = isJump(srcPiece, destPiece);
		if (jumped) {
			// Gets collection available jumps in each direction from given
			// source piece.
			ArrayList<ArrayList<GameBlock>> jumpSet = getJumpSet(srcPiece);
			// Jumpset split into direction collections
			ArrayList<GameBlock> jumpPieces_NW = jumpSet.get(0);
			ArrayList<GameBlock> jumpPieces_NE = jumpSet.get(1);
			ArrayList<GameBlock> jumpPieces_SW = jumpSet.get(2);
			ArrayList<GameBlock> jumpPieces_SE = jumpSet.get(3);

			// Each direction is checked if an available jump is available.
			// The jump is already validated, each check must now check if the
			// move is valid.
			// Any time a jump is available in any direction, returns true.
			if (!jumpPieces_NW.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() - 2][srcPiece
								.getGridY() - 2])) {
					return true;
				}
			}
			if (!jumpPieces_NE.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() + 2][srcPiece
								.getGridY() - 2])) {
					return true;
				}
			}
			if (!jumpPieces_SW.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() - 2][srcPiece
								.getGridY() + 2])) {
					return true;
				}
			}
			if (!jumpPieces_SE.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() + 2][srcPiece
								.getGridY() + 2])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Temporarily removes highlighted moves. Settings are not affected by this.
	 */
	public void clearMoveHighlights() {
		boolean flag = settingsManager.isMoveGuides();
		settingsManager.setMoveGuides(false);
		updateAvailableMoves();
		settingsManager.setMoveGuides(flag);
	}

	/**
	 * Finds and collects all of the available moves
	 * 
	 */
	public void updateAvailableMoves() {
		// Remove the highlight on all the current available-moves.
		for (MoveData md : availableMoves) {
			md.getSrcPiece().setHighlightAsMovable(false);
		}
		// Remove all collected available moves
		availableMoves.clear();

		// A move is a jump move, store for possible "Force Jump" settings.
		boolean jumpMoveExist = false;

		// Loop over board
		for (int y = 0; y < gameBoard.getSizeY(); y++) {
			for (int x = 0; x < gameBoard.getSizeX(); x++) {
				GameBlock[][] grid = gameBoard.getBoard();
				GameBlock piece = gameBoard.getBoard()[x][y];
				// If the Block is empty or null, move on.
				if (piece.getOccupant() == BlockOccupant.EMPTY
						|| piece.getOccupant() == BlockOccupant.NULL
						|| piece.isDisabled()) {
					continue;
				}

				// Check player-1 moves.
				if (piece.getOccupant() == BlockOccupant.PLAYER
						&& playerTurn == BlockOccupant.PLAYER.getValue()) {
					// SE Direction
					if (x < gameBoard.getSizeX() - 1
							&& y < gameBoard.getSizeY() - 1) {
						if (isValidMove(piece, grid[x + 1][y + 1])) {
							availableMoves.add(new MoveData(piece,
									grid[x + 1][y + 1], null,
									BlockOccupant.PLAYER));
						}
					}
					// SW Direction
					if (x > 0 && y < gameBoard.getSizeY() - 1) {
						if (isValidMove(piece, grid[x - 1][y + 1])) {
							availableMoves.add(new MoveData(piece,
									grid[x - 1][y + 1], null,
									BlockOccupant.PLAYER));
						}
					}
					// SE Jump
					if (x + 1 < gameBoard.getSizeX() - 1
							&& y + 1 < gameBoard.getSizeY() - 1) {
						if (isValidMove(piece, grid[x + 2][y + 2])) {
							availableMoves.add(new MoveData(piece,
									grid[x + 2][y + 2], grid[x + 1][y + 1],
									BlockOccupant.PLAYER));
							jumpMoveExist = true;
						}
					}
					// SW Jump
					if (x - 1 > 0 && y + 1 < gameBoard.getSizeY() - 1) {
						if (isValidMove(piece, grid[x - 2][y + 2])) {
							availableMoves.add(new MoveData(piece,
									grid[x - 2][y + 2], grid[x - 1][y + 1],
									BlockOccupant.PLAYER));
							jumpMoveExist = true;
						}
					}

					if (piece.isKing()) {
						// NE Direction
						if (x < gameBoard.getSizeX() - 1 && y > 0) {
							if (isValidMove(piece, grid[x + 1][y - 1])) {
								availableMoves.add(new MoveData(piece,
										grid[x + 1][y - 1], null,
										BlockOccupant.PLAYER));
							}
						}
						// NW Direction
						if (x > 0 && y > 0) {
							if (isValidMove(piece, grid[x - 1][y - 1])) {
								availableMoves.add(new MoveData(piece,
										grid[x - 1][y - 1], null,
										BlockOccupant.PLAYER));
							}
						}
						// NE Jump
						if (x + 1 < gameBoard.getSizeX() - 1 && y - 1 > 0) {
							if (isValidMove(piece, grid[x + 2][y - 2])) {
								availableMoves.add(new MoveData(piece,
										grid[x + 2][y - 2], grid[x + 1][y - 1],
										BlockOccupant.PLAYER));
								jumpMoveExist = true;
							}
						}
						// NW Jump
						if (x - 1 > 0 && y - 1 > 0) {
							if (isValidMove(piece, grid[x - 2][y - 2])) {
								availableMoves.add(new MoveData(piece,
										grid[x - 2][y - 2], grid[x - 1][y - 1],
										BlockOccupant.PLAYER));
								jumpMoveExist = true;
							}
						}
					}
				}

				// Check player-2 moves.
				if (piece.getOccupant() == BlockOccupant.PLAYER2
						&& playerTurn == BlockOccupant.PLAYER2.getValue()) {
					// NE Direction
					if (x < gameBoard.getSizeX() - 1 && y > 0) {
						if (isValidMove(piece, grid[x + 1][y - 1])) {
							availableMoves.add(new MoveData(piece,
									grid[x + 1][y - 1], null,
									BlockOccupant.PLAYER2));
						}
					}
					// NW Direction
					if (x > 0 && y > 0) {
						if (isValidMove(piece, grid[x - 1][y - 1])) {
							availableMoves.add(new MoveData(piece,
									grid[x - 1][y - 1], null,
									BlockOccupant.PLAYER2));
						}
					}
					// NE Jump
					if (x + 1 < gameBoard.getSizeX() - 1 && y - 1 > 0) {
						if (isValidMove(piece, grid[x + 2][y - 2])) {
							availableMoves.add(new MoveData(piece,
									grid[x + 2][y - 2], grid[x + 1][y - 1],
									BlockOccupant.PLAYER2));
							jumpMoveExist = true;
						}
					}
					// NW Jump
					if (x - 1 > 0 && y - 1 > 0) {
						if (isValidMove(piece, grid[x - 2][y - 2])) {
							availableMoves.add(new MoveData(piece,
									grid[x - 2][y - 2], grid[x - 1][y - 1],
									BlockOccupant.PLAYER2));
							jumpMoveExist = true;
						}
					}

					if (piece.isKing()) {
						// SE Direction
						if (x < gameBoard.getSizeX() - 1
								&& y < gameBoard.getSizeY() - 1) {
							if (isValidMove(piece, grid[x + 1][y + 1])) {
								availableMoves.add(new MoveData(piece,
										grid[x + 1][y + 1], null,
										BlockOccupant.PLAYER2));
							}
						}
						// SW Direction
						if (x > 0 && y < gameBoard.getSizeY() - 1) {
							if (isValidMove(piece, grid[x - 1][y + 1])) {
								availableMoves.add(new MoveData(piece,
										grid[x - 1][y + 1], null,
										BlockOccupant.PLAYER2));
							}
						}
						// SE Jump
						if (x + 1 < gameBoard.getSizeX() - 1
								&& y + 1 < gameBoard.getSizeY() - 1) {
							if (isValidMove(piece, grid[x + 2][y + 2])) {
								availableMoves.add(new MoveData(piece,
										grid[x + 2][y + 2], grid[x + 1][y + 1],
										BlockOccupant.PLAYER2));
								jumpMoveExist = true;
							}
						}
						// SW Jump
						if (x - 1 > 0 && y + 1 < gameBoard.getSizeY() - 1) {
							if (isValidMove(piece, grid[x - 2][y + 2])) {
								availableMoves.add(new MoveData(piece,
										grid[x - 2][y + 2], grid[x - 1][y + 1],
										BlockOccupant.PLAYER2));
								jumpMoveExist = true;
							}
						}
					}
				}
			}
		}
		// If Force Jump is on and a jump move exists, remove all non-jump moves
		// from available list
		if (settingsManager.isForceJumps() && jumpMoveExist) {
			// Temporarily clone the collection to avoid conflicts
			ArrayList<MoveData> tempClone = new ArrayList<MoveData>(
					availableMoves);
			for (MoveData md : tempClone) {
				if (md.getJumpedPiece() == null) {
					availableMoves.remove(md);
				}
			}
		}
		// If settings have Guided Moves enabled, highlight the available moves.
		if (settingsManager.isMoveGuides()) {
			for (MoveData md : availableMoves) {
				md.getSrcPiece().setHighlightAsMovable(true);
			}
		}
	}

	/**
	 * Moves given source block to destination block.
	 * 
	 * @param srcPiece
	 * @param destPiece
	 * @return
	 */
	public boolean move(GameBlock srcPiece, GameBlock destPiece) {
		// If game over, move isn't made
		if (gameOver) {
			return false;
		}
		// Add copy of this game state to the history collection.
		gameHistory.add(this.copy());
		for (MoveData md : availableMoves) {
			// Loop through available moves
			// If the source block and destination block match, swap their data.
			if (md.getSrcPiece().equals(srcPiece)
					&& md.getDestPiece().equals(destPiece)) {
				// Swap an occupied block with empty block
				gameBoard.swapPiece(srcPiece, destPiece);
				// If move involved a jump then
				if (md.getJumpedPiece() != null) {
					// Decrement appropriate count
					if (md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER) {
						pieceCount_P1--;
					} else if (md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER2) {
						pieceCount_P2--;
					}
					// Remove the jumped piece
					gameBoard.eliminatePiece(md.getJumpedPiece());
					// If the jump can be continued, set the continuedJumpPiece
					// to source.
					if (isContJump(srcPiece, destPiece)) {
						continueJumpPiece = srcPiece;
					} else {
						continueJumpPiece = null;
					}
				}

				// If a jump can be continued, check the game state without
				// rotating turns.
				if (continueJumpPiece != null) {
					checkGameState(false);
				} else {
					// Check game state, rotate turns.
					checkGameState(true);
				}
				return true;
			}
		}
		// If move isn't available, remove the last history entry, and return
		// false.
		gameHistory.remove(gameHistory.size() - 1);
		return false;
	}

	/**
	 * Checks the game state, given boolean determines if the player turn is
	 * rotated after the check.
	 * 
	 * @param rotateTurns
	 */
	private void checkGameState(boolean rotateTurns) {
		// If the game is over, do not continue checking.
		if (gameOver) {
			return;
		}
		// Has a king been made?
		boolean kinged = checkKing();
		// If player 1 runs out of pieces, react based on Game Type
		if (pieceCount_P1 == 0) {
			if (gameType == GameType.REGULAR) {
				gameOver(BlockOccupant.PLAYER2);
			} else if (gameType == GameType.REVERSE) {
				gameOver(BlockOccupant.PLAYER);
			}
			// Game over, return.
			return;
		} else if (pieceCount_P2 == 0) {
			// If player 2 runs out of pieces, react based on Game Type
			if (gameType == GameType.REGULAR) {
				gameOver(BlockOccupant.PLAYER);
			} else if (gameType == GameType.REVERSE) {
				gameOver(BlockOccupant.PLAYER2);
			}
			// Game over, return.
			return;
		}
		// If a king has been made, do not allow a multi-jump to continue.
		if (kinged) {
			continueJumpPiece = null;
		}
		// If a multi-jump cannot be made then
		if (continueJumpPiece == null) {
			// If turns will be rotated, then set new player's turn
			if (rotateTurns) {
				if (playerTurn == BlockOccupant.PLAYER.getValue()) {
					playerTurn = BlockOccupant.PLAYER2.getValue();

					if (gameOpponent != GameOpponent.PLAYER) {
						// Perform AI move, for future implementation
					}
				} else if (playerTurn == BlockOccupant.PLAYER2.getValue()) {
					playerTurn = BlockOccupant.PLAYER.getValue();
				}
			}
		}
		// Update the listeners with this current instance of the game.
		for (GameStateListener<CheckersGame> gsl : gameStateListeners) {
			gsl.boardChange(this);
		}

		// If Reverse game type, apply the updates related to Reverse mode
		if (gameType == GameType.REVERSE) {
			updateReverseMode();
		}
		updateAvailableMoves();
		// If no moves are available, the opposing player automatically wins.
		if (availableMoves.isEmpty()) {
			if (playerTurn == BlockOccupant.PLAYER.getValue()) {
				gameOver(BlockOccupant.PLAYER2);
			} else if (playerTurn == BlockOccupant.PLAYER2.getValue()) {
				gameOver(BlockOccupant.PLAYER);
			}
		}
	}

	/**
	 * Checks if a piece needs to be kinged.
	 * 
	 * @return
	 */
	private boolean checkKing() {
		int offset = 0;
		boolean kinged = false;
		// If the Game Type is Reverse, the board shrinks in certain cases.
		// Due to this, an offset must be determined for the new edges of the
		// board.
		if (gameType == GameType.REVERSE) {
			for (int i = 0; i < gameBoard.getSizeX(); i++) {
				if (!gameBoard.getBoard()[i][i].isDisabled()) {
					offset = i;
					break;
				}
			}
		}
		int sizeX = gameBoard.getSizeX() - 1;
		int sizeY = gameBoard.getSizeY() - 1;
		// Loop over the top and bottom edge of board
		for (int x = offset; x <= sizeX - offset; x++) {
			// If top of board, for P1, contains a P2 piece, and isn't already a
			// king, then king them.
			if (gameBoard.getBoard()[x][0 + offset].getOccupant() == BlockOccupant.PLAYER2) {
				if (gameBoard.getBoard()[x][0 + offset].isKing()) {
					continue;
				}
				kinged = true;
				gameBoard.getBoard()[x][0 + offset].setKing(true);
			}
			// If bottom of board, for P2, contains a P1 piece, and isn't
			// already a king, then king them.
			if (gameBoard.getBoard()[x][sizeY - offset].getOccupant() == BlockOccupant.PLAYER) {
				if (gameBoard.getBoard()[x][sizeY - offset].isKing()) {
					continue;
				}
				kinged = true;
				gameBoard.getBoard()[x][sizeY - offset].setKing(true);
			}
		}
		return kinged;

	}

	/**
	 * Creates and returns a copy of the current Checkers Game.
	 * 
	 * @return
	 */
	public CheckersGame copy() {
		// Create a new game with the same opponent, type, and settings.
		CheckersGame newGame = new CheckersGame(this.getGameOpponent(),
				this.getGameType(), settingsManager);
		// Set game variables to current game variables.
		newGame.playerTurn = this.getPlayerTurn();
		newGame.gameOver = this.isGameOver();
		if (this.activePiece != null) {
			// Active piece is set to a copy of current active piece.
			newGame.setActive(this.activePiece.copy());
		} else {
			newGame.setActive(null);
		}
		// Board is set to a copy of the board.
		newGame.gameBoard = this.gameBoard.copy();
		newGame.setPieceCountP1(this.pieceCount_P1);
		newGame.setPieceCountP2(this.pieceCount_P2);
		if (continueJumpPiece != null) {
			// Jump piece is set to a copy of the continued jump piece.
			newGame.setJumpPiece(this.continueJumpPiece.copy());
		} else {
			newGame.setJumpPiece(null);
		}
		// Game state listeners are added to new game.
		for (GameStateListener<CheckersGame> gsl : this.gameStateListeners) {
			newGame.addStateListener(gsl);
		}
		return newGame;
	}

	/**
	 * Sets player 1 piece count to given value.
	 * 
	 * @param val
	 */
	private void setPieceCountP1(int val) {
		this.pieceCount_P1 = val;
	}

	/**
	 * Sets player 2 piece count to given value.
	 * 
	 * @param val
	 */
	private void setPieceCountP2(int val) {
		this.pieceCount_P2 = val;
	}

	/**
	 * Sets the current continuing jump-piece to given Game Block. This is the
	 * piece that is used to make a multi-jump.
	 * 
	 * @param piece
	 */
	private void setJumpPiece(GameBlock piece) {
		this.continueJumpPiece = piece;
	}

	/**
	 * Ends the game, setting winner to given BlockOccupant player.
	 * 
	 * @param player
	 */
	private void gameOver(BlockOccupant player) {
		gameOver = true;
		for (GameStateListener<CheckersGame> gsl : gameStateListeners) {
			gsl.gameOver(this, player);
		}
	}

	/**
	 * Checks if a move from give source to given destination is valid.
	 * 
	 * @param srcPiece
	 * @param destPiece
	 * @return
	 */
	private boolean isValidMove(GameBlock srcPiece, GameBlock destPiece) {
		// If the source or destination are disabled blocks, it isn't a valid
		// move.
		if (srcPiece.isDisabled() || destPiece.isDisabled()) {
			return false;
		}
		// Collect the grid positions
		int srcLocX = srcPiece.getGridX();
		int srcLocY = srcPiece.getGridY();
		int destLocX = destPiece.getGridX();
		int destLocY = destPiece.getGridY();
		int sizeX = gameBoard.getSizeX() - 1;
		int sizeY = gameBoard.getSizeY() - 1;
		// The destination is not empty, null block, off the board, or another
		// piece requires a jump
		if (destPiece.getOccupant() != BlockOccupant.EMPTY
				|| destPiece.getOccupant() == BlockOccupant.NULL
				|| (srcLocY > destLocY && !srcPiece.isKing() && playerTurn == 1)
				|| (srcLocY < destLocY && !srcPiece.isKing() && playerTurn == 2)
				|| destLocY > sizeY
				|| destLocX > sizeX
				|| destLocY < 0
				|| destLocX < 0
				|| (continueJumpPiece != null && !srcPiece.getID().equals(
						continueJumpPiece.getID()))) {
			return false;
		}

		// Checking if jump attempt is valid.
		// Collect jump sets
		ArrayList<ArrayList<GameBlock>> jumpSet = getJumpSet(srcPiece);
		ArrayList<GameBlock> jumpPieces_NW = jumpSet.get(0);
		ArrayList<GameBlock> jumpPieces_NE = jumpSet.get(1);
		ArrayList<GameBlock> jumpPieces_SW = jumpSet.get(2);
		ArrayList<GameBlock> jumpPieces_SE = jumpSet.get(3);

		// South East jump isn't off board
		if (srcLocX + 2 <= sizeX && srcLocY + 2 <= sizeY) {
			// That direction is a jump only Player 1 can make naturally, or
			// Player 2 jump in that direction if they are kinged.
			if ((jumpPieces_SE.contains(srcPiece) && playerTurn == 1 && gameBoard
					.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)
					|| (jumpPieces_SE.contains(srcPiece) && playerTurn == 2
							&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)) {
				return true;
			}
		}
		// South West jump isn't off board
		if (srcLocX - 2 >= 0 && srcLocY + 2 <= sizeY) {
			// That direction is a jump only Player 1 can make naturally, or
			// Player 2 jump in that direction if they are kinged.
			if ((jumpPieces_SW.contains(srcPiece) && playerTurn == 1 && gameBoard
					.getBoard()[srcLocX - 2][srcLocY + 2] == destPiece)
					|| (jumpPieces_SW.contains(srcPiece) && playerTurn == 2
							&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX - 2][srcLocY + 2] == destPiece)) {
				return true;
			}
		}
		// North West Jump isn't off board
		if (srcLocX - 2 >= 0 && srcLocY - 2 >= 0) {
			// That direction is a jump only Player 2 can make naturally, or
			// Player 1 jump in that direction if they are kinged.
			if ((jumpPieces_NW.contains(srcPiece) && playerTurn == 1
					&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX - 2][srcLocY - 2] == destPiece)
					|| (jumpPieces_NW.contains(srcPiece) && playerTurn == 2 && gameBoard
							.getBoard()[srcLocX - 2][srcLocY - 2] == destPiece)) {
				return true;
			}
		}
		// North East Jump isn't off board
		if (srcLocX + 2 <= sizeX && srcLocY - 2 >= 0) {
			// That direction is a jump only Player 2 can make naturally, or
			// Player 1 jump in that direction if they are kinged.
			if ((jumpPieces_NE.contains(srcPiece) && playerTurn == 1
					&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX + 2][srcLocY - 2] == destPiece)
					|| (jumpPieces_NE.contains(srcPiece) && playerTurn == 2 && gameBoard
							.getBoard()[srcLocX + 2][srcLocY - 2] == destPiece)) {
				return true;
			}
		}

		// Checks a non-jump, single, move. If player cannot make a move
		// naturally, they must be a king.
		if (((srcLocX + 1 == destLocX && srcLocY + 1 == destLocY && playerTurn == 1)
				|| (srcLocX - 1 == destLocX && srcLocY + 1 == destLocY && playerTurn == 1)
				|| (srcLocX - 1 == destLocX && srcLocY - 1 == destLocY
						&& playerTurn == 1 && srcPiece.isKing())
				|| (srcLocX + 1 == destLocX && srcLocY - 1 == destLocY
						&& playerTurn == 1 && srcPiece.isKing())
				|| (srcLocX + 1 == destLocX && srcLocY + 1 == destLocY
						&& srcPiece.isKing() && playerTurn == 2)
				|| (srcLocX - 1 == destLocX && srcLocY + 1 == destLocY
						&& srcPiece.isKing() && playerTurn == 2)
				|| (srcLocX - 1 == destLocX && srcLocY - 1 == destLocY && playerTurn == 2) || (srcLocX + 1 == destLocX
				&& srcLocY - 1 == destLocY && playerTurn == 2))
				&& (continueJumpPiece == null)) {
			return true;
		}
		// All conditions fail, move isn't valid.
		return false;
	}

	/**
	 * Gets and returns a collection of smaller collections, which store
	 * organized possible jumps available for given source piece. Element 0 is
	 * North-West direction collection, Element 1 is North-East direction
	 * collection, Element 2 is South-West direction collection, Element 3 is
	 * South-East direction collection.
	 * 
	 * @param srcPiece
	 * @return
	 */
	private ArrayList<ArrayList<GameBlock>> getJumpSet(GameBlock srcPiece) {
		int srcLocX = srcPiece.getGridX();
		int srcLocY = srcPiece.getGridY();
		int sizeX = gameBoard.getSizeX() - 1;
		int sizeY = gameBoard.getSizeY() - 1;
		BlockOccupant opponent = null;
		if (playerTurn == 1) {
			opponent = BlockOccupant.PLAYER2;
		} else if (playerTurn == 2) {
			opponent = BlockOccupant.PLAYER;
		}
		ArrayList<ArrayList<GameBlock>> jumpSet = new ArrayList<ArrayList<GameBlock>>();
		ArrayList<GameBlock> jumpPieces_NW = new ArrayList<GameBlock>();
		ArrayList<GameBlock> jumpPieces_NE = new ArrayList<GameBlock>();
		ArrayList<GameBlock> jumpPieces_SW = new ArrayList<GameBlock>();
		ArrayList<GameBlock> jumpPieces_SE = new ArrayList<GameBlock>();

		// Loop over board
		for (int y = 0; y < gameBoard.getSizeY(); y++) {
			for (int x = 0; x < gameBoard.getSizeX(); x++) {

				// Check for the South West jumps
				if (srcLocX - 2 >= 0 && srcLocY + 2 <= sizeY) {
					if (gameBoard.getBoard()[srcLocX - 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX - 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_SW.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
				// Check for the South East Jumps
				if (srcLocX + 2 <= sizeX && srcLocY + 2 <= sizeY) {
					if (gameBoard.getBoard()[srcLocX + 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX + 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_SE.add(srcPiece);
						}

					}
				}
				// Check for the North East jumps
				if (srcLocX + 2 <= sizeX && srcLocY - 2 >= 0) {
					if (gameBoard.getBoard()[srcLocX + 1][srcLocY - 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX + 2][srcLocY - 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NE.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
				// Check for North West jumps
				if (srcLocX - 2 >= 0 && srcLocY - 2 >= 0) {
					if (gameBoard.getBoard()[srcLocX - 1][srcLocY - 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX - 2][srcLocY - 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NW.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
			}
		}
		// This order is essential for other methods using the jumpSet
		// collection. Do not change.
		jumpSet.add(jumpPieces_NW);
		jumpSet.add(jumpPieces_NE);
		jumpSet.add(jumpPieces_SW);
		jumpSet.add(jumpPieces_SE);
		return jumpSet;
	}

	/**
	 * Get and return scores. Player 1 in index 0, Player 2 in index 1. Score
	 * based on piece count, worth 1 point each, and kinged pieces, worth an
	 * additional 1 point.
	 * 
	 * @return
	 */
	public int[] getScores() {
		int[] scores = new int[2];
		// Initial points based on piece count.
		scores[0] = this.getPieceCount(BlockOccupant.PLAYER);
		scores[1] = this.getPieceCount(BlockOccupant.PLAYER2);
		// Traverse board, kinged pieces gain additional points.
		for (int y = 0; y < this.getGameBoard().getSizeY(); y++) {
			for (int x = 0; x < this.getGameBoard().getSizeX(); x++) {
				GameBlock gameBlock = this.getGameBoard().getBoard()[x][y];
				if (gameBlock.isKing()) {
					if (gameBlock.getOccupant() == BlockOccupant.PLAYER) {
						scores[0]++;
					} else if (gameBlock.getOccupant() == BlockOccupant.PLAYER2) {
						scores[1]++;
					}
				}
			}
		}
		return scores;
	}

	/**
	 * Updates game functionality based on Reverse Mode game type.
	 */
	public void updateReverseMode() {
		// List of disabled blocks, for closing edges.
		ArrayList<GameBlock> disabled = new ArrayList<GameBlock>();

		int x_max = 0;
		int y_max = 0;
		int x_min = 0;
		int y_min = 0;
		// Get next corner piece that is still available.
		for (int i = 0; i < gameBoard.getSizeX(); i++) {
			if (!gameBoard.getBoard()[i][i].isDisabled()) {
				x_min = y_min = i;
				x_max = y_max = gameBoard.getSizeX() - i - 1;
				break;
			}
		}
		// Minimum size is 4 x 4
		if (x_max - x_min < 4) {
			return;
		}
		// If minimum size not met, disable edge pieces.
		for (int i = x_min; i <= x_max; i++) {
			GameBlock block1 = gameBoard.getBoard()[i][y_min];
			GameBlock block2 = gameBoard.getBoard()[i][y_max];
			GameBlock block3 = gameBoard.getBoard()[x_min][i];
			GameBlock block4 = gameBoard.getBoard()[x_max][i];
			// If any edge block is occupied, no blocks are disabled.
			if ((block1.getOccupant() != BlockOccupant.NULL && block1
					.getOccupant() != BlockOccupant.EMPTY)
					|| (block2.getOccupant() != BlockOccupant.NULL && block2
							.getOccupant() != BlockOccupant.EMPTY)
					|| (block3.getOccupant() != BlockOccupant.NULL && block3
							.getOccupant() != BlockOccupant.EMPTY)
					|| (block4.getOccupant() != BlockOccupant.NULL && block4
							.getOccupant() != BlockOccupant.EMPTY)) {
				return;
			}
			// Edge is clear, add disabled blocks.
			disabled.add(block1);
			disabled.add(block2);
			disabled.add(block3);
			disabled.add(block4);
		}

		// Traverse disabled collection, set blocks as disabled.
		for (GameBlock b : disabled) {
			b.setDisabled(true);
		}
	}
}
