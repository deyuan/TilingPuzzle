package dlx;

import java.util.List;

import util.Tile;


/**
 * DLX Algorithm Configuration
 * @author Deyuan Guo, Dawei Fan
 */
public class DLXConfig {

	/******************** Public Member Variables ********************/

	/** The character for splitting tiles */
	public final char S = ' ';

	/** The debug information switch */
	public boolean verb = true;

	/** Reference to the board and tile list */
	public Tile board = null;
	public List<Tile> tiles = null;

	/******************** Private Member Variables ********************/

	/** Enable spin of tiles or not. */
	private boolean enableSpin = false;

	/** Enable spin of tiles or not. */
	private boolean enableSpinFlip = false;

	/** Enable spin of tiles or not, this is set by compare area. */
	private boolean enableExtra = false;

	/** Directly Fail */
	private boolean directlyFail = false;

	/** Search Finished */
	private boolean searchFinished = false;

	/** Single step search */
	private boolean singleStepSearch = false;

	/** Single step search */
	private boolean singleSolutionSearch = false;

	/** Eliminate symmetry solution */
	private boolean eliminateSymmetry = true;

	/** Eliminate same solution due to duplicated tiles
	 *  Note it is enabled by default.
	 */
	private boolean eliminateDuplica = true;

	/** Tile duplication recodes */
	private int[] duplica = null;
	private int[] duplicaS = null;
	private int[] duplicaSF = null;

	/** The ID of the leader tile. -1 for no leader. */
	private int leaderId = -1;

	/** True if use leader tile to eliminate symmetry */
	private boolean symmetryEliminatedByLeader = false;

	/******************** Public Member Functions ********************/

	public DLXConfig() {
	}

	/**
	 * Reset the configuration before every new process.
	 */
	public void reset(){
		/* Reset all search related configuration. */
		directlyFail = false;
		searchFinished = false;
		singleStepSearch = false;
		singleSolutionSearch = false;
		symmetryEliminatedByLeader = false;
	}

	public boolean isEnableSpin() { return enableSpin; }
	public void setEnableSpin(boolean b) { enableSpin = b; }

	public boolean isEnableSpinFlip() { return enableSpinFlip; }
	public void setEnableSpinFlip(boolean b) { enableSpinFlip = b; }

	public boolean isEnableExtra() { return enableExtra; }
	public void setEnableExtra(boolean b) { enableExtra = b; }

	public boolean isDirectlyFail() { return directlyFail; }
	public void setDirectlyFail(boolean b) { directlyFail = b; }

	public boolean searchFinished() { return searchFinished; }
	public void setSearchFinished(boolean b) { searchFinished = b; }

	public boolean singleStepSearch() { return singleStepSearch; }
	public void setSingleStepSearch(boolean b) { singleStepSearch = b; }

	public boolean singleSolutionSearch() { return singleSolutionSearch; }
	public void setSingleSolutionSearch(boolean b) { singleSolutionSearch = b; }

	public boolean eliminateSymmetry() { return eliminateSymmetry; }
	public void setEliminateSymmetry(boolean b) { eliminateSymmetry = b; }

	public int getLeaderId() { return leaderId; }
	public void setLeaderId(int id) { leaderId = id; }

	public boolean symmetryEliminatedByLeader() { return symmetryEliminatedByLeader; }
	public void setSymmetryEiminatedByLeader(boolean b) { symmetryEliminatedByLeader = b; }

	public boolean eliminateDuplica() { return eliminateDuplica; }
	public void setEliminateDuplica(boolean b) { eliminateDuplica = b; }
	public void autoSetEliminateDuplica() {
		for (int i = 0; i < duplica.length; i++) {
			if ((duplica[i] != i) ||
					(duplicaS[i] != i && enableSpin) ||
					(duplicaSF[i] != i && enableSpinFlip)) {
				eliminateDuplica = true;
				return;
			}
		}
		eliminateDuplica = false;
	}

	/**
	 * Return the reference of the correct duplica array.
	 * @return
	 */
	public int[] duplica() {
		if (enableSpinFlip) return duplicaSF;
		if (enableSpin) return duplicaS;
		return duplica;
	}

	public void print() {
		System.out.println("DLXConfig Class:");
		System.out.println("Spin = " + enableSpin);
		System.out.println("Spin/Flip = " + enableSpinFlip);
		System.out.println("Extra = " + enableExtra);
		System.out.println("Eliminate symmetry = " + eliminateSymmetry);
		System.out.println("Eliminate duplica = " + eliminateDuplica);
		System.out.println("Directly fail = " + directlyFail);
		System.out.println("Search finished = " + searchFinished);
		System.out.println("Single step search = " + singleStepSearch);
		System.out.println("Single solution search = " + singleSolutionSearch);
		System.out.println();
	}

	/**
	 * Recognize the duplicated tiles, i.e. fill in duplica/S/SF fields.
	 * @param tiles - Tile list in area descending order
	 */
	public void recognizeDuplica(List<Tile> tiles) {
		for (int i = 0; i < tiles.size(); i++) {
			tiles.get(i).setId(i);
			tiles.get(i).setDuplica(-1);
			tiles.get(i).setDuplicaS(-1);
			tiles.get(i).setDuplicaSF(-1);
		}

		int b = 0, e;
		while (b < tiles.size()) {

			/* Find the same area interval [b, e] */
			for (e = b; e < tiles.size(); e++) {
				if (tiles.get(e).area != tiles.get(b).area) {
					break;
				}
			}
			e--;

			/* Recognize */
			for (int i = e; i >= b; i--) {
				Tile ti = tiles.get(i);
				if (ti.getDuplica() == -1) {
					ti.setDuplica(i);
					Tile tprev = ti;
					for (int j = i - 1; j >= b; j--) {
						Tile tj = tiles.get(j);
						if (tj.equal(ti)) {
							tprev.setDuplica(j);
							tj.setDuplica(i);
							tprev = tj;
						}
					}
				}
				if (ti.getDuplicaS() == -1) {
					ti.setDuplicaS(i);
					Tile tprev = ti;
					for (int j = i - 1; j >= b; j--) {
						Tile tj = tiles.get(j);
						if (tj.equalS(ti)) {
							tprev.setDuplicaS(j);
							tj.setDuplicaS(i);
							tprev = tj;
						}
					}
				}
				if (ti.getDuplicaSF() == -1) {
					ti.setDuplicaSF(i);
					Tile tprev = ti;
					for (int j = i - 1; j >= b; j--) {
						Tile tj = tiles.get(j);
						if (tj.equalSF(ti)) {
							tprev.setDuplicaSF(j);
							tj.setDuplicaSF(i);
							tprev = tj;
						}
					}
				}
			}

			/* Next iteration */
			b = e + 1;
		}

		duplica = new int[tiles.size()];
		duplicaS = new int[tiles.size()];
		duplicaSF = new int[tiles.size()];
		for (int i = 0; i < tiles.size(); i++) {
			duplica[i] = tiles.get(i).getDuplica();
			duplicaS[i] = tiles.get(i).getDuplicaS();
			duplicaSF[i] = tiles.get(i).getDuplicaSF();
		}
	}

	/******************** Private Member Functions ********************/

}
