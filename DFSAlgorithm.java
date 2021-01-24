package pathfindingVisualizer;

/**
 * @author Aayushi Pandey
 * Graph implementation for Depth First Search algorithm
 */
import java.util.ArrayList;

public class DFSAlgorithm extends Graph{

	public DFSAlgorithm(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	//Computes and returns a path between the source and destination node
	public ArrayList<Node> findPath() {
		//An array list to maintain a stack
		ArrayList<Node> stack = new ArrayList<Node>();
	    Node previous = null;
		
		stack.add(this.source);
		String sourceKey = this.generateKey(this.source);
		String destinationKey = this.generateKey(this.destination);
		
		//Loop through the stack removing the top node and to mark it as visited if not visited yet
		// and to add all its neigbours to the stack until the destination is found
		while(!stack.isEmpty()) {
			//pop the top node
			Node n = stack.remove(stack.size() - 1);
			String currentKey = this.generateKey(n);
			
			//if the current node is not visited mark it as visited and add its neighbours to the stack
			if(!n.isVisited) {
				n.isVisited = true;
				//Invoke all the registered listeners 
				for(NodeVisitedListener listener : this.listeners) {
					listener.nodeVisited(n.x, n.y);
				}
				//set the previous of the current node if not set yet
				if(previous != null) {
					n.previous = previous;
				}
				//update the previous
				previous = n;
				
				//Stop execution as soon as the destination is found
				if(currentKey.compareTo(destinationKey) == 0) {
					break;
				}
				
				//Get all the neigbours of the current node and add them to the stack
				Node neighbour1 = this.getNeighbour(this.nodes.get(currentKey), DIR.LEFT);
				if(neighbour1 != null && !neighbour1.isWall) {
					stack.add(neighbour1);
				}
				Node neighbour2 = this.getNeighbour(this.nodes.get(currentKey), DIR.UP);
				if(neighbour2 != null && !neighbour2.isWall) {
					stack.add(neighbour2);
				}
				Node neighbour3 = this.getNeighbour(this.nodes.get(currentKey), DIR.RIGHT);
				if(neighbour3 != null && !neighbour3.isWall) {
					stack.add(neighbour3);
				}
				Node neighbour4 = this.getNeighbour(this.nodes.get(currentKey), DIR.DOWN);
				if(neighbour4 != null && !neighbour4.isWall) {
					stack.add(neighbour4);
				}
			}
			
		}
		
		ArrayList<Node> path = new ArrayList<Node>();
		Node prevNode = this.nodes.get(destinationKey);
		String prevKey = this.generateKey(prevNode);
		
		//generate path by visiting previous nodes starting from the destination to the source
		while(prevKey.compareTo(sourceKey) != 0) {
			path.add(prevNode);
			prevNode = prevNode.previous;
			prevKey = this.generateKey(prevNode);
		}
		
		//Return the path
		return path;
	}
	
	

}
