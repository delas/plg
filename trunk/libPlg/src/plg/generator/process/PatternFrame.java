package plg.generator.process;

import plg.exceptions.IllegalSequenceException;
import plg.model.FlowObject;
import plg.model.Process;
import plg.utils.Pair;

/**
 * This class describes a frame containing a process pattern. Each pattern is
 * basically a container, and hooks to the left- and right-most boundaries are
 * provided.
 * 
 * @author Andrea Burattin
 */
public class PatternFrame extends Pair<FlowObject, FlowObject> {

	/**
	 * Class constructor
	 * 
	 * @param singleton the only contained object
	 */
	public PatternFrame(FlowObject singleton) {
		super(singleton, singleton);
	}
	
	/**
	 * Class constructor
	 * 
	 * @param leftBound the leftmost (i.e., the first) activity
	 * @param rightBound the rightmost (i.e., the last) activity
	 */
	public PatternFrame(FlowObject leftBound, FlowObject rightBound) {
		super(leftBound, rightBound);
	}
	
	/**
	 * This method returns the left bound of the frame
	 * 
	 * @return
	 */
	public FlowObject getLeftBound() {
		return getFirst();
	}
	
	/**
	 * This method returns the left bound of the frame
	 * 
	 * @return
	 */
	public FlowObject getRightBound() {
		return getSecond();
	}
	
	/**
	 * This method returns the process that this frame belongs to
	 * 
	 * @return the process of the frame
	 */
	public Process getProcess() {
		if (getFirst() != null) {
			return getFirst().getOwner();
		} else if (getSecond() != null) {
			return getSecond().getOwner();
		}
		return null;
	}
	
	public static PatternFrame connect(PatternFrame left, PatternFrame right) {
		if (left == null && right == null) {
			return null;
		}
		if (left == null) {
			return right;
		}
		if (right == null) {
			return left;
		}
		try {
			left.getProcess().newSequence(left.getRightBound(), right.getLeftBound());
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(left.getLeftBound(), right.getRightBound());
	}
	
	public static PatternFrame connect(FlowObject left, PatternFrame right) {
		if (right == null) {
			return new PatternFrame(left);
		}
		try {
			left.getOwner().newSequence(left, right.getLeftBound());
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(left, right.getRightBound());
	}
	
	public static PatternFrame connect(PatternFrame left, FlowObject right) {
		if (left == null) {
			return new PatternFrame(right);
		}
		try {
			left.getProcess().newSequence(left.getRightBound(), right);
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(left.getLeftBound(), right);
	}
	
	public static PatternFrame connect(FlowObject left, FlowObject right) {
		try {
			left.getOwner().newSequence(left, right);
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		}
		return new PatternFrame(left, right);
	}
	
	public PatternFrame connect(PatternFrame right) {
		return connect(this, right);
	}
	
	public PatternFrame connect(FlowObject right) {
		return connect(this, right);
	}
}
