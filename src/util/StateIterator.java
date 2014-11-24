package util;

/**
 * A class to facilitate iterating available space of a tile. It is kind of like
 * interface of Iterator, also with functions to check current position and
 * validness. Compared with old Iterator (now it is Iterator) multiple patterns
 * are considered.
 *
 * @author Dawei Fan
 * @version 1.0 11/15/2014
 *
 * @see java.util.Iterator
 * @see util.Iterator
 */
public class StateIterator {
	/**
	 * The specified tile. As the width and length will change if there are
	 * multiple patterns, the tile is needed rather than only the width and
	 * length.
	 */
	private Tile tile;

	/**
	 * The width and length of the board.
	 */
	private int wb, lb;

	/**
	 * The width limit and length limit of the available matrix: (wb-wt+1,
	 * lb-lt+1). Note different from one pattern, multiple patterns scenario
	 * will have different lt and wt.
	 */
	private int wl, ll;

	/**
	 * The total patterns of the tile.
	 */
	private int spin;

	/**
	 * Current width, length and pattern.
	 */
	public int curW, curL, curP;

	/**
	 * Initialize a StateIterator with a tile and size of the board.
	 *
	 * @param t
	 *            the specified tile
	 * @param w
	 *            the width of the board
	 * @param l
	 *            the length of the board
	 */
	public StateIterator(Tile t, int w, int l) {
		this.wb = w;
		this.lb = l;
		/* Warning: this is a shallow copy! Change if incur errors! */
		this.tile = t;
		curW = 0;
		curL = 0;
		curP = 0;
		wl = wb - tile.spattern.get(0).length + 1;
		ll = lb - tile.spattern.get(0)[0].length + 1;
		spin = t.spin;
	}

	/**
	 * Return current position and move the pointer to next position.
	 *
	 * @return current position
	 */
	public int[] next() {
		int point[] = { curW, curL, curP };
		/*
		 * If currently it is the end of a matrix, then reset the matrix and
		 * ++curP
		 */
		if ((curW == wl - 1) && (curL == ll - 1)) {
			curW = 0;
			curL = 0;
			++curP;
			if (curP <= spin) {
				wl = wb - tile.spattern.get(curP).length + 1;
				ll = lb - tile.spattern.get(curP)[0].length + 1;
			}

		} else {
			curW += (curL + 1) / ll;
			curL = (curL + 1) % ll;
		}

		return point;
	}

	/**
	 * Return current position.
	 *
	 * @return current position
	 */
	public int[] getPos() {
		int point[] = { curW, curL, curP };
		return point;
	}

	/**
	 * Check if current position is a legal position.
	 *
	 * @return true: legal position
	 *         <p>
	 *         false: out of range
	 */
	public boolean isValid() {
		if (curP > spin)
			return false;
		return true;
	}

	/**
	 * Check if current position has next position.
	 *
	 * @return true: has next
	 *         <p>
	 *         false: it is the last
	 */
	public boolean hasNext() {
		if (curW == wl - 1 && curL == ll - 1 && curP == spin)
			return false;
		return true;
	}

	/**
	 * Reset current position to 0.
	 */
	public void reset() {
		curW = 0;
		curL = 0;
		curP = 0;
	}

}
