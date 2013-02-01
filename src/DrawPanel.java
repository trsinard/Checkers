import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;

public class DrawPanel extends JPanel implements MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private Drawable pict;
	private double ratio;

	public DrawPanel() {
		pict = null;
		ratio = 1.0;
		addMouseMotionListener(this);
	}

	public void setDrawable(Drawable drawable) {
		if (drawable != null) {
			pict = drawable;
		}
	}

	public void paint(Graphics g) {
		int width = getSize().width;
		int height = getSize().height;
		ScreenData sd = new ScreenData(1024, 1024, width, height, ratio);		
		if (pict != null) {
			pict.draw((Graphics2D)g, sd);
		}
	}
	 
	public Drawable getDrawable(){
		return pict;
	}
	
	public void setRatio(double ratio){
		this.ratio = ratio;
	}
	
	public double getRatio(){
		return ratio;
	}

	public void mouseMoved(MouseEvent event) {
		if (pict != null) {
			pict.mouseClickPosition(event.getX(), event.getY());
		}
		repaint();
	}

	public void mouseDragged(MouseEvent event) {
	}
	
	public void removeAllMouseListeners(){
		for(MouseListener ml : this.getMouseListeners()){
			this.removeMouseListener(ml);
		}
		for(MouseMotionListener ml : this.getMouseMotionListeners()){
			this.removeMouseMotionListener(ml);
		}
		for(MouseWheelListener ml : this.getMouseWheelListeners()){
			this.removeMouseWheelListener(ml);
		}
	}
}
