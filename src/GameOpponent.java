
public enum GameOpponent {
	PLAYER(1), AI_EASY(2), AI_MEDIUM(3), AI_HARD(3);
	//Store integer value associated with game-opponent.
	private int value;

	private GameOpponent(int v){
		value = v;
	}
	
	/**
	 * Get and return integer value associated with the opponent.
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns integer value associated with the opponent.
	 *<b>Throws:</b> None
	 */
	public int getValue(){
		return value;
	}
}
