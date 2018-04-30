# Smart Sudoku Solver

This project provides a solver for the popular logic puzzle game sudoku. The solver provided here users repeated passes of logical rules in order to find a solution which is more like what a human would do than the standard recursive brute force approach. A summary of each of the logical passes and their current stage of implementation can be found below.

## Logical Passes

List of passes will go here when I get time to write it out.

## Getting Started

These provided instructions apply to linux but should be easily adaptable to a JDK install on any platform.

Make a directory to house the compiled files

```
mkdir classes
```

Compile all the files in src placing the compiled files in classes.

```
javac src/*java -d classes
```

To run the program change directory into classes.

```
cd classes
```

Then run the program and give it a file to run on. Example below on one of the files in the problems directory

```
java SudokuSolver ../Problems/hard.txt
```

### Prerequisites

Java JDK - Developed and tested using JDK 8.

## Authors

* **Benjamin Morris**

## Acknowledgments

* Sample problems generated from [Web Sudoku](https://www.websudoku.com/)
