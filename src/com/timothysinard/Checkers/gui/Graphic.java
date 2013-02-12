package com.timothysinard.Checkers.gui;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Graphic implements Drawable{

	private BufferedImage image;
	public String id;
	private int x_pos;
	private int y_pos;
	private int x_grid;
	private int y_grid;
	private int rotation;
	private int layerZ;
	private Dimension originalSize;
	private Dimension size;

	public Graphic(String id, BufferedImage image, int x_pos, int y_pos, Dimension size, int layerZ) {
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

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setPosX(int x_pos) {
		this.x_pos = x_pos;
	}

	public void setPosY(int y_pos) {
		this.y_pos = y_pos;
	}

	public void setRotation(int rot) {
		this.rotation = rot;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setGridPos(int gx, int gy) {
		this.x_grid = gx;
		this.y_grid = gy;
	}

	public void setZ(int z){
		this.layerZ = z;
	}
	
	public String getID(){
		return id;
	}
	
	public int getGridPosX() {
		return x_grid;
	}

	public int getGridPosY() {
		return y_grid;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getPosX() {
		return x_pos;
	}

	public int getPosY() {
		return y_pos;
	}

	public int getRotation() {
		return rotation;
	}

	public Dimension getOriginalSize() {
		return this.originalSize;
	}

	public Dimension getSize() {
		return size;
	}
	
	public int getZ(){
		return this.layerZ;
	}

	public void draw(Graphics2D g, ScreenData sd) {

		int w = (int) Math.round(image.getWidth() * sd.getScaleRatio());
		int h = (int) Math.round(image.getHeight() * sd.getScaleRatio());
		AffineTransform at = new AffineTransform();
		at.translate(x_pos * sd.getScaleRatio() , y_pos * sd.getScaleRatio());
		
		at.rotate(Math.toRadians(rotation), w/2, h/2);
		at.scale(sd.getScaleRatio(), sd.getScaleRatio());
		g.drawImage(image, at, null);
		
		//g.drawImage(image, x_pos, y_pos, w, h, null);
	}

	public void mouseClickPosition(int x, int y) {

	}

}
