package pathfindingVisualizer;
/**
 * @author Aayushi Pandey
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

/**
 * A listener to be called to update the graph
 */
interface ViewListener{
	public void runAlgorithm();
	public void setWall(int x, int y);
	public void resetModel();
	public void clearModel();
	public void resetWeight();
	public void setWeight(int x, int y);
}

/**
 * An enum class of algorithms
 * This class defines available algorithms to find a path 
 */
enum ALGORITHMS{
	DIJKSTRA,
	BFS,
	DFS,
	BELLMANFORD
}

/**
 * An enum class of speeds
 * This class defines available speeds to run the algorithm
 */
enum SPEED{
	FAST,
	MEDIUM,
	SLOW
}

/**
 * A class to define a GUI and handle user actions.
 * It notifies the controller to update the graph
 */
public class GraphView extends JFrame implements ActionListener {	
	private static final int CELL_SIZE = 20;
    private static final int SPACING = 1;
    private static final int topPanelHeight = 38;
    private static final int titleBarHeight = 22;
    private int columns;
    private int rows;
    private Cell source;
    private Cell destination;
    private ALGORITHMS selectedAlgorithm;
    private HashMap<String, Cell> cells;
	private JPanel optionPanel, gridPanel;
	private JRadioButton wallButton, sourceButton, destinationButton, weightButton;
	private JButton runButton, resetButton, clearButton;
	private JComboBox<ALGORITHMS> algorithmsList;
	private JComboBox<SPEED> speedList;
	private JLabel speedLabel;
	private ArrayList<ViewListener> listeners;
	
	
	/**
	 * Un-argumented constructor
	 */
	public GraphView() {
		
		//An array list of ViewListener
		listeners = new ArrayList<ViewListener>();
	
		//Get the screen size of the device
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//set JFrame Window's position (upper left corner position) and size
		setBounds(0, 0, screenSize.width, screenSize.height);
	   
	    //store width of the window into windowwidth
		int windowWidth = getSize().width;
		//stiore height of the window into windowHeight
        int windowHeight = getSize().height;
        
        //Compute number of columns
        this.columns = windowWidth / CELL_SIZE;
        //Compute number of rows
        this.rows = (windowHeight / CELL_SIZE) - 9;
        
        
        //top panel
        optionPanel = new JPanel();
        optionPanel.setBounds(0, 0, windowWidth, topPanelHeight);
        optionPanel.setLayout(new FlowLayout());
        optionPanel.setBackground(Color.DARK_GRAY);
        add(optionPanel);
        	   
        //Create a button group
        ButtonGroup bg = new ButtonGroup();
        
        //Instantiate wallButton
        wallButton = new JRadioButton();
        wallButton.setText("Wall");
        wallButton.setForeground(Color.WHITE);
        wallButton.setSelected(true);
        
        //Instantiate sourceButton
        sourceButton = new JRadioButton();
        sourceButton.setText("Source");
        sourceButton.setForeground(Color.WHITE);
        
        //Instantiate destinationButton
        destinationButton = new JRadioButton();
        destinationButton.setText("Destination");
        destinationButton.setForeground(Color.WHITE);
        
        //Instantiate weightButton
        weightButton = new JRadioButton();
        weightButton.setText("Weight");
        weightButton.setForeground(Color.WHITE);
        
        //Group all the radio buttons
        bg.add(wallButton);
        bg.add(sourceButton);
        bg.add(destinationButton);
        bg.add(weightButton);
        
        //Add all the buttons to the option panel
        optionPanel.add(wallButton);
        optionPanel.add(sourceButton);
        optionPanel.add(destinationButton);
        optionPanel.add(weightButton);
        
        //An array of enum ALGORITHMS
        ALGORITHMS[] algorithm = new ALGORITHMS[]{ALGORITHMS.DIJKSTRA,ALGORITHMS.BFS,ALGORITHMS.DFS, ALGORITHMS.BELLMANFORD};
        algorithmsList = new JComboBox<ALGORITHMS>(algorithm);
        algorithmsList.setBounds(50, 100, 90, 20); 
        optionPanel.add(algorithmsList);
        algorithmsList.addActionListener(this);
        
        //An array of enum SPEED
        SPEED[] speeds = new SPEED[]{SPEED.FAST, SPEED.MEDIUM, SPEED.SLOW};
        speedList = new JComboBox<SPEED>(speeds);
        algorithmsList.setBounds(50, 100, 90, 20); 
        speedLabel = new JLabel("Speed");
        speedLabel.setForeground(Color.WHITE);
        optionPanel.add(speedLabel);
        optionPanel.add(speedList);
            
        //Instantiate the run button
        runButton = new JButton("Run");
        optionPanel.add(runButton);
        runButton.addActionListener(this);
       
        //Instantiate the reset button
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        optionPanel.add(resetButton);
     
        //Instantiate the clear button
        clearButton = new JButton("Clear grid");
        clearButton.addActionListener(this);
        optionPanel.add(clearButton);
                
        //botttom panel
	    gridPanel = new JPanel();
        add(gridPanel);
        gridPanel.setLayout(new GridLayout(1,1));
        gridPanel.setBounds(0, 38, windowWidth, windowHeight - topPanelHeight - titleBarHeight);
	    
        //create a Grid oblect
        Grid grid = new Grid();
        gridPanel.add(grid);
        grid.setBounds(gridPanel.getX(), gridPanel.getY(), gridPanel.getWidth(), gridPanel.getHeight());
        //set mouse listeners
        grid.addMouseListener(new Mouse());
        grid.addMouseMotionListener(new Mouse());
        
        //Instantiate the hash map cells
        cells = new HashMap<String,Cell>();
        for(int i = 0; i < rows; i++) {
        	for(int j = 0; j < columns; j++) {
        		Cell cell = new Cell(i,j);
        		String key = generateKey(cell);
        		cells.put(key,cell);
        	}
        }
	}
	
	
	@Override
	//Action listener
	//This method handles JComponents events
	public void actionPerformed(ActionEvent e) {
		
		//When an algorithm is selected from the algorithmsList combo box
		if(e.getSource() == algorithmsList) {
			ALGORITHMS algorithm = (ALGORITHMS)algorithmsList.getSelectedItem();
			//If Dijkstra or Bellman-Ford is selected enable the weight button
			if(algorithm == ALGORITHMS.DIJKSTRA || algorithm == ALGORITHMS.BELLMANFORD){
				weightButton.setSelected(true);
				weightButton.setEnabled(true);
				repaint();
			}
			//if BFS or DFS is selected disable the weight button
			else if(algorithm == ALGORITHMS.BFS || algorithm == ALGORITHMS.DFS) {
				weightButton.setSelected(false);
				weightButton.setEnabled(false);
				this.clearWeight();
				repaint();
			}
		}
		
		//When the run button is clicked
		if(e.getSource() == runButton) {
			this.reset();
			this.selectedAlgorithm = (ALGORITHMS)algorithmsList.getSelectedItem();
			optionPanel.repaint();
			
			if(this.source == null && this.destination == null) {
				JOptionPane.showMessageDialog(null, "Please select source and destination nodes.");
			}
			else if(this.source == null) {
				JOptionPane.showMessageDialog(null, "Please select a source node.");
			}
			else if(this.destination == null) {
				JOptionPane.showMessageDialog(null, "Please select a destination node.");
			}
			else {
				//disable all the buttons while an algorithm is running
				resetButton.setEnabled(false);
				speedList.setEnabled(false);
				algorithmsList.setEnabled(false);
				wallButton.setEnabled(false);
				sourceButton.setEnabled(false);
				destinationButton.setEnabled(false);
				weightButton.setEnabled(false);
				clearButton.setEnabled(false);
				
				// Without the SwingWorker, The buttons get enabled immediately
				//So we dispatch a new thread to enable them after the algorithm is done running
				SwingWorker sw = new SwingWorker() {
	
					@Override
					protected Object doInBackground() throws Exception {
						//Invoke all the registered listeners
						for(ViewListener listener : listeners) {
							listener.runAlgorithm();
						}
						return null;
					}
					
					@Override
		            protected void done()  
		            {
						//enable all the buttons
						resetButton.setEnabled(true);
						speedList.setEnabled(true);
						algorithmsList.setEnabled(true);
						wallButton.setEnabled(true);
						sourceButton.setEnabled(true);
						destinationButton.setEnabled(true);
						clearButton.setEnabled(true);
						if(selectedAlgorithm.equals("Dijkstra") || selectedAlgorithm.equals("Bellman-Ford")) {
							weightButton.setEnabled(true);
						}
		            }
	        		
	        	};
	        	sw.execute();
			}
		}
		
		//When the reset button is clicked call this.reset()
		if(e.getSource() == resetButton) {
			this.reset();
		}
		
		//When the clear button is clicked reset everything
		if(e.getSource() == clearButton) {
			this.source = null;
			this.destination = null;
			for(Cell cell: cells.values()) {
	    		cell.isPath = false;
	    		cell.previous = null;
	    		cell.isVisited = false;
	    		cell.isWall = false;
	    		cell.isSource = false;
	    		cell.isDestination = false;
	    	}
			//Invoke all the registered listeners
			for(ViewListener listener : listeners) {
				listener.clearModel();
			}
	    	repaint();
		}
		
	}
	
/**
 * A class to create a boxed grid using the paintComponent method.
 */
public class Grid extends JPanel {
		public void paintComponent (Graphics g) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, gridPanel.getWidth(), gridPanel.getHeight());
			g.setColor(Color.WHITE);
			
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < columns; j++) {
					g.setColor(Color.WHITE);
					
					//Set color according to their status
					if(cells != null) {
						Cell cell = getCell(i, j);
						if(cell != null) {
							if(cell == source) {
	                            g.setColor(Color.RED);
		                    }
		                    else if(cell == destination) {
	                            g.setColor(Color.GREEN);
		                    }
		                    else if(cell.isWall) {
	                            g.setColor(Color.BLACK);
    	                    }
		                    else if(cell.isVisited && !cell.isWeightCell) {
	                            g.setColor(Color.CYAN);
		                    }
		                    else if(cell.isPath) {
	                            g.setColor(Color.YELLOW);
		                    }
		                    else if(cell.isWeightCell) {
		                    	ALGORITHMS algorithm = (ALGORITHMS)algorithmsList.getSelectedItem();
		    					if(algorithm == ALGORITHMS.DIJKSTRA || algorithm == ALGORITHMS.BELLMANFORD) {
		                    		g.setColor(Color.MAGENTA);
		                    	}
		                    }
						}
					}
					g.fillRect( j * CELL_SIZE, (i * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				}
			}
		}
		
	}
	
