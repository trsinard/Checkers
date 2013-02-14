package com.timothysinard.Checkers.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Graphic class used to store data of a Drawable, which draws itself.
 */
public class Graphic implements Drawable {

	// Image to be drawn
	private BufferedImage image;
	// Graphic ID
	public String id;
	// Screen x-position, y-position
	private int x_pos;
	private int y_pos;
	// Grid x-position, y-position. Block of a 2-dimensional matrix grid.
	private int x_grid;
	private int y_grid;
	// Rotation of image
	private int rotation;
	// Layer position
	private int layerZ;
	// Original size of image
	private Dimension originalSize;
	// Scaled size of image
	private Dimension size;

	public Graphic(String id, BufferedImage image, int x_pos, int y_pos,
			Dimension size, int layerZ) {
		this.image = image;
		this.id = id;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.size = this.originalSize = size;
		this.layerZ = layerZ;
		this.x_grid = 1;
		this.y_grid = 1;
		this.rotation = 0;
	}

	/**
	 * Sets Graphic's image to given buffered image.
	 * 
	 * @param image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Sets graphic's screen position on x-axis
	 * 
	 * @param x_pos
	 */
	public void setPosX(int x_pos) {
		this.x_pos = x_pos;
	}

	/**
	 * Sets graphic's screen position on y-axis
	 * 
	 * @param y_pos
	 */
	public void setPosY(int y_pos) {
		this.y_pos = y_pos;
	}

	/**
	 * Sets image rotation
	 * 
	 * @param rot
	 */
	public void setRotation(int rot) {
		this.rotation = rot;
	}

	/**
	 * Sets new size of image
	 * 
	 * @param size
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

	/**
	 * Sets the grid position, in terms of 2-dimensional matrix
	 * 
	 * @param gx
	 * @param gy
	 */
	public void setGridPos(int gx, int gy) {
		this.x_grid = gx;
		this.y_grid = gy;
	}

	/**
	 * Sets z-layer position
	 * 
	 * @param z
	 */
	public void setZ(int z) {
		this.layerZ = z;
	}

	@Override
	public String getID() {
		return id;
	}

	/**
	 * Gets grid position x/column.
	 * 
	 * @return
	 */
	public int getGridPosX() {
		return x_grid;
	}

	/**
	 * Gets grid position y/row.
	 * 
	 * @return
	 */
	public int getGridPosY() {
		return y_grid;
	}

	/**
	 * Gets image reference.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Gets screen x-position.
	 * 
	 * @return
	 */
	public int getPosX() {
		return x_pos;
	}

	/**
	 * Gets screen y-position.
	 * 
	 * @return
	 */
	public int getPosY() {
		return y_pos;
	}

	/**
	 * Gets image rotation.
	 * 
	 * @return
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * Gets image's original size.
	 * 
	 * @return
	 */
	public Dimension getOriginalSize() {
		return this.originalSize;
	}

	/**
	 * Gets current size of image.
	 * 
	 * @return
	 */
	public Dimension getSize() {
		return size;
	}

	@Override
	public int getZ() {
		return this.layerZ;
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {

		int w = (int) Math.round(image.getWidth() * sd.getScaleRatio());
		int h = (int) Math.round(image.getHeight() * sd.getScaleRatio());
		AffineTransform at = new AffineTransform();
		at.translate(x_pos * sd.getScaleRatio(), y_pos * sd.getScaleRatio());

		at.rotate(Math.toRadians(rotation), w / 2, h / 2);
		at.scale(sd.getScaleRatio(), sd.getScaleRatio());
		g.drawImage(image, at, null);

		// g.drawImage(image, x_pos, y_pos, w, h, null);
	}

	@Override
	public void mouseClickPosition(int x, int y) {

	}

}
