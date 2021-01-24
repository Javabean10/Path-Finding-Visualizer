package pathfindingVisualizer;
/**
 * @author Aayushi Pandey
 * This class defines a cell
 */

public class Cell {
	
	int x;
    int y;
    Cell previous;
	boolean isWall;
	boolean isWeightCell;
	boolean isSource;
	boolean isDestination;
	boolean isVisited;
	boolean isPath;
	double weight;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	

}
