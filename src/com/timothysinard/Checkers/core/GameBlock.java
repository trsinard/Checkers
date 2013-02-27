package com.timothysinard.Checkers.core;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.timothysinard.Checkers.gui.Drawable;
import com.timothysinard.Checkers.gui.ScreenData;
import com.timothysinard.Checkers.utils.ThemeManager;

/**
 * GameBlock class which stores the data of each block on the board. It is a
 * replacement for a "piece" class, and simply references each square on the
 * board.
 */
public class GameBlock implements Drawable {

	// Store block id
	private String id;
	// Store type of occupant on block
	private BlockOccupant occupant;
	// Is a kinged block?
	private boolean king;
	// Is block selected?
	private boolean selected;
	// Is mouse hovering over block?
	private boolean hover;
	// Is this block highlighted as available to move?
	private boolean highlightAsMove;
	// Is this block highlighted as a move location?
	private boolean highlightAsTarget;
	// Is this block unavailable to use?
	private boolean disabled;
	// Grid positions x and y, reference to a 2D matrix grid.
	private int loc_x;
	private int loc_y;
	// Global position
	private int pos_x;
	private int pos_y;
	// Layer position
	private int layerZ;
	// Rescaled block size
	private Dimension newSize;
	// Original block size
	private final Dimension originalSize;

	public GameBlock(String id, BlockOccupant occupant, int loc_x, int loc_y) {
		this.id = id;
		this.occupant = occupant;
		this.king = false;
		this.selected = false;
		this.hover = false;
		this.highlightAsTarget = false;
		this.disabled = false;
		this.loc_x = loc_x;
		this.loc_y = loc_y;
		this.pos_x = -1;
		this.pos_y = -1;
		this.originalSize = new Dimension(64, 64);
		this.newSize = originalSize;
		this.layerZ = 1;
	}

	/**
	 * Returns block occupant
	 * 
	 * @return
	 */
	public BlockOccupant getOccupant() {
		return occupant;
	}

	/**
	 * Returns x-grid position
	 * 
	 * @return
	 */
	public int getGridX() {
		return loc_x;
	}

	/**
	 * Returns y-grid position
	 * 
	 * @return
	 */
	public int getGridY() {
		return loc_y;
	}

	/**
	 * Returns global board x-position
	 * 
	 * @return
	 */
	public int getBoardPosX() {
		return pos_x;
	}

	/**
	 * Returns global board y-position
	 * 
	 * @return
	 */
	public int getBoardPosY() {
		return pos_y;
	}

	/**
	 * Returns if the block is a king
	 * 
	 * @return
	 */
	public boolean isKing() {
		return king;
	}

	/**
	 * Returns if the block is selected
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Returns if the block is being hovered by the mouse interaction.
	 * 
	 * @return
	 */
	public boolean isHover() {
		return hover;
	}

	/**
	 * Returns if the block is being highlighted as a movable piece.
	 * 
	 * @return
	 */
	public boolean isHighlightAsMovable() {
		return highlightAsMove;
	}

	/**
	 * Returns if the block is highlighted as a target location for another
	 * movable piece.
	 * 
	 * @return
	 */
	public boolean isHighlightAsTarget() {
		return highlightAsTarget;
	}

	/**
	 * Returns if the block is disabled.
	 * 
	 * @return
	 */
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public String getID() {
		return id;
	}

	/**
	 * Returns the original size of the block.
	 * 
	 * @return
	 */
	public Dimension getOriginalSize() {
		return originalSize;
	}

	/**
	 * Returns the resized dimensions of the block.
	 * 
	 * @return
	 */
	public Dimension getNewSize() {
		return newSize;
	}

	@Override
	public int getZ() {
		return layerZ;
	}

	/**
	 * Sets the block ID as given string value.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * Sets the matrix grid x-position to given integer value.
	 * 
	 * @param loc_x
	 */
	public void setGridX(int loc_x) {
		this.loc_x = loc_x;
	}

	/**
	 * Sets the matrix grid y-position to given integer value.
	 * 
	 * @param loc_y
	 */
	public void setGridY(int loc_y) {
		this.loc_y = loc_y;
	}

	/**
	 * Sets the global board x-position to given integer value.
	 * 
	 * @param x
	 */
	public void setPosX(int x) {
		this.pos_x = x;
	}

	/**
	 * Sets the global board y-position to given integer value.
	 * 
	 * @param y
	 */
	public void setPosY(int y) {
		this.pos_y = y;
	}

	/**
	 * Sets the block as a kinged piece.
	 * 
	 * @param bool
	 */
	public void setKing(boolean bool) {
		this.king = bool;
	}

	/**
	 * Sets block as selected or deselected. If the occupant is Empty or Null no
	 * changes are made.
	 * 
	 * @param bool
	 */
	public void setSelected(boolean bool) {
		if (occupant == BlockOccupant.EMPTY || occupant == BlockOccupant.NULL) {
			return;
		}
		this.selected = bool;
	}

