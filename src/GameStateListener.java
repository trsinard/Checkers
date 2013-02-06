
public interface GameStateListener {
	
	public void boardChange(CheckersGame game);
	
	public void gameOver(CheckersGame game, BlockOccupant player);
	

}