//A class to implement mouse and mouse motion listeners
//handles user actions done on the grid
	public class Mouse implements MouseMotionListener, MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			if( x > 0 && x % 20 != 0 && y > 40 && y % 20 != 0) {
				int row = getRowNum(y);
				int column = getColumnNum(x);
				Cell clickedCell = getCell(row,column);
				
				//set source
				if(sourceButton.isSelected() && sourceButton.isEnabled()) {
			        clickedCell.isSource = !clickedCell.isSource;
			        clickedCell.isDestination = false;
			        clickedCell.isWall = false;
			        clickedCell.isWeightCell = false;
			        source = clickedCell;
			        gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				}
				//set destination node
				else if(destinationButton.isSelected() && destinationButton.isEnabled()) {
			        clickedCell.isDestination = !clickedCell.isDestination;
			        clickedCell.isSource = false;
			        clickedCell.isWall = false;
			        clickedCell.isWeightCell = false;
			        destination = clickedCell;
			        gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				}
				//set wall node
				else if(wallButton.isSelected() && wallButton.isEnabled()) {
					clickedCell.isWall = !clickedCell.isWall;
					clickedCell.isSource = false;
					clickedCell.isDestination = false;
					clickedCell.isWeightCell = false;
					//Invoke all the registered listeners
					for(ViewListener listener : listeners) {
						listener.setWall(row, column);
					}
					gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				}
				//set weight node
				else if(weightButton.isSelected() && weightButton.isEnabled()) {
					
					ALGORITHMS algorithm = (ALGORITHMS)algorithmsList.getSelectedItem();
					if(algorithm == ALGORITHMS.DIJKSTRA || algorithm == ALGORITHMS.BELLMANFORD) {
						clickedCell.isWeightCell = !clickedCell.isWeightCell;
						clickedCell.isSource = false;
						clickedCell.isDestination = false;
						clickedCell.isWall = false;
						//Invoke all the registered listeners
						for(ViewListener listener : listeners) {
							listener.setWeight(row, column);
						}
						gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				
					}
				}
			}
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			if( x > 0 && x % 20 != 0 && y > 40 && y % 20 != 0) {
				int row = getRowNum(y);
				int column = getColumnNum(x);
				Cell clickedCell = getCell(row,column);
				
				//set wall node
				if(wallButton.isSelected() && wallButton.isEnabled()) {
					clickedCell.isWall = !clickedCell.isWall;
					clickedCell.isSource = false;
					clickedCell.isDestination = false;
					//Invoke all the registered listeners
					for(ViewListener listener : listeners) {
						listener.setWall(row, column);
					}
					gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				}
				
				//set weight node
				else if(weightButton.isSelected() && weightButton.isEnabled()) {
					ALGORITHMS algorithm = (ALGORITHMS)algorithmsList.getSelectedItem();
					if(algorithm == ALGORITHMS.DIJKSTRA || algorithm == ALGORITHMS.BELLMANFORD) {
						clickedCell.isWeightCell = !clickedCell.isWeightCell;
						clickedCell.isSource = false;
						clickedCell.isDestination = false;
						clickedCell.isWall = false;
						//Invoke all the registered listeners
						for(ViewListener listener : listeners) {
							listener.setWeight(row, column);
						}
						gridPanel.paintImmediately(column * CELL_SIZE, (row * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
				
					}
				}
				
			}
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
			
		}
		
	}
	
	/**
	 * Computes and returns row number
	 * @param y: y coordinate of the mouse position
	 * @return computed row number
	 */
	public int getRowNum(int y) {
		return y % 20 != 0 && y > 40 ? y / 20 - 2 : -1;
		
	}
	
	/**
	 * Computes and returns row number
	 * @param x: x coordinate of the mouse position
	 * @return computed column number
	 */
	public int getColumnNum(int x) {
		return x % 20 != 0 ? x/20 : -1;
	}
	
	/**
	 * return total number of rows
	 * @return number of rows
	 */
	public int getRows() {
		return this.rows;
	}
	
	/**
	 * returns total number of columns
	 * @return number of columns
	 */
	public int getColumns() {
		return this.columns;
	}
	
	/**
	 * Highlights the cell being visited
	 * @param x: x coordinate of the cell
	 * @param y: y coordinate of the cell
	 */
	public void visitCell(int x, int y) {
		this.delay();
		Cell cell = getCell(x,y);
		cell.isVisited = true;
		this.gridPanel.revalidate();
		gridPanel.paintImmediately(cell.y * CELL_SIZE, (cell.x * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);		
	}
	
	/**
	 * Highlights the path
	 * @param path: an array of cells forming the path
	 */
	public void drawPath(Cell[] path) {
		for(Cell cell : path) {
			this.delay();
			cell.isPath = true;
			gridPanel.paintImmediately(cell.y * CELL_SIZE, (cell.x * CELL_SIZE) + 38, CELL_SIZE - SPACING, CELL_SIZE - SPACING);
		}
	}
	
	/**
	 * Delays code execution based on the speed selected
	 */
	public void delay() {
		SPEED speed = (SPEED)speedList.getSelectedItem();
		if(speed == SPEED.MEDIUM) {
			try {
	            Thread.sleep(100);
	        } catch (InterruptedException ie)
	        {
	            //System.out.println("Scanning...");
	        }
		}
		else if(speed == SPEED.SLOW) {
			try {
	            Thread.sleep(250);
	        } catch (InterruptedException ie)
	        {
	            //System.out.println("Scanning...");
	        }
		}
	}
	
	/**
	 * get the source
	 * @return source
	 */
	public Cell getSource() {
		return this.source;
	}
	
	/**
	 * get the destination
	 * @return destination
	 */
	public Cell getDestination() {
		return this.destination;
	}
	
	/**
	 * returns the cell at x and y
	 * @param x: x coordinate 
	 * @param y: y coordinate
	 * @return the cell at x and y
	 */
	public Cell getCell(int x, int y) {
		String key = this.generateKey(x,y);
		return this.cells.get(key);
		
	}
	
	/**
	 * returns the selected algorithm's name	
	 * @return selectedAlgorithm
	 */
	public ALGORITHMS getSelectedAlgorithm() {
		return this.selectedAlgorithm;
	}
	
	/**
	 * adds node visited listener to the listeners array list
	 * @param listener
	 */
	public void addViewLisnter(ViewListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * resets the path and visited nodes
	 */
	public void reset() {
		for(Cell cell: cells.values()) {
    		cell.isPath = false;
    		cell.previous = null;
    		cell.isVisited = false;
    		//cell.weight = 0;
    	}
		//Invoke all the registered listeners
		for(ViewListener listener : listeners) {
			listener.resetModel();
		}
    	repaint();
	}
	
	/**
	 * clears weights of all cells
	 */
	public void clearWeight() {
		for(Cell cell: cells.values()) {
    		cell.isWeightCell = false;
    	}
		//Invoke all the registered listeners
		for(ViewListener listener : listeners) {
			listener.resetWeight();
		}
    	repaint();
	}
	
	/**
	 * Creates and returns key of format "0x0y" for the coordinates x and y
	 * @param x: x coordinate
	 * @param y: y coordinate
	 * @return key in the format "0x0y"
	 */
	public String generateKey(int x, int y) {
		return String.format("%02d", x) + String.format("%02d", y);
	}
	
	/**
	 * Creates and returns key of format "0x0y" of a given cell
	 * @param cell: cell to return its key
	 * @return key of the cell
	 */
	public String generateKey(Cell cell) {
		return String.format("%02d", cell.x) + String.format("%02d", cell.y);
	}

	
}

	