	/**
	 * Sets the block as being hovered or not being hovered. If occupant is a
	 * Null block, does nothing.
	 * 
	 * @param bool
	 */
	public void setHover(boolean bool) {
		if (occupant == BlockOccupant.NULL) {
			return;
		}
		this.hover = bool;

	}

	/**
	 * Sets block as a highlighted, movable, piece. If occupant is Null or
	 * Empty, does nothing.
	 * 
	 * @param bool
	 */
	public void setHighlightAsMovable(boolean bool) {
		if (occupant == BlockOccupant.NULL || occupant == BlockOccupant.EMPTY) {
			return;
		}
		this.highlightAsMove = bool;
	}

	/**
	 * Sets block as a highlighted target block. If occupant is null, does
	 * nothing.
	 * 
	 * @param bool
	 */
	public void setHighlightAsTarget(boolean bool) {
		if (occupant == BlockOccupant.NULL) {
			return;
		}
		this.highlightAsTarget = bool;
	}

	/**
	 * Disables block from being used.
	 * 
	 * @param bool
	 */
	public void setDisabled(boolean bool) {
		this.disabled = bool;
	}

	/**
	 * Sets block occupant to given occupant.
	 * 
	 * @param occupant
	 */
	public void setOccupant(BlockOccupant occupant) {
		this.occupant = occupant;
	}

	/**
	 * Sets new block size to given dimension.
	 * 
	 * @param size
	 */
	public void setNewSize(Dimension size) {
		this.newSize = size;
	}

	/**
	 * Sets the z-layer value, which is the layer position to be drawn. 0 being
	 * the highest priority.
	 * 
	 * @param z
	 */
	public void setZ(int z) {
		this.layerZ = z;
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {
		BufferedImage image = null;

		// Get appropriate image
		ThemeManager tm = ThemeManager.getThemeManager();
		if (occupant == BlockOccupant.PLAYER) {
			if (isKing()) {
				image = tm.getImage("p1k");
			} else {
				image = tm.getImage("p1");
			}
		} else if (occupant == BlockOccupant.PLAYER2) {
			if (isKing()) {
				image = tm.getImage("p2k");
			} else {
				image = tm.getImage("p2");
			}
		}

		// If not null, rescale and reposition, then draw.
		double scaledWidth, scaledHeight = 0.00;
		if (image != null) {
			scaledWidth = image.getWidth() * sd.getScaleRatio();
			scaledHeight = image.getHeight() * sd.getScaleRatio();
			this.newSize = new Dimension((int) (Math.round(scaledWidth)),
					(int) (Math.round(scaledHeight)));
			this.pos_x = (int) Math.round(scaledWidth * loc_x);
			this.pos_y = (int) Math.round(scaledHeight * loc_y);

			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
			// Draws "selected" effect ontop of the block image.
			image = tm.getImage("block-selected");
			if (image != null && isSelected()) {
				g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
						(int) Math.round(scaledHeight), null);
			}
		}

		image = tm.getImage("block-available");
		// Ensures there isn't a null pointer exception
		if (image == null) {
			return;
		}
		// Rescales images. Duplicate code due to possible event the images
		// start at different sizes.
		scaledWidth = image.getWidth() * sd.getScaleRatio();
		scaledHeight = image.getHeight() * sd.getScaleRatio();
		this.newSize = new Dimension((int) (Math.round(scaledWidth)),
				(int) (Math.round(scaledHeight)));
		this.pos_x = (int) Math.round(scaledWidth * loc_x);
		this.pos_y = (int) Math.round(scaledHeight * loc_y);
		if (image != null && isHighlightAsMovable()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
		}

		image = tm.getImage("block-target");
		if (image != null && isHighlightAsTarget()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
		}

		image = tm.getImage("block-hover");
		if (image != null && isHover()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
		}

		image = tm.getImage("block-disabled");
		if (image != null && isDisabled()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
		}

	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	/**
	 * Creates and returns a copy of the game block.
	 * 
	 * @return
	 */
	public GameBlock copy() {
		GameBlock copy = new GameBlock(this.id, this.occupant, this.loc_x,
				this.loc_y);
		copy.setHover(this.isHover());
		copy.setKing(this.isKing());
		copy.setNewSize(this.getNewSize());
		copy.setSelected(this.isSelected());
		copy.setDisabled(this.isDisabled());
		copy.setHighlightAsMovable(this.isHighlightAsMovable());
		copy.setHighlightAsTarget(this.isHighlightAsTarget());
		return copy;
	}

	@Override
	public String toString() {
		String str = "[" + occupant.getStringValue() + "-(" + loc_x + ", "
				+ loc_y + ")]";
		if (isKing()) {
			str = "K" + str;
		}
		return str;
	}
}
