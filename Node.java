package pathfindingVisualizer;
/**
 * @author Aayushi Pandey
 * This class defines a node
 */
public class Node {
	int x;
    int y;
	boolean isWall;
	Node previous;
	double weight;
	boolean isVisited;
	boolean isWeightNode;
	
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.weight = Double.POSITIVE_INFINITY;
	}
}
