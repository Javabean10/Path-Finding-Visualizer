package pathfindingVisualizer;

import java.util.ArrayList;

public class BellmanFordAlgorithm extends Graph{

	public BellmanFordAlgorithm(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	//Computes and returns a path between the source and destination node
	public ArrayList<Node> findPath() {

		this.source.weight = 0;
		
		//loop through the all nodes number of nodes-1 times
		for(int k = 0; k < this.nodes.size() - 1; k++) {
			for(int i= 0; i < this.rows; i++) {
				for(int j = 0; j < this.columns; j++) {
					String currentKey = this.generateKey(i, j);
					Node n = this.nodes.get(currentKey);
					//if the current node is not a wall evaluate distances of each node and its neighbours from the source one by one
					if(!n.isWall) {
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
						
						//Loop through each neigbours and evaluate their distances
						for(Node neighbour : neighbours) {
							//distance to be added is 1
							double distance = 1;
							String currrentNeighbourKey = this.generateKey(neighbour);
							//for the the weight nodes distance to be added is 15
							if(this.nodes.get(currrentNeighbourKey).isWeightNode) {
								distance = 15;
							}
							//if the weight of the currentNode+distance is less neighbour's weight, update the neighbour's weight to it
							if(neighbour.weight > n.weight + distance) {
								neighbour.weight = n.weight + distance;
								neighbour.previous = n;
								
								//Invoke all the registered listeners
								for(NodeVisitedListener listener : this.listeners) {
									listener.nodeVisited(neighbour.x, neighbour.y);
								}
							}
						}
					}
				}
			}	
		}
		
		String sourceKey = this.generateKey(this.source);
		String destinationKey = this.generateKey(this.destination);
		Node prevNode = this.nodes.get(destinationKey);
		String prevNodeKey = this.generateKey(prevNode);
		ArrayList<Node> path = new ArrayList<Node>();
		
		//generate path by visiting previous nodes starting from the destination to the source
		while(prevNodeKey.compareTo(sourceKey) != 0) {
			path.add(prevNode);
			prevNode = prevNode.previous;
			prevNodeKey = this.generateKey(prevNode);
		}
		
		//return the path
		return path;
	}

}
