import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class SudokuReaderWriter {

	//function to read in a sodoku problem
	//blank grid cells should be denoted with a -
	//the layout of the input file doesn't matter it looks for -'s and digits
	public SudokuGrid readGrid(Reader reader) {
		//Initialise a blank grid
		SudokuGrid toReturn = new SudokuGrid();
		
		//counter for the number of characters detected
		int count = 0;
		while (true) {
			
			//initialise to negative 2 in case of a failure
			int nextInt = -2;
			
			//attempt to read the next characcter and quit program if read fails
			try {
				nextInt = reader.read();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			//if eof then report not enough characters and quit
			if (nextInt == -1) {
				System.out.println("not enough characters in input file");
				System.exit(1);
			}
			
			//convert to char and check if its a valid character then insert it into the grid
			char nextChar = (char) nextInt;
			if (Character.isDigit(nextChar) || nextChar == '-') {
				toReturn.addCell(nextChar, count/9, count%9);
				count++;
			}
			
			//break the loop when all characters have been read
			if (count == 81) {
				break;
			}
		}
		
		return toReturn;
	}

	//write the solution in csv form to a file
	public void writeSolution(SudokuGrid gameGrid, Writer writer) {
		// TODO implement this function
		
	}
	
}
