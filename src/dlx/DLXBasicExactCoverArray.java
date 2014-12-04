package dlx;

import java.util.ArrayList;
import java.util.List;

import util.Debug;
import util.Tile;

/**
 * DLX Exact Cover Array Generator (Basic Version)
 * @author Deyuan Guo, Dawei Fan
 */
public class DLXBasicExactCoverArray {

	/******************** Public Member Variables ********************/

	/** The number of Tiles */
	public int numTiles = 0;

	/** The number of DLXCells */
	public int numCells = 0;

	/** The number of Columns */
	public int numColumns = 0;

	/** The number of Rows */
	public int numRows = 0;

	/** The exact cover array */
	public int[][] ECA = null;

	/******************** Private Member Variables ********************/

	/** Reference of class DLXConfig */
	private DLXConfig Config = null;

	/** The puzzle board */
	private Tile board;

	/** The puzzle tile list */
	private List<Tile> tiles;

	/** The index of each board cells */
	private int[][] boardIdx = null;

	/** Debug class for printing */
	private Debug dbg = new Debug();

	/******************** Public Member Functions ********************/

	public DLXBasicExactCoverArray(Tile b, List<Tile> t, DLXConfig config) {
		board = b;
		tiles = t;
		Config = config;

		numTiles = tiles.size();
		numCells = board.area;
		numColumns = numTiles + numCells;

		boardIdx = buildBoardIdxArray(board);

		ECA = buildExactCoverArray(board, tiles);

		numRows = ECA.length;

		/* check tile area */
		int total_area = 0;
		for (Tile i: tiles) {
			total_area += i.area;
		}
		if (total_area < board.area) {
			Config.setTileAreaNotEnough(true);
			if (Config.verb)
				System.out.println("Directly fail (no enough tiles).");
		} else {
			Config.setTileAreaNotEnough(false);
		}
	}

	/******************** Private Member Functions ********************/

	/**
	 * Build the Auxiliary board for getting indices of cells. Set extra!
	 *
	 * @param board
	 */
	private int[][] buildBoardIdxArray(Tile board) {
		int[][] boardidx = new int[board.data.length][board.data[0].length];
		int cnt = 0;
		for (int r = 0; r < board.data.length; r++) {
			for (int c = 0; c < board.data[0].length; c++) {
				if (board.data[r][c] != Config.S) {
					boardidx[r][c] = cnt;
					cnt++;
				} else {
					boardidx[r][c] = -1;
				}
			}
		}
		if (Config.verb) {
			System.out.println("Indices of board cells:");
			dbg.print2DArray(boardidx);
		}


		/* Calculate the area of the all tiles. */
		int area = 0;
		for (int i = 0; i < tiles.size(); i++)
			area += tiles.get(i).area;

		/* The area of the board is cnt. */
		// System.out.println("Tiles area: "+area+" board area: "+cnt);
		if (area > cnt) {
			if (Config.verb) System.out.println("Extra is set to be true!");
			Config.setEnableExtra(true);
		}

		return boardidx;
	}

	/**
	 * Determine if a tile can be placed on board at position r,c.
	 *
	 * @param board
	 * @param tile
	 * @param r
	 * @param c
	 * @return
	 */
	private boolean isValidPosition(char[][] board, char[][] tile, int r, int c) {
		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[0].length; j++) {
				if (tile[i][j] != Config.S && tile[i][j] != board[r + i][c + j])
					return false;
			}
		}
		return true;
	}

	/**
	 * Build a Exact Cover row.
	 *
	 * @param board
	 * @param tile
	 * @param r
	 * @param c
	 * @param tileid
	 * @return
	 */
	private int[] buildExactCoverRow(int[][] board, char[][] tile, int r,
			int c, int tileid) {
		int[] row = new int[numColumns];
		row[tileid] = 1;
		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[0].length; j++) {
				if (tile[i][j] != Config.S) {
					row[numTiles + board[r + i][c + j]] = 1;
				}
			}
		}
		return row;
	}

	/**
	 * Build the Exact Cover Array.
	 *
	 * @param board
	 * @param tiles
	 * @return
	 */
	private int[][] buildExactCoverArray(Tile board, List<Tile> tiles) {
		/* Use a list to store all the rows */
		List<int[]> ECL = new ArrayList<int[]>();
		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			/* Set available spins and flips of tiles. */
			int available = 0;
			if (Config.isEnableSpinFlip())
				available = tile.sfpattern.size();
			else if (Config.isEnableSpin())
				available = tile.spattern.size();
			else
				available = 1;

			for (int j = 0; j < available; j++) {
				/* Set tiles. */
				char[][] t;
				if (Config.isEnableSpinFlip())
					t = tile.sfpattern.get(j);
				else if (Config.isEnableSpin())
					t = tile.spattern.get(j);
				else
					t = tile.data;

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

		if (Config.verb) {
			System.out.println("Exact Cover Array:");
			dbg.print2DArray(ECA);
		}
		return ECA;
	}

}
