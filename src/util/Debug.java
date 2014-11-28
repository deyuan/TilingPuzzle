package util;

import java.util.Arrays;

public class Debug {
	public static final boolean debug = true;

	public static void echo(String str) {
		if (debug)
			System.out.println(str);
	}

	public static void echo(String str, int d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, double d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, short d) {
		if (debug)
			System.out.println(str + d);
	}

	public static void echo(String str, String d) {
		if (debug)
			System.out.println(str + d);
	}

	/**
	 * Print out a 2D array.
	 * @param a
	 */
	public void print2DArray(int[][] a) {
		System.out.println("2D array (" + a.length + "x" + a[0].length + ")");
		for (int i = 0; i < a.length; i++)
			System.out.println(Arrays.toString(a[i]));
		System.out.println();
	}

}
