import java.util.ArrayList;

public class AINode {

	private int point;
	private AIAdapter gameState;
	private AINode parent;
	private ArrayList<AINode> childrenNode = new ArrayList<AINode>();

	public AINode(AIAdapter gamestate) {
		this.gameState = gameState.copy();
	}

	public AIAdapter getGameState() {
		return gameState;
	}

	public int getPoint() {
		return point;
	}

	public AINode getParent() {
		return parent;
	}

	public void addChildren(AINode node) {
		childrenNode.add(node);
		node.parent(this);
	}

	private void parent(AINode node) {
		this.parent = node;
	}

	public void setPoint(int val) {
		point = val;

	}
}
