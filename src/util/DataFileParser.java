package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class to parse the input ASCII text file to generate puzzle tile instances and boards.
 *
 * @author Deyuan Guo, Dawei Fan
 *
 * @version 1.0
 * 			11/15/2014
 * 			1) Simplify interface
 *
 * @version 0.2
 * 			11/14/2014 <p>
 * 			1) Change some functions to non-static, public. <p>
 * 			2) Substitute Piece to Tile. <p>
 *
 * @version 0.1
 * 			Nov 12, 2014 <p>
 *
 */
public class DataFileParser {
	/**
	 * White space for splitting pieces
	 */
    private static char S = ' ';
    private String filePath = " ";

    /**
     * Constructor of DataFileParser.
     * @param f - Path to a puzzle text file.
     */
    public DataFileParser(String f){
        filePath = f;
    }

    /**
     * Read puzzle file and return a String array.
     * @return String array represents the tiles and a board.
     */
    private String[] readPuzzleFile() {
        File f = new File(filePath);
        FileReader fr;
        try {
            fr = new FileReader(f);
        } catch (FileNotFoundException e) {
        	System.out.println("File not found!");
            return new String[0];
        }
        BufferedReader br = new BufferedReader(fr);
        List<String> lines = new ArrayList<String>();
        String line;
        try {
            line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Print out 2-D char array
     * @param buf - A 2D char array containing a tile
     */
    public void printCharArray(char[][] buf) {
        for (int i = 0; i < buf[0].length + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
        for (int i = 0; i < buf.length; i++) {
            System.out.print("|");
            for (int j = 0; j < buf[0].length; j++) {
                System.out.print(buf[i][j]);
            }
            System.out.println("|");
        }
        for (int i = 0; i < buf[0].length + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Recursively copy all blocks of a tile.
     * @param buf - Source area containing multiple tiles
     * @param buf_p - Target area containing only current tile
     * @param i - Row position of a tile block
     * @param j - Column position of a tile block
     * @param row_offset - Vertical adjustment: move to topmost
     */
    private void copyTile(char[][] buf, char[][] buf_p, int i, int j,
            int row_offset){

        if (buf[i][j] != S) {
            buf_p[i-row_offset][j] = buf[i][j];
            buf[i][j] = S;
        }
        if (buf[i][j+1] != S) copyTile(buf, buf_p, i, j+1, row_offset);
        if (buf[i][j-1] != S) copyTile(buf, buf_p, i, j-1, row_offset);
        if (buf[i+1][j] != S) copyTile(buf, buf_p, i+1, j, row_offset);
        if (buf[i-1][j] != S) copyTile(buf, buf_p, i-1, j, row_offset);
    }

    /**
     * Crop the leftmost blank and create the tile 2d array.
     * @param buf_p - An area containing a tile at top
     * @return A new Tile object
     */
    private Tile cropTile(char[][] buf_p) {
        /* move to leftmost */
        int col_offset = -1;
        for (int col = 0; col < buf_p[0].length; col++) {
            for (int row = 0; row < buf_p.length; row++) {
                if (buf_p[row][col] != S) {
                    if (col_offset < 0) col_offset = col;
                    if (col_offset > 0) {
                        buf_p[row][col - col_offset] = buf_p[row][col];
                        buf_p[row][col] = S;
                    }
                }
            }
        }
        /* calculate tile size */
        int piece_h = 0;
        for (int row = buf_p.length - 1; row >= 0; row--) {
            for (int col = buf_p[0].length - 1; col >= 0; col--) {
                if (buf_p[row][col] != S) {
                    piece_h = row + 1;
                    break;
                }
            }
            if (piece_h > 0) break;
        }
        int piece_w = 0;
        for (int col = buf_p[0].length - 1; col >= 0; col--) {
            for (int row = buf_p.length - 1; row >= 0; row--) {
                if (buf_p[row][col] != S) {
                    piece_w = col + 1;
                    break;
                }
            }
            if (piece_w > 0) break;
        }
        /* create 2d array for a tile */
        char[][] data = new char[piece_h][piece_w];
        for (int row = 0; row < piece_h; row++) {
            for (int col = 0; col < piece_w; col++) {
                data[row][col] = buf_p[row][col];
                buf_p[row][col] = S;
            }
        }
        return new Tile(data);
    }

    /**
     * Extract puzzle pieces from the input String array.
     * @return A list includes all tiles.
     */
    public List<Tile> ExtractTiles() {

        /* Read in all lines in puzzle file */
        String[] lines = readPuzzleFile();
        /* Output tile list which includes all tiles and the board. */
        List<Tile> tiles = new ArrayList<Tile>();

        /* convert string array to 2-D char array with margin. */
        int buf_rows = lines.length + 2;
        int buf_cols = 0;
        for (int row = 0; row < lines.length; row++) {
            if (lines[row].length() > buf_cols)
                buf_cols = lines[row].length();
        }
        buf_cols += 2;
        char[][] buf = new char[buf_rows][buf_cols]; // buffer for input
        char[][] buf_p = new char[buf_rows][buf_cols]; // buffer for piece
        for (int row = 0; row < buf_rows; row++) {
            for (int col = 0; col < buf_cols; col++) {
                buf[row][col] = S;
                buf_p[row][col] = S;
            }
        }
        for (int row = 0; row < lines.length; row++) {
            for (int col = 0; col < lines[row].length(); col++) {
                buf[row + 1][col + 1] = lines[row].charAt(col);
            }
        }

        /* Find and add tiles. */
        for (int row = 1; row < buf_rows - 1; row++) {
            for (int col = 1; col < buf_cols - 1; col++) {
                if (buf[row][col] != S) {
                    copyTile(buf, buf_p, row, col, row);
                    Tile tile = cropTile(buf_p);
                    tiles.add(tile);
                }
            }
        }
        Tile candidates[] = new Tile[tiles.size()];
        for(int i = 0; i<tiles.size(); i++)
            candidates[i] = tiles.get(i);

        /* Sort tiles */
        Arrays.sort(candidates);
        tiles = new ArrayList<Tile>(Arrays.asList(candidates));

        return tiles;
    }

}

