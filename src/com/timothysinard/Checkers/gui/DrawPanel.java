package com.timothysinard.Checkers.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class DrawPanel extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = 1L;
	// Picture to be drawn
	private Drawable pict;
	// Ratio of picture
	private double ratio;
	// GUI Mouse Listener
	private GUIMouseEventListener meListener;

	public DrawPanel() {
		pict = null;
		ratio = 1.0;
		addMouseMotionListener(this);
	}

	/**
	 * Sets given GUIMouseEventListener, which listens to MouseListener,
	 * MotionListener, and WheelListener
	 * 
	 * @param meListener
	 */
	public void setMouseListener(GUIMouseEventListener meListener) {
		this.meListener = meListener;
		this.addMouseListener(meListener);
		this.addMouseMotionListener(meListener);
		this.addMouseWheelListener(meListener);
	}

	/**
	 * Removes current GUIMouseEventListener from listening to MouseListener,
	 * MouseMotionListener, and MouseWheelListener
	 */
	public void removeMouseListener() {
		this.removeMouseListener(meListener);
		this.removeMouseMotionListener(meListener);
		this.removeMouseWheelListener(meListener);
		this.meListener = null;
	}

	/**
	 * Get reference to current GUIMouseEventListener
	 * 
	 * @return
	 */
	public GUIMouseEventListener getMouseListener() {
		return this.meListener;
	}

	/**
	 * Sets picture to be drawn, no changes are made if null.
	 * 
	 * @param drawable
	 */
	public void setDrawable(Drawable drawable) {
		if (drawable != null) {
			pict = drawable;
		}
	}

	@Override
	public void paint(Graphics g) {
		int width = getSize().width;
		int height = getSize().height;
		ScreenData sd = new ScreenData(1024, 1024, width, height, ratio);
		if (pict != null) {
			pict.draw((Graphics2D) g, sd);
		}
	}

	/**
	 * Get reference to picture to be drawn
	 * 
	 * @return
	 */
	public Drawable getDrawable() {
		return pict;
	}

	/**
	 * Set the drawing size ratio.
	 * 
	 * @param ratio
	 */
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	/**
	 * Get drawing size ratio.
	 * 
	 * @return
	 */
	public double getRatio() {
		return ratio;
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (pict != null) {
			pict.mouseClickPosition(event.getX(), event.getY());
		}
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent event) {
	}

	/**
	 * Removes all Mouse Listeners of any type from this object.
	 */
	public void removeAllMouseListeners() {
		for (MouseListener ml : this.getMouseListeners()) {
			this.removeMouseListener(ml);
		}
		for (MouseMotionListener ml : this.getMouseMotionListeners()) {
			this.removeMouseMotionListener(ml);
		}
		for (MouseWheelListener ml : this.getMouseWheelListeners()) {
			this.removeMouseWheelListener(ml);
		}
	}
}
