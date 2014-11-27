package dlx;

import java.util.Stack;


/**
 * DLX Search Trail Data Structure
 * @author Deyuan Guo
 */
public class DLXTrail {

	/******************** Public Member Variables ********************/

	/******************** Private Member Variables ********************/

	private Stack<DLXCell> trail = null;
	private boolean isComplete = false;

	/******************** Public Member Functions ********************/

	public DLXTrail() {
		trail = new Stack<DLXCell>();
	}

	public void push(DLXCell c) { trail.push(c); }
	public DLXCell pop() { return trail.pop(); }
	public int size() { return trail.size(); }
	public DLXCell get(int i) { return trail.get(i); }
	public DLXCell top() { return trail.lastElement(); }
	public boolean isEmpty() { return trail.isEmpty(); }
	public void clear() { trail.clear(); }
	public boolean isComplete() { return isComplete; }
	public void setComplete(boolean b) { isComplete = b; }

	public void print() {
		for (int i = 0; i < trail.size(); i++) {
			DLXCell x = trail.get(i);
			System.out.print("(r" + x.row + "c" + x.col + ")");
		}
		System.out.println();
	}

	/******************** Private Member Functions ********************/

}
