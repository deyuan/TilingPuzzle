package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing a tile.
 *
 * @author Dawei Fan
 * @version 0.2 11/14/2014 1) Change the data from int to char;
 *
 * @version 0.1 11/14/2014
 *
 */
public class Tile implements Comparable<Tile> {

	/**
	 * 2-D array of core data of a tile, in which 0 represents empty and
	 * positive numbers represent block. Different positive numbers represent
	 * different colors.
	 */
	public char[][] data;

	/**
	 * A list of all possible available patterns with spin, including 1, 2, or 4
	 * patterns.
	 */
	public List<char[][]> spattern = new ArrayList<char[][]>();

	/**
	 * A list of all possible available patterns with spin and flip, including
	 * 1, 2, or 4 patterns.
	 */
	public List<char[][]> sfpattern = new ArrayList<char[][]>();

	/**
	 * Width and length of a tile, Eg:
	 * <p>
	 * 1 0
	 * <p>
	 * <p>
	 * 2 1
	 * <p>
	 * <p>
	 * 1 0
	 * <p>
	 * w = 3, l = 2
	 */
	public int w, l;

	/**
	 * The spin of a tile with 0, 1, 3 which means it has 1, 2, 4 available
	 * directions.
	 */
	public int spin;

	/**
	 * Total area of a tile, bigger tiles should be tried first in the main
	 * search routine to reduce complexity.
	 */
	public int area;

	/**
	 * Use these to record the indices of the next duplicated tiles (as rings).
	 * e.g. if (tileList.get(i).duplica == i) then tile i is identity;
	 * else the next same tile index is tileList.get(i).duplica
	 * Note: duplica index may not be the same in condition of spin/flip.
	 */
	private int id = -1;
	private int duplica = -1;
	private int duplicaS = -1;
	private int duplicaSF = -1;
	public void setId(int i) { id = i; }
	public void setDuplica(int i) { duplica = i; }
	public void setDuplicaS(int i) { duplicaS = i; }
	public void setDuplicaSF(int i) { duplicaSF = i; }
	public int getId() { return id; }
	public int getDuplica() { return duplica; }
	public int getDuplicaS() { return duplicaS; }
	public int getDuplicaSF() { return duplicaSF; }

	public Tile(char[][] t) {
		w = t.length;
		l = t[0].length;
		data = new char[w][l];
		area = 0;
		/* Deep copy the array. */
		for (int i = 0; i < l; i++) {
			for (int j = 0; j < w; j++) {
				data[j][i] = t[j][i];
				if (t[j][i] != ' ')
					area++;
			}
		}
		TransformMatrix tm = new TransformMatrix(data);
		/* This list includes all possible patterns. */
		List<char[][]> patterns = new ArrayList<char[][]>();
		patterns.add(tm.rotateC0());
		patterns.add(tm.rotateC1());
		patterns.add(tm.rotateC2());
		patterns.add(tm.rotateC3());
		patterns.add(tm.frotateC0());
		patterns.add(tm.frotateC1());
		patterns.add(tm.frotateC2());
		patterns.add(tm.frotateC3());

		/* Print the patterns */
		/*
		 * System.out.println("All the patterns: "); for(int i = 0;
		 * i<patterns.size(); i++){ for(int j = 0; j< patterns.get(i).length;
		 * j++) System.out.println(Arrays.toString(patterns.get(i)[j]));
		 * System.out.println(); }
		 */

		/* Initialize spattern list. */
		int cur = 0;
		spattern.add(patterns.get(0));
		for (int i = 1; i < 4; i++) {
			boolean e = true;
			for (int j = 0; j <= cur; j++) {
				/* It seems that Arrays.deepEqual and Arrays.equal do not work. */
				if (equal(spattern.get(j), patterns.get(i))) {
					e = false;
					break;
				}
			}
			if (e) {
				spattern.add(patterns.get(i));
				cur++;
			}
		}

		/* Initialize sfpattern list. */
		cur = 0;
		sfpattern.add(patterns.get(0));
		for (int i = 1; i < 8; i++) {
			boolean e = true;
			for (int j = 0; j <= cur; j++) {
				/* It seems that Arrays.deepEqual and Arrays.equal do not work. */
				if (equal(sfpattern.get(j), patterns.get(i))) {
					e = false;
					break;
				}
			}
			if (e) {
				sfpattern.add(patterns.get(i));
				cur++;
			}
		}
	}

	public void printTile() {
		System.out.println("Tile ID = " + id);
		System.out.print("Next = " + duplica);
		System.out.print(", NextS = " + duplicaS);
		System.out.println(", NextSF = " + duplicaSF);
		System.out.print("Spin = " + spattern.size());
		System.out.println(", Spin/Flip = " + sfpattern.size());
		for (int i = 0; i < w; i++)
			System.out.println(Arrays.toString(data[i]));
		System.out.println();
	}

	/**
	 * Print all the spatterns of a tile.
	 *
	 */
	public void printSPattern() {
		System.out.println("SPattern: ");
		for (int i = 0; i < spattern.size(); i++) {
			System.out.println();
			for (int j = 0; j < spattern.get(i).length; j++)
				System.out.println(Arrays.toString(spattern.get(i)[j]));
		}
	}

	/**
	 * Print all the spatterns of a tile.
	 *
	 */
	public void printFSPattern() {
		System.out.println("SFPattern: ");
		for (int i = 0; i < sfpattern.size(); i++) {
			System.out.println();
			for (int j = 0; j < sfpattern.get(i).length; j++)
				System.out.println(Arrays.toString(sfpattern.get(i)[j]));
		}
	}

	@Override
	/**
	 * Note this is inverted in order to sort in a descending order.
	 */
	public int compareTo(Tile t) {
		// TODO Auto-generated method stub
		return t.area - area;
	}

	/**
	 * Determine if two 2D char arrays are equal
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean equal(char[][] a, char[][] b) {
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

	/**
	 * Determine if a tile is equal to current tile (without spin or flip)
	 * @param t
	 * @return
	 */
	public boolean equal(Tile t) {
		if (area != t.area) return false;
		return equal(data, t.data);
	}

	/**
	 * Determine if a tile is equal to current tile (with spin)
	 * @param t
	 * @return
	 */
	public boolean equalS(Tile t) {
		if (area != t.area) return false;
		if (spattern.size() != t.spattern.size()) return false;
		for (int i = 0; i < spattern.size(); i++) {
			if (equal(spattern.get(i), t.spattern.get(0)))
				return true;
		}
		return false;
	}

	/**
	 * Determine if a tile is equal to current tile (with spin and flip)
	 * @param t
	 * @return
	 */
	public boolean equalSF(Tile t) {
		if (area != t.area) return false;
		if (sfpattern.size() != t.sfpattern.size()) return false;
		for (int i = 0; i < sfpattern.size(); i++) {
			if (equal(sfpattern.get(i), t.sfpattern.get(0)))
				return true;
		}
		return false;
	}

}