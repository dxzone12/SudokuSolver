import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class SudokuReaderWriter {

	//function to read a sudoku problem from a file
	//blank grid cells should be denoted with a -
	public SudokuGrid readFromFile(Reader reader) {
		SudokuGrid toReturn = new SudokuGrid();
		
		int count = 0;
		while (true) {
			int nextInt = -2;
			try {
				nextInt = reader.read();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			if (nextInt == -1) {
				System.out.println("not enough characters in input file");
				System.exit(1);
			}
			
			char nextChar = (char) nextInt;
			
			if (Character.isDigit(nextChar) || nextChar == '-') {
				toReturn.addCell(nextChar, count/9, count%9);
				count++;
			}
			
			if (count == 81) {
				break;
			}
		}
		
		return toReturn;
	}

	//function to read a sudoku problem from stdin
	public SudokuGrid readFromInput() {
		// TODO Auto-generated method stub
		return null;
	}

	//write the solution in csv form to a file
	public void writeSolution(SudokuGrid gameGrid, Writer writer) {
		// TODO Auto-generated method stub
		
	}
	
}
