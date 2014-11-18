package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.RotateMatrix;

/**
 * A class representing a tile.
 *
 * @author Dawei Fan
 * @version 0.2
 * 			11/14/2014
 * 			1) Change the data from int to char;
 *
 * @version 0.1
 * 			11/14/2014
 *
 */
public class Tile implements Comparable<Tile>{

	/**
	 * 2-D array of core data of a tile, in which 0 represents empty and positive numbers represent block.
	 * Different positive numbers represent different colors.
	 */
	public char[][] data;

	/**
	 * A list of all possible available patterns, including 1, 2, or 4 patterns.
	 */
	public List<char [][]> pattern = new ArrayList<char [][]>();

	/**
	 * Width and length of a tile,
	 * Eg:
	 * <p>  1 0  <p>
	 * <p>  2 1  <p>
	 * <p>  1 0  <p>
	 * w =  3, l = 2
	 */
	public int w, l;

	/**
	 * The spin of a tile with 0, 1, 3 which means it has 1, 2, 4 available directions.
	 */
	public int spin;

	/**
	 * Total area of a tile, bigger tiles should be tried first in the main search routine to
	 *  reduce complexity.
	 */
	public int area;

	public Tile(char[][] t){
		w = t.length;
		l = t[0].length;
		data = new char[w][l];
		area = 0;
		/* Deep copy the array. */
		for(int i = 0; i<l; i++){
			for(int j = 0; j<w; j++){
				data[j][i] = t[j][i];
				if(t[j][i] != ' ')
					area++;
			}
		}
		RotateMatrix rm = new RotateMatrix(data);
		spin = rm.spin();
		switch(spin){
			/* No break is needed here. */
			case 3:
				pattern.add(rm.rotateC2());
				pattern.add(rm.rotateC3());
			case 1:
				pattern.add(rm.rotateC1());
			case 0:
				pattern.add(rm.rotateC0());
				break;
			default:
				System.out.println("Error! Not a valid spin");
		}
	}

	/**
	 * Calculate the spin of a tile.
	 * @return spin
	 */
	public int decideSpin(){
		return 0;
	}

	public void printTile(){
		System.out.println("Tile (area: " + area + ", space: "+ w + "x" + l + ","
				+ " spin: " + pattern.size() + "):");
		for(int i = 0; i<w; i++)
			System.out.println(Arrays.toString(data[i]));
			System.out.println();
	}

	/**
	 * Print all the patterns of a tile.
	 *
	 */
	public void printPattern(){
		System.out.println("Pattern: ");
		for(int i = 0; i<pattern.size(); i++){
			System.out.println();
			for(int j = 0; j<pattern.get(i).length; j++)
				System.out.println(Arrays.toString(pattern.get(i)[j]));
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

}
