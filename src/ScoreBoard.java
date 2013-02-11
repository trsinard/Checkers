import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * - object to draw the game score-board background, and the score number.
 */
public class ScoreBoard implements Drawable, GameStateListener {

	//Stores current score-board image.
	private BufferedImage scoreBoardImage;
	//Stores player scores
	private int playerScore1;
	private int playerScore2;
	//The new size of the rescaled image.
	private Dimension newSize;
	//The original image size.
	private Dimension originalSize;
	//Z-layer
	private int layerZ;
	//ID
	private String id;

	public ScoreBoard(String id) {
		this.id = id;
		this.layerZ = 0;
		this.originalSize = new Dimension(1024, 64);
		this.newSize = originalSize;
	}
	
	/**
	 * Functioned called to update the score-board data of the current game.
	 *<b>Preconditions:</b>  Takes a valid CheckersGame reference as parameter.
	 *<b>Postconditions:</b> Changes the drawn image of score-board based on turn, and
	 * updates the score to be drawn. Nothing is changed if received game is null.
	 *<b>Throws:</b> None
	 */
	public void boardChange(CheckersGame game) {
		if(game == null){
			return;
		}
		if (game.getPlayerTurn() == 1) {
			this.scoreBoardImage = ThemeManager.getThemeManager().getImage(
					"p1turnbar");
		} else if (game.getPlayerTurn() == 2) {
			this.scoreBoardImage = ThemeManager.getThemeManager().getImage(
					"p2turnbar");
		}
		playerScore1 = game.getPieceCount(BlockOccupant.PLAYER);
		playerScore2 = game.getPieceCount(BlockOccupant.PLAYER2);
	}

	/**
	 *  Gets and returns score-board image.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions: Returns reference to score-board image.</b>
	 *<b>Throws:</b> None
	 */
	public BufferedImage getScoreBoardImage() {
		return scoreBoardImage;
	}

	/**
	 * Gets and returns score for player 1.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns value of player 1 score.
	 *<b>Throws:</b> None
	 */
	public int getPlayerScore1() {
		return this.playerScore1;
	}

	/**
	 * Gets and returns score for player 2.
	 *<b>Preconditions:</b> None 
	 *<b>Postconditions:</b> Returns value of player 2 score.
	 *<b>Throws:</b> None
	 */
	public int getPlayerScore2() {
		return this.playerScore2;
	}

	/**
	 * Gets and returns score-board image original dimension.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to score-board's original dimension.
	 *<b>Throws:</b> None
	 */
	public Dimension getOriginalSize() {
		return originalSize;
	}

	/**
	 *  Gets and returns score-board image new dimension.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Returns reference to score-board's rescaled dimension
	 *<b>Throws:</b> None
	 */
	public Dimension getNewSize() {
		return newSize;
	}

	public void gameOver(CheckersGame game, BlockOccupant player) {
		boardChange(game);
	}

	/**
	 * Method to get the layer priority
	 * <b>Preconditions:</b> None
	 * <b>Postconditions:</b> Returns layer priority
	 * <b>Throws:</b> None
	 */
	public int getZ(){
		return layerZ;
	}
	/**
	 * Method to apply the images and draw the graphics.
	 *<b>Preconditions:</b> None
	 *<b>Postconditions:</b> Draws score-board and data
	 *<b>Throws:</b> None
	 */
	public void draw(Graphics2D g, ScreenData sd) {
		//Set anti-aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		//Initialize scale-variables
		int scaledWidth = 0, scaledHeight = 0;
		
		
		if (scoreBoardImage != null) {
			//Determine the scale of the image from received screen-data.
			scaledWidth = (int) Math.round(scoreBoardImage.getWidth()
					* sd.getScaleRatio());
			scaledHeight = (int) Math.round(scoreBoardImage.getHeight()
					* sd.getScaleRatio());
			//Create new size
			this.newSize = new Dimension((int) (Math.round(scaledWidth)),
					(int) (Math.round(scaledHeight)));

			g.drawImage(scoreBoardImage, 0, 0, scaledWidth, scaledHeight, null);

			// Draw P2 Score
			int[] splitScore = splitScoreDigits(this.playerScore2);
			BufferedImage tens = ThemeManager.getThemeManager().getImage(
					Integer.toString(splitScore[0]));
			BufferedImage ones = ThemeManager.getThemeManager().getImage(
					Integer.toString(splitScore[1]));
			
			//Assumes digit images are 64 x 64 pixels.
			//Image offsets from one's and ten's position. Half-size scaled to ratio
			int numOffset = (int) Math.round(32 * sd.getScaleRatio());
			if (tens != null && ones != null) {
				//X-pos offset to canvas is 150-pixels to scale.
				int xPosP2 = (int) Math.round(150 * sd.getScaleRatio());
				scaledWidth = (int) Math.round(tens.getWidth()
						* sd.getScaleRatio());

				g.drawImage(tens, xPosP2, 0, scaledWidth, scaledWidth, null);
				g.drawImage(ones, xPosP2 + numOffset, 0, scaledWidth,
						scaledWidth, null);
			}
			//Draw P1 Score
			splitScore = splitScoreDigits(this.playerScore1);
			tens = ThemeManager.getThemeManager().getImage(
					Integer.toString(splitScore[0]));
			ones = ThemeManager.getThemeManager().getImage(
					Integer.toString(splitScore[1]));
			//Offset is 150 pixels from the right, to scale.
			int xPos = (int) Math.round(scoreBoardImage.getWidth()
					* sd.getScaleRatio() - (150 * sd.getScaleRatio())
					- scaledWidth - numOffset);
			if (tens != null && ones != null) {
				scaledWidth = (int) Math.round(tens.getWidth()
						* sd.getScaleRatio());
				g.drawImage(tens, xPos, 0, scaledWidth, scaledWidth, null);
				g.drawImage(ones, xPos + numOffset, 0, scaledWidth,
						scaledWidth, null);
			}
		}

	}

	@Override
	public void mouseClickPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	/**
	 * Splits a two-digit number into array of single digits.
	 *<b>Preconditions:</b> Given number must be between 0 and 99.
	 *<b>Postconditions:</b> Returns new array with digits split, 0 and 99 being bounds. 
	 *<b>Throws:</b> None
	 */
	public int[] splitScoreDigits(int score) {

		
		int[] split = new int[2];
		if (score == 10) {
			split[0] = 1;
			split[1] = 0;
		} else if (score < 10 && score > 0) {
			split[0] = 0;
			split[1] = score;
		} else if (score > 10 && score < 99) {
			split[0] = 1;
			split[1] = score % 10;
		} else if(score < 0){
			split[0] = split[1] = 0;
		} else if(score > 99){
			split[0] = split[1] = 9;
		}
		return split;
	}

	@Override
	public String getID() {
		return id;
	}

}
