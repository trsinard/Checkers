package com.timothysinard.Checkers.core;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.timothysinard.Checkers.gui.Drawable;
import com.timothysinard.Checkers.gui.Graphic;
import com.timothysinard.Checkers.gui.ScreenData;
import com.timothysinard.Checkers.utils.ThemeManager;

public class Board implements Drawable {

	// Store Board ID
	private final String id;
	// Store Board width and height
	private final int boardSizeX;
	private final int boardSizeY;
	// Store number of moves
	private int moveCount;
	// Collection of drawable graphic objects
	private final ArrayList<Drawable> graphics;
	// Store the layer the board will be drawn
	private final int layerZ = 0;
	// 2-Dimensional array representing the board grid
	private final GameBlock[][] board;

	public Board(int size_x, int size_y) {
		this.id = "drawable-board";
		this.boardSizeX = size_x;
		this.boardSizeY = size_y;
		this.graphics = new ArrayList<Drawable>();
		board = new GameBlock[boardSizeX][boardSizeY];
		// Fill board with empty blocks
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				board[x][y] = (new GameBlock(x + "" + y, BlockOccupant.EMPTY,
						x, y));
			}
		}
	}

	/**
	 * Get board width
	 * 
	 * @return
	 */
	public int getSizeX() {
		return boardSizeX;
	}

	/**
	 * Get board height
	 * 
	 * @return
	 */
	public int getSizeY() {
		return boardSizeY;
	}

	/**
	 * Get move count
	 * 
	 * @return
	 */
	public int getMoveCount() {
		return moveCount;
	}

	/**
	 * Get collection of drawable graphics
	 * 
	 * @return
	 */
	public ArrayList<Drawable> getGraphics() {
		return graphics;
	}

	/**
	 * Get 2-Dimensional board
	 * 
	 * @return
	 */
	public GameBlock[][] getBoard() {
		return board;
	}

	/**
	 * Set move count
	 * 
	 * @param val
	 */
	public void setMoveCount(int val) {
		moveCount = val;
	}

	/**
	 * Set grid position of given piece.
	 * 
	 * @param pos_x
	 * @param pos_y
	 * @param piece
	 */
	public void setPiece(int pos_x, int pos_y, GameBlock piece) {
		board[pos_x][pos_y] = piece;
	}

	/**
	 * Get piece from given grid position
	 * 
	 * @param pos_x
	 * @param pos_y
	 * @return
	 */
	public GameBlock getPiece(int pos_x, int pos_y) {
		return board[pos_x][pos_y];
	}

	@Override
	public String getID() {
		return id;
	}

	/**
	 * Add drawable graphic to drawable collection.
	 * 
	 * @param graphic
	 */
	public void addGraphic(Drawable graphic) {
		graphics.add(graphic);
	}

	/**
	 * Swap the grid position of two given pieces.
	 * 
	 * @param srcPiece
	 * @param destPiece
	 */
	public void swapPiece(GameBlock srcPiece, GameBlock destPiece) {
		int srcLocX = srcPiece.getGridX();
		int srcLocY = srcPiece.getGridY();
		int destLocX = destPiece.getGridX();
		int destLocY = destPiece.getGridY();

		srcPiece.setGridX(destLocX);
		srcPiece.setGridY(destLocY);
		destPiece.setGridX(srcLocX);
		destPiece.setGridY(srcLocY);
		board[destLocX][destLocY] = srcPiece;
		board[srcLocX][srcLocY] = destPiece;
	}

	@Override
	public int getZ() {
		return layerZ;
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {

		BufferedImage image = ThemeManager.getThemeManager().getImage(
				"background");
		if (image != null) {
			// Draw rescaled image
			g.drawImage(image, 0, 0,
					(int) Math.round(image.getWidth() * sd.getScaleRatio()),
					(int) Math.round(image.getHeight() * sd.getScaleRatio()),
					null);
		}

		// Draw collection of graphics
		for (Drawable d : graphics) {
			if (d instanceof Graphic) {
				Graphic graphic = (Graphic) d;
				int size_x = (int) Math.round(graphic.getOriginalSize()
						.getWidth() * sd.getWidthScale());
				int size_y = (int) Math.round(graphic.getOriginalSize()
						.getHeight() * sd.getHeightScale());
				graphic.setSize(new Dimension(size_x, size_y));
				int loc_x = (int) ((graphic.getGridPosX() * (128 * sd
						.getWidthScale())) + (size_x / 2) + (128 * sd
						.getWidthScale()));
				int loc_y = (int) ((graphic.getGridPosY() * (128 * sd
						.getHeightScale())) + (size_y / 2) + (128 * sd
						.getHeightScale()));
				System.out.println(loc_x + ", " + loc_y);
				graphic.setPosX(loc_x);
				graphic.setPosY(loc_y);
			}
			d.draw(g, sd);
		}

		drawBoardPieces(g, sd);

	}

	/**
	 * Call individual draw function for each piece on board. Receives the
	 * graphic to draw on and the latest screen data.
	 * 
	 * @param g
	 * @param sd
	 */
	private void drawBoardPieces(Graphics2D g, ScreenData sd) {
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				board[x][y].draw(g, sd);
			}
		}
	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		String str = "";
		for (int y = 0; y < boardSizeY; y++) {
			str += "\n";
			for (int x = 0; x < boardSizeX; x++) {
				str += this.getBoard()[x][y].getOccupant().getStringValue()
						+ " ";
			}
		}
		return str;
	}

	/**
	 * Replace a given GameBlock with an EMPTY enum.
	 * 
	 * @param piece
	 */
	public void eliminatePiece(GameBlock piece) {
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				if (getBoard()[x][y].equals(piece)) {
					piece.setOccupant(BlockOccupant.EMPTY);
					// Piece "eliminated"
					return;
				}
			}
		}
	}
}
