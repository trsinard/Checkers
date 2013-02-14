package com.timothysinard.Checkers.core;

public enum GameType {
	REGULAR(1, "Regular"), REVERSE(2, "Reverse"), CAPTURE(3, "Capture");
	// Store integer value associated with game-type.
	private int value;
	private String id;

	private GameType(int v, String id) {
		value = v;
		this.id = id;
	}

	/**
	 * Get and return integer value associated with game-type.
	 * 
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns string ID
	 */
	@Override
	public String toString() {
		return id;
	}
}
