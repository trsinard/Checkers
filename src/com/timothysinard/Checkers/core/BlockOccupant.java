package com.timothysinard.Checkers.core;
/**
 * Type of Block occupying a given space
 * 
 */
public enum BlockOccupant {
	EMPTY(0), PLAYER(1), PLAYER2(2), NULL(3);
	
	//Integer value of the block occupant
	private int value;
	
	private BlockOccupant(int v){
		value = v;
	}
	
	/**
	 * Get and return integer value of occupant
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns integer value of occupant
	 *<b>Throws:</b> None
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * String value of occupant, supporting ASCII board layout
	 *<b>Preconditions:</b>  None
	 *<b>Postconditions:</b> Returns string representation of occupant
	 *<b>Throws:</b> None
	 */
	public String getStringValue(){
		if(value == 0){
			return "[NB]";
		} else if(value == 1){
			return "[P1]";
		} else if(value == 2){
			return "[P2]";
		} else if(value == 3){
			return "[--]";
		} else{
			return null;
		}
	}
}
