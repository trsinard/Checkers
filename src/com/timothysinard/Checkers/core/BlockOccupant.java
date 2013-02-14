package com.timothysinard.Checkers.core;

/**
 * Type of Block occupying a given space
 * 
 */
public enum BlockOccupant {
	EMPTY(0), PLAYER(1), PLAYER2(2), NULL(3);

	// Integer value of the block occupant
	private int value;

	private BlockOccupant(int v) {
		value = v;
	}

	/**
	 * Get and return integer value of occupant
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get and return string value of occupant, supporting ASCII board layout
	 */
	public String getStringValue() {
		if (value == 0) {
			return "[NB]";
		} else if (value == 1) {
			return "[P1]";
		} else if (value == 2) {
			return "[P2]";
		} else if (value == 3) {
			return "[--]";
		} else {
			return null;
		}
	}
}
