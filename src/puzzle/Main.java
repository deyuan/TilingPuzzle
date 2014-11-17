package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gui.DisplayResults;
import util.DataFileParser;
import util.RotateMatrix;
import util.StateIterator;
import util.Tile;

/**
 * A main class to test code.
 * 
 * @author Dawei Fan
 * @version 0.1
 * 			11/14/2014
 *
 */
public class Main {
	

	public static void main(String[] args) {
	
		DataFileParser dfp = new DataFileParser("./tests/simple1.txt");
	    /* Extract puzzle pieces, board are included in this list. */
	    List<Tile> tileList = dfp.ExtractTiles();
	    
	    /* Enable or disable spin. */
	    boolean spin = true;
	    /* Get the board and the remained is tileList. */
		char board[][] = tileList.get(0).data;		
		tileList.remove(0);
		/* Create a puzzle instance with a board and a tile list. */
		Puzzle puzzle = new Puzzle(board, tileList);
					
		/* Test the backtrack function. */
		List<List<int[]>> result = new ArrayList<List<int[]>>();
//		result = puzzle.solve();
		
		for(int i = 0; i<result.size(); i++){
			for(int j = 0; j<result.get(0).size(); j++)
				System.out.print(Arrays.toString(result.get(i).get(j)));
			System.out.println();
		}		

		
//		DisplayResults dr = new DisplayResults(board, tileList, result, spin);
		DisplayResults dr = new DisplayResults(puzzle);
		dr.createAndShowGUI();
//		dr.displayResults(7);
//		testRotateMatrix();
//		testTile();
//		testStateIterator();
		
		
		
		
	}
	
	
	public static void testStateIterator(){
		char l[][] = {{' ', 'X'},
	      	          {' ', 'X'},
                      {'X', 'X'}};

		char c[][] = {{'X', 'X'},
	                  {'X', 'X'},
	                  {'X', 'X'}};
	
		Tile t1 = new Tile(l);
		Tile t2 = new Tile(c);
		StateIterator si = new StateIterator(t2, 3, 4);
		while(si.isValid()){
			System.out.println(Arrays.toString(si.getPos()));
			si.next();			
		}
	}
	
	public static void testRotateMatrix(){
		char l[][] = {{' ', 'X'},
		 	      	  {' ', 'X'},
		              {'X', 'X'}};
	
		char c[][] = {{'X', ' '},
			          {'X', 'X'},
			          {'X', ' '}};
		char result[][];
		RotateMatrix rm = new RotateMatrix(c);
		System.out.println(rm.spin());
	
		result = rm.rotateC3();
		rm.printMatrix(result);
	}
	
	public static void testTile(){
		char l[][] = {{' ', 'X'},
	 	      	      {' ', 'X'},
	                  {'X', 'X'}};

		char c[][] = {{'X', 'X'},
		              {'X', 'X'},
		              {'X', 'X'}};
		
		char p[][] = {{'X'}};
		
		Tile t1 = new Tile(l);
		Tile t2 = new Tile(c);
		Tile t3 = new Tile(p);
		System.out.println(t1.spin);
		t1.printPattern();
		System.out.println(t2.spin);
		t2.printPattern();
		System.out.println(t3.spin);
	}
	
}





/*
	public void createAndShowGUI() {
	
		JFrame frame = new JFrame("Puzzle solver"); 
		Container contentPane = frame.getContentPane(); 
		
		frame.setLayout(new FlowLayout());

//		f.setLayout(new BorderLayout());	

		frame.setSize(frameSize[0], frameSize[1]);
		frame.setLocation(framePos[0], framePos[1]);
		frame.setResizable(true); 
		frame.setVisible(true); 
		contentPane.add(this);
//		setupBoard();

//		return frame;
	} 
 
 */


//List<Tile> tileList = new ArrayList<Tile>();
/*
char board[][] = {{'X', 'X', 'X', 'X'}, 
				 {'X', 'X', 'X', 'X'},
				 {'X', 'X', 'X', 'X'}};

char c[][] = {{'X', ' '},
			 {'X', 'X'},
			 {'X', ' '}};

char t[][] = {{'X', 'X'},
		 	 {' ', 'X'}};

char p[][] = {{'X'}};

char l[][] = {{' ', 'X'},
		 	 {' ', 'X'},
		     {'X', 'X'}};
*/
/*
char board[][] = {{'X', 'X', 'X', 'X', 'X', 'X', 'X'}, 
				 {'X', 'X', 'X', 'X', 'X', 'X', 'X'}};

char c[][] = {{'X', 'X'},
	         {'X', 'X'}};

char t[][] = {{' ', 'X', 'X'},
 	         {'X', 'X', ' '}};

char l[][] = {{' ', 'X'},
	             {'X', 'X'}};

char p[][] = {{'X', 'X'},
 	         {'X', ' '}};

tileList.add(new Tile(c));
tileList.add(new Tile(t));
tileList.add(new Tile(l));
tileList.add(new Tile(p));
*/
