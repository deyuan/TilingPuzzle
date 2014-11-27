package dlx;

import java.util.List;

import util.Tile;

/**
 * Class DLX. Public interface by Main or GUI.
 * @author Deyuan Guo, Dawei Fan
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

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLX.
	 */
	public DLX(Tile b, List<Tile> t) {
		board = b;
		tiles = t;

		Config = new DLXConfig();
		Config.setEnableSpin(true);
		Config.setSingleSolutionSearch(true);

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
	public DLXTrail nextSolution() {
		return null;
	}

	/**
	 * Solve with only a single step search.
	 * @return a partial solution
	 */
	public DLXTrail nextSingleStep() {
		return null;
	}

	/**
	 * Solve and find all solutions.
	 * @return a list of solution
	 */
	public List<DLXTrail> solve() {
		if (solverSelection == 0) {
			basicSearch.solve();

			resetSearch();
			while (!Config.searchFinished()) {
				basicSearch.solveSingleStep();
			}

			resetSearch();
			while (!Config.searchFinished()) {
				basicSearch.solveSingleSolution();
			}
		}
		return null;
	}

	/**
	 * Reset the DLX search.
	 */
	public void resetSearch() {
		basicSearch.reset();
		System.out.println("DLX search has been reset.");
	}

	/******************** Private Member Functions ********************/

	private int[][] solutionTo2DArray(DLXTrail trail) {
		return null;
	}

}
