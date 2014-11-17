package util;

import java.util.Arrays;

/**
 * A class to process the rotation of a matrix, including 3 positions.
 * @author David
 *
 */
public class RotateMatrix {
	
	private char data[][];
	
	public RotateMatrix(char d[][]){
		/* Deep copy the array. */
		data = new char[d.length][d[0].length];
		for(int i = 0; i<d.length; i++){
			for(int j = 0; j<d[0].length; j++)
				this.data[i][j] = d[i][j];
		}
	}

	public int spin(){
		int w = data.length;
		int l = data[0].length;
		
		/* Horizontal and vertical symmetry */
		int s = 1;
		for(int i = 0; i<=w/2; i++){
			for(int j = 0; j<=l/2; j++){
				if(data[i][j] != data[w-i-1][j] || data[i][j] != data[i][l-1-j]){
					s = 0;
					break;
				}				
			}
		}

		/* Only one shape. */
		if(w == l && s == 1)
			return 0;	
		/* Two shapes. */
		else if(w != l && s == 1)
			return 1;
		/* Four shapes. */
		else
			return 3;		
	}
	
	
	/**
	 * Rotate the matrix clockwise by 0.
	 * 
	 * @return result
	 */
	public char[][] rotateC0(){
		return data;
	}
	
	/**
	 * Rotate the matrix clockwise by pi/2.
	 * 
	 * @return result
	 */
	public char[][] rotateC1(){
		int w = data.length;
		int l = data[0].length;
		char result[][] = new char[l][w];
		
		for(int i = 0; i<w; i++){
			for(int j = 0; j<l; j++)
				result[j][w-i-1] = data[i][j];
		}
		return result;
	}
	
	/**
	 * Rotate the matrix clockwise by pi.
	 * 
	 * @return result
	 */
	public char[][] rotateC2(){
		int w = data.length;
		int l = data[0].length;
		char result[][] = new char[w][l];
		
		for(int i = 0; i<w; i++){
			for(int j = 0; j<l; j++)
				result[w-i-1][l-j-1] = data[i][j];
		}
		return result;
		
	}
	
	/**
	 * Rotate the matrix clockwise by 3*pi/2.
	 * 
	 * @return result
	 */
	public char[][] rotateC3(){
		int w = data.length;
		int l = data[0].length;
		char result[][] = new char[l][w];
		
		for(int i = 0; i<w; i++){
			for(int j = 0; j<l; j++)
				result[l-1-j][i] = data[i][j];
		}
		return result;
		
	}
	
	public void printMatrix(char d[][]){
		System.out.println();
		for(int i = 0; i< d.length; i++)
			System.out.println(Arrays.toString(d[i]));
	}
	
}
