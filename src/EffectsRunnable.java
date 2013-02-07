
public class EffectsRunnable implements Runnable {

	private CheckersGUI gui;
	private Timer timer;
	private final double REFRESH_RATE = 0.10;
	
	public EffectsRunnable(CheckersGUI gui){
		this.gui = gui;
		this.timer = new Timer();
	}
	public void run() {
		timer.startTimer();
		while(timer.getElapsedTime() < 0.10){
			
			if(gui.isShowingStart()){
				//adjustStartEffects();
			}
			
			
			gui.repaint();
		}

	}

}
