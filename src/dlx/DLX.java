package dlx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Tile;

/**
 * Class DLX. Public interface by Main or GUI.
 * @author Deyuan Guo, Dawei Fan
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

	/** Select the DLX algorithm version */
	public int solverSelection = 0;

	/** DLX algorithm configuration. */
	public DLXConfig Config = null;

	/******************** Private Member Variables ********************/

	private Tile board = null;
	private List<Tile> tiles = null;
	private DLXBasicExactCoverArray basicECA = null;
	private DLXBasicLinksArray basicDLA = null;
	private DLXBasicSearch basicSearch = null;

	private List<List<List<Integer>>> Solutions = null;

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLX.
	 */
	public DLX(Tile b, List<Tile> t) {
		board = b;
		tiles = t;
		Config = new DLXConfig();
		Config.verb = false;
	}

	public void preProcess(){
		Solutions = new ArrayList<List<List<Integer>>>();
		if (solverSelection == 0) {
			basicECA = new DLXBasicExactCoverArray(board, tiles, Config);
			basicDLA = new DLXBasicLinksArray(basicECA, Config);
			basicSearch = new DLXBasicSearch(basicDLA, Config);
		}
	}

	/**
	 * Solve until find next solution.
	 * @return a valid solution
	 */
	public List<List<Integer>> nextSolution() {
		List<List<Integer>> solution = null;
		if (solverSelection == 0) {
			solution = basicSearch.solveSingleSolution();
		}
		if (isCompleteSolution()) {
			Solutions.add(solution);
		}
		return solution;
	}

	/**
	 * Solve with only a single step search.
	 * @return a partial solution
	 */
	public List<List<Integer>> nextSingleStep() {
		List<List<Integer>> solution = null;
		if (solverSelection == 0) {
			solution = basicSearch.solveSingleStep();
		}
		if (isCompleteSolution()) {
			Solutions.add(solution);
		}
		return solution;
	}

	/**
	 * Solve and find all solutions.
	 * @return a list of solution
	 */
	public List<List<List<Integer>>> solve() {
		System.out.println("Extra? "+Config.isEnableExtra());
		if (solverSelection == 0) {
			Solutions.addAll(basicSearch.solve());
		}
		return Solutions;
	}

	/**
	 * Reset the DLX search.
	 */
	public void resetSearch() {
		if (solverSelection == 0) basicSearch.reset();
		Solutions.clear();
		Config.reset();
		if (Config.verb) System.out.println("DLX search has been reset.");
	}

	/**
	 * Determine if the last single step search finds a complete solution.
	 * @return
	 */
	public boolean isCompleteSolution() {
		if (solverSelection == 0) return basicSearch.isCompleteSolution();
		return false;
	}

	/**
	 * Convert a solution (list of list of int) into 2D array.
	 * @param solution
	 * @return
	 */
	public int[][] solutionView(List<List<Integer>> solution) {
		/* Put all the tiles onto a serialized board position. */
		int[] boardPosition = new int[board.area];
		for (int i = 0; i < board.area; i++) boardPosition[i] = -1;
		for (int i = 0; i < solution.size(); i++) {
			int tileIdx = solution.get(i).get(0);
			for (int j = 1; j < solution.get(i).size(); j++) {
				boardPosition[solution.get(i).get(j)] = tileIdx;
			}
		}

		/* Convert a serialized board position to a 2D board */
		int rows = board.data.length;
		int cols = board.data[0].length;
		int[][] view = new int[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				view[i][j] = boardPosition[i * cols + j];
			}
		}

		return view;
	}

	/**
	 * Print all the solutions.
	 */
	public void printAllSolutions() {
		if (Config.verb) {
			int[][] view = null;
			for (int i = 0; i < Solutions.size(); i++) {
				System.out.println("Solution " + i + ":");
				view = solutionView(Solutions.get(i));
				for (int j = 0; j < view.length; j++) {
					System.out.println(Arrays.toString(view[j]));
				}
				System.out.println();
			}
		}
	}

	/******************** Private Member Functions ********************/

}
