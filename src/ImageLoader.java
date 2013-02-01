import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ImageLoader {
	private static ImageLoader il = new ImageLoader();
	private Toolkit toolkit;

	// singleton design pattern hides the constructor
	private ImageLoader() {
		toolkit = Toolkit.getDefaultToolkit();
	}

	// the only way to get at the singleton reference
	public static ImageLoader getImageLoader() {
		return il;
	}
	
	/**
	 *  Method to create and get image from given directory.
	 *<b>Preconditions:</b> Take file name / director as parameter.
	 *<b>Postconditions:</b> Get and create image of given file, returns image if valid. Returns null if not found.
	 *<b>Throws:</b> None
	 */

	public Image getImage(String file) {
		Image img = null;

		try {
			BufferedImage bi = ImageIO.read(new File(file));
			img = toolkit.createImage(bi.getSource());
		} catch (IOException e) {
		}

		return img;
	}
	
	/**
	 * Loads and creates a buffered image
	 *<b>Preconditions:</b> Receive file location
	 *<b>Postconditions:</b> Creates and returns buffered image.
	 *<b>Throws:</b> None
	 */
	public BufferedImage getBufferedImage(String file){
		BufferedImage bi = null;

		try {
			bi = ImageIO.read(new File(file));
			
		} catch (IOException e) {
		}

		return bi;
	}
}
