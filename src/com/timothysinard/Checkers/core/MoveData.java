package com.timothysinard.Checkers.core;

/**
 * Class which stores data related to a movable piece
 */
public class MoveData {

	// GameBlock that is being moved
	private GameBlock srcPiece;
	// GameBlock destination
	private GameBlock destPiece;
	// GameBlock that will be jumped
	private GameBlock jumpedPiece;
	// Player that owns the move
	private final BlockOccupant player;

	public MoveData(GameBlock srcPiece, GameBlock destPiece,
			GameBlock jumpedPiece, BlockOccupant player) {
		this.srcPiece = srcPiece;
		this.destPiece = destPiece;
		this.jumpedPiece = jumpedPiece;
		this.player = player;

	}

	/**
	 * Get the piece being moved
	 * 
	 * @return
	 */
	public GameBlock getSrcPiece() {
		return srcPiece;
	}

	/**
	 * Set the piece that will be moved
	 * 
	 * @param srcPiece
	 */
	public void setSrcPiece(GameBlock srcPiece) {
		this.srcPiece = srcPiece;
	}

	/**
	 * Get the block-piece the source will be moving to.
	 * 
	 * @return
	 */
	public GameBlock getDestPiece() {
		return destPiece;
	}

	/**
	 * Set the block-piece the source will move to.
	 * 
	 * @param destPiece
	 */
	public void setDestPiece(GameBlock destPiece) {
		this.destPiece = destPiece;
	}

	/**
	 * Get the piece that will be jumped.
	 * 
	 * @return
	 */
	public GameBlock getJumpedPiece() {
		return jumpedPiece;
	}

	/**
	 * Set the block-piece that will be jumped.
	 * 
	 * @param jumpedPiece
	 */
	public void setJumpedPiece(GameBlock jumpedPiece) {
		this.jumpedPiece = jumpedPiece;
	}

	/**
	 * Get the player who owns the jump.
	 * 
	 * @return
	 */
	public BlockOccupant getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		String str = srcPiece.toString() + " to " + destPiece.toString();
		if (jumpedPiece != null) {
			str += " jumped " + jumpedPiece.toString() + ". ";
		}
		return str;
	}
}
