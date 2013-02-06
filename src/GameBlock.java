import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GameBlock implements Drawable {

	private String id;
	private BlockOccupant occupant;
	private boolean king;
	private boolean selected;
	private boolean hover;
	private boolean highlightAsMove;
	private boolean highlightAsTarget;
	private boolean disabled;
	private int loc_x;
	private int loc_y;
	private int pos_x;
	private int pos_y;
	private Dimension newSize;
	private Dimension originalSize;

	public GameBlock(String id, BlockOccupant occupant, int loc_x, int loc_y) {
		this.id = id;
		this.occupant = occupant;
		this.king = false;
		this.selected = false;
		this.hover = false;
		this.highlightAsTarget = false;
		this.highlightAsTarget = false;
		this.disabled = false;
		this.loc_x = loc_x;
		this.loc_y = loc_y;
		this.pos_x = -1;
		this.pos_y = -1;
		this.originalSize = new Dimension(64, 64);
		this.newSize = originalSize;
	}

	public BlockOccupant getOccupant() {
		return occupant;
	}

	public int getGridX() {
		return loc_x;
	}

	public int getGridY() {
		return loc_y;
	}

	public int getBoardPosX() {
		return pos_x;
	}

	public int getBoardPosY() {
		return pos_y;
	}

	public boolean isKing() {
		return king;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isHover() {
		return hover;
	}
	
	public boolean isHighlightAsMovable(){
		return highlightAsMove;
	}
	
	public boolean isHighlightAsTarget(){
		return highlightAsTarget;
	}
	
	public boolean isDisabled(){
		return disabled;
	}

	public String getID() {
		return id;
	}

	public Dimension getOriginalSize() {
		return originalSize;
	}

	public Dimension getNewSize() {
		return newSize;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setGridX(int loc_x) {
		this.loc_x = loc_x;
	}

	public void setGridY(int loc_y) {
		this.loc_y = loc_y;
	}
	
	public void setPosX(int x){
		this.pos_x = x;
	}
	
	public void setPosY(int y){
		this.pos_y = y;
	}

	public void setKing(boolean bool) {
		this.king = bool;
	}

	public void setSelected(boolean bool) {
		if (occupant == BlockOccupant.EMPTY || occupant == BlockOccupant.NULL) {
			return;
		}
		this.selected = bool;
	}

	public void setHover(boolean bool) {
		if (occupant == BlockOccupant.NULL) {
			return;
		}
		this.hover = bool;

	}
	
	public void setHighlightAsMovable(boolean bool) {
		if (occupant == BlockOccupant.NULL || occupant == BlockOccupant.EMPTY) {
			return;
		}
		this.highlightAsMove = bool;
	}

	public void setHighlightAsTarget(boolean bool){
		if (occupant == BlockOccupant.NULL){
			return;
		}
		this.highlightAsTarget = bool;
	}
	
	public void setDisabled(boolean bool){
		this.disabled = bool;
	}
	
	public void setOccupant(BlockOccupant occupant) {
		this.occupant = occupant;
	}

	public void setNewSize(Dimension size) {
		this.newSize = size;
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {
		BufferedImage image = null;
		ThemeManager tm = ThemeManager.getThemeManager();
		if (occupant == BlockOccupant.PLAYER) {
			if (isKing()) {
				image = tm.getImage("p1k");
			} else {
				image = tm.getImage("p1");
			}
		} else if (occupant == BlockOccupant.PLAYER2) {
			if (isKing()) {
				image = tm.getImage("p2k");
			} else {
				image = tm.getImage("p2");
			}
		}

		double scaledWidth, scaledHeight = 0.00;
		if (image != null) {
			scaledWidth = image.getWidth() * sd.getScaleRatio();
			scaledHeight = image.getHeight() * sd.getScaleRatio();
			this.newSize = new Dimension((int) (Math.round(scaledWidth)),
					(int) (Math.round(scaledHeight)));
			this.pos_x = (int) Math.round(scaledWidth * loc_x);
			this.pos_y = (int) Math.round(scaledHeight * loc_y);
			
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
					(int) Math.round(scaledHeight), null);
			image = tm.getImage("block-selected");
			if (image != null && isSelected()) {
				g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth),
						(int) Math.round(scaledHeight), null);
			}
		} 
		
		
		image = tm.getImage("block-available");
		scaledWidth = image.getWidth() * sd.getScaleRatio();
		scaledHeight = image.getHeight() * sd.getScaleRatio();
		this.newSize = new Dimension((int) (Math.round(scaledWidth)),
				(int) (Math.round(scaledHeight)));
		this.pos_x = (int) Math.round(scaledWidth * loc_x);
		this.pos_y = (int) Math.round(scaledHeight * loc_y);
		if(image != null && isHighlightAsMovable()){
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth), (int) Math.round(scaledHeight), null);
		}
		
		image = tm.getImage("block-target");
		if(image != null && isHighlightAsTarget()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth), (int) Math.round(scaledHeight), null);
		}
		
		image = tm.getImage("block-hover");
		if(image != null && isHover()) {
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth), (int) Math.round(scaledHeight), null);
		}
		
		image = tm.getImage("block-disabled");
		if(image != null && isDisabled()){
			g.drawImage(image, pos_x, pos_y, (int) Math.round(scaledWidth), (int) Math.round(scaledHeight), null);
		}
		
	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	public GameBlock copy() {
		GameBlock copy = new GameBlock(this.id, this.occupant, this.loc_x, this.loc_y);
		copy.setHover(this.isHover());
		copy.setKing(this.isKing());
		copy.setNewSize(this.getNewSize());
		copy.setSelected(this.isSelected());
		return copy;
	}
	
	public String toString(){
		String str =  "[" + occupant.getStringValue() + "-(" + loc_x + ", " + loc_y + ")]";
		if(isKing()){
			str = "K" + str;
		}
		return str;
	}
}
