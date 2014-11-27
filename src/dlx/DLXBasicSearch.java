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

	/** The number of solutions */
	private int cntSolution = 0;

	/** The positions list for GUI to draw. */
	private List<List<List<Integer>>> position;

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
		position = new ArrayList<List<List<Integer>>>();
	}


	public void solve() {
		if (!Config.isDirectlyFail())
			search(0);
	}

	public List<List<List<Integer>>> getPosition() {
		return position;
	}

	public void setCntSolution(int n) { cntSolution = n; }

	public int getCntSolution() { return cntSolution; }

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
		//if (Config.isEnableExtra()) {
		//	while (c.col < DLA.numTiles)
		//		c = c.R;
		//}

		if (minimizeBranchingFactor) {
			int s = Integer.MAX_VALUE;
			for (DLXColumnHeader j = c; j != DLA.H; j = j.R) {
				if (j.S < s) {
					c = j;
					s = j.S;
				}
			}
		}
		//if (Config.verb) System.out.println("Choose column " + c.N);
		return c;
	}

	/**
	 * Cover Column - Part of the Dancing Link Algorithm
	 *
	 * @param c
	 */
	private void coverColumn(DLXColumnHeader c) {
		// if (Config.verb) System.out.println("Cover " + c.N);
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
		// if (Config.verb) System.out.println("Uncover " + c.N);
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
	 * Searching with Dancing Links.
	 *
	 * @param k
	 */
	private void search(int k) {

		if (Config.verb) {
			System.out.print("LV"+k+": ");
			Trail.print();
		}
		if (DLA.H.R == DLA.H || DLA.H.L.col < DLA.numTiles) {
			cntSolution++;
			//setPosition(k);
			printSolution(k);
			return;
		}
		DLXColumnHeader c = chooseColumnObject();
		coverColumn(c);
		for (DLXCell r = c.D; r != c; r = r.D) {
			Trail.push(r);
			for (DLXCell j = r.R; j != r; j = j.R) {
				coverColumn(j.C);
			}
			search(k + 1);
			r = Trail.pop();
			c = r.C;
			for (DLXCell j = r.L; j != r; j = j.L) {
				uncoverColumn(j.C);
			}
		}
		uncoverColumn(c);

		return;
	}

	/**
	 * Print out current solution
	 * @param k
	 */
	private void printSolution(int k) {
		System.out.print("Solution" + cntSolution + " (" + k + "): ");
		for (int i = 0; i < Trail.size(); i++) {
			DLXCell x = Trail.get(i);

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
	 * Get positions from stack O, the output format is a list of integers.
	 */
	private void setPosition(int k) {

		List<List<Integer>> pos = new ArrayList<List<Integer>>();
		for (int i = 0; i < Trail.size(); i++) {
			List<Integer> tpos = new ArrayList<Integer>();
			/* Add the tile number in order to choose a color. */
			DLXCell x = Trail.get(i);

			/* Find the leftmost cell */
			DLXCell tile = x;
			while (tile.L.col < tile.col) tile = tile.L;

			tpos.add(tile.C.col);
			for (DLXCell j = tile.R; j != tile; j = j.R) {
				tpos.add(j.C.col);
			}
			pos.add(tpos);
		}
		position.add(pos);
	}

}
