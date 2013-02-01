import java.util.ArrayList;

public class CheckersGame implements AIAdapter {

	// Reference to game board
	private CheckersBoard gameBoard;
	// Manager to store settings across games
	private CheckersSettingsManager settingsManager;
	// Collection of previous game states.
	private ArrayList<CheckersGame> gameHistory;
	// Collection of available moves
	private ArrayList<MoveData> availableMoves;
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
	private ArrayList<GameStateListener> gameStateListeners;
	// Type of game, two-player, ai-easy, ai-medium, ai-hard.
	private GameType gameType;

	public CheckersGame(GameType gameType, CheckersSettingsManager settingsManager) {

		this.settingsManager = settingsManager;
		this.gameHistory = new ArrayList<CheckersGame>();
		this.availableMoves = new ArrayList<MoveData>();
		this.gameStateListeners = new ArrayList<GameStateListener>();
		this.gameBoard = new CheckersBoard();
		this.gameOver = false;
		this.continueJumpPiece = null;
		this.playerTurn = 1;
		this.pieceCount_P1 = this.pieceCount_P2 = 12;
		this.gameType = gameType;
		updateAvailableMoves();
		if (gameType != GameType.TWO_PLAYERS) {
			// Create new AI
		}
	}

	public void undo() {
		if (!gameHistory.isEmpty()) {
			int undoIndex = gameHistory.size() - 1;
			CheckersGame prev = gameHistory.get(undoIndex);
			this.gameBoard = prev.getGameBoard();
			this.setActive(prev.getActivePiece());
			this.setGameOver(prev.isGameOver());
			this.setPlayerTurn(prev.getPlayerTurn());
			this.setPieceCountP1(prev.getPieceCount(BlockOccupant.PLAYER));
			this.setPieceCountP2(prev.getPieceCount(BlockOccupant.PLAYER2));
			gameHistory.remove(undoIndex);
			checkGameState(false);

		}
	}
	
