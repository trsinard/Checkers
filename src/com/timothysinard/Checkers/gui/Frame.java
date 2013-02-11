package com.timothysinard.Checkers.gui;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Frame class to help build GUI, which is an abstract class. Extends JFrame
 */
public abstract class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	public Frame(Dimension dimension, String title) {
		super(title);
		center((int) dimension.getWidth(), (int) dimension.getHeight());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		addWindowStateListener(new WindowAdapter() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == JFrame.MAXIMIZED_BOTH
						|| e.getNewState() == JFrame.MAXIMIZED_HORIZ
						|| e.getNewState() == JFrame.MAXIMIZED_VERT) {
					setExtendedState(JFrame.NORMAL);
				}
			}
		});
	}
	
	private void center(int width, int height) {
		setSize(width, height);

		// center the window
		Dimension screenSize = getToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		setLocation(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2);
	}
}
