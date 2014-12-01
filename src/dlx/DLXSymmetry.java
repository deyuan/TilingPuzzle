package dlx;

import java.util.Arrays;
import java.util.List;

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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[i][l - j - 1] != data[i][j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[j][w - i - 1] != data[i][l - 1 - j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[w - i - 1][l - j - 1] != data[i][l - 1 - j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[l - 1 - j][i] != data[i][l - 1 - j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[i][j] != data[i][j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[j][w - i - 1] != data[i][j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[w - i - 1][l - j - 1] != data[i][j])
					return false;
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
		int w = data.length;
		int l = data[0].length;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++){
				if(result[l - 1 - j][i] != data[i][j])
					return false;
			}

		}
		return true;
	}
}
