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
	private boolean verb = true;	//debug information
	private int numTiles = 0;
	private int numCells = 0;
	private int numColumns = 0;
	private int numRows = 0;

	private int[][] boardIdx = null;	//the index of each board cells
	private int[][] ECA = null;			//the exact cover array
	private String[] rowName = null;	//for outputting solution
	private Y[] CHA = null;				//the array of column head
	private X[][] DLA = null;			//the exact cover cell objects
	private Y H = null;					//the head of dancing links
	private List<X> O = null;			//the searching trail
	private int cntSolution = 0;		//count the number of solutions

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
		int row;
		int col;
	}

	/**
	 * Column object Y.
	 *
	 */
	private class Y extends X {
		Y L;
		Y R;
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
		O = new ArrayList<X>();

		verifyDancingLinks();
	}

	/**
	 * Verify Dancing Links.
	 */
	private void verifyDancingLinks() {
		if (verb) {
			int cnt1, cnt2, total = 0;

			System.out.println();
			System.out.println("Verifying Dancing Links:");

			System.out.print("Column Size: ");
			for (Y i = H.R; i != H; i = i.R) {
				System.out.print(i.S + " ");
				total += i.S;
			}
			System.out.println("Total: " + total);

			/* Check links between column head objects */
			cnt1 = cnt2 = 0;
			for (Y i = H.R; i != H; i = i.R) cnt1++;
			for (Y i = H.L; i != H; i = i.L) cnt2++;
			if (cnt1 != numColumns || cnt2 != numColumns)
				System.out.println("Column Head Links Error.");

			/* Check links in column direction */
			for (Y i = H.R; i != H; i = i.R) {
				cnt1 = cnt2 = 0;
				for (X j = i.D; j != i; j = j.D) cnt1++;
				for (X j = i.U; j != i; j = j.U) cnt2++;
				if (cnt1 != i.S || cnt2 != i.S)
					System.out.println("Column Links Error at " + i.N + " " + cnt1 + " " + cnt2 + " " + i.S);
			}

			/* Check links in row direction */
			int[] rowSize = new int[numRows];
			for (Y i = H.R; i != H; i = i.R) {
				for (X j = i.D; j != i; j = j.D) {
					cnt1 = cnt2 = 0;
					for (X k = j.R; k != j; k = k.R) cnt1++;
					for (X k = j.L; k != j; k = k.L) cnt2++;
					if (cnt1 != cnt2)
						System.out.println("Row Links Error at " + j.row);
					if (rowSize[j.row] == 0) {
						rowSize[j.row] = cnt1 + 1;
					}
				}
			}
			int total_cnt = 0;
			for (int i = 0; i < numRows; i++) total_cnt += rowSize[i];
			if (total != total_cnt)
				System.out.println("Number of Row Links Error");

			System.out.println("Verifying Finished.");
			System.out.println();
		}
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
		/* Use a list to store all the rows */
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
		h.row = -1;
		h.col = -1;

		/* Allocate column head objects CHA and build the links */
		for (int i = 0; i < numColumns; i++) {
			Y y = new Y();
			y.U = y;
			y.D = y;
			y.C = y;
			y.row = -1;
			y.col = i;
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
			if (i < numTiles) {
				CHA[i].N = "T" + Integer.toString(i);
			} else {
				CHA[i].N = "P" + Integer.toString(i - numTiles);
			}
		}

		/* Allocate cell objects DLA */
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				if (ECA[i][j] != 0) {
					X x = new X();
					x.row = i;
					x.col = j;
					DLA[i][j] = x;
				} else {
					DLA[i][j] = null;
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
						CHA[i].D = curr;
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

		return h;
	}

	/**
	 * Choose Column Object - Part of the Dancing Link Algorithm
	 * @return reference to a column object
	 */
	private Y chooseColumnObject() {
		boolean minimizeBranchingFactor = true;

		Y c = H.R;
		if (minimizeBranchingFactor) {
			int s = Integer.MAX_VALUE;
			for (Y j = H.R; j != H; j = j.R) {
				if (j.S < s) {
					c = j;
					s = j.S;
				}
			}
		}
		if (verb) {
			//System.out.println("Choose column " + c.N);
		}
		return c;
	}

	/**
	 * Cover Column - Part of the Dancing Link Algorithm
	 * @param c
	 */
	private void coverColumn(Y c) {
		//if (verb) System.out.println("Cover " + c.N);
		c.R.L = c.L;
		c.L.R = c.R;
		for (X i = c.D; i != c; i = i.D) {
			for (X j = i.R; j != i; j = j.R) {
				j.D.U = j.U;
				j.U.D = j.D;
				j.C.S -= 1;
			}
		}
		//if (verb) {
		//	System.out.print("Column Size: ");
		//	for (Y i = H.R; i != H; i = i.R) {
		//		System.out.print("(" + i.N + ")" + i.S + " ");
		//	}
		//	System.out.println();
		//}
	}

	/**
	 * Uncover Column - Part of the Dancing Link Algorithm
	 * @param c
	 */
	private void uncoverColumn(Y c) {
		//if (verb) System.out.println("Uncover " + c.N);
		for (X i = c.U; i != c; i = i.U) {
			for (X j = i.L; j != i; j = j.L) {
				j.C.S += 1;
				j.D.U = j;
				j.U.D = j;
			}
		}
		c.R.L = c;
		c.L.R = c;
	}

	private void printSolution(int k) {
		System.out.print("Solution" + cntSolution + " (" + k + "): ");
		for (int i = 0; i < k; i++) {
			X x = O.get(i);
			/* Find the leftmost cell */
			X tile = null;
			for (int j = 0; j < numTiles; j++) {
				if (DLA[x.row][j] != null) {
					tile = DLA[x.row][j];
					break;
				}
			}
			/* Print out the row */
			System.out.print(tile.C.N + "(");
			for (X j = tile.R; j != tile; j = j.R) {
				System.out.print(j.C.N);
			}
			System.out.print(") ");
		}
		System.out.println();
	}

	/**
	 * Searching with Dancing Links.
	 * @param k
	 */
	public void search(int k) {
		if (verb) {
			//System.out.println("Enter Dancing Links Search. k = " + k);
		}
		//if (k > 100) verb = false;
		if (H.R == H) {
			cntSolution++;
			printSolution(k);
			return;
		}
		Y c = chooseColumnObject();
		coverColumn(c);
		for (X r = c.D; r != c; r = r.D) {
			if (k >= O.size()) O.add(r);
			else O.set(k, r);
			for (X j = r.R; j != r; j = j.R) {
				coverColumn(j.C);
			}
			search(k + 1);
			r = O.get(k);
			c = r.C;
			for (X j = r.L; j != r; j = j.L) {
				uncoverColumn(j.C);
			}
		}
		uncoverColumn(c);
		if (verb) {
			//System.out.println("Exit Dancing Links Search. k = " + k);
		}
		return;
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
