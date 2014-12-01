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
	private List<int[][]> ViewList = null;

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLX.
	 */
	public DLX(Tile b, List<Tile> t) {
		board = b;
		tiles = t;
		Config = new DLXConfig();
		Config.verb = false;
		Config.recognizeDuplica(tiles);
	}

	public DLX(String puzzleFilePath) {
		DataFileParser dfp = new DataFileParser(puzzleFilePath);
		/* Extract puzzle pieces, board are included in this list. */
		tiles = dfp.ExtractTiles();
		/* Get the board and the remained is tileList. */
		board = tiles.get(0);
		tiles.remove(0);

		Config = new DLXConfig();
		Config.verb = false;
		Config.recognizeDuplica(tiles);
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
		if (solverSelection == 0)
			solution = basicSearch.solveSingleSolution();

		if (solution!=null) {

			int view[][] = solutionView(solution);
			/* Check the solution is a unique solution. */
			if(ViewList.size()==0){
				Solutions.add(solution);
				ViewList.add(view);
			}
			else{
				/* Remove symmetry. */
				if( (Config.eliminateSymmetry()&&Config.isEnableSpinFlip()&&(board.sfpattern.size()!=8))
						||(Config.eliminateSymmetry()&&Config.isEnableSpin()&&(board.spattern.size()!=4)) ){

					while(!DLXSymmetry.isAsymmetricList(view, ViewList, true) && solution!=null){
						solution = basicSearch.solveSingleSolution();
						if(solution!=null)
							view = solutionView(solution);
					}
					if(solution!=null){
						Solutions.add(solution);
						ViewList.add(view);
					}
				}

				/* Don't remove symmetry. */
				else
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
		List<List<Integer>> step = null;
		if (solverSelection == 0) step = basicSearch.solveSingleStep();

		if (isCompleteSolution()) {

			int view[][] = solutionView(step);
			/* Check the solution is a unique solution. */
			/* The first solution. */
			if(ViewList.size()==0){
				Solutions.add(step);
				ViewList.add(view);
			}
			else{
				/* Remove symmetry. */
				if( (Config.eliminateSymmetry()&&Config.isEnableSpinFlip()&&(board.sfpattern.size()!=8))
						||(Config.eliminateSymmetry()&&Config.isEnableSpin()&&(board.spattern.size()!=4)) ){

					if(DLXSymmetry.isAsymmetricList(view, ViewList, true)){
						Solutions.add(step);
						ViewList.add(view);
					}
				}

				/* Don't remove symmetry. */
				else
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
		if (solverSelection == 0) Solutions.addAll(basicSearch.solve());
		return Solutions;
	}

	/**
	 * Solve and find all solutions using single step function.
	 * @return a list of solution
	 */
	public List<List<List<Integer>>> solveAll() {
		Config.autoSetEliminateDuplica();

		List<List<Integer>> s = nextSolution();
		while(s!=null){
			s = new ArrayList<List<Integer>>();
			s = nextSolution();
//			s = nextSolution();
		}

		System.out.println("In solve all: ");
		System.out.println(Solutions);
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
		int cnt = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (board.data[i][j] != Config.S) {
					view[i][j] = boardPosition[cnt];
					cnt++;
				} else {
					view[i][j] = -1;
				}
			}
		}

		return view;
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
			view = solutionView(Solutions.get(i));
			for (int j = 0; j < view.length; j++) {
				System.out.println(Arrays.toString(view[j]));
			}
			System.out.println();
		}
	}

	/******************** Private Member Functions ********************/

}
