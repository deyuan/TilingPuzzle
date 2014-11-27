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

	public int solverSelection = 0;

	/******************** Private Member Variables ********************/

	private DLXBasicExactCoverArray basicECA = null;
	private DLXBasicLinksArray basicDLA = null;
	private DLXBasicSearch basicSearch = null;
	private DLXConfig Config = null;

	/******************** Public Member Functions ********************/

	public DLX(Tile board, List<Tile> tiles) {
		Config = new DLXConfig();
		Config.setEnableSpin(true);

		if (solverSelection == 0) {
			basicECA = new DLXBasicExactCoverArray(board, tiles, Config);
			basicDLA = new DLXBasicLinksArray(basicECA, Config);
			basicSearch = new DLXBasicSearch(basicDLA, Config);
		}
	}

	public DLXTrail nextSolution() {
		return null;
	}

	public DLXTrail nextSingleStep() {
		return null;
	}

	public void solve() {
		if (solverSelection == 0) {
			basicSearch.solve();
		}
	}

}