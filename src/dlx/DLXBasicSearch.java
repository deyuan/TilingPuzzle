package dlx;

import java.util.ArrayList;
import java.util.List;


/**
 * DLX Search Algorithm (Basic Version)
 * @author Deyuan Guo, Dawei Fan
 */
public class DLXBasicSearch {

	/******************** Public Member Variables ********************/

	/******************** Private Member Variables ********************/

	/** Reference of class DLXConfig */
	private DLXConfig Config = null;

	/** The head of dancing link structure */
	private DLXBasicLinksArray DLA = null;

	/** The DLXTrail for keeping searching trail (stack) */
	private DLXTrail Trail = null;

	/** The DLXTrail for keeping partial solution (stack) */
	private DLXTrail Solution = null;

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLXBasicSearch class
	 * @param dla
	 * @param config
	 */
	public DLXBasicSearch(DLXBasicLinksArray dla, DLXConfig config) {
		DLA = dla;
		Config = config;

		Trail = new DLXTrail();
		Solution = new DLXTrail();
	}

	/**
	 * Solve the puzzle and find all solutions.
	 * @return a list of valid solutions
	 */
	public List<List<List<Integer>>> solve() {
		Config.setSingleStepSearch(false);
		Config.setSingleSolutionSearch(false);

		List<List<List<Integer>>> solutions =
				new ArrayList<List<List<Integer>>>();
		while (!Config.searchFinished()) {
			solveSingleSolution();
			if (Solution.size() > 0) {
				solutions.add(solutionToPosition(Solution));
			}
		}
		return solutions;
	}

	/**
	 * Solve until find next solution.
	 * @return a valid solution
	 */
	public List<List<Integer>> solveSingleSolution() {
		Config.setSingleSolutionSearch(true);
		searchLoop(Trail);
		return solutionToPosition(Solution);
	}

	/**
	 * Solve with only a single step search.
	 * @return a partial solution
	 */
	public List<List<Integer>> solveSingleStep() {
		Config.setSingleStepSearch(true);
		searchLoop(Trail);
			return solutionToPosition(Solution);
	}

	/**
	 * Reset the whole DLX search so that we can start over again.
	 */
	public void reset() {
		Trail.clear();
		Solution.clear();
		Config.setSearchFinished(false);
	}

	/**
	 * Return true if the last single step search finds a complete solution.
	 * @return
	 */
	public boolean isCompleteSolution() {
		return Solution.isComplete();
	}
	/******************** Private Member Functions ********************/

	/**
	 * Choose Column Object - Part of the Dancing Link Algorithm
	 *
	 * @return reference to a column object
	 */
	private DLXColumnHeader chooseColumnObject() {
		boolean minimizeBranchingFactor = true;

		/* Choose the first column */
		DLXColumnHeader c = DLA.H.R;

		/* Do we really need this to deal with extra tiles? */
		if (Config.isEnableExtra()) {
			while (c.col < DLA.numTiles)
				c = c.R;
		}

		if (minimizeBranchingFactor) {
			int s = Integer.MAX_VALUE;
			for (DLXColumnHeader j = c; j != DLA.H; j = j.R) {
				if (j.S < s) {
					c = j;
					s = j.S;
				}
			}
		}
		//if (Config.verb) System.out.println("Choose column c" + c.col);
		return c;
	}

	/**
	 * Cover Column - Part of the Dancing Link Algorithm
	 *
	 * @param c
	 */
	private void coverColumn(DLXColumnHeader c) {
		//if (Config.verb) System.out.println("Cover c" + c.col);
		c.R.L = c.L;
		c.L.R = c.R;
		for (DLXCell i = c.D; i != c; i = i.D) {
			for (DLXCell j = i.R; j != i; j = j.R) {
				j.D.U = j.U;
				j.U.D = j.D;
				j.C.S -= 1;
			}
		}
	}

	/**
	 * Uncover Column - Part of the Dancing Link Algorithm
	 *
	 * @param c
	 */
	private void uncoverColumn(DLXColumnHeader c) {
		//if (Config.verb) System.out.println("Uncover c" + c.col);
		for (DLXCell i = c.U; i != c; i = i.U) {
			for (DLXCell j = i.L; j != i; j = j.L) {
				j.C.S += 1;
				j.D.U = j;
				j.U.D = j;
			}
		}
		c.R.L = c;
		c.L.R = c;
	}

