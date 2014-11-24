package util;

/**
 * A class to facilitate iterating available space of a tile. It is kind of like
 * interface of Iterator, also with functions to check current position and
 * validness.
 *
 * @author Dawei Fan
 * @version 0.1 11/14/2014
 *
 * @see java.util.Iterator
 */
public class Iterator {
	/**
	 * The range of width and length.
	 * <p>
	 *
	 * Eg, a = {{1, 2, 3, 4},
	 * <p>
	 * {2, 3, 4, 5},
	 * <p>
	 * {3, 4, 5, 6}}; Then width = 3, length = 4.
	 */
	private int w, l;
	/**
	 * Current width and length.
	 */
	public int curW, curL;

	public Iterator(int w, int l) {
		this.w = w;
		this.l = l;
		curW = 0;
		curL = 0;
	}

	/**
	 * Return current position and move the pointer to next position.
	 *
	 * @return current position
	 */
	public int[] next() {
		int point[] = { curW, curL };
		curW += (curL + 1) / l;
		curL = (curL + 1) % l;
		return point;
	}

	/**
	 * Return current position.
	 *
	 * @return current position
	 */
	public int[] getPos() {
		int point[] = { curW, curL };
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
		if (curW >= w)
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
		if (curW == w - 1 && curL == l - 1)
			return false;
		return true;
	}

	/**
	 * Reset current position to 0.
	 *
	 */
	public void reset() {
		curW = 0;
		curL = 0;
	}

}
