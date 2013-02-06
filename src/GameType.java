
public enum GameType {
	REGULAR(1, "Regular"), REVERSE(2, "Reverse"), CAPTURE(3, "Capture");
	//Store integer value associated with game-type.
	private int value;
	private String id;

	private GameType(int v, String id){
		value = v;
		this.id = id;
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
	
	public String toString(){
		return id;
	}
}