	public ArrayList<CheckersGame> getGameHistory(){
		return (ArrayList<CheckersGame>) gameHistory.clone();
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setPlayerTurn(int player) {
		this.playerTurn = player;
	}

	public void setGameOver(boolean flag) {
		this.gameOver = flag;
	}
	
	public ArrayList<GameStateListener> getGameStateListeners() {
		return this.gameStateListeners;
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
			for(MoveData md : availableMoves){
				if(activePiece.equals(md.getSrcPiece())){
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
		}
		else {
			activePiece = block;
			activePiece.setSelected(true);
		}
		if(settingsManager.isMoveGuides()){
			if(activePiece == null){
				for(MoveData md : availableMoves){
					md.getSrcPiece().setHighlightAsMovable(true);
				}
			} else {
				for(MoveData md : availableMoves){
					md.getSrcPiece().setHighlightAsMovable(false);
					if(activePiece.equals(md.getSrcPiece())){
						md.getDestPiece().setHighlightAsTarget(true);
					}
				}
			}
		}
	}

	public void addStateListener(GameStateListener listener) {
		gameStateListeners.add(listener);
		for(GameStateListener gsl : gameStateListeners){
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
	
	public void clearMoveHighlights(){
		boolean flag = settingsManager.isMoveGuides();
		settingsManager.setMoveGuides(false);
		updateAvailableMoves();
		settingsManager.setMoveGuides(flag);
	}
	
	public void updateAvailableMoves() {
		for(MoveData md : availableMoves){
			md.getSrcPiece().setHighlightAsMovable(false);
		}
		availableMoves.clear();
		
		boolean jumpMoveExist = false;
		
		for (int y = 0; y < gameBoard.getSizeY(); y++) {
			for (int x = 0; x < gameBoard.getSizeX(); x++) {
				GameBlock[][] grid = gameBoard.getBoard();
				GameBlock piece = gameBoard.getBoard()[x][y];
				if (piece.getOccupant() == BlockOccupant.EMPTY
						|| piece.getOccupant() == BlockOccupant.NULL) {
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
					if (x + 1 < gameBoard.getSizeX() - 1 && y - 1> 0) {
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
		if(settingsManager.isForceJumps() && jumpMoveExist){
			ArrayList<MoveData> tempClone = new ArrayList<MoveData>();
			tempClone = (ArrayList<MoveData>) availableMoves.clone();
			for(MoveData md : tempClone){
				if(md.getJumpedPiece() == null){
					availableMoves.remove(md);
				}
			}
		}
		if(settingsManager.isMoveGuides()){
			for(MoveData md : availableMoves){
				md.getSrcPiece().setHighlightAsMovable(true);
			}
		}
	}

	public boolean move(GameBlock srcPiece, GameBlock destPiece) {
		gameHistory.add(this.copy());
		for (MoveData md : availableMoves) {
			if (md.getSrcPiece().equals(srcPiece)
					&& md.getDestPiece().equals(destPiece)) {
				gameBoard.swapPiece(srcPiece, destPiece);
				if (md.getJumpedPiece() != null) {
					if(md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER){
						pieceCount_P1--;
					} else if(md.getJumpedPiece().getOccupant() == BlockOccupant.PLAYER2){
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

		boolean kinged = checkKing();
		if (pieceCount_P1 == 0) {
			gameOver(BlockOccupant.PLAYER2);
			return;
		} else if (pieceCount_P2 == 0) {
			gameOver(BlockOccupant.PLAYER);
			return;
		}
		if (kinged) {
			continueJumpPiece = null;
		}
		if (continueJumpPiece == null) {
			if (rotateTurns) {
				if (playerTurn == 1) {
					playerTurn = 2;

					if (gameType != GameType.TWO_PLAYERS) {
						// Perform AI move
					}
				} else if (playerTurn == 2) {
					playerTurn = 1;
				}
			}
		}
		for(GameStateListener gsl : gameStateListeners){
			gsl.boardChange(this);
		}
		updateAvailableMoves();
	}

	private boolean checkKing() {
		for (int x = 0; x < 8; x++) {
			if (gameBoard.getBoard()[x][0].getOccupant() == BlockOccupant.PLAYER2) {
				if (gameBoard.getBoard()[x][0].isKing()) {
					continue;
				}
				gameBoard.getBoard()[x][0].setKing(true);
				return true;
			}
		}
		for (int x = 0; x < 8; x++) {
			if (gameBoard.getBoard()[x][7].getOccupant() == BlockOccupant.PLAYER) {
				if (gameBoard.getBoard()[x][7].isKing()) {
					continue;
				}
				gameBoard.getBoard()[x][7].setKing(true);
				return true;
			}
		}
		return false;

	}

	public CheckersGame copy() {
		CheckersGame newGame = new CheckersGame(this.getGameType(), settingsManager);
		newGame.setPlayerTurn(this.getPlayerTurn());
		newGame.setGameOver(this.isGameOver());
		try {
			newGame.setActive(this.activePiece.copy());
		} catch (NullPointerException e) {
			newGame.setActive(null);
		}
		try {
			newGame.setGameBoard(this.gameBoard.copy());
		} catch (NullPointerException e) {
			System.exit(1);
		}
		newGame.setPieceCountP1(this.pieceCount_P1);
		newGame.setPieceCountP2(this.pieceCount_P2);
		try {
			newGame.setJumpPiece(this.continueJumpPiece.copy());
		} catch (NullPointerException e) {
			newGame.setJumpPiece(null);
		}

		for(GameStateListener gsl : this.gameStateListeners){
			newGame.addStateListener(gsl);
		}
		return newGame;
	}

	private void setGameBoard(CheckersBoard board) {
		this.gameBoard = board;
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
		setGameOver(true);
		for(GameStateListener gsl : gameStateListeners){
			gsl.gameOver(player);
		}
		System.out.println(player.getStringValue() + " wins.");
	}

	private boolean isValidMove(GameBlock srcPiece, GameBlock destPiece) {
		int srcLocX = srcPiece.getGridX();
		int srcLocY = srcPiece.getGridY();
		int destLocX = destPiece.getGridX();
		int destLocY = destPiece.getGridY();

		// The destination is not empty, null block, off the board, or another
		// piece requires a jump
		if (destPiece.getOccupant() != BlockOccupant.EMPTY
				|| destPiece.getOccupant() == BlockOccupant.NULL
				|| (srcLocY > destLocY && !srcPiece.isKing() && playerTurn == 1)
				|| (srcLocY < destLocY && !srcPiece.isKing() && playerTurn == 2)
				|| destLocY > 7
				|| destLocX > 7
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

		if (srcLocX + 2 <= 7 && srcLocY + 2 <= 7) {
			if ((jumpPieces_NE.contains(srcPiece) && playerTurn == 1 && gameBoard
					.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)
					|| (jumpPieces_NE.contains(srcPiece) && playerTurn == 2
							&& srcPiece.isKing() && gameBoard.getBoard()[srcLocX + 2][srcLocY + 2] == destPiece)) {
				return true;
			}
		}
		if (srcLocX - 2 >= 0 && srcLocY + 2 <= 7) {
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
		if (srcLocX + 2 <= 7 && srcLocY - 2 >= 0) {
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
				if (srcLocX - 2 >= 0 && srcLocY + 2 <= 7) {
					if (gameBoard.getBoard()[srcLocX - 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX - 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NW.add(gameBoard.getBoard()[x][y]);
						}
					}
				}
				if (srcLocX + 2 <= 7 && srcLocY + 2 <= 7) {
					if (gameBoard.getBoard()[srcLocX + 1][srcLocY + 1]
							.getOccupant() == opponent) {
						if (gameBoard.getBoard()[srcLocX + 2][srcLocY + 2]
								.getOccupant() == BlockOccupant.EMPTY) {
							jumpPieces_NE.add(srcPiece);
						}

					}
				}
				if (srcLocX + 2 <= 7 && srcLocY - 2 >= 0) {
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

	@Override
	public int[] getScores(AINode node) {
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
}
