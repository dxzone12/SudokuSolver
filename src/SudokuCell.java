import java.util.HashSet;
import java.util.Set;

public class SudokuCell {
	
	//callback to the grid for updating
	private SudokuGrid grid;
	//the current value of this cell, -1 for not set
	private int value;
	//set of possible values for this cell
	private Set<Integer> possibilities;
	
	public SudokuCell(char nextChar, SudokuGrid sudokuGrid) {
		grid = sudokuGrid;
		
		//create the set of possibilities
		possibilities = new HashSet<Integer>();
		for (int i = 0; i < 10; i++) {
			possibilities.add(i);
		}
		
		//set the character that is passed
		if (nextChar == '-') {
			value = -1;
		} else if (Character.isDigit(nextChar)) {
			value = Character.getNumericValue(nextChar);
		} else {
			System.out.println("incorrect input for a cell");
		}
		
	}
	
	//remove passed val from list of possibilities
	//set this cells value if only 1 possibility is left
	public boolean removePossibility(int val) {
		possibilities.remove(val);
		
		if (possibilities.size() == 1 && value == -1) {
			for (Integer i : possibilities) {
				value = i;
				return true;
			}
		}
		
		return false;
	}
	
	//setter and getter for cell value
	public void setValue(int val) {
		value = val;
	}
	public int getValue() {
		return value;
	}
	
	//returns true if val is in the posiblity list and this cells value is not set
	public boolean canBe(int val) {
		if (value == -1) {
			return possibilities.contains(val);
		} else {
			return false;
		}
	}

}
