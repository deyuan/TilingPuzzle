package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Tile;

/**
 * Tiling Puzzle Solver with Dancing Links.
 * @author Deyuan Guo
 * @version 0.1
 * 			Created on 11/17/2014
 */

public class DancingLinks {

	private static char S = ' '; 		//tile splitter
	private static boolean verb = true;	//debug information
	private static int numTiles = 0;
	private static int numCells = 0;
	private static int numColumns = 0;
	private static int numRows = 0;

	private int[][] boardIdx = null;	//the index of each board cells
	private int[][] ECA = null;			//the exact cover array
	private Y[] CHA = null;				//the array of column head
	private X[][] DLA = null;			//the exact cover cell objects
	private static Y H = null;			//the head of dancing links

	/**
	 * Data object X.
	 *
	 */
	private class X {
		X L;
		X R;
		X U;
		X D;
		Y C;
	}

	/**
	 * Column object Y.
	 *
	 */
	private class Y extends X {
		Y L;
		Y R;
		X U;
		X D;
		Y C;
		int S;
		String N;
	}

	/**
	 * Constructor of Dancing Links Solver.
	 * @param board
	 * @param tiles
	 */
	public DancingLinks(Tile board, List<Tile> tiles) {
		if (verb) {
			System.out.println();
			System.out.println("Constructor of Dancing Links.");
		}

		numTiles = tiles.size();
		numCells = board.area;
		numColumns = numTiles + numCells;

		boardIdx = buildBoardIdxArray(board);
		ECA = buildExactCoverArray(board, tiles);

		numRows = ECA.length;

		DLA = new X[numRows][numColumns];
		CHA = new Y[numColumns];
		H = buildDancingLinks();
	}

	/**
	 * Build the Auxiliary board for getting indices of cells.
	 * @param board
	 */
	private int[][] buildBoardIdxArray(Tile board) {
		int[][] boardidx = new int[board.data.length][board.data[0].length];
		int cnt = 0;
		for (int r = 0; r < board.data.length; r++) {
			for (int c = 0; c < board.data[0].length; c++) {
				if (board.data[r][c] != S) {
					boardidx[r][c] = cnt;
					cnt++;
				} else {
					boardidx[r][c] = -1;
				}
			}
		}
		if (verb) {
			System.out.println("Indices of board cells:");
			printArray(boardidx);
		}
		return boardidx;
	}

	/**
	 * Determine if a tile can be placed on board at position r,c.
	 * @param board
	 * @param tile
	 * @param r
	 * @param c
	 * @return
	 */
	private boolean isValidPosition(char[][] board, char[][] tile, int r, int c) {
		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[0].length; j++) {
				if (tile[i][j] != S && tile[i][j] != board[r+i][c+j]) return false;
			}
		}
		return true;
	}

	/**
	 * Build a Exact Cover row.
	 * @param board
	 * @param tile
	 * @param r
	 * @param c
	 * @param tileid
	 * @return
	 */
	private int[] buildExactCoverRow(int[][] board, char[][] tile, int r, int c,
			int tileid)
	{
		int[] row = new int[numColumns];
		row[tileid] = 1;
		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[0].length; j++) {
				if (tile[i][j] != S) {
					row[numTiles + board[r+i][c+j]] = 1;
				}
			}
		}
		return row;
	}

	/**
	 * Build the Exact Cover Array.
	 * @param board
	 * @param tiles
	 * @return
	 */
	private int[][] buildExactCoverArray(Tile board, List<Tile> tiles) {
		List<int[]> ECL = new ArrayList<int[]>();
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			int spin = tile.pattern.size();
			for (int j = 0; j < spin; j++) {
				char[][] t = tile.pattern.get(j);
				for (int r = 0; r < board.data.length - t.length + 1; r++) {
					for (int c = 0; c < board.data[0].length - t[0].length + 1; c++) {
						if (isValidPosition(board.data, t, r, c)) {
							int[] row = buildExactCoverRow(boardIdx, t, r, c, i);
							ECL.add(row);
						}
					}
				}
			}

		}
		if (verb) {
			//for (int[] t: ECL) System.out.println(Arrays.toString(t));
		}

		/* Convert list of int[] to int[][] */
		int[][] ECA = new int[ECL.size()][numColumns];
		for (int i = 0; i < ECL.size(); i++) {
			for (int j = 0; j < numColumns; j++) {
				if (j < ECL.get(i).length) {
					ECA[i][j] = ECL.get(i)[j];
				}
			}
		}

		if (verb) {
			System.out.println("Exact Cover Array:");
			printArray(ECA);
		}
		return ECA;
	}

	/**
	 * Build the Dancing Links.
	 * @param a
	 * @return
	 */
	private Y buildDancingLinks () {
		Y h = new Y(); // head

		/* Allocate column head objects CHA and build the links */
		for (int i = 0; i < numColumns; i++) {
			Y y = new Y();
			y.U = y;
			y.D = y;
			y.C = y;
			if (i == 0) {
				y.L = h;
				h.R = y;
			} else {
				y.L = CHA[i-1];
				CHA[i-1].R = y;
				if (i == numColumns - 1) {
					y.R = h;
					h.L = y;
				}
			}
			CHA[i] = y;
		}

		/* Allocate cell objects DLA */
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				if (ECA[i][j] != 0) {
					DLA[i][j] = new X();
				}
			}
		}

		/* Build the dancing links (row direction) */
		for (int i = 0; i < numRows; i++) {
			X leftmost = null;
			X prev = null;
			X curr = null;
			for (int j = 0; j < numColumns; j++) {
				if (DLA[i][j] != null) {
					curr = DLA[i][j];
					if (leftmost == null) {
						leftmost = curr;
						prev = curr;
					} else {
						curr.L = prev;
						prev.R = curr;
						prev = curr;
					}
				}
			}
			curr.R = leftmost;
			leftmost.L = curr;
		}
		/* Build the dancing links (column direction) */
		for (int i = 0; i < numColumns; i++) {
			X prev = null;
			X curr = null;
			int size = 0;
			for (int j = 0; j < numRows; j++) {
				if (DLA[j][i] != null) {
					curr = DLA[j][i];
					curr.C = CHA[i];
					size++;
					if (prev == null) {
						curr.U = CHA[i];
						prev = curr;
					} else {
						curr.U = prev;
						prev.D = curr;
						prev = curr;
					}
				}
			}
			curr.D = CHA[i];
			CHA[i].U = curr;
			CHA[i].S = size;
		}

		if (verb) {
			System.out.println("Information of Dancing Links:");
			System.out.print("Column Size: ");
			for (Y hi = h.R; hi != h; hi = hi.R) {
				System.out.print(hi.S + " ");
			}
			System.out.println();
		}

		return h;
	}

	/**
	 * Searching with Dancing Links.
	 * @param k
	 */
	public void search(int k) {
		if (verb) {
			System.out.println();
			System.out.println("Dancing Links Search. Not Yes Implemented.");
		}
		// TODO: Implement Search Algorithm.
	}

	/**
	 * Print out a 2D array.
	 * @param a
	 */
	public void printArray(int[][] a) {
		System.out.println("2D array ("+ a.length + "x" + a[0].length + ")");
		for(int i = 0; i < a.length; i++)
			System.out.println(Arrays.toString(a[i]));
			System.out.println();
	}

}
