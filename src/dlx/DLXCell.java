package dlx;


/**
 * DLX Cell Data Structure
 * @author Deyuan Guo
 */
public class DLXCell {

	/******************** Public Member Variables ********************/

	/** Link to left cell */
	public DLXCell L;

	/** Link to right cell */
	public DLXCell R;

	/** Link to up cell or column header */
	public DLXCell U;

	/** Link to down cell or column header */
	public DLXCell D;

	/** Direct link to column header */
	public DLXColumnHeader C;

	/** The row of this cell in exact cover array */
	public int row;

	/** The column of this cell in exact cover array */
	public int col;

	/******************** Private Member Variables ********************/

	/******************** Public Member Functions ********************/

	/******************** Private Member Functions ********************/

}
