package dlx;

import java.util.Arrays;
import java.util.List;

public class DLXSymmetry {


	public static boolean isAsymmetric(int cur[][], int pattern[][], boolean sf){

		/* Enable spin and flip. At most 8 symmetric patterns. */
		if(sf){
			/* Width == height. At most 8 symmetric patterns.*/
			if(cur.length == cur[0].length){
				if(equalValue(cur, pattern)
						||equalValue(cur, rotateC1(pattern))
						||equalValue(cur, rotateC2(pattern))
						||equalValue(cur, rotateC3(pattern))
						||equalValue(cur, frotateC0(pattern))
						||equalValue(cur, frotateC1(pattern))
						||equalValue(cur, frotateC2(pattern))
						||equalValue(cur, frotateC3(pattern)))
					return false;
				else
					return true;
			}

			/* Width != height. At most 4 symmetric patterns.*/
			else{
				if(equalValue(cur, pattern)
						||equalValue(cur, rotateC2(pattern))
						||equalValue(cur, frotateC0(pattern))
						||equalValue(cur, frotateC2(pattern)))
					return false;
				else
					return true;
			}
		}

		/* Enable only spin. At most 4 symmetric patterns. */
		else{
			/* Width == height. */
			if(cur.length == cur[0].length){
				if(equalValue(cur, pattern)
						||equalValue(cur, rotateC1(pattern))
						||equalValue(cur, rotateC2(pattern))
						||equalValue(cur, rotateC3(pattern)))
					return false;
				else
					return true;
			}

			/* Width != height. At most 2 symmetric patterns. */
			else{
				if(equalValue(cur, pattern)||equalValue(cur, rotateC2(pattern)))
					return false;
				else
					return true;
			}
		}

	}


	public static boolean isAsymmetricList(int cur[][], List<int[][]> pattern, boolean sf){
		for(int i = 0; i< pattern.size(); i++){
			if(!isAsymmetric(cur, pattern.get(i), sf)){
				return false;
			}
		}
		return true;
	}


	/**
	 * Flip the matrix horizontally, rotate clockwise by 0.
	 *
	 * @return result
	 */
	private static  int[][] frotateC0(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[w][l];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[i][l - j - 1] = data[i][j];
		}
		return result;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by pi/2.
	 *
	 * @return result
	 */
	private static  int[][] frotateC1(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[l][w];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[j][w - i - 1] = data[i][l - 1 - j];
		}
		return result;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by pi.
	 *
	 * @return result
	 */
	private static  int[][] frotateC2(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[w][l];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[w - i - 1][l - j - 1] = data[i][l - 1 - j];
		}
		return result;
	}

	/**
	 * Flip the matrix horizontally, rotate clockwise by 3*pi/2.
	 *
	 * @return result
	 */
	private static  int[][] frotateC3(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[l][w];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[l - 1 - j][i] = data[i][l - 1 - j];
		}
		return result;
	}

	/**
	 * Rotate the matrix clockwise by 0.
	 *
	 * @return result
	 */
	private static  int[][] rotateC0(int data[][]) {
		return data;
	}

	/**
	 * Rotate the matrix clockwise by pi/2.
	 *
	 * @return result
	 */
	private static  int[][] rotateC1(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[l][w];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[j][w - i - 1] = data[i][j];
		}
		return result;
	}

	/**
	 * Rotate the matrix clockwise by pi.
	 *
	 * @return result
	 */
	private static int[][] rotateC2(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[w][l];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[w - i - 1][l - j - 1] = data[i][j];
		}
		return result;
	}

	/**
	 * Rotate the matrix clockwise by 3*pi/2.
	 *
	 * @return result
	 */
	private static  int[][] rotateC3(int data[][]) {
		int w = data.length;
		int l = data[0].length;
		int result[][] = new int[l][w];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++)
				result[l - 1 - j][i] = data[i][j];
		}
		return result;
	}


	/**
	 * Determine if two 2D int arrays are equal
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean equalValue(int[][] a, int[][] b) {
		if (a.length != b.length || a[0].length != b[0].length)
			return false;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] != b[i][j])
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

}
