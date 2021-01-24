package pathfindingVisualizer;

/**
 * @author Aayushi Pandey
 * Graph implementation for Breadth First Search algorithm
 */
import java.util.ArrayList;

public class BFSAlgorithm extends Graph {

	public BFSAlgorithm(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	//Computes and returns a path between the source and destination node
	public ArrayList<Node> findPath() {
		//An array list to maintain a queue
		ArrayList<Node> queue = new ArrayList<Node>();
		
		this.source.isVisited = true;
		
		String sourceKey = this.generateKey(this.source);
		String destinationKey = this.generateKey(this.destination);

		queue.add(this.source);
		
		//Loop through the array list removing the front node 
		//and to mark its neighbours as visited and add them at the end of the queue if they are not already visited 
		//until the destination is visited/found
		while(!queue.isEmpty()) {
			//remove the front node
			Node current = queue.get(0);
			queue.remove(0);
			
			String currentKey = this.generateKey(current);
			
			//Stop execution as soon as the destination is found
			if(currentKey.compareTo(destinationKey) == 0) {
				break;
			}
			
			ArrayList<Node> neighbours = new ArrayList<Node>();
			
			//get all the neighbours of the current node
			Node neighbour1 = this.getNeighbour(this.nodes.get(currentKey), DIR.LEFT);
			if(neighbour1 != null && !neighbour1.isWall) {
				neighbours.add(neighbour1);
			}
			Node neighbour2 = this.getNeighbour(this.nodes.get(currentKey), DIR.UP);
			if(neighbour2 != null && !neighbour2.isWall) {
				neighbours.add(neighbour2);
			}
			Node neighbour3 = this.getNeighbour(this.nodes.get(currentKey), DIR.RIGHT);
			if(neighbour3 != null && !neighbour3.isWall) {
				neighbours.add(neighbour3);
			}
			Node neighbour4 = this.getNeighbour(this.nodes.get(currentKey), DIR.DOWN);
			if(neighbour4 != null && !neighbour4.isWall) {
				neighbours.add(neighbour4);
			}
			
			//loop through each neigbours evalate their status
			for(int i = 0; i < neighbours.size(); i++) {
				String currentNeighbourKey = this.generateKey(neighbours.get(i));
				//if the neighbour is not visited yet, mark is as visited and add it at the end of the queue
				if(!neighbours.get(i).isVisited) {
					neighbours.get(i).isVisited = true;
					Node n = this.nodes.get(currentNeighbourKey);
					n.previous = this.nodes.get(currentKey);
					queue.add(neighbours.get(i));
					
					//Invoke all the registered listeners
					for(NodeVisitedListener listener: this.listeners) {
						listener.nodeVisited(n.x, n.y);
					}
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
		
		//return the path
		return path;
	}
	
	
	

}
