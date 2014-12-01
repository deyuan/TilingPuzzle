package dlx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DLXSymmetry {

	/** Reference to DLXConfig. Used for dealing with duplicated tiles. */
	private static DLXConfig Config;

	/**
	 * Determine if two solutions are symmetric
	 * @param cur
	 * @param pattern
	 * @return
	 */
	public static boolean isAsymmetric(int cur[][], int pattern[][]) {

		/* Width == height. At most 8 symmetric patterns.*/
		if (cur.length == cur[0].length) {
			if (checkrotateC0(cur, pattern)
					||checkrotateC1(cur, pattern)
					||checkrotateC2(cur, pattern)
					||checkrotateC3(cur, pattern)
					||checkfrotateC0(cur, pattern)
					||checkfrotateC1(cur, pattern)
					||checkfrotateC2(cur, pattern)
					||checkfrotateC3(cur, pattern))
				return false;
			else
				return true;
		}

		/* Width != height. At most 4 symmetric patterns.*/
		else {
			if (checkrotateC0(cur, pattern)
					||checkrotateC2(cur, pattern)
					||checkfrotateC0(cur, pattern)
					||checkfrotateC2(cur, pattern))
				return false;
			else
				return true;
		}

	}

	/**
	 * Determine if a new solution is symmetric to existing solutions.
	 * @param cur
	 * @param pattern
	 * @param config
	 * @return
	 */
	public static boolean isAsymmetricList(int cur[][], List<int[][]> pattern,
			DLXConfig config) {
		Config = config;
		for (int i = 0; i < pattern.size(); i++) {
			if (!isAsymmetric(cur, pattern.get(i))) {
				if (config.verb) System.out.println("Symmetric solution.");
				return false;
			}
		}
		return true;
	}


	public static void printMatrix(int d[][]) {
		System.out.println();
		for (int i = 0; i < d.length; i++)
			System.out.println(Arrays.toString(d[i]));
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by 0.
	 *
	 * @return result
	 */
	private static boolean checkfrotateC0(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[i][l - j - 1];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by pi/2.
	 *
	 * @return result
	 */
	private static boolean checkfrotateC1(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[l - j - 1][w - i - 1];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by pi.
	 *
	 * @return result
	 */
	private static boolean checkfrotateC2(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[w - i - 1][j];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by 3*pi/2.
	 *
	 * @return result
	 */
	private static boolean checkfrotateC3(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[j][i];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Rotate the matrix clockwise by 0.
	 *
	 * @return result
	 */
	private static boolean checkrotateC0(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[i][j];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Rotate the matrix clockwise by pi/2.
	 *
	 * @return result
	 */
	private static boolean checkrotateC1(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[j][w - i - 1];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Rotate the matrix clockwise by pi.
	 *
	 * @return result
	 */
	private static boolean checkrotateC2(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				int t1 = result[w - i - 1][l - j - 1];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Rotate the matrix clockwise by 3*pi/2.
	 *
	 * @return result
	 */
	private static boolean checkrotateC3(int data[][], int result[][]) {
		// For detecting tile duplication
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				int t1 = result[l - j - 1][i];
				int t2 = data[i][j];
				if (!matched(t1, t2, map)) return false;
			}
		}
		return true;
	}

	/**
	 * Determine if two tile id are matched.
	 * @param t1
	 * @param t2
	 * @param map
	 * @return
	 */
	private static boolean matched(int t1, int t2, Map<Integer, Integer> map) {
		if (t1 >= 0 && Config.eliminateDuplica()) {
			if (Config.duplica()[t1] == t1) { //unique tile
				if (t1 != t2) return false;
			} else { //duplicated tile, a map is needed.
				if (map.containsKey(t1)) {
					if (map.get(t1) != t2) return false;
				} else {
					boolean same = false;
					if (t1 == t2) { // map to itself
						same = true;
						map.put(t1, t2);
					} else for (int k = Config.duplica()[t1];
									k != t1;
									k = Config.duplica()[k]) {
						if (k == t2) {
							same = true;
							map.put(t1, t2);
							break;
						}
					}
					if (!same) return false;
				}
			}
		} else { //just compare them
			if (t1 != t2) return false;
		}
		return true;
	}

}