	/**
	 * Searching with Dancing Links (recursive version)
	 *
	 * @param k
	 */
	private void searchRecur(int k) {

		//if (Config.verb) { System.out.print("LV" + k + ": "); Solution.print(); }

		if (DLA.H.R == DLA.H || DLA.H.L.col < DLA.numTiles) {
			printSolution();
			return;
		}
		DLXColumnHeader c = chooseColumnObject();
		coverColumn(c);
		for (DLXCell r = c.D; r != c; r = r.D) {
			Solution.push(r);
			for (DLXCell j = r.R; j != r; j = j.R) {
				coverColumn(j.C);
			}
			searchRecur(k + 1);
			r = Solution.pop();
			c = r.C;
			for (DLXCell j = r.L; j != r; j = j.L) {
				uncoverColumn(j.C);
			}
		}
		uncoverColumn(c);

		return;
	}

	/**
	 * Searching with Dancing Links (while loop with stack version)
	 *
	 * @param k
	 */
	private void searchLoop(DLXTrail trail) {
		Solution.setComplete(false);

		/* Directly Failed */
		if (Config.isDirectlyFail()) return;

		/* If start from an empty trail */
		if (!Config.searchFinished() && trail.isEmpty()) {
			DLXColumnHeader c = chooseColumnObject();
			for (DLXCell i = c.U; i != c; i = i.U) {
				trail.push(i);
			}
		}

		/* Search kernel */
		do {
			/* Backtracking */
			while (!Solution.isEmpty() && Solution.top() == trail.top()) {
				DLXCell t = Solution.pop();
				trail.pop();
				for (DLXCell i = t.L; i != t; i = i.L) {
					uncoverColumn(i.C);
				}
				uncoverColumn(t.C);
			}
			if (trail.size() == 0) {
				Config.setSearchFinished(true);
				break; // finished
			}

			/* Search a cell */
			DLXCell x = trail.top();
			Solution.push(x);
			coverColumn(x.C);
			for (DLXCell i = x.R; i != x; i = i.R) {
				coverColumn(i.C);
			}

			/* Search next level */
			DLXColumnHeader c = chooseColumnObject();
			if (c.S > 0) {
				for (DLXCell i = c.U; i != c; i = i.U) {
					trail.push(i);
				}
				continue;
			}

			/* Output */
			if (DLA.H.R == DLA.H || DLA.H.L.col < DLA.numTiles) {
				if (Config.verb) {
					System.out.print("Find: ");
					Solution.print();
				}
				Solution.setComplete(true);
			}
		} while (!Config.singleStepSearch() &&
				!(Config.singleSolutionSearch() && Solution.isComplete()));

		return;
	}

	/**
	 * Print out current solution
	 * @param k
	 */
	private void printSolution() {
		System.out.print("Solution with " + Solution.size() + " tiles: ");
		for (int i = 0; i < Solution.size(); i++) {
			DLXCell x = Solution.get(i);

			/* Find the leftmost cell */
			DLXCell tile = x;
			while (tile.L.col < tile.col) tile = tile.L;

			/* Print out the row */
			System.out.print(tile.C.N + "(");
			for (DLXCell j = tile.R; j != tile; j = j.R) {
				System.out.print(j.C.N);
			}
			System.out.print(") ");
		}
		System.out.println();
	}

	/**
	 * Convert a solution trail into list of (tile index and tile positions).
	 * @param solution
	 * @return
	 */
	public List<List<Integer>> solutionToPosition(DLXTrail solution) {

		List<List<Integer>> pos = new ArrayList<List<Integer>>();
		for (int i = 0; i < Trail.size(); i++) {
			List<Integer> tpos = new ArrayList<Integer>();

			/* Find the leftmost cell */
			DLXCell x = Trail.get(i);
			while (x.L.col < x.col) x = x.L;

			/* The first element in the list is the index of a tile,
			 *  the others are indices of positions on board. */
			tpos.add(x.C.col);
			for (DLXCell j = x.R; j != x; j = j.R) {
				tpos.add(j.C.col - DLA.numTiles);
			}

			pos.add(tpos);
		}
		return pos;
	}

}
