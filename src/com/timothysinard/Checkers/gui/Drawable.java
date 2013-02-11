package com.timothysinard.Checkers.gui;
import java.awt.Graphics2D;

public interface Drawable {
	
	/**
	 * Draw function to apply graphics to screen with given ScreenData
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Draws graphic to screen by preference of ScreenData
	 *<b>Throws:</b> None
	 */
	public void draw(Graphics2D g, ScreenData sd);

	
	public void mouseClickPosition(int x, int y);
	
	/**
	 * Method to get the layer priority
	 * <b>Preconditions:</b> None
	 * <b>Postconditions:</b> Returns layer priority
	 * <b>Throws:</b> None
	 */
	public int getZ();
	
	/**
	 * Method to get the drawable ID
	 * <b>Preconditions:</b> None
	 * <b>Postconditions:</b> Returns String value identification
	 * <b>Throws:</b> None
	 */
	
	public String getID();
	
}