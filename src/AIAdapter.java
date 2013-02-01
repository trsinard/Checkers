
public interface AIAdapter {
	
	public int[] getScores(AINode node);
	public boolean move(GameBlock srcPiece, GameBlock destPiece);
	public AIAdapter copy();

}
