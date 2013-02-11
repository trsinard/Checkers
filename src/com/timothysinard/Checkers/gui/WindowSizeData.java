package com.timothysinard.Checkers.gui;
/**
 * Class to store scale-profile for the window.
 *
 */
public class WindowSizeData {
	//Store the ID name of WindowSize Data
	private String sizeID;
	//Store size ratio
	private double ratio;
	//Stores if size profile is active
	private boolean active;
	
	
	public WindowSizeData(String sizeID, double ratio){
		this.sizeID = sizeID;
		this.ratio = ratio;
		this.active = false;
	}
	
	/**
	 * Get and return ratio
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Return ratio
	 *<b>Throws:</b> None
	 */
	public double getRatio(){
		return this.ratio;
	}
	
	/**
	 * Get and return profile ID
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Return profile ID
	 *<b>Throws:</b> None
	 */
	public String getID(){
		return this.sizeID;
	}
	
	/**
	 * Gets and returns active-boolean of size profile.
	 *<b>Preconditions:</b> 
	 *<b>Postconditions:</b> 
	 *<b>Throws:</b> None
	 */
	public boolean isActive(){
		return active;
	}
	
	/**
	 * Sets active state of size profile to given boolean.
	 *<b>Preconditions:</b> Boolean as parameter
	 *<b>Postconditions:</b> Sets size profile state to given boolean.
	 *<b>Throws:</b> None
	 */
	public void setActive(boolean bool){
		this.active = bool;
	}

}
