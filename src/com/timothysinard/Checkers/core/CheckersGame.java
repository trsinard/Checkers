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
		if (gameOpponent != GameOpponent.PLAYER) {
			// Create new AI
		}
	}

	public void undo() {
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

	public ArrayList<CheckersGame> getGameHistory() {
		return new ArrayList<CheckersGame>(gameHistory);
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public GameOpponent getGameOpponent() {
		return gameOpponent;
	}

	public GameType getGameType() {
		return gameType;
	}

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

	public int getPlayerTurn() {
		return playerTurn;
	}

	public CheckersBoard getGameBoard() {
		return gameBoard;
	}

	public GameBlock getActivePiece() {
		return activePiece;
	}

	public void setActive(GameBlock block) {
		if (activePiece != null) {
			for (MoveData md : availableMoves) {
				if (activePiece.equals(md.getSrcPiece())) {
					md.getDestPiece().setHighlightAsTarget(false);
				}
			}
			activePiece.setSelected(false);
			if (activePiece != block) {
				move(activePiece, block);
			}
			activePiece = continueJumpPiece;
			if (continueJumpPiece != null) {
				activePiece.setSelected(true);
			}
		} else if (block.getOccupant() == BlockOccupant.EMPTY
				|| block.getOccupant() == BlockOccupant.NULL) {
			activePiece = null;
		} else {
			activePiece = block;
			activePiece.setSelected(true);
		}
		if (settingsManager.isMoveGuides()) {
			if (activePiece == null) {
				for (MoveData md : availableMoves) {
					md.getSrcPiece().setHighlightAsMovable(true);
				}
			} else {
				for (MoveData md : availableMoves) {
					md.getSrcPiece().setHighlightAsMovable(false);
					if (activePiece.equals(md.getSrcPiece())) {
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

	private boolean isJump(GameBlock srcPiece, GameBlock destPiece) {
		GameBlock[][] grid = gameBoard.getBoard();
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

	private boolean isContJump(GameBlock srcPiece, GameBlock destPiece) {
		boolean jumped = isJump(srcPiece, destPiece);

		if (jumped) {
			ArrayList<ArrayList<GameBlock>> jumpSet = getJumpSet(srcPiece);
			ArrayList<GameBlock> jumpPieces_NW = jumpSet.get(0);
			ArrayList<GameBlock> jumpPieces_NE = jumpSet.get(1);
			ArrayList<GameBlock> jumpPieces_SW = jumpSet.get(2);
			ArrayList<GameBlock> jumpPieces_SE = jumpSet.get(3);
			if (!jumpPieces_NW.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() - 2][srcPiece
								.getGridY() + 2])) {
					return true;
				}
			}
			if (!jumpPieces_NE.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() + 2][srcPiece
								.getGridY() + 2])) {
					return true;
				}
			}
			if (!jumpPieces_SW.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() - 2][srcPiece
								.getGridY() - 2])) {
					return true;
				}
			}
			if (!jumpPieces_SE.isEmpty()) {
				if (isValidMove(srcPiece,
						gameBoard.getBoard()[srcPiece.getGridX() + 2][srcPiece
								.getGridY() - 2])) {
					return true;
				}
			}
		}
		return false;
	}

	public void clearMoveHighlights() {
		boolean flag = settingsManager.isMoveGuides();
		settingsManager.setMoveGuides(false);
		updateAvailableMoves();
		settingsManager.setMoveGuides(flag);
	}

	public void updateAvailableMoves() {
		for (MoveData md : availableMoves) {
			md.getSrcPiece().setHighlightAsMovable(false);
		}
		availableMoves.clear();

		boolean jumpMoveExist = false;

		for (int y = 0; y < gameBoard.getSizeY(); y++) {
			for (int x = 0; x < gameBoard.getSizeX(); x++) {
				GameBlock[][] grid = gameBoard.getBoard();
				GameBlock piece = gameBoard.getBoard()[x][y];
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
		if (settingsManager.isForceJumps() && jumpMoveExist) {
			ArrayList<MoveData> tempClone = new ArrayList<MoveData>(
					availableMoves);
			for (MoveData md : tempClone) {
				if (md.getJumpedPiece() == null) {
					availableMoves.remove(md);
				}
			}
		}
		if (settingsManager.isMoveGuides()) {
			for (MoveData md : availableMoves) {
				md.getSrcPiece().setHighlightAsMovable(true);
			}
		}
	}

	public boolean move(GameBlock srcPiece, GameBlock destPiece) {
		if (gameOver) {
			return false;
		}
		gameHistory.add(this.copy());
		for (MoveData md : availableMoves) {
			if (md.getSrcPiece().equals(srcPiece)
					&& md.getDestPiece().equals(destPiece)) {
				gameBoard.swapPiece(srcPiece, destPiece);
				if (md.getJumpedPiece() != null) {
					if (md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER) {
						pieceCount_P1--;
					} else if (md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER2) {
						pieceCount_P2--;
					}
					gameBoard.eliminatePiece(md.getJumpedPiece());
					if (isContJump(srcPiece, destPiece)) {
						continueJumpPiece = srcPiece;
					} else {
						continueJumpPiece = null;
					}
				}

				if (continueJumpPiece != null) {
					checkGameState(false);
				} else {
					checkGameState(true);
				}
				return true;
			}
		}
		gameHistory.remove(gameHistory.size() - 1);
		return false;
	}

	private void checkGameState(boolean rotateTurns) {
		if (gameOver) {
			return;
		}
		boolean kinged = checkKing();
		if (pieceCount_P1 == 0) {
			if (gameType == GameType.REGULAR) {
				gameOver(BlockOccupant.PLAYER2);
			} else if (gameType == GameType.REVERSE) {
				gameOver(BlockOccupant.PLAYER);
			}
			return;
		} else if (pieceCount_P2 == 0) {
			if (gameType == GameType.REGULAR) {
				gameOver(BlockOccupant.PLAYER);
			} else if (gameType == GameType.REVERSE) {
				gameOver(BlockOccupant.PLAYER2);
			}
			return;
		}
		if (kinged) {
			continueJumpPiece = null;
		}
		if (continueJumpPiece == null) {
			if (rotateTurns) {
				if (playerTurn == BlockOccupant.PLAYER.getValue()) {
					playerTurn = BlockOccupant.PLAYER2.getValue();

					if (gameOpponent != GameOpponent.PLAYER) {
						// Perform AI move
					}
				} else if (playerTurn == BlockOccupant.PLAYER2.getValue()) {
					playerTurn = BlockOccupant.PLAYER.getValue();
				}
			}
		}
		for (GameStateListener<CheckersGame> gsl : gameStateListeners) {
			gsl.boardChange(this);
		}

		if (gameType == GameType.REVERSE) {
			updateReverseMode();
		}
		updateAvailableMoves();
		if (availableMoves.isEmpty()) {
			if (playerTurn == BlockOccupant.PLAYER.getValue()) {
				gameOver(BlockOccupant.PLAYER2);
			} else if (playerTurn == BlockOccupant.PLAYER2.getValue()) {
				gameOver(BlockOccupant.PLAYER);
			}
		}
	}

	private boolean checkKing() {
		int offset = 0;
		boolean kinged = false;
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
		for (int x = offset; x <= sizeX - offset; x++) {
			if (gameBoard.getBoard()[x][0 + offset].getOccupant() == BlockOccupant.PLAYER2) {
				if (gameBoard.getBoard()[x][0 + offset].isKing()) {
					continue;
				}
				kinged = true;
				gameBoard.getBoard()[x][0 + offset].setKing(true);
			}
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

	public CheckersGame copy() {
		CheckersGame newGame = new CheckersGame(this.getGameOpponent(),
				this.getGameType(), settingsManager);
		newGame.playerTurn = this.getPlayerTurn();
		newGame.gameOver = this.isGameOver();
		if (this.activePiece != null) {
			newGame.setActive(this.activePiece.copy());
		} else {
			newGame.setActive(null);
		}
		newGame.gameBoard = this.gameBoard.copy();
		newGame.setPieceCountP1(this.pieceCount_P1);
		newGame.setPieceCountP2(this.pieceCount_P2);
		if (continueJumpPiece != null) {
			newGame.setJumpPiece(this.continueJumpPiece.copy());
		} else {
			newGame.setJumpPiece(null);
		}

		for (GameStateListener<CheckersGame> gsl : this.gameStateListeners) {
			newGame.addStateListener(gsl);
		}
		return newGame;
	}

	private void setPieceCountP1(int val) {
		this.pieceCount_P1 = val;
	}

	private void setPieceCountP2(int val) {
		this.pieceCount_P2 = val;
	}

	private void setJumpPiece(GameBlock piece) {
		this.continueJumpPiece = piece;
	}

	private void gameOver(BlockOccupant player) {
		// Perform gameover operations
		// ---------------------------------------------
		gameOver = true;
		for (GameStateListener<CheckersGame> gsl : gameStateListeners) {
			gsl.gameOver(this, player);
		}
	}

	private boolean isValidMove(GameBlock srcPiece, GameBlock destPiece) {
		if (srcPiece.isDisabled() || destPiece.isDisabled()) {
			return false;
		}
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

		ArrayList<ArrayList<GameBlock>> jumpSet = getJumpSet(srcPiece);
		ArrayList<GameBlock> jumpPieces_NW = jumpSet.get(0);
		ArrayList<GameBlock> jumpPieces_NE = jumpSet.get(1);
		ArrayList<GameBlock> jumpPieces_SW = jumpSet.get(2);
		ArrayList<GameBlock> jumpPieces_SE = jumpSet.get(3);

		if (srcLocX + 2 <= sizeX && srcLocY + 2 <= sizeY) {
			if ((jumpPieces_NE.contains(srcPiece) && playerTurn == 1 && gameBoard
					.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)
					|| (jumpPieces_NE.contains(srcPiece) && playerTurn == 2
							&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)) {
				return true;
			}
		}
		if (srcLocX - 2 >= 0 && srcLocY + 2 <= sizeY) {
			if ((jumpPieces_NW.contains(srcPiece) && playerTurn == 1 && gameBoard
					.getBoard()[srcLocX - 2][srcLocY + 2] == destPiece)
					|| (jumpPieces_NW.contains(srcPiece) && playerTurn == 2
							&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX - 2][srcLocY + 2] == destPiece)) {
				return true;
			}
		}
		if (srcLocX - 2 >= 0 && srcLocY - 2 >= 0) {
			if ((jumpPieces_SW.contains(srcPiece) && playerTurn == 1
					&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX - 2][srcLocY - 2] == destPiece)
					|| (jumpPieces_SW.contains(srcPiece) && playerTurn == 2 && gameBoard
							.getBoard()[srcLocX - 2][srcLocY - 2] == destPiece)) {
				return true;
			}
		}
		if (srcLocX + 2 <= sizeX && srcLocY - 2 >= 0) {
			if ((jumpPieces_SE.contains(srcPiece) && playerTurn == 1
					&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX + 2][srcLocY - 2] == destPiece)
					|| (jumpPieces_SE.contains(srcPiece) && playerTurn == 2 && gameBoard
							.getBoard()[srcLocX + 2][srcLocY - 2] == destPiece)) {
				return true;
			}
		}

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
		return false;
	}

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
		for (int y = 0; y < gameBoard.getSizeY(); y++) {
			for (int x = 0; x < gameBoard.getSizeX(); x++) {
				if (srcLocX - 2 >= 0 && srcLocY + 2 <= sizeY) {
					if (gameBoard.getBoard()[srcLocX - 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX - 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NW.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
				if (srcLocX + 2 <= sizeX && srcLocY + 2 <= sizeY) {
					if (gameBoard.getBoard()[srcLocX + 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX + 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NE.add(srcPiece);
						}

					}
				}
				if (srcLocX + 2 <= sizeX && srcLocY - 2 >= 0) {
					if (gameBoard.getBoard()[srcLocX + 1][srcLocY - 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX + 2][srcLocY - 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_SE.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
				if (srcLocX - 2 >= 0 && srcLocY - 2 >= 0) {
					if (gameBoard.getBoard()[srcLocX - 1][srcLocY - 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX - 2][srcLocY - 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_SW.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
			}
		}
		jumpSet.add(jumpPieces_NW);
		jumpSet.add(jumpPieces_NE);
		jumpSet.add(jumpPieces_SW);
		jumpSet.add(jumpPieces_SE);
		return jumpSet;
	}

	public int[] getScores() {
		int[] scores = new int[2];
		scores[0] = this.getPieceCount(BlockOccupant.PLAYER);
		scores[1] = this.getPieceCount(BlockOccupant.PLAYER2);
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

	public void updateReverseMode() {
		ArrayList<GameBlock> disabled = new ArrayList<GameBlock>();

		int x_max = 0;
		int y_max = 0;
		int x_min = 0;
		int y_min = 0;
		for (int i = 0; i < gameBoard.getSizeX(); i++) {
			if (!gameBoard.getBoard()[i][i].isDisabled()) {
				x_min = y_min = i;
				x_max = y_max = gameBoard.getSizeX() - i - 1;
				break;
			}
		}
		if (x_max - x_min < 4) {
			return;
		}
		for (int i = x_min; i <= x_max; i++) {
			GameBlock block1 = gameBoard.getBoard()[i][y_min];
			GameBlock block2 = gameBoard.getBoard()[i][y_max];
			GameBlock block3 = gameBoard.getBoard()[x_min][i];
			GameBlock block4 = gameBoard.getBoard()[x_max][i];
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
			disabled.add(block1);
			disabled.add(block2);
			disabled.add(block3);
			disabled.add(block4);
		}

		for (GameBlock b : disabled) {
			b.setDisabled(true);
		}
	}
}
