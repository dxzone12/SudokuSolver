import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class SudokuSolver {

	public static void main(String[] args) {
		//create the grid and the reader writer
		SudokuReaderWriter rw = new SudokuReaderWriter();
		SudokuGrid gameGrid = new SudokuGrid();
		
		//Check if a filename was given as a parameter and read in accordingly 
		if (args.length >= 1) {
			Reader reader = null;
			try {
				reader = new FileReader(args[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			gameGrid = rw.readFromFile(reader);
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			//ask the user to input one
			gameGrid = rw.readFromInput();
		}
		
		//process the starting values
		initialPass(gameGrid);
		
		//keep processing the next steps until one does not make progress
		Boolean progressed = true;
		int passes = 0;
		
		while (progressed) {
			progressed = false;
			
			//Calls to all of the logic passes
			boolean boxes = boxOnlyPass(gameGrid);
			boolean rows = rowOnlyPass(gameGrid);
			boolean columns = columnOnlyPass(gameGrid);
			boolean forcedRows = forcedRowPass(gameGrid);
			
			//detect whether progressed and update number of passes
			progressed = boxes || rows || columns || forcedRows;
			passes++;
		}
		
		//check if output file name was given else use default
		Writer writer = null;
		try {
			if (args.length >= 2) {
				writer = new FileWriter(args[1]);
			} else {
				writer = new FileWriter("sol.txt");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		rw.writeSolution(gameGrid, writer);
		
		//display the solution on terminal
		outputSolution(gameGrid, passes);
	}
	
	//Initial pass to start the process
	//Calls set cell for all the values present initialy 
	private static void initialPass(SudokuGrid grid) {
		//lists to hold all the values and their locations
		ArrayList<Integer> value = new ArrayList<Integer>();
		ArrayList<Integer> row = new ArrayList<Integer>();
		ArrayList<Integer> column = new ArrayList<Integer>();
		
		//loop over every cell and add the ones with values to the lists
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int val = grid.getCellValue(i, j);
				if (val > 0) {
					value.add(grid.getCellValue(i, j));
					row.add(i);
					column.add(j);
				}
			}
		}
		
		//call set cell for all the values in the list
		for (int i = 0; i < value.size(); i++) {
			grid.setCell(value.get(i), row.get(i), column.get(i));
		}
	}
	
	//this pass checks to see if there is only 1 place in any 3x3 box that a number can go
	//returns true if it placed at least 1 number in the grid
	private static boolean boxOnlyPass(SudokuGrid gameGrid) {
		boolean foundValue = false;
		
		//iterate over each box
		for (int rowOffset = 0; rowOffset < 7; rowOffset+=3) {
			for (int columnOffset = 0; columnOffset < 7; columnOffset+=3) {
				//deal with each box in here
				
				//create the list of remaining digits
				HashSet<Integer> remaining = new HashSet<Integer>();
				for (int i = 1; i <= 9; i++) {
					remaining.add(i);
				}
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if (gameGrid.getCellValue((rowOffset + i), (columnOffset + j)) > 0) {
							remaining.remove(gameGrid.getCellValue((rowOffset + i), (columnOffset + j)));
						}
					}
				}
				
				//create empty array lists in the hashmap
				HashMap<Integer, ArrayList<Integer>> possibleLocations = new HashMap<Integer, ArrayList<Integer>>();
				for (Integer i : remaining) {
					possibleLocations.put(i, new ArrayList<Integer>());
				}
				
				//build the array lists
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						for (Integer k: remaining) {
							if (gameGrid.canBe(k, (rowOffset + i), (columnOffset + j))) {
								possibleLocations.get(k).add((i*3) + j);
							}
						}
					}
				}
				
				//check for lists of length 1
				for (Entry<Integer, ArrayList<Integer>> i : possibleLocations.entrySet()) {
					if (i.getValue().size() == 1) {
						gameGrid.setCell(i.getKey(), (i.getValue().get(0)/3) + rowOffset, (i.getValue().get(0)%3) + columnOffset);
						foundValue = true;
						System.out.println("box");
					}
				}
			}			
		}
		return foundValue;
	}
	
	//pass that checks each row to determine if any of the values yet to be placed can only go in one spot
	//returns true if a value was placed
	private static boolean rowOnlyPass(SudokuGrid gameGrid) {
		//iterate over every row
		boolean foundValue = false;
		for (int row = 0; row < 9; row++) {
			
			//create the set of remaining values for the row
			HashSet<Integer> remaining = new HashSet<Integer>();
			for (int i = 1; i < 10; i++) {
				remaining.add(i);
			}
			for (int i = 0; i < 9; i++) {
				int cellValue = gameGrid.getCellValue(row, i);
				if (cellValue > 0) {
					remaining.remove(cellValue);
				}
			}
			
			//create an empty array list for each value yet to be placed
			HashMap<Integer, ArrayList<Integer>> possibleLocations = new HashMap<Integer, ArrayList<Integer>>();
			for (Integer i : remaining) {
				possibleLocations.put(i, new ArrayList<Integer>());
			}
			
			//populate the above lists with their possible locations
			for (int i = 0; i < 9; i++) {
				for (Integer k : remaining) {
					if (gameGrid.canBe(k, row, i)) {
						possibleLocations.get(k).add(i);
					}
				}
			}
			
			//look for possible locations lists with length 1 and assign their value
			for (Entry<Integer, ArrayList<Integer>> i : possibleLocations.entrySet()) {
				if (i.getValue().size() == 1) {
					gameGrid.setCell(i.getKey(), row, i.getValue().get(0));
					foundValue = true;
					System.out.println("row");
				}
			}
		}
		
		return foundValue;
	}
	
	//Pass that checks each column and for all remaining values determines if there is only one place it can go
	//Returns true if at least 1 value was set
	private static boolean columnOnlyPass(SudokuGrid gameGrid) {
		//iterate over each column
		boolean foundValue = false;
		for (int column = 0; column < 9; column++) {
			
			//create the set of remaining values for the column
			HashSet<Integer> remaining = new HashSet<Integer>();
			for (int i = 1; i < 10; i++) {
				remaining.add(i);
			}
			for (int i = 0; i < 9; i++) {
				int cellValue = gameGrid.getCellValue(i, column);
				if (cellValue > 0) {
					remaining.remove(cellValue);
				}
			}
			
			//create an array list of possible locations for each remaining value
			HashMap<Integer, ArrayList<Integer>> possibleLocations = new HashMap<Integer, ArrayList<Integer>>();
			for (Integer i : remaining) {
				possibleLocations.put(i, new ArrayList<Integer>());
			}
			
			//populate the possibility lists 
			for (int i = 0; i < 9; i++) {
				for (Integer k : remaining) {
					if (gameGrid.canBe(k, i, column)) {
						possibleLocations.get(k).add(i);
					}
				}
			}
			
			//check for possibility lists of length 1 and set the value accordingly
			for (Entry<Integer, ArrayList<Integer>> i : possibleLocations.entrySet()) {
				if (i.getValue().size() == 1) {
					gameGrid.setCell(i.getKey(), i.getValue().get(0), column);
					foundValue = true;
					System.out.println("column");
				}
			}
		}
		
		return foundValue;
	}
	
	//pass that determines if remaining values can only go in 1 row and removes the possibilities from the rest of the row
	private static boolean forcedRowPass(SudokuGrid gameGrid) {
		
		//iterate over every box
		boolean toReturn = false;
		
		
		return toReturn;
	}
	
	//display the solution nicely on the terminal
	private static void outputSolution(SudokuGrid gameGrid, int passes) {
		//Output the result to the terminal
		System.out.println("Completed in " + passes + " passes");
		gameGrid.testOutput();
	}

}
