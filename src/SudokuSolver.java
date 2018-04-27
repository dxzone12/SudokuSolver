import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class SudokuSolver {

	public static void main(String[] args) {
		//check if first parameter is help and if so print usage
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				System.out.println("This program can be run with 0, 1 or 2 parameters.");
				System.out.println("0 parameters: will read from standard in and output to sol.txt and standard out");
				System.out.println("1 parameter: will read from the given file name and output to sol.txt and standard out");
				System.out.println("2 parameters: will read from the given file name and output to the second given file name and standard out");			
				return;
			}
		}
		
		//create the grid and the reader writer
		SudokuReaderWriter rw = new SudokuReaderWriter();
		SudokuGrid gameGrid = new SudokuGrid();
		
		//create a reader for input
		Reader reader = null;
		//open a reader from the file or std input depending on args
		if (args.length >= 1) {
			try {
				reader = new FileReader(args[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			reader = new InputStreamReader(System.in);
		}
		
		//read from the reader and then close the reader
		gameGrid = rw.readGrid(reader);
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//process the starting values
		initialPass(gameGrid);
		
		//boolean to track if a pas makes progress and int to count number of passes
		boolean progressed = true;
		int passes = 0;
		
		//main loop keep making passes till no new progress is made 
		while (progressed) {
			progressed = false;
			
			//Calls to all of the logic passes
			boolean boxes = boxOnlyPass(gameGrid);
			boolean rows = rowOnlyPass(gameGrid);
			boolean columns = columnOnlyPass(gameGrid);
			boolean forcedRows = forcedRowEliminationPass(gameGrid);
			boolean forcedColumns = forcedColumnEliminationPass(gameGrid);
			boolean forcedBoxRow = forcedBoxRowEliminationPass(gameGrid);
			boolean forcedBoxColumn = forcedBoxColumnEliminationPass(gameGrid);
			// TODO pass for two cells with the same 2 possibilities in the same box, column or row
			
			//detect whether progressed and update number of passes
			progressed = boxes || rows || columns;
			passes++;
		}
		
		//check if output file name was given else use sol.txt
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
		
		//write solution to file and output to terminal
		rw.writeSolution(gameGrid, writer);
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
	
	//pass determines if for any box a number can only go in a single row and eliminates it as a possibility for the rest of the row
	private static boolean forcedRowEliminationPass(SudokuGrid gameGrid) {
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
				
				//create empty hashsets in the hashmap
				HashMap<Integer, HashSet<Integer>> possibleRows = new HashMap<Integer, HashSet<Integer>>();
				for (Integer i : remaining) {
					possibleRows.put(i, new HashSet<Integer>());
				}
				
				//build the hashsets
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						for (Integer k: remaining) {
							if (gameGrid.canBe(k, (rowOffset + i), (columnOffset + j))) {
								possibleRows.get(k).add(i);
							}
						}
					}
				}
				
				//check for sets of size 1
				for (Entry<Integer, HashSet<Integer>> i : possibleRows.entrySet()) {
					if (i.getValue().size() == 1) {
						//for any set of size 1 eliminate that column in all other boxes
						gameGrid.clearRowExceptOffest(i.getKey(), (rowOffset + i.getValue().iterator().next()), columnOffset);
						foundValue = true;
						System.out.println("forcedRow");
					}
				}
			}			
		}
		return foundValue;
	}
	
	//pass determines if for any box a number can only go in a single column and eliminates it as a possiblity in the rest of the column
		private static boolean forcedColumnEliminationPass(SudokuGrid gameGrid) {
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
					
					//create empty hashsets in the hashmap
					HashMap<Integer, HashSet<Integer>> possibleColumns = new HashMap<Integer, HashSet<Integer>>();
					for (Integer i : remaining) {
						possibleColumns.put(i, new HashSet<Integer>());
					}
					
					//build the hashsets
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							for (Integer k: remaining) {
								if (gameGrid.canBe(k, (rowOffset + i), (columnOffset + j))) {
									possibleColumns.get(k).add(j);
								}
							}
						}
					}
					
					//check for sets of size 1
					for (Entry<Integer, HashSet<Integer>> i : possibleColumns.entrySet()) {
						if (i.getValue().size() == 1) {
							//for any set of size 1 eliminate that column in all other boxes
							gameGrid.clearColumnExceptOffest(i.getKey(), (columnOffset + i.getValue().iterator().next()), rowOffset);
							foundValue = true;
							System.out.println("forcedColumn");
						}
					}
				}			
			}
			return foundValue;
		}
		
		//pass to determine which boxes each number must go in for each column and eliminate it as a possibility for all other cells in box
		private static boolean forcedBoxColumnEliminationPass(SudokuGrid gameGrid) {
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
				
				//create a hashset of possible boxes for each remaining value
				HashMap<Integer, HashSet<Integer>> possibleLocations = new HashMap<Integer, HashSet<Integer>>();
				for (Integer i : remaining) {
					possibleLocations.put(i, new HashSet<Integer>());
				}
				
				//populate the possibility lists 
				for (int i = 0; i < 9; i++) {
					for (Integer k : remaining) {
						if (gameGrid.canBe(k, i, column)) {
							possibleLocations.get(k).add(i/3);
						}
					}
				}
				
				//check for possibility lists of length 1 and set the value accordingly
				for (Entry<Integer, HashSet<Integer>> i : possibleLocations.entrySet()) {
					if (i.getValue().size() == 1) {
						gameGrid.clearBoxExceptColumn(i.getKey(), i.getValue().iterator().next(), column);
						foundValue = true;
						System.out.println("forcedColumnElimination");
					}
				}
			}
			
			return foundValue;
		}

		//pass to determine which boxes each number must go in for each row and eliminate it as a possibility for all other cells in box
		private static boolean forcedBoxRowEliminationPass(SudokuGrid gameGrid) {
			//iterate over each column
			boolean foundValue = false;
			for (int row = 0; row < 9; row++) {
				
				//create the set of remaining values for the column
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
				
				//create a hashset of possible boxes for each remaining value
				HashMap<Integer, HashSet<Integer>> possibleLocations = new HashMap<Integer, HashSet<Integer>>();
				for (Integer i : remaining) {
					possibleLocations.put(i, new HashSet<Integer>());
				}
				
				//populate the possibility lists 
				for (int i = 0; i < 9; i++) {
					for (Integer k : remaining) {
						if (gameGrid.canBe(k, row, i)) {
							possibleLocations.get(k).add(i/3);
						}
					}
				}
				
				//check for possibility lists of length 1 and set the value accordingly
				for (Entry<Integer, HashSet<Integer>> i : possibleLocations.entrySet()) {
					if (i.getValue().size() == 1) {
						gameGrid.clearBoxExceptRow(i.getKey(), i.getValue().iterator().next(), row);
						foundValue = true;
						System.out.println("forcedRowElimination");
					}
				}
			}
			
			return foundValue;
		}
	
	//display the solution nicely on the terminal
	private static void outputSolution(SudokuGrid gameGrid, int passes) {
		//Output the result to the terminal
		System.out.println("Completed in " + passes + " passes");
		gameGrid.printGrid();
	}

}
