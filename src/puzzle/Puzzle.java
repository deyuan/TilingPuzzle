package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import util.Iterator;
import util.StateIterator;
import util.Tile;

/**
 * A class to solve the puzzle problem
 *
 * @author Dawei Fan
 *
 * @version 1.0
 * 			11/14/2014 <p>
 * 			1) Add spin
 *
 * @version 0.3
 * 			11/14/2014 <p>
 * 			1) Change the data from int to char; <p>
 *
 * @version 0.2
 * 			11/14/2014 <p>
 * Description: 1) No spin is allowed; <p>
 * 				2) Color is allowed.
 *
 * @see util.Tile
 * @see util.Iterator
 *
 */
public class Puzzle {
	/**
	 *  Board array which the target.
	 */
	private char[][] board;

	/**
	 * An array of record if a position of the board has been occupied by a tile.
	 */
	private char[][] occupied;

	/**
	 * A list of candidate tiles.
	 */
	private List<Tile> tileList;

	/**
	 * Enable spin of tiles or not.
	 */
	private boolean enableSpin;

	public Puzzle(char b[][], List<Tile> t){
		/**
		 *  Warning, this is a shallow copy of the list and the board.
		 */
		tileList = t;
		board = b;
		int w = b.length;
		int l = b[0].length;
		occupied = new char[w][l];
		for(int i = 0; i<occupied.length; i++)
			Arrays.fill(occupied[i], ' ');
		enableSpin = false;
	}

	/**
	 * Kernel backtrack algorithm to solve the puzzle, spin disabled.
	 *
	 * @return  a list of positions
	 */
	private List<List<int[]>> backTracking(){
		int width = board.length;
		int length = board[0].length;
		int number = tileList.size();
		/**
		 *  A list includes all possible solutions.
		 */
		List<List<int[]>> metaList = new ArrayList<List<int[]>>();
		/**
		 * The stack is used for backtracking. The index of tiles are store in this stack, and their positions are
		 *  stored in curPos.
		 */
		Stack<Integer> stack = new Stack<Integer>();

		/**
		 * Available positions of all tiles from (0, 0) to (length, width)
		 * The Iter is in a format of (y, x).
		 */
		Iterator it[] = new Iterator[number];

		/**
		 *  Note that methods of cover, uncover and check are designed for spin, for no spin scenario, the data is
		 *  stored at the end of pattern list. So pattern[] is just for no spin scenario to access data.
		 */
		int pattern[] = new int[number];
		for(int i = 0; i<number; i++){
			it[i] = new Iterator(width-tileList.get(i).w+1, length-tileList.get(i).l+1);
			pattern[i] = tileList.get(i).spin;
		}
		int top = 0;
		stack.push(0);
		/**
		 *  While the first tile is available.
		 */
		while(top >= 0){
			top = stack.peek();

			/* If the tile is satisfied, then store the status; or cover the tile and push it. */
			if(check(top, it[top].getPos(), pattern[top])){
				/* If current is the last tile, restore the position and add it to metaList.  */
				if(top == number-1){
					List<int[]> pos = new ArrayList<int[]>();
					for(int i = 0; i<tileList.size(); i++)
						pos.add(new int[]{it[i].curW, it[i].curL});
					metaList.add(pos);
					/* Backtracking to check next available positions.  */
					/* If backtrack to the first one and there is no next position, then top = -1, end. */
					while(top>=0 && !it[top].hasNext()){
						it[top--].reset();
						stack.pop();
						if(top>=0){
							/* When undo a upper layer, uncover the tile. */
							uncover(top, it[top].getPos(), pattern[top]);
						}
					}
					if(top>=0){
						stack.pop();
						it[top].next();
						stack.push(top);
					}
				}
				else{
					cover(top, it[top].getPos(), pattern[top]);
					stack.push(++top);
				}
			}

			/* If the tile is not satisfied, then try next available position if there is, or backtrack to upper layer. */
			else{
				while(top>=0 && !it[top].hasNext()){
					it[top--].reset();
					stack.pop();
					if(top>=0)
						/* When undo a upper layer, uncover the tile. */
						uncover(top, it[top].getPos(), pattern[top]);
				}
				if(top>=0){
					stack.pop();
					it[top].next();
					stack.push(top);
				}
			}
		}
		if(metaList.size() == 0)
			System.out.println("No solution!");
		return metaList;
	}

