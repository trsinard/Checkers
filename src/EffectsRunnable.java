


public class EffectsRunnable implements Runnable {

	private CheckersGUI gui;
	private Timer timer;
	private final double REFRESH_RATE = 0.0115;
	
	public EffectsRunnable(CheckersGUI gui){
		this.gui = gui;
		this.timer = new Timer();
		
	}
	public void run() {
		timer.startTimer();
		adjustStartEffects(true);
		while(true){
			if(timer.getElapsedTime() * 1.0e-9 > REFRESH_RATE){
				if(gui.isShowingStart()){
					adjustStartEffects(false);
				}	
				timer.resetTimer();
				gui.repaint();
				
				
				/*
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						gui.repaint();
					}
				});
				*/
			}
		}

	}
	
	public void adjustStartEffects(boolean first){
		Drawable img = gui.getBoardCanvas().getDrawable("gloss-board");
		if(img instanceof Graphic && img != null){
			int maxX = (int) Math.round(((Graphic) img).getImage().getWidth() * gui.getRatio());
			int maxY = (int) Math.round(((Graphic) img).getImage().getHeight() * gui.getRatio());
			int minX = -maxX;
			int currPosX = ((Graphic) img).getPosX();
			int currPosY = ((Graphic) img).getPosY();
			int xOffset = 0, yOffset = 0;
			if(first){
				((Graphic) img).setPosX(minX);
				((Graphic) img).setPosY(minX);
				return;
			}
			if(currPosX >= maxX && currPosY < maxY){
				//Far right, room to move up
				yOffset++;
			} else if(currPosX < maxX && currPosY >= maxY){
				//Max height, room to move right
				xOffset++;
			} else if(currPosX >= maxX && currPosY >= maxY){
				//Max height, far right
				xOffset = yOffset = minX * 2;
			} else if(currPosX < maxX && currPosY < maxY){
				//Neither edge hit
				xOffset++;
				yOffset++;
			}
			
			((Graphic) img).setPosX(((Graphic) img).getPosX() + xOffset);
			((Graphic) img).setPosY(((Graphic) img).getPosY() + yOffset);
		}
	}

}
