
public enum GameType {
	TWO_PLAYERS(1), AI_EASY(2), AI_MEDIUM(3), AI_HARD(3);
	//Store integer value associated with game-type.
	private int value;

	private GameType(int v){
		value = v;
	}
	
	/**
	 * Get and return integer value associated with game-type.
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns integer value associated with game-type.
	 *<b>Throws:</b> None
	 */
	public int getValue(){
		return value;
	}
}
