
/**
 * CheckersBoard object class which extends Board
 *
 */
public class CheckersBoard extends Board {

	public CheckersBoard() {
		//Create 8x8 board
		super(8, 8);
		//Even horizontal-blocks null?
		boolean null_even_x;

		// Nullify unusable blocks
		for (int y = 0; y < super.getSizeY(); y++) {
			if (y % 2 == 0) {
				null_even_x = true;
			} else {
				null_even_x = false;
			}
			for (int x = 0; x < super.getSizeX(); x++) {
				if ((null_even_x && x % 2 == 0) || (!null_even_x && x % 2 != 0)) {
					super.getBoard()[x][y].setOccupant(BlockOccupant.NULL);
				}
			}
		}

		// Fill Player 1 blocks.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < super.getSizeX(); x++) {
				if (super.getBoard()[x][y].getOccupant() != BlockOccupant.NULL) {
					super.getBoard()[x][y].setOccupant(BlockOccupant.PLAYER);
				}
			}
		}
		// Fill Player 2 blocks.
		for (int y = 5; y < 8; y++) {
			for (int x = 0; x < super.getSizeX(); x++) {
				if (super.getBoard()[x][y].getOccupant() != BlockOccupant.NULL) {
					super.getBoard()[x][y].setOccupant(BlockOccupant.PLAYER2);
				}
			}
		}
	}

	/**
	 * Create and return copy of this instance of CheckersBoard
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Creates and returns new replica.
	 *<b>Throws:</b> None
	 */
	public CheckersBoard copy() {
		CheckersBoard newBoard = new CheckersBoard();
		for (int y = 0; y < newBoard.getSizeY(); y++) {
			for (int x = 0; x < newBoard.getSizeX(); x++) {
				newBoard.getBoard()[x][y] = this.getBoard()[x][y].copy();
			}
		}

		return newBoard;
	}

	public String toString() {
		return super.toString();
	}
}
