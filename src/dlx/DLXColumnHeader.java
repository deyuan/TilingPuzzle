package dlx;


/**
 * DLX Column Header Data Structure
 * @author Deyuan Guo
 */
public class DLXColumnHeader extends DLXCell {

	/******************** Public Member Variables ********************/

	/** Link to left column header */
	public DLXColumnHeader L;

	/** Link to right column header */
	public DLXColumnHeader R;

	/** The size of column */
	public int S;

	/** The name of column */
	public String N;

	/******************** Private Member Variables ********************/

	/******************** Public Member Functions ********************/

	/******************** Private Member Functions ********************/

}
