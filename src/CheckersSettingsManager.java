
public class CheckersSettingsManager {
	
	
	private boolean forceJumps;
	private boolean moveGuides;
	private GameType gameType;
	
	
	public CheckersSettingsManager(){
		forceJumps = false;
		moveGuides = false;
		gameType = GameType.REGULAR;
	}


	public boolean isForceJumps() {
		return forceJumps;
	}


	public void setForceJumps(boolean forceJumps) {
		this.forceJumps = forceJumps;
	}


	public boolean isMoveGuides() {
		return moveGuides;
	}


	public void setMoveGuides(boolean moveGuides) {
		this.moveGuides = moveGuides;
	}
	
	public GameType getGameType(){
		return this.gameType;
	}
	
	public void setGameType(GameType type){
		this.gameType = type;
	}
	
	

}
