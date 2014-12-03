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


	/**
	 * Pack all tiles into a rectangular 2d array, and roughly keep the
	 * height-width ratio.
	 * @param tiles
	 * @param hwRatio
	 * @return
	 */
	public static int[][] packAllTiles(List<Tile> tiles, double hwRatio) {
		List<List<Integer>> p = new ArrayList<List<Integer>>();
		p.add(new ArrayList<Integer>());

		// pack all tiles
		for (int i = 0; i < tiles.size(); i++) {
			while (!packTile(p, tiles.get(i), i))
				packEnlarge(p, hwRatio);
			//DLXSymmetry.printMatrix(packToArray(p));
		}

		// remove outer margin
		p.remove(0);
		p.remove(p.size() - 1);
		for (int i = 0; i < p.size(); i++) {
			p.get(i).remove(0);
			p.get(i).remove(p.get(i).size() - 1);
		}
		for (int i = p.size() - 1; i >= 0; i--) {
			boolean empty = true;
			for (int j = 0; j < p.get(0).size(); j++) {
				if (p.get(i).get(j) != 0 && p.get(i).get(j) != -2) {
					empty = false;
					break;
				}
			}
			if (empty) p.remove(i);
			else break;
		}
		for (int i = p.get(0).size() - 1; i >= 0; i--) {
			boolean empty = true;
			for (int j = 0; j < p.size(); j++) {
				if (p.get(j).get(i) != 0 && p.get(j).get(i) != -2) {
					empty = false;
					break;
				}
			}
			if (empty) {
				for (int j = 0; j < p.size(); j++)
					p.get(j).remove(p.get(j).size() - 1);
			}
			else break;
		}
		// adjust tile id
		for (int i = 0; i < p.size(); i++) {
			for (int j = 0; j < p.get(i).size(); j++) {
				int k = p.get(i).get(j);
				if (k <= 0) p.get(i).set(j, -1);
				else p.get(i).set(j, k - 1);
			}
		}

		return packToArray(p);
	}

	private static boolean packable(List<List<Integer>> p, Tile t, int r, int c) {
		int th = t.data.length;
		int tw = t.data[0].length;
		// tile area
		for (int i = 0; i < th; i++) {
			List<Integer> l = p.get(r + i);
			if ((l.get(c) != 0) || (l.get(c + tw - 1) != 0)
					|| (l.get(c + tw) != 0 && l.get(c + tw) != -2))
				return false;
		}
		for (int i = 0; i < tw; i++) {
			if ((p.get(r).get(c + i) != 0)
					|| (p.get(r + th - 1).get(c + i) != 0)
					|| (p.get(r + th).get(c + i) != 0
						&& p.get(r + th).get(c + i) != -2))
				return false;
		}
		if (p.get(r + th).get(c + tw) != 0 && p.get(r + th).get(c + tw) != -1)
			return false;
		return true;
	}

	private static boolean packTile(List<List<Integer>> p, Tile t, int id) {
		int h = p.size();
		int w = p.get(0).size();
		int th = t.data.length;
		int tw = t.data[0].length;

		for (int i = 0; i < h - th; i++) {
			for (int j = 0; j < w - tw; j++) {
				if (packable(p, t, i, j)) {
					// pack tile
					//System.out.println("Pack tile "+id+" to "+i+","+j);
					for (int m = 0; m < th; m++) {
						for (int n = 0; n < tw; n++) {
							p.get(i + m).set(j + n, id + 1);
						}
					}
					// margin
					for (int m = i - 1; m <= i + th; m++) {
						p.get(m).set(j - 1, -2);
						p.get(m).set(j + tw, -2);
					}
					for (int m = j - 1; m <= j + tw; m++) {
						p.get(i - 1).set(m, -2);
						p.get(i + th).set(m, -2);
					}
					return true;
				}
			}
		}
		return false;
	}

	private static void packEnlarge(List<List<Integer>> p, double hwRatio) {
		int h = p.size();
		int w = p.get(0).size();
		//System.out.print("Enlarge " + h + "x" + w + " -> ");
		if ((double)h / w <= hwRatio) { // enlarge height
			p.add(new ArrayList<Integer>());
			p.get(h).add(-2);
			for (int i = 1; i < w; i++) p.get(h).add(0);
		} else { // enlarge width
			p.get(0).add(-2);
			for (int i = 1; i < h; i++) p.get(i).add(0);
		}
		//System.out.println(p.size() + "x" + p.get(0).size());
	}

	private static int[][] packToArray(List<List<Integer>> p) {
		int h = p.size();
		int w = p.get(0).size();
		int[][] a = new int[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				a[i][j] = p.get(i).get(j);
			}
		}
		return a;
	}

}