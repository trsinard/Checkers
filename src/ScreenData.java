
/**
 * Screen data use to store important information regarding current display state.
 */
public class ScreenData {

	public static final String BUILD_MODEL = "1.0.3B";
	//Original height and width of the screen
	private int originalWidth;
	private int originalHeight;
	//Current height and width of the screen
	private int width;
	private int height;
	//Scale of height and width of the screen
	private double widthScale;
	private double heightScale;
	//Scale ratio
	private double scaleRatio;
	
	
	public ScreenData(int originalWidth, int originalHeight, int width, int height, double ratio) {
		this.originalWidth = originalWidth;
		this.originalHeight = originalHeight;
		this.width = width;
		this.height = height;
		//Dividing by 1.0 to convert to double
		this.widthScale = width / (originalWidth / 1.0);
		this.heightScale = height / (originalHeight / 1.0);
		this.scaleRatio = ratio;
	}
	
	/**
	 * Return original width of screen.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b> None
	 */
	public int getOriginalWidth(){
		return originalWidth;
	}
	
	/**
	 * Return original height of screen.
	 *<b>Preconditions:</b>  None
	 *<b>Postconditions:</b>  None
	 *<b>Throws:</b> None
	 */
	public int getOriginalHeight(){
		return originalHeight;
	}
	
	/**
	 * Return current width
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b> None
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Return current height
	 *<b>Preconditions:</b>  None
	 *<b>Postconditions:</b>  None
	 *<b>Throws:</b> None
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Return width scale
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b> None
	 */
	public double getWidthScale(){
		return widthScale;
	}
	
	/**
	 * Returns height scale
	 *<b>Preconditions:</b> 
	 *<b>Postconditions:</b> 
	 *<b>Throws:</b> None
	 */
	public double getHeightScale(){
		return heightScale;
	}
	
	/**
	 * Returns scale ratio
	 *<b>Preconditions:</b> None 
	 *<b>Postconditions:</b> None
	 *<b>Throws:</b> None
	 */
	public double getScaleRatio(){
		return scaleRatio;
	}

}
