package dlx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.DataFileParser;
import util.Tile;

/**
 * Class DLX. Public interface by Main or GUI.
 * @author Deyuan Guo, Dawei Fan
 *
 * @version 2.1 Support eliminating duplicated solutions caused by identical
 *          tiles. 11/29/2014
 *
 * @version 2.0 Support single step and single solution searching. 11/27/2014
 *
 * @version 1.1 Split the algorithm into several files. 11/26/2014
 *
 * @version 1.0 Modified on 11/22/2014 Dawei Fan
 *          1) Added a list position used to store the result for GUI.
 *          2) Added spin, spinflip, modified chooseColumnObject to deal with
 *             all scenarios: no spin, spin, spin and flip.
 *          3) Added a directly fail detection. If there are no extra tiles,
 *             and one tile has no positions, then directly fail.
 *          4) Minor changes: getters and setters for private members.

 * @version 0.1 Created on 11/17/2014
 *
 */
public class DLX {

	/******************** Public Member Variables ********************/

	/** DLX algorithm configuration. */
	public DLXConfig Config = null;

	/******************** Private Member Variables ********************/

	private Tile board = null;
	private List<Tile> tiles = null;
	private DLXBasicExactCoverArray basicECA = null;
	private DLXBasicLinksArray basicDLA = null;
	private DLXBasicSearch basicSearch = null;

	private List<List<List<Integer>>> Solutions = null;
	private List<int[][]> ViewList = null;

	private boolean isSolutionSymmetric = false;

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLX.
	 */
	public DLX(Tile b, List<Tile> t) {
		board = b;
		tiles = t;
		Config = new DLXConfig();
		Config.board = b;
		Config.tiles = t;
		Config.verb = false;
		Config.recognizeDuplica(tiles);
		DLXSymmetry.Config = Config;
	}

	public DLX(String puzzleFilePath) {
		DataFileParser dfp = new DataFileParser(puzzleFilePath);
		/* Extract puzzle pieces, board are included in this list. */
		tiles = dfp.ExtractTiles();
		/* Get the board and the remained is tileList. */
		board = tiles.get(0);
		tiles.remove(0);

		Config = new DLXConfig();
		Config.board = board;
		Config.tiles = tiles;
		Config.verb = false;
		Config.recognizeDuplica(tiles);
		DLXSymmetry.Config = Config;
	}

	/**
	 * DLX instance can be configured before calling preProcess
	 */
	public void preProcess() {
		if (Config.verb) {
			board.printTile();
			for (Tile t: tiles) t.printTile();
		}

		Config.autoSetEliminateDuplica();
		Solutions = new ArrayList<List<List<Integer>>>();
		ViewList = new ArrayList<int[][]>();

		basicECA = new DLXBasicExactCoverArray(board, tiles, Config);
		basicDLA = new DLXBasicLinksArray(basicECA, Config);
		basicSearch = new DLXBasicSearch(basicDLA, Config);
	}

	/**
	 * Solve until find next solution.
	 * @return a valid solution
	 */
	public List<List<Integer>> nextSolution() {
		List<List<Integer>> solution = basicSearch.solveSingleSolution();
		if (solution != null) {
			/* Check if the solution is a unique solution. */
			if (Config.eliminateSymmetry() && board.sfpattern.size() != 8
					&& !Config.symmetryEliminatedByLeader()) {
				int view[][] = DLXSymmetry.solutionView(solution);
				if (ViewList.size() == 0) {
					Solutions.add(solution);
					ViewList.add(view);
				} else {
					/* Remove symmetry. */
					while (!DLXSymmetry.isAsymmetricList(view, ViewList)) {
						solution = basicSearch.solveSingleSolution();
						if (solution != null)
							view = DLXSymmetry.solutionView(solution);
						else break;
					}
					if (solution != null) {
						Solutions.add(solution);
						ViewList.add(view);
					}
				}
			} else { //don't remove symmetry
				Solutions.add(solution);
			}
		}
		return solution;
	}

	/**
	 * Solve with only a single step search.
	 * @return a partial solution
	 */
	public List<List<Integer>> nextSingleStep() {
		isSolutionSymmetric = false;
		List<List<Integer>> step = basicSearch.solveSingleStep();

		if (isCompleteSolution()) {
			/* Check if the solution is a unique solution. */
			if (Config.eliminateSymmetry() && board.sfpattern.size() != 8
					&& !Config.symmetryEliminatedByLeader()) {
				int view[][] = DLXSymmetry.solutionView(step);
				if (ViewList.size() == 0) {
					Solutions.add(step);
					ViewList.add(view);
				} else {
					/* Remove symmetry. */
					if (DLXSymmetry.isAsymmetricList(view, ViewList)) {
						Solutions.add(step);
						ViewList.add(view);
					} else {
						isSolutionSymmetric = true;
					}
				}
			} else { //don't remove symmetry
				Solutions.add(step);
			}
		}
		return step;
	}

	/**
	 * Solve and find all solutions.
	 * @return a list of solution
	 */
	public List<List<List<Integer>>> solve() {
		Config.autoSetEliminateDuplica();
		//Config.print();
		Solutions.addAll(basicSearch.solve());
		return Solutions;
	}

	/**
	 * Solve and find all solutions using single solution function.
	 * @return a list of solution
	 */
	public List<List<List<Integer>>> solveAll() {
		Config.autoSetEliminateDuplica();
		while (nextSolution() != null);
		return Solutions;
	}

	/**
	 * Reset the DLX search.
	 */
	public void resetSearch() {
		basicSearch.reset();
		Solutions.clear();
		Config.reset();
		if (Config.verb) System.out.println("DLX search has been reset.");
	}

	/**
	 * Determine if the last single step search finds a complete solution.
	 * @return
	 */
	public boolean isCompleteSolution() {
		return basicSearch.isCompleteSolution() && !isSolutionSymmetric;
	}

	/**
	 * Print all the solutions.
	 */
	public void printAllSolutions() {
		System.out.println();
		System.out.println("Find " + Solutions.size() + " solutions!");
		System.out.println();
		int[][] view = null;
		for (int i = 0; i < Solutions.size(); i++) {
			System.out.println("Solution " + (i+1) + ":");
			view = DLXSymmetry.solutionView(Solutions.get(i));
			for (int j = 0; j < view.length; j++) {
				System.out.println(Arrays.toString(view[j]));
			}
			System.out.println();
		}
	}

	public List<List<List<Integer>>> getSolutions(){
		return Solutions;
	}

	/******************** Private Member Functions ********************/

}
