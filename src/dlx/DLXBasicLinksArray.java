package dlx;


/**
 * DLX Links Generator (Basic Version)
 * @author Deyuan Guo, Dawei Fan
 */
public class DLXBasicLinksArray {

	/******************** Public Member Variables ********************/

	/** The number of Tiles */
	public int numTiles = 0;

	/** The number of DLXCells */
	public int numCells = 0;

	/** The number of Columns */
	public int numColumns = 0;

	/** The number of Rows */
	public int numRows = 0;

	/** The head of dancing links */
	public DLXColumnHeader H = null;

	/******************** Private Member Variables ********************/

	/** Reference of class DLXConfig */
	private DLXConfig Config = null;

	/** Reference of class DLXBasicExactCoverArray */
	private DLXBasicExactCoverArray ECA = null;

	/** The array of column header */
	private DLXColumnHeader[] CHA = null;

	/** The exact cover cell objects */
	private DLXCell[][] DLA = null;

	/******************** Public Member Functions ********************/

	/**
	 * Constructor of DLXBasicLinksArray
	 * @param eca
	 * @param config
	 */
	public DLXBasicLinksArray(DLXBasicExactCoverArray eca, DLXConfig config) {
		ECA = eca;
		Config = config;
		numTiles = ECA.numTiles;
		numCells = ECA.numCells;
		numColumns = ECA.numColumns;
		numRows = ECA.numRows;

		CHA = new DLXColumnHeader[numColumns];
		DLA = new DLXCell[numRows][numColumns];

		H = buildDancingLinks();

		verifyDancingLinks();

		chooseLeaderTile();
	}

	/**
	 * Return true if a column header is reachable from H.
	 * @param col
	 * @return
	 */
	public boolean isReachableColumnHeader(int col) {
		for (DLXColumnHeader h = H.R; h.col <= col && h != H; h = h.R) {
			if (h.col == col) return true;
		}
		return false;
	}

	/******************** Private Member Functions ********************/

	/**
	 * Build the Dancing Links.
	 *
	 * @return
	 */
	private DLXColumnHeader buildDancingLinks() {
		DLXColumnHeader h = new DLXColumnHeader(); // head
		h.row = -1;
		h.col = -1;

		/* Allocate column head objects CHA and build the links */
		for (int i = 0; i < ECA.numColumns; i++) {
			DLXColumnHeader y = new DLXColumnHeader();
			y.U = y;
			y.D = y;
			y.C = y;
			y.row = -1;
			y.col = i;
			if (i == 0) {
				y.L = h;
				h.R = y;
			} else {
				y.L = CHA[i - 1];
				CHA[i - 1].R = y;
				if (i == numColumns - 1) {
					y.R = h;
					h.L = y;
				}
			}
			CHA[i] = y;
			if (i < numTiles) {
				CHA[i].N = "T" + Integer.toString(i);
				CHA[i].tid = i;
			} else {
				CHA[i].N = "_" + Integer.toString(i - numTiles);
				CHA[i].tid = -1;
			}
		}

		/* Allocate cell objects DLA */
		Config.setHasUnreachablePosition(false);
		for (int j = 0; j < numColumns; j++) {
			int n = 0;
			for (int i = 0; i < numRows; i++) {
				if (ECA.ECA[i][j] != 0) {
					DLXCell x = new DLXCell();
					x.row = i;
					x.col = j;
					DLA[i][j] = x;
					n++;
				} else {
					DLA[i][j] = null;
				}
			}
			/* If a cell cannot be covered by any tile, then directly fail. */
			if (n == 0 && j >= numTiles) {
				Config.setHasUnreachablePosition(true);
				if (Config.verb)
					System.out.println("Directly fail (unreachable positions).");
				return null;
			}
		}

		/* Build the dancing links (row direction) */
		for (int i = 0; i < numRows; i++) {
			DLXCell leftmost = null;
			DLXCell prev = null;
			DLXCell curr = null;
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
					curr.tid = leftmost.col;  // Assign tid for each DLXCell
				}
			}
			curr.R = leftmost;
			leftmost.L = curr;
		}
		/* Build the dancing links (column direction) */
		for (int i = 0; i < numColumns; i++) {
			DLXCell prev = null;
			DLXCell curr = CHA[i];
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
	 * Verify Dancing Links.
	 */
	private void verifyDancingLinks() {
		if (Config.verb) {
			int cnt1, cnt2, total = 0;

			System.out.println();
			System.out.println("Verifying Dancing Links:");

			System.out.print("Column Size: ");
			for (DLXColumnHeader i = H.R; i != H; i = i.R) {
				System.out.print(i.S + " ");
				total += i.S;
			}
			System.out.println("Total: " + total);

			/* Check links between column head objects */
			cnt1 = cnt2 = 0;
			for (DLXColumnHeader i = H.R; i != H; i = i.R)
				cnt1++;
			for (DLXColumnHeader i = H.L; i != H; i = i.L)
				cnt2++;
			if (cnt1 != numColumns || cnt2 != numColumns)
				System.out.println("Column Head Links Error.");

			/* Check links in column direction */
			for (DLXColumnHeader i = H.R; i != H; i = i.R) {
				cnt1 = cnt2 = 0;
				for (DLXCell j = i.D; j != i; j = j.D)
					cnt1++;
				for (DLXCell j = i.U; j != i; j = j.U)
					cnt2++;
				if (cnt1 != i.S || cnt2 != i.S)
					System.out.println("Column Links Error at " + i.N + " "
							+ cnt1 + " " + cnt2 + " " + i.S);
			}

			/* Check links in row direction */
			int[] rowSize = new int[numRows];
			for (DLXColumnHeader i = H.R; i != H; i = i.R) {
				for (DLXCell j = i.D; j != i; j = j.D) {
					cnt1 = cnt2 = 0;
					for (DLXCell k = j.R; k != j; k = k.R)
						cnt1++;
					for (DLXCell k = j.L; k != j; k = k.L)
						cnt2++;
					if (cnt1 != cnt2)
						System.out.println("Row Links Error at " + j.row);
					if (rowSize[j.row] == 0) {
						rowSize[j.row] = cnt1 + 1;
					} else {
						if (cnt1 + 1 != rowSize[j.row])
							System.out.println("Row Size Error at " + j.row);
					}
				}
			}
			int total_cnt = 0;
			for (int i = 0; i < numRows; i++)
				total_cnt += rowSize[i];
			if (total != total_cnt)
				System.out.println("Number of Row Links Error");

			System.out.println("Verifying Finished.");
			System.out.println();
		}
	}

	/**
	 * Choose a leader tile from tile list. (for eliminating symmetry)
	 */
	private void chooseLeaderTile() {
		int bestId = -1, bestSize = Integer.MAX_VALUE;
		if (!Config.isEnableExtra())
		for (DLXColumnHeader h = H.R; h.col < numTiles; h = h.R) {
			if (Config.tiles.get(h.col).sfpattern.size() == 8) {
				if (h.S < bestSize) {
					bestId = h.col;
					bestSize = h.S;
				}
			}
		}
		Config.setLeaderId(bestId);
	}

}
