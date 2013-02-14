package com.timothysinard.Checkers.gui;

import com.timothysinard.Checkers.utils.Random;
import com.timothysinard.Checkers.utils.Timer;

/**
 * Runnable thread dedicated to special effects.
 * 
 */
public class EffectsRunnable implements Runnable {

	// Reference to main GUI class
	private final CheckersGUI gui;
	// Reference to Timer
	private final Timer timer;
	// Gloss effect on window, on or off.
	private boolean showGloss;
	// Constant refresh rate
	private final double REFRESH_RATE = 0.0015;

	public EffectsRunnable(CheckersGUI gui) {
		this.gui = gui;
		this.timer = new Timer();
		this.showGloss = true;

	}

	@Override
	public void run() {
		timer.startTimer();
		adjustStartEffects(true);
		while (true) {
			if (timer.getElapsedTime() * 1.0e-9 > REFRESH_RATE) {
				// if(gui.isShowingStart()){
				if (Random.getRandomNumberGenerator().randomInt(1, 10000) == 50) {
					showGloss = true;
				}
				if (showGloss == true) {
					adjustStartEffects(false);
				}
				// }
				timer.resetTimer();
				gui.repaint();

				/*
				 * SwingUtilities.invokeLater(new Runnable(){ public void run(){
				 * gui.repaint(); } });
				 */
			}
		}

	}

	/**
	 * Convenience method which calls all effects to activate. Received boolean
	 * param indicates if it is the first call or not.
	 * 
	 * @param first
	 */
	private void adjustStartEffects(boolean first) {
		// Only one method at the moment.
		adjustTitleGloss(first);
	}

	/**
	 * Moves gloss-board image 1 pixel to the right and down. If param is true,
	 * it sets the image off-screen to the top-left.
	 * 
	 * @param first
	 */
	private void adjustTitleGloss(boolean first) {
		Drawable img = gui.getBoardCanvas().getDrawable("gloss-board");
		if (img instanceof Graphic && img != null) {
			int maxX = (int) Math.round(((Graphic) img).getImage().getWidth()
					* gui.getRatio());
			int maxY = (int) Math.round(((Graphic) img).getImage().getHeight()
					* gui.getRatio());
			int minX = -maxX;
			int currPosX = ((Graphic) img).getPosX();
			int currPosY = ((Graphic) img).getPosY();
			int xOffset = 0, yOffset = 0;
			if (first) {
				((Graphic) img).setPosX(minX);
				((Graphic) img).setPosY(minX);
				return;
			}
			if (currPosX >= maxX && currPosY < maxY) {
				// Far right, room to move up
				yOffset++;
			} else if (currPosX < maxX && currPosY >= maxY) {
				// Max height, room to move right
				xOffset++;
			} else if (currPosX >= maxX && currPosY >= maxY) {
				// Max height, far right
				xOffset = yOffset = minX * 2;
				showGloss = false;
			} else if (currPosX < maxX && currPosY < maxY) {
				// Neither edge hit
				xOffset++;
				yOffset++;
			}

			((Graphic) img).setPosX(((Graphic) img).getPosX() + xOffset);
			((Graphic) img).setPosY(((Graphic) img).getPosY() + yOffset);
		}
	}
}
