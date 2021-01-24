package pathfindingVisualizer;
/**
 * @author Aayushi Pandey
 * Graph implementation for Dijkstra's algorithm
 */

import java.util.ArrayList;
import java.util.HashSet;

public class DijkstraAlgorithm extends Graph {

	public DijkstraAlgorithm(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	//Computes and returns a path between the source and destination node
	public ArrayList<Node> findPath() {
		// TODO implement a faster DS for this
		//A hash set to maintain a priority queue
		HashSet<Node> hSet = new HashSet<Node>();		
		
		for(Node n : this.nodes.values()) {
			hSet.add(n);
		}
		
		this.source.weight = 0;
		String sourceKey = generateKey(this.source);
		String destinationKey = generateKey(this.destination);
		String currentKey = sourceKey;
		
		//Loop through the hash set removing one node at a time 
		//and to evaluate distances of each node and its neighbours from the source one by one
		// until the destination is found
		while(!hSet.isEmpty()) {
			hSet.remove(nodes.get(currentKey));
			
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
			
			//Loop through each neigbours and evaluate their distances
			for(int i = 0; i < neighbours.size(); i++) {
				//Calculate the new distance by adding neighbour's weight and distance
				double distance = 1;
				String currrentNeighbourKey = this.generateKey(neighbours.get(i));
				//for the the weight nodes distance to be added is 15
				if(this.nodes.get(currrentNeighbourKey).isWeightNode) {
					distance = 15;
				}
				double newDistance = this.nodes.get(currentKey).weight + distance;
			
				//if the new distance is less neighbour's weight, update the neighbour's weight to newDistance
				if(newDistance < neighbours.get(i).weight) {
					neighbours.get(i).weight = newDistance;
					Node n = this.nodes.get(currrentNeighbourKey);
					n.previous = this.nodes.get(currentKey);
					
					//Invoke all the registered listeners
					for(NodeVisitedListener listener : this.listeners) {
						listener.nodeVisited(n.x, n.y);
					}
				}
			}
			//Update the current key
			currentKey = getCurrentKey(hSet);			
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

	/**
	 * Returns the key of the node with the minimum weight
	 * @param hs: hash set of the nodes
	 * @return: String/key of the minimum weight node
	 */
	public String getCurrentKey(HashSet<Node> hs) {
		
		String minKey = "";
		double minDist = Double.POSITIVE_INFINITY;
		//loop through the hash set to get the key of the minimum weight node
		for(Node n : hs) {
			String key = this.generateKey(n);
			if(nodes.get(key).weight < minDist) {
				minDist = nodes.get(key).weight;
				minKey = key;
			}
			
		}
		
		return minKey;
	}

}
