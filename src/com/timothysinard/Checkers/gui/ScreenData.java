package com.timothysinard.Checkers.gui;

/**
 * Screen data use to store important information regarding current display
 * state.
 */
public class ScreenData {

	public static final String BUILD_MODEL = "1.0.4B";
	// Original height and width of the screen
	private final int originalWidth;
	private final int originalHeight;
	// Current height and width of the screen
	private final int width;
	private final int height;
	// Scale of height and width of the screen
	private final double widthScale;
	private final double heightScale;
	// Scale ratio
	private final double scaleRatio;

	public ScreenData(int originalWidth, int originalHeight, int width,
			int height, double ratio) {
		this.originalWidth = originalWidth;
		this.originalHeight = originalHeight;
		this.width = width;
		this.height = height;
		// Dividing by 1.0 to convert to double
		this.widthScale = width / (originalWidth / 1.0);
		this.heightScale = height / (originalHeight / 1.0);
		this.scaleRatio = ratio;
	}

	/**
	 * Return original width of screen.
	 */
	public int getOriginalWidth() {
		return originalWidth;
	}

	/**
	 * Return original height of screen.
	 */
	public int getOriginalHeight() {
		return originalHeight;
	}

	/**
	 * Return current width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return current height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return width scale
	 */
	public double getWidthScale() {
		return widthScale;
	}

	/**
	 * Returns height scale
	 */
	public double getHeightScale() {
		return heightScale;
	}

	/**
	 * Returns scale ratio
	 */
	public double getScaleRatio() {
		return scaleRatio;
	}

}
