import javax.swing.SwingUtilities;

/**
 * Main driver to create and run Checkers.
 */
public class CheckersDriver {

	public static void main(String[] args) {
		// Run in gui thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create GUI game
				new CheckersGUI();
			}
		});
	}
}
