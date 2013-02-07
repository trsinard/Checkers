import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * A drawable class that works as a canvas for a collection of images. 
 * Stores a collection of drawables identified by a string-key, and then works as 
 * the master draw-call, calling the draw functions for each drawable in the 
 * collection.
 */
public class GraphicCanvas implements Drawable {
	private final ImageLoader il = ImageLoader.getImageLoader();
	//Collection storing the drawable items, by key
	private Map<String, Drawable> drawItems;
	//Stores the most recent screen data/properties.
	private ScreenData recentScreenData;
	
	public GraphicCanvas() {
			this.drawItems = new TreeMap<String, Drawable>();
			this.recentScreenData = null;
	}
	
	/**
	 * Gets and returns the image-loader.
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to image-loader.
	 *<b>Throws:</b> None
	 */
	public ImageLoader getImageLoader() {
		return il;
	}
	
	/**
	 * Adds a drawable to collection of items being drawn.
	 *
	 *<b>Preconditions:</b> Requires String identification and reference to drawable item.
	 *<b>Postconditions:</b> Stores given item in collection, replacing any item with same key.
	 *<b>Throws:</b> None
	 */
	public void addDrawable(String key, Drawable item){
		if(this.drawItems.containsKey(key)){
			this.drawItems.remove(key);
		}
		this.drawItems.put(key, item);
	}
	
	/**
	 * Get and return a drawable item from collection
	 * 
	 * <b>Preconditions:</b> Requires String key identification
	 * <b>Postconditions:</b> Get and return the drawable item that matches key. Returns null if not found.
	 * <b>Throws:<b> None
	 * @param key
	 * @return
	 */
	public Drawable getDrawable(String key){
		if(this.drawItems.containsKey(key)){
			return this.drawItems.get(key);
		} else{
			return null;
		}
	}
	public void removeDrawable(String key){
		drawItems.remove(key);
	}
	
	public void draw(Graphics2D g, ScreenData sd) {
		this.recentScreenData = sd;
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for(Entry<String, Drawable> entry : this.drawItems.entrySet()) {
			entry.getValue().draw(g, sd);
		}
	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Get and return reference to the recent screen-data object.
	 *
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to recent screen-data object.
	 *<b>Throws:</b> None
	 */
	public ScreenData getRecentScreenData() {
		return this.recentScreenData;
	}

}
