
public class SudokuGrid {
	
	private SudokuCell[][] grid = new SudokuCell[9][9];

	//add a cell to the grid used for constructing the grid
	public void addCell(char nextChar, int row, int col) {
		grid[row][col] = new SudokuCell(nextChar, this);
	}
	
	//set the value of a cell
	public void setCell(int val, int row, int col) {
		grid[row][col].setValue(val);
		clearBox(val, row, col);
		clearRow(val, row);
		clearColumn(val, col);
	}
	
	//private functions to remove possibilities from columns and rows 
	private boolean clearRow(int val, int row) {
		for (int i = 0; i < 9; i++) {
			if (grid[row][i].removePossibility(val)) {
				System.out.println("Possibilities");
				setCell(val, row, i);
			}
		}
		return false;
	}
	private boolean clearColumn(int val, int column) {
		for (int i = 0; i < 9; i++) {
			if (grid[i][column].removePossibility(val)) {
				System.out.println("Possibilities");
				setCell(val, i, column);
			}
		}
		return false;
	}
	private boolean clearBox(int val, int row, int col) {
		int rowOffset = (row/3) * 3;
		int columnOffset = (col/3) * 3;
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[(rowOffset + i)][(columnOffset + j)].removePossibility(val)) {
					System.out.println("Possibilities");
					setCell(val, (rowOffset + i), (columnOffset + j));
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

}
