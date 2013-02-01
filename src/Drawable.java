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
	
}