import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A class to simplify keyboard input. Abstracts the Scanner class provided in
 * the Java SDK.
 */
public class Keyboard implements KeyListener {

	/** The SDK provided Scanner object, used to obtain keyboard input */
	private java.util.Scanner scan;
	
	private CheckersGUI gui;

	public Keyboard(CheckersGUI gui) {
		scan = new java.util.Scanner(System.in);
		this.gui = gui;
	}

	/**
	 * Reads an integer from the keyboard and returns it. <br>
	 * Uses the provided prompt to request an integer from the user.
	 */
	public int readInt(String prompt) {
		System.out.print(prompt);
		int num = 0;

		try {
			num = scan.nextInt();
			readString(""); // clear the buffer
		} catch (java.util.InputMismatchException ime) // wrong type inputted
		{
			readString(""); // clear the buffer
			num = 0;
		} catch (java.util.NoSuchElementException nsee) // break out of program
														// generates an
														// exception
		{
			readString(""); // clear the buffer
			num = 0;
		}
		return num;
	}

	/**
	 * Reads a double from the keyboard and returns it. <br>
	 * Uses the provided prompt to request a double from the user.
	 */
	public double readDouble(String prompt) {
		System.out.print(prompt);
		double num = 0.0;

		try {
			num = scan.nextDouble();
			readString(""); // clear the buffer
		} catch (java.util.InputMismatchException ime) {
			readString(""); // clear the buffer
			num = 0;
		} catch (java.util.NoSuchElementException nsee) {
			readString(""); // clear the buffer
			num = 0;
		}

		return num;
	}

	/**
	 * Reads a line of text from the keyboard and returns it as a String. <br>
	 * Uses the provided prompt to request a line of text from the user.
	 */
	public String readString(String prompt) {
		System.out.print(prompt);
		String str = "";

		try {
			str = scan.nextLine();
		} catch (java.util.NoSuchElementException nsee) {
			readString(""); // clear the buffer
			str = "";
		}

		return str;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyChar() == 'j'){
			if(gui.getCurrentGame().getActivePiece() != null){
				gui.getCurrentGame().getGameBoard().eliminatePiece(gui.getCurrentGame().getActivePiece());
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
