
public class SudokuGrid {
	//underlying array of grid cells
	private SudokuCell[][] grid = new SudokuCell[9][9];

	//add a cell to the grid used for constructing the grid
	public void addCell(char nextChar, int row, int col) {
		grid[row][col] = new SudokuCell(nextChar, this);
	}
	
	//set the value of a cell
	//clearing it as a possiblity in its column, row and box
	public void setCell(int val, int row, int col) {
		grid[row][col].setValue(val);
		clearBox(val, row, col);
		clearRow(val, row);
		clearColumn(val, col);
	}
	
	//private functions to remove possibilities from a column, row and box
	private boolean clearRow(int val, int row) {
		//iterate over the row removing the value as a possibility
		for (int i = 0; i < 9; i++) {
			int autoVal = grid[row][i].removePossibility(val);
			if (autoVal > 0) {
				System.out.println("Possibilities " + val + " row: " + row + " column: " + i);
				
				setCell(autoVal, row, i);
			}
		}
		return false;
	}
	private boolean clearColumn(int val, int column) {
		//iterate over the column removing the value as a possibility
		for (int i = 0; i < 9; i++) {
			int autoVal = grid[i][column].removePossibility(val);
			if ( autoVal > 0) {
				System.out.println("Possibilities " + val + " row: " + i + " column: " + column);
				setCell(autoVal, i, column);
			}
		}
		return false;
	}
	private boolean clearBox(int val, int row, int col) {
		int rowOffset = (row/3) * 3;
		int columnOffset = (col/3) * 3;
		
		//iterate over the box and remove the value as a posibility for each cell
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int autoVal = grid[(rowOffset + i)][(columnOffset + j)].removePossibility(val);
				if (autoVal > 0) {
					System.out.println("Possibilities " + val + " row: " + (rowOffset + i) + " column: " + (columnOffset + j));
					setCell(autoVal, (rowOffset + i), (columnOffset + j));
				}
			}
		}
		return false;
	}
	
	//return the value of cell at row and col
	public int getCellValue(int row, int col) {
		return grid[row][col].getValue();
	}
	
	//test if given grid position can be val
	public boolean canBe(int val, int row, int col) {
		return grid[row][col].canBe(val);
	}
	
	//test function to be removed later just displays the grid to terminal
	public void testOutput() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(grid[i][j].getValue());
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	public void clearColumnExceptOffest(int val, int column, int rowOffset) {
		for (int i = 0; i < 9; i++) {
			if (i < rowOffset || i >= (rowOffset+3)) {
				int autoVal = grid[i][column].removePossibility(val);
				if (autoVal > 0) {
					System.out.println("Possibilities");
					setCell(val, i, column);
				}
			}
		}
	}
	
	public void clearRowExceptOffest(int val, int row, int columnOffset) {
		for (int i = 0; i < 9; i++) {
			if (i < columnOffset || i >= (columnOffset+3)) {
				int autoVal = grid[row][i].removePossibility(val);
				if (autoVal > 0) {
					System.out.println("Possibilities");
					setCell(autoVal, row, i);
				}
			}
		}
	}

	public void printCellPoss(int i, int j) {
		System.out.print("Possible values of " + i + " " + j + " are:");
		for (int k = 1; k <= 9; k++) {
			if (grid[i][j].canBe(k)) {
				System.out.print(" " + k);
			}
		}
		System.out.println(".");
	}

}