	/**
	 * Kernel backtrack algorithm to solve the puzzle, spin enabled.
	 *
	 * @return  a list of positions
	 */
	private List<List<int[]>> backTrackingSpin(){
		int width = board.length;
		int length = board[0].length;
		int number = tileList.size();
		/**
		 *  A list includes all possible solutions.
		 */
		List<List<int[]>> metaList = new ArrayList<List<int[]>>();
		/**
		 * The stack is used for backtracking. The index of tiles are store in this stack, and their positions are
		 *  stored in curPos.
		 */
		Stack<Integer> stack = new Stack<Integer>();
		/**
		 * Available positions of all tiles from (0, 0) to (length, width)
		 * The Iter is in a format of (y, x).
		 */
		StateIterator si[] = new StateIterator[number];
		for(int i = 0; i<number; i++)
			si[i] = new StateIterator(tileList.get(i), width, length);

		int top = 0;
		stack.push(0);
		/**
		 *  While the first tile is available.
		 */
		while(top >= 0){
			top = stack.peek();

			/* If the tile is satisfied, then store the status; or cover the tile and push it. */
			if(check(top, si[top].getPos(), si[top].curP)){
				/* If current is the last tile, restore the position and add it to metaList.  */
				if(top == number-1){
					List<int[]> pos = new ArrayList<int[]>();
					for(int i = 0; i<tileList.size(); i++)
						pos.add(new int[]{si[i].curW, si[i].curL, si[i].curP});
					metaList.add(pos);
					/* Backtracking to check next available positions.  */
					/* If backtrack to the first one and there is no next position, then top = -1, end. */
					while(top>=0 && !si[top].hasNext()){
						si[top--].reset();
						stack.pop();
						if(top>=0){
							/* When undo a upper layer, uncover the tile. */
							uncover(top, si[top].getPos(), si[top].curP);
			//				System.out.println("uncovered: ");
			//				printBoard();
						}
					}
					if(top>=0){
						stack.pop();
						si[top].next();
						stack.push(top);
					}
				}
				else{
					cover(top, si[top].getPos(), si[top].curP);
		//			System.out.println("covered: ");
		//			printBoard();
					stack.push(++top);
				}
			}

			/* If the tile is not satisfied, then try next available position if there is, or backtrack to upper layer. */
			else{
				while(top>=0 && !si[top].hasNext()){
					si[top--].reset();
					stack.pop();
					if(top>=0){
						/* When undo a upper layer, uncover the tile. */
						uncover(top, si[top].getPos(), si[top].curP);
		//				System.out.println("uncovered: ");
		//				printBoard();
					}
				}
				if(top>=0){
					stack.pop();
					si[top].next();
					stack.push(top);
				}
			}
		}
		if(metaList.size() == 0)
			System.out.println("No solution!");
		return metaList;
	}

	public List<List<int[]>> solve(){
		if(enableSpin)
			return backTrackingSpin();
		else
			return backTracking();
	}

	/**
	 * Cover the board with the tile, set occupied data array.
	 *
	 * @param index tile in the tileList
	 * @param pos [y, x]
	 */
	private void cover(int index, int pos[], int p){
		Iterator it = new Iterator(tileList.get(index).pattern.get(p).length, tileList.get(index).pattern.get(p)[0].length);
		while(it.isValid()){
			int ds[] = it.next();
			if(tileList.get(index).pattern.get(p)[ds[0]][ds[1]]!=' ')
				occupied[pos[0]+ds[0]][pos[1]+ds[1]] = tileList.get(index).pattern.get(p)[ds[0]][ds[1]];
		}
	}

	/**
	 * Uncover the board with the tile, reset occupied data array.
	 *
	 * @param index tile in the tileList
	 * @param pos [y, x]
	 */
	private void uncover(int index, int pos[], int p){
		Iterator it = new Iterator(tileList.get(index).pattern.get(p).length, tileList.get(index).pattern.get(p)[0].length);
		while(it.isValid()){
			int ds[] = it.next();
			if(tileList.get(index).pattern.get(p)[ds[0]][ds[1]]!=' ')
				occupied[pos[0]+ds[0]][pos[1]+ds[1]] = ' ';
		}
	}

	/**
	 * Print the status of current board
	 */
	public void printBoard(){
		System.out.println();
		for(int i = 0; i<occupied.length; i++)
			System.out.println(Arrays.toString(occupied[i]));
		System.out.println();
	}

	/**
	 * Check if a tile in the tileList at the position of pos[y, x] overlaps with the board.
	 *
	 * @param index the index in the tileList
	 * @param pos position of the tile
	 * @return true if no overlaps
	 */
	private boolean check(int index, int pos[], int p){
		Iterator it = new Iterator(tileList.get(index).pattern.get(p).length, tileList.get(index).pattern.get(p)[0].length);
		/* Don't use it.hasNext method which will fail  */
		while(it.isValid()){
			int ds[] = it.next();
			/**
			 * If the position has been occupied, or the color is different from board color, return false.
			 */
			if(tileList.get(index).pattern.get(p)[ds[0]][ds[1]]!=' ' ){
				if(occupied[pos[0]+ds[0]][pos[1]+ds[1]]!= ' ' ||board[pos[0]+ds[0]][pos[1]+ds[1]] != tileList.get(index).pattern.get(p)[ds[0]][ds[1]])
					return false;
			}

		}
		return true;
	}

	public void setEnableSpin(boolean s){
		enableSpin = s;
	}

	public boolean getEnableSpin(){
		return enableSpin;
	}

	public char[][] getBoard(){
		return board;
	}

	public List<Tile> getTileList(){
		return tileList;
	}
}
