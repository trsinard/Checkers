package com.timothysinard.Checkers.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.timothysinard.Checkers.core.BlockOccupant;
import com.timothysinard.Checkers.core.GameBlock;
import com.timothysinard.Checkers.utils.Timer;

public class GUIMouseEventListener implements MouseListener,
		MouseMotionListener, MouseWheelListener {

	private final CheckersGUI gui;
	private final Timer timer;
	private final double SCROLL_DELAY_SECS = 0.10;

	public GUIMouseEventListener(CheckersGUI gui, DrawPanel panel) {
		this.gui = gui;
		this.timer = new Timer();
		timer.startTimer();
		panel.addMouseMotionListener(this);
		panel.addMouseListener(this);
		panel.addMouseWheelListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		gui.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateMousePosition(e);
		gui.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		gui.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		gui.repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		gui.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (gui.getCurrentGame() == null) {
			return;
		}
		GameBlock[][] blocks = gui.getCurrentGame().getGameBoard().getBoard();
		GameBlock block = null;
		for (int y = 0; y < gui.getCurrentGame().getGameBoard().getSizeY(); y++) {
			for (int x = 0; x < gui.getCurrentGame().getGameBoard().getSizeX(); x++) {
				block = blocks[x][y];
				int xPos = block.getBoardPosX();
				int yPos = block.getBoardPosY();
				if ((e.getX() > xPos)
						&& (e.getX() < xPos + block.getNewSize().width)
						&& (e.getY() > yPos)
						&& (e.getY() < yPos + block.getNewSize().height)
						&& ((gui.getCurrentGame().getPlayerTurn() == block
								.getOccupant().getValue()) || block
								.getOccupant() == BlockOccupant.EMPTY)) {
					gui.getCurrentGame().setActive(block);
				}
			}
		}
		gui.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		updateMousePosition(arg0);
		gui.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (timer.getElapsedTime() * 1e-9 > SCROLL_DELAY_SECS) {
			int rots = (int) e.getPreciseWheelRotation();
			gui.adjustScale(rots * 0.01);
			timer.resetTimer();
		} else {
			int rots = (int) e.getPreciseWheelRotation();
			gui.adjustScale(rots * 0.10);
		}
	}

	private void updateMousePosition(MouseEvent e) {
		if (gui.getCurrentGame() != null) {

			GameBlock[][] blocks = gui.getCurrentGame().getGameBoard()
					.getBoard();
			GameBlock block = null;
			for (int y = 0; y < gui.getCurrentGame().getGameBoard().getSizeY(); y++) {
				for (int x = 0; x < gui.getCurrentGame().getGameBoard()
						.getSizeX(); x++) {
					block = blocks[x][y];
					int xPos = block.getBoardPosX();
					int yPos = block.getBoardPosY();
					if ((e.getX() > xPos)
							&& (e.getX() < xPos + block.getNewSize().width)
							&& (e.getY() > yPos)
							&& (e.getY() < yPos + block.getNewSize().height)
							&& (block.getOccupant() != BlockOccupant.NULL)) {
						block.setHover(true);
					} else {
						block.setHover(false);
					}
				}
			}
		}
	}
}
