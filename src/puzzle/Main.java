package puzzle;

import gui.DisplayDancingLinks;

import java.util.Arrays;

import util.StateIterator;
import util.Tile;

/**
 * A main class to test code.
 *
 * @author Dawei Fan
 * @version 0.1 11/14/2014
 *
 */
public class Main {

	public static void main(String[] args) {

		// dl.search(0);
		// dl.printPositions();

		// System.out.println("Finished!");

		// DisplayPuzzle dp = new DisplayPuzzle(puzzle);
		// dp.createAndShowGUI();
		DisplayDancingLinks ddl = new DisplayDancingLinks();
		ddl.createAndShowGUI();

		// dr.displayResults(7);
		// testRotateMatrix();
		// testTile();
		// testStateIterator();

		// board.printPattern();

	}

	public static void testStateIterator() {
		char l[][] = { { ' ', 'X' }, { ' ', 'X' }, { 'X', 'X' } };

		char c[][] = { { 'X', 'X' }, { 'X', 'X' }, { 'X', 'X' } };

		Tile t1 = new Tile(l);
		Tile t2 = new Tile(c);
		StateIterator si = new StateIterator(t2, 3, 4);
		while (si.isValid()) {
			System.out.println(Arrays.toString(si.getPos()));
			si.next();
		}
	}

	public static void testTile() {
		char l[][] = { { ' ', 'X' }, { ' ', 'X' }, { 'X', 'X' } };

		char c[][] = { { 'X', 'X' }, { 'X', 'X' }, { 'X', 'X' } };

		/*
		 * char p[][] = {{'X', ' '}, {'X', 'X'}, {' ', 'X'}};
		 */

		char p[][] = { { ' ', 'X' }, { 'X', 'X' }, { 'X', ' ' } };

		char u[][] = { { 'X' } };

		Tile l1 = new Tile(l);
		Tile c1 = new Tile(c);
		Tile p1 = new Tile(p);
		Tile u1 = new Tile(u);
		System.out.println("Spin:" + l1.spin);
		l1.printSPattern();
		System.out.println("Spin:" + c1.spin);
		c1.printFSPattern();
		System.out.println("Spin:" + p1.spin);
		p1.printSPattern();
		System.out.println("Spin:" + u1.spin);
		u1.printFSPattern();
	}

}

// List<Tile> tileList = new ArrayList<Tile>();
/*
 * char board[][] = {{'X', 'X', 'X', 'X'}, {'X', 'X', 'X', 'X'}, {'X', 'X', 'X',
 * 'X'}};
 *
 * char c[][] = {{'X', ' '}, {'X', 'X'}, {'X', ' '}};
 *
 * char t[][] = {{'X', 'X'}, {' ', 'X'}};
 *
 * char p[][] = {{'X'}};
 *
 * char l[][] = {{' ', 'X'}, {' ', 'X'}, {'X', 'X'}};
 */
/*
 * char board[][] = {{'X', 'X', 'X', 'X', 'X', 'X', 'X'}, {'X', 'X', 'X', 'X',
 * 'X', 'X', 'X'}};
 *
 * char c[][] = {{'X', 'X'}, {'X', 'X'}};
 *
 * char t[][] = {{' ', 'X', 'X'}, {'X', 'X', ' '}};
 *
 * char l[][] = {{' ', 'X'}, {'X', 'X'}};
 *
 * char p[][] = {{'X', 'X'}, {'X', ' '}};
 *
 * tileList.add(new Tile(c)); tileList.add(new Tile(t)); tileList.add(new
 * Tile(l)); tileList.add(new Tile(p));
 */
