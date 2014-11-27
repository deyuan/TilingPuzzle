package dlx;

import java.util.Stack;


/**
 * DLX Search Trail Data Structure
 * @author Deyuan Guo
 */
public class DLXTrail {

	/******************** Public Member Variables ********************/

	/******************** Private Member Variables ********************/

	private Stack<DLXCell> Trail = null;

	/******************** Public Member Functions ********************/

	public DLXTrail() {
		Trail = new Stack<DLXCell>();
	}

	public void push(DLXCell c) { Trail.push(c); }
	public DLXCell pop() { return Trail.pop(); }
	public int size() { return Trail.size(); }
	public DLXCell get(int i) { return Trail.get(i); }

	public void print() {
		for (int i = 0; i < Trail.size(); i++) {
			System.out.print("(r"+Trail.get(i).row+"c"+Trail.get(i).col+")");
		}
		System.out.println();
	}
	/******************** Private Member Functions ********************/

}
