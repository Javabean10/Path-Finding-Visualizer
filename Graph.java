package pathfindingVisualizer;

/**
 * @author Aayushi Pandey
 */
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A listener to be called when a node is visited
 */
interface NodeVisitedListener{
	public void nodeVisited(int x, int y);
}

/**
 * A class to define the basic model/structure of the graph.
 */
public abstract class Graph {
	protected HashMap<String,Node> nodes;
	protected Node source;
	protected Node destination;
	protected int rows;
	protected int columns;
	protected ArrayList<NodeVisitedListener> listeners;
	
	/**
	 * An enum class of directions
	 * This class defines possible directions to find a neighbour node in
	 */
	enum DIR {
		LEFT,
		UP,
		RIGHT,
		DOWN
	}
	
	/**
	 * argumented constructor
	 * @param rows : number of rows
	 * @param columns: number of columns
	 */
	public Graph(int rows, int columns) {
		//an array list of node visited listeners
		this.listeners = new ArrayList<NodeVisitedListener>();
		this.rows = rows;
		this.columns = columns;
		this.nodes = new HashMap<String,Node>();
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.columns; j++) {
				Node node = new Node(i,j);
				String key = generateKey(node);
				nodes.put(key, node);			
			}
		}
	}
	
	/**
	 * sets the source node in the graph
	 * @param x: x coordinate of the source
	 * @param y: y coordinate of the source
	 */
	public void setSource(int x, int y) {
		String key = this.generateKey(x,y);
		Node node = this.nodes.get(key);
		this.source = node;
	}
	
	
	/**
	 * sets the destination node in the graph 
	 * @param x: x coordinate of the destination
	 * @param y: y coordinate of the destination
	 */
	public void setDestination(int x, int y) {
		String key = this.generateKey(x,y);
		Node node = this.nodes.get(key);
		this.destination = node;
	}
	
	/**
	 * Updates the wall node in the graph 
	 * @param x: x coordinate of the wall node
	 * @param y: y coordinate of the wall node
	 */
	public void setWall(int x, int y) {
		String key = this.generateKey(x,y);
		Node node = this.nodes.get(key);
		node.isWeightNode = false;
		node.isWall = !node.isWall;
	}
	
	/**
	 * Updates weight of the node at x and y
	 * @param x: x coordinate of the node
	 * @param y: y coordinate of the node
	 */
	public void setWeight(int x, int y) {
		String key = this.generateKey(x,y);
		Node node = this.nodes.get(key);
		node.isWall = false;
		node.isWeightNode = !node.isWeightNode;
		node.weight = node.isWeightNode ? 15 : Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Adds node visited listener to the listeners array list
	 * @param listener: listener for the graph
	 */
	public void addNodeVisitedListener(NodeVisitedListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Creates key of format "0x0y" for the coordinates x and y
	 * @param x: x coordinate
	 * @param y: y coordinate
	 * @return key in the format "0x0y"
	 */
	public String generateKey(int x, int y) {
		return String.format("%02d", x) + String.format("%02d", y);
	}
	
	/**
	 * Creates and returns key of format "0x0y" of a given node
	 * @param node: a node to get its key
	 * @return key of the node
	 */
	public String generateKey(Node node) {
		return String.format("%02d", node.x) + String.format("%02d", node.y);
	}
	

	/**
	 * Returns a neighbour at specified direction of a node
	 * @param node: a node whose neighbour to return
	 * @param direction: neighbour's direction
	 * @return neigbour node of the node at direction
	 */
	public Node getNeighbour(Node node, DIR direction) {
		String neighbourKey = "";
		
		switch(direction) {
			//if the direction is LEFT
			case LEFT:
				//if the y coordinate of the node 0 (it is in the first column) then return null
				if(node.y == 0) {
					return null;
				}
				//get the key of the left neighbour node (at x and y-1) of the node 
				neighbourKey = this.generateKey(node.x, node.y-1);
				break;
				
			//if the direction is UP	
			case UP:
				//if the x coordinate of the node is 0 (it is in the first row) then return null
				if(node.x == 0) {
					return null;
				}
				//get the key of the up neighbour node (at x-1 and y) of the node 
				neighbourKey = this.generateKey(node.x - 1, node.y);
				break;
				
			//if the direction is RIGHT	
			case RIGHT:
				//if the y coordinate of the node is columns-1 (it is in the last column) then return null
				if(node.y == this.columns - 1) {
					return null;
				}
				//get the key of the right neighbour node (at x and y+1) of the node
				neighbourKey = this.generateKey(node.x, node.y + 1);
				break;
				
			//if the direction id DOWN	
			case DOWN:
				//if the x coordinate of the node is rows-1 (it is in the last row) then return null
				if(node.x == this.rows - 1) {
					return null;
				}
				//get the key of the down neighbour node (at x+1 and y) of the node 
				neighbourKey = this.generateKey(node.x + 1, node.y);
				break;
			
			//if the direction is not correct, return null
			default:
				return null;
		}
			
		// return the node at neighbourKey
		return this.nodes.get(neighbourKey);
		
	}
	
	/**
	 * Resets isVisited and weights of all nodes
	 */
	public void reset() {
		 for(Node node : nodes.values()) {
			 node.isVisited = false;
			 node.previous = null;
			 node.weight = node.isWeightNode ? 15 : Double.POSITIVE_INFINITY;
		 }
	}
	
	/**
	 * Clears source, destination and resets all nodes' weights, previous node,
	 * isWall, isVisited.
	 */
	public void clear() {
		this.source = null;
		this.destination = null;
		for(Node node : nodes.values()) {
			node.isVisited = false;
			node.previous = null;
			node.isWall = false;
			node.isWeightNode = false;
			node.weight = Double.POSITIVE_INFINITY;
		}
	}
	
	/**
	 * Resets weights of all nodes.
	 */
	public void clearWeight() {
		for(Node node : nodes.values()) {
			 node.weight = Double.POSITIVE_INFINITY;
			 node.isWeightNode = false;
		 }
	}
	
	/**
	 * An abstract method, which is implemented in sub classes.
	 * Returns a path between the source and destination node.
	 * @return an array list of nodes in path from source to destination node.
	 */
	public abstract ArrayList<Node> findPath();
}
