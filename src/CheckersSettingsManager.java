
public class CheckersSettingsManager {
	
	
	private boolean forceJumps;
	private boolean moveGuides;
	
	
	public CheckersSettingsManager(){
		forceJumps = false;
		moveGuides = false;
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
	
	

}
