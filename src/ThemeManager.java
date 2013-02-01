import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * Reusable singleton class to manage themes.
 *
 */
public class ThemeManager implements Iterable<String> {

	//Create new theme manager, themes listed in file called data.dat
	private static ThemeManager themeManager = new ThemeManager("data.dat");
	//Stores the current active theme.
	private String currentTheme;
	//Stores the collection of themes that are found in data file.
	private ArrayList<String> themes;
	//Boolean set to notify a theme-change has been requested.
	private boolean changedThemeFlag;
	//Map collection of theme images.
	private Map<String, BufferedImage> images;
	//Reference to singleton image loader
	private final ImageLoader il = ImageLoader.getImageLoader();

	/**
	 * Constructor
	 *<b>Preconditions:</b> Valid file name for theme data
	 *<b>Postconditions:</b> Constructs theme manager
	 *<b>Throws:</b> FileIOException
	 */
	public ThemeManager(String fileName) throws FileIOException {
		this.themes = new ArrayList<String>();
		this.images = new TreeMap<String, BufferedImage>();
		this.currentTheme = null;
		this.changedThemeFlag = false;
		try {
			readThemes(fileName);
		} catch (FileIOException e) {
			throw e;
		}
	}

	/**
	 * Get and return reference to singleton theme manager
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to theme manager
	 *<b>Throws:</b> None
	 */
	public static ThemeManager getThemeManager(){
		return themeManager;
	}

	/**
	 * Get and return reference to themes iterator
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to theme iterator
	 *<b>Throws:</b> None
	 */
	public Iterator<String> iterator() {
		return themes.iterator();
	}

	/**
	 * Get and return reference to theme collection
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to theme collection.
	 *<b>Throws:</b> None
	 */
	public ArrayList<String> getThemes() {
		return themes;
	}

	/**
	 * Sets current theme to given theme name
	 *<b>Preconditions:</b> Receives theme name string
	 *<b>Postconditions:</b> Sets current theme to given theme name
	 *<b>Throws:</b> None
	 */
	public void setTheme(String themeName) {
		currentTheme = themeName;
	}
	
	/**
	 * Sets boolean flag to determine if theme has been changed
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Sets changed-theme flag
	 *<b>Throws:</b> None
	 */
	public void setChangedThemeFlag(boolean flag) {
		this.changedThemeFlag = flag;
	}

	/**
	 * Gets and returns theme-change flag
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns if theme-change has been requested
	 *<b>Throws:</b> None
	 */
	public boolean getChangedThemeFlag() {
		return changedThemeFlag;
	}

	/**
	 * Get and return name of current theme
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to current theme.
	 *<b>Throws:</b> None
	 */
	public String getCurrentTheme() {
		return currentTheme;
	}
	
	/**
	 * Get and return BufferedImage from the current-theme's image collection via name.
	 *<b>Preconditions:</b> Receive image name
	 *<b>Postconditions:</b> Returns reference to image matching name.
	 *<b>Throws:</b> None
	 */
	public BufferedImage getImage(String name){
		return images.get(name);
	}

	/**
	 * Updates theme's image collection
	 *<b>Preconditions:</b> themes/ folder exist with current theme available
	 *<b>Postconditions:</b> Updates image collection with new theme images, null collection if not found.
	 *<b>Throws:</b> None
	 */
	public void updateTheme() {
		String imgLoc = "themes/" + currentTheme;

		File folder = new File(imgLoc);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				images.put(name.substring(0, name.indexOf('.')), il.getBufferedImage(imgLoc + "/" + name));
			}
		}
		changedThemeFlag = true;

	}

	/**
	 * Read the theme names from given file name
	 *<b>Preconditions:</b> Valid file name
	 *<b>Postconditions:</b> Adds to theme collection
	 *<b>Throws:</b> FileIOException
	 */
	private void readThemes(String fileName) {

		File fileCheck = new File(fileName);
		if (!fileCheck.exists()) {

			throw new FileIOException("File cannot be found.");
		}
		FileIO file = new FileIO(fileName, FileIO.FOR_READING);

		while (!file.EOF()) {
			String line = file.readLine();
			if (line != null) {
				themes.add(line);
			}
		}
	}

}
