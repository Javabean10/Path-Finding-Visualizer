package pathfindingVisualizer;

/**
 * @author Aayushi Pandey
 * A controller class controlling communications between the view and algorithm implementations
 */

import java.util.ArrayList;

import javax.swing.JFrame;

public class GraphController implements NodeVisitedListener, ViewListener{
	
	//GraphView object, view creates a GUI window
	private GraphView view;
	
	//Algorithms' objects
	private DijkstraAlgorithm dijkstra;
	private BFSAlgorithm bfs;
	private DFSAlgorithm dfs;
	private BellmanFordAlgorithm bellmanFord;
	
	/**
	 * Unargumented constructor
	 */
	public GraphController() {
		 
		//Instantiate the view object
		view = new GraphView();
		//set GUI specifics
		view.setVisible(true);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setTitle("Pathfinding Visualizer");
		
		//add controller as View Listener
		view.addViewLisnter(this);
		
		
		//Grid rows and columns
		int rows = view.getRows();
		int columns = view.getColumns();
		
		
		//Instantiate the dijkstra object
		dijkstra = new DijkstraAlgorithm(rows, columns);
		//add controller as Node Visited Listener for the dijkstra object
		dijkstra.addNodeVisitedListener(this);
		
		//Instantiate the bfs object
		bfs = new BFSAlgorithm(rows, columns);
		//add controller as Node Visited Listener for the bfs object
		bfs.addNodeVisitedListener(this);
		
		//Instantiate the dfs object
		dfs = new DFSAlgorithm(rows, columns);
		//add controller as Node Visited Listener for the dfs object
		dfs.addNodeVisitedListener(this);
		
		//Instantiate the bellmanFord object
		bellmanFord = new BellmanFordAlgorithm(rows, columns);
		//add controller as Node Visited Listener for the bellmanFord object
		bellmanFord.addNodeVisitedListener(this);
	}
	
	@Override
	/**
	 * Implements NodeVisitedListener's method nodeVisited.
	 * Calls view's visitCell method to highlight the cell being visited by the algorithm
	 * in the GUI.
	 */
	public void nodeVisited(int x, int y) {
		view.visitCell(x, y);		
	}
	
	@Override
	/**
	 * Implements ViewListener's method runAlgorithm.
	 * Calls the run method to find the path between the source and destination.
	 */
	public void runAlgorithm() {
		this.run();		
	}
	
	@Override
	/**
	 * Implements ViewListener's method setWall.
	 * Calls the setWalls method to update wall of all the algorithms.
	 */
	public void setWall(int x, int y) {
		this.setWalls(x, y);
	}

	@Override
	/**
	 * Implements ViewListener's method resetModels.
	 * Calls the reset method to reset the path and nodeVisited.
	 */
	public void resetModel() {
		this.reset();		
	}
	
	/**
	 * Implements ViewListener's method clearModel.
	 * Calls the clear method to clear the graph for all algorithms' object.
	 */
	@Override
	public void clearModel() {
		this.clear();
	}
	
	@Override
	/**
	 * Implements ViewListener's method resetWeight.
	 * This method calls the resetWeights method to reset node weight 
	 * for the dijkstra and bellmanFord objects.
	 */
	public void resetWeight() {
		this.resetWeights();		
	}
	
	@Override
	/**
	 * Implements ViewListener's method setWeight.
	 * This method calls the setWeight method to set node weight 
	 * for the dijkstra and bellmanFord objects.
	 */
	public void setWeight(int x, int y) {
		this.setNodeWeight(x, y);
		
	}
	
	/**
	 * Calls findPath method of the user specified algorithm
	 * to get the path between the source and destination
	 * and then asks the view to highlight the path in the GUI.
	 */
	public void run() {

		// Get the user set source
		Cell source = view.getSource();
		//get the user set destination
		Cell destination = view.getDestination();
		//get the user chosen algorithm
		ALGORITHMS algorithm = view.getSelectedAlgorithm();
		
		//An array list of node to store the path between the source and destination
		ArrayList<Node> nodePath = new ArrayList<Node>();
		
	
		switch(algorithm) {
			//If Dijkstra is selected
			case DIJKSTRA:
				//set the source for the dijkstra object
				dijkstra.setSource(source.x, source.y);
				//set the destination for the dijkstra object
				dijkstra.setDestination(destination.x, destination.y);
				//call the findPath method to get the path between the source and destination
				nodePath = dijkstra.findPath();	
				break;
			
			//if BFS is selected
			case BFS:
				//set the source for the bfs object
				bfs.setSource(source.x, source.y);
				//set the destination for the bfs object
				bfs.setDestination(destination.x, destination.y);
				//call the findPath method to get the path between the source and destination
				nodePath = bfs.findPath();	
				break;

			//if DFS is selected
			case DFS:
				//set the source for the dfs object
				dfs.setSource(source.x, source.y);
				//set the destination for the dfs object
				dfs.setDestination(destination.x, destination.y);
				//call the findPath method to get the path between the source and destination
				nodePath = dfs.findPath();	
				break;
			
			//if bellmanFord is selected
			case BELLMANFORD: 
				//set the source for the bellmanFord object
				bellmanFord.setSource(source.x, source.y);
				//set the destination for the bellmanFord object
				bellmanFord.setDestination(destination.x, destination.y);
				//call the findPath method to get the path between the source and destination
				nodePath = bellmanFord.findPath();
				break;
			
		}

		//An array of Cell to store the path computed by an algorithm
		Cell[] cellPath = new Cell[nodePath.size()];
		
		//Iterate through the nodePath
        for(int i = 0; i < nodePath.size(); i++) {
        
        	//get the x coordinator
            int x = nodePath.get(i).x;
            //get the y coordinator
            int y = nodePath.get(i).y;
            
            //get the cell at x and y
            Cell cell = view.getCell(x, y);
            //set isPath to true to highlight the cell t=yellow
            cell.isPath = true;
            //set isVisited false to remove the cyan color
            cell.isVisited = false;
            //add the cell to cellPath in revverse order
            cellPath[nodePath.size() - i - 1] = cell;
        }
        
        //call view's drawPath method to highlight the path in GUI
        view.drawPath(cellPath);
	}
	
	/**
	 * Resets the path and nodeVisisted for all algorithms' object 
	 * by calling their stopExecution method.
	 */
	public void reset() {
		dijkstra.reset();
		bfs.reset();
		dfs.reset();
		bellmanFord.reset();
	}
	
	/**
	 * Clears the graph for all algorithms' object by calling their clear method.
	 */
	public void clear() {
		dijkstra.clear();
		bfs.clear();
		dfs.clear();
		bellmanFord.clear();
	}
	
	/**
	 * Updates wall by calling algorithms' setWall method
	 * @param x: x coordinator of a node
	 * @param y: y coordinator of a node
	 */
	public void setWalls(int x, int y) {
    	dijkstra.setWall(x, y);
    	bfs.setWall(x, y);
    	dfs.setWall(x, y);
    	bellmanFord.setWall(x, y);
	}
	
	/**
	 * Updates node's weight by calling algorithms' setNodeWeight method
	 * @param x: x coordinator of a node
	 * @param y: y coordinator of a node
	 */
	public void setNodeWeight(int x, int y) {
		dijkstra.setWeight(x, y);
		bellmanFord.setWeight(x, y);
	}
	
	/**
	 * Resets weights of all nodes of the dijkstra and bellmnaFord objects
	 */
	public void resetWeights() {
		dijkstra.clearWeight();
		bellmanFord.clearWeight();
	}
}
