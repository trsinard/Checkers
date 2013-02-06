import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Board implements Drawable, GameStateListener {

	private int boardSizeX;
	private int boardSizeY;
	private int moveCount;
	private ArrayList<Drawable> graphics;
	private GameBlock[][] board;

	public Board(int size_x, int size_y) {
		this.boardSizeX = size_x;
		this.boardSizeY = size_y;
		this.graphics = new ArrayList<Drawable>();
		board = new GameBlock[boardSizeX][boardSizeY];
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				board[x][y] = (new GameBlock(x + "" + y, BlockOccupant.EMPTY,
						x, y));
			}
		}
	}

	public int getSizeX() {
		return boardSizeX;
	}

	public int getSizeY() {
		return boardSizeY;
	}

	public int getMoveCount() {
		return moveCount;
	}
	
	public ArrayList<Drawable> getGraphics(){
		return graphics;
	}
	public GameBlock[][] getBoard() {
		return board;
	}

	public void setMoveCount(int val) {
		moveCount = val;
	}

	public void setPiece(int pos_x, int pos_y, GameBlock piece) {
		board[pos_x][pos_y] = piece;
	}

	public GameBlock getPiece(int pos_x, int pos_y) {
		return board[pos_x][pos_y];
	}
	
	public void addGraphic(Drawable graphic){
		graphics.add(graphic);
	}

	public void swapPiece(GameBlock srcPiece, GameBlock destPiece) {
		int srcLocX = srcPiece.getGridX();
		int srcLocY = srcPiece.getGridY();
		int destLocX = destPiece.getGridX();
		int destLocY = destPiece.getGridY();

		srcPiece.setGridX(destLocX);
		srcPiece.setGridY(destLocY);
		destPiece.setGridX(srcLocX);
		destPiece.setGridY(srcLocY);
		board[destLocX][destLocY] = srcPiece;
		board[srcLocX][srcLocY] = destPiece;
	}

	@Override
	public void draw(Graphics2D g, ScreenData sd) {

		BufferedImage image = ThemeManager.getThemeManager().getImage(
				"background");
		if (image != null) {
			g.drawImage(image, 0, 0,
					(int) Math.round(image.getWidth() * sd.getScaleRatio()),
					(int) Math.round(image.getHeight() * sd.getScaleRatio()),
					null);
		}
		
		for(Drawable d : graphics){
			if(d instanceof Graphic){
				Graphic graphic = (Graphic)d;
				int size_x = (int) Math.round(graphic.getOriginalSize().getWidth() * sd.getWidthScale());
				int size_y = (int) Math.round(graphic.getOriginalSize().getHeight() * sd.getHeightScale());
				graphic.setSize(new Dimension(size_x, size_y));
				int loc_x = (int) ((graphic.getGridPosX() * (128 * sd.getWidthScale())) + (size_x / 2)  + (128 * sd.getWidthScale()));
				int loc_y = (int) ((graphic.getGridPosY() * (128 * sd.getHeightScale())) + (size_y / 2)  + (128 * sd.getHeightScale()));
				System.out.println(loc_x + ", " + loc_y);
				graphic.setPosX(loc_x);
				graphic.setPosY(loc_y);
			}
			d.draw(g, sd);
		}
		
		drawBoardPieces(g, sd);

	}

	private void drawBoardPieces(Graphics2D g, ScreenData sd) {
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				board[x][y].draw(g, sd);
			}
		}
	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		String str = "";
		for (int y = 0; y < boardSizeY; y++) {
			str += "\n";
			for (int x = 0; x < boardSizeX; x++) {
				str += this.getBoard()[x][y].getOccupant().getStringValue()
						+ " ";
			}
		}
		return str;
	}
	
	public void eliminatePiece(GameBlock piece){
		for (int y = 0; y < boardSizeY; y++) {
			for (int x = 0; x < boardSizeX; x++) {
				if(getBoard()[x][y].equals(piece)){
					piece.setOccupant(BlockOccupant.EMPTY);
					//Piece "eliminated"
					return;
				}
			}
		}
	}

	@Override
	public void boardChange(CheckersGame game) {
	}

	@Override
	public void gameOver(CheckersGame game, BlockOccupant player) {
		// TODO Auto-generated method stub
		
	}
}
