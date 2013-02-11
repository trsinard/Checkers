package com.timothysinard.Checkers.core;
public class MoveData {

	private GameBlock srcPiece;
	private GameBlock destPiece;
	private GameBlock jumpedPiece;
	private BlockOccupant player;
	

	public MoveData(GameBlock srcPiece, GameBlock destPiece, GameBlock jumpedPiece, BlockOccupant player){
		this.srcPiece = srcPiece;
		this.destPiece = destPiece;
		this.jumpedPiece = jumpedPiece;
		this.player = player;
		
	}
	public GameBlock getSrcPiece() {
		return srcPiece;
	}

	public void setSrcPiece(GameBlock srcPiece) {
		this.srcPiece = srcPiece;
	}

	public GameBlock getDestPiece() {
		return destPiece;
	}

	public void setDestPiece(GameBlock destPiece) {
		this.destPiece = destPiece;
	}
	
	public GameBlock getJumpedPiece(){
		return jumpedPiece;
	}
	
	public void setJumpedPiece(GameBlock jumpedPiece){
		this.jumpedPiece = jumpedPiece;
	}

	public BlockOccupant getPlayer(){
		return player;
	}
	
	public String toString(){
		String str = srcPiece.toString() + " to " + destPiece.toString();
		if(jumpedPiece != null){
			str+=" jumped " + jumpedPiece.toString() + ". ";
		}
		return str;
	}
}
