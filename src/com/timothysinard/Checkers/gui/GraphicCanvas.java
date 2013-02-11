package com.timothysinard.Checkers.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;

import com.timothysinard.Checkers.utils.ImageLoader;

/**
 * A drawable class that works as a canvas for a collection of images. Stores a
 * collection of drawables identified by a string-key, and then works as the
 * master draw-call, calling the draw functions for each drawable in the
 * collection.
 */
public class GraphicCanvas implements Drawable {
	private final ImageLoader il = ImageLoader.getImageLoader();
	// Collection storing the drawable items
	private final ArrayList<Drawable> drawItems;
	// Stores the most recent screen data/properties.
	private ScreenData recentScreenData;
	// Z-layer
	private final int layerZ;
	// Drawable ID
	private final String id;

	public GraphicCanvas(String id) {
		this.id = id;
		this.drawItems = new ArrayList<Drawable>();
		this.recentScreenData = null;
		this.layerZ = 0;
	}

	/**
	 * Gets and returns the image-loader.
	 * 
	 * <b>Preconditions:</b> None <b>Postconditions:</b> Returns reference to
	 * image-loader. <b>Throws:</b> None
	 */
	public ImageLoader getImageLoader() {
		return il;
	}

	/**
	 * Adds a drawable to collection of items being drawn.
	 * 
	 * <b>Preconditions:</b> Requires String identification and reference to
	 * drawable item. <b>Postconditions:</b> Stores given item in collection,
	 * replacing any item with same key. <b>Throws:</b> None
	 */
	public void addDrawable(String key, Drawable item) {
		for (Drawable d : drawItems) {
			if (d.getID().equals(item.getID())) {
				drawItems.remove(d);
				drawItems.add(item);
				return;
			}
		}
		drawItems.add(item);
		Collections.sort(drawItems, new GraphicsLayerComparator<Drawable>());
	}

	/**
	 * Get and return a drawable item from collection
	 * 
	 * <b>Preconditions:</b> Requires String key identification
	 * <b>Postconditions:</b> Get and return the drawable item that matches key.
	 * Returns null if not found. <b>Throws:<b> None
	 * 
	 * @param key
	 * @return
	 */
	public Drawable getDrawable(String key) {
		for (Drawable d : drawItems) {
			if (d.getID().equals(key)) {
				return d;
			}
		}
		return null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public int getZ() {
		return layerZ;
	}

	public boolean removeDrawable(String id) {
		for (Drawable d : drawItems) {
			if (d.getID().equals(id)) {
				drawItems.remove(d);
				return true;
			}
		}
		return false;
	}

	public boolean removeDrawable(Drawable d) {
		return drawItems.remove(d);
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {
		this.recentScreenData = sd;
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		for (Drawable d : drawItems) {
			d.draw(g, sd);
		}
	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	/**
	 * Get and return reference to the recent screen-data object.
	 * 
	 * <b>Preconditions:</b> None <b>Postconditions:</b> Returns reference to
	 * recent screen-data object. <b>Throws:</b> None
	 */
	public ScreenData getRecentScreenData() {
		return this.recentScreenData;
	}

}
