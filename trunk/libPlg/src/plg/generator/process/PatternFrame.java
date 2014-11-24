package plg.generator.process;

import plg.model.FlowObject;
import plg.model.Process;

public class PatternFrame {

	private FlowObject leftBound;
	private FlowObject rightBound;
	
	public PatternFrame(FlowObject leftBound, FlowObject rightBound) {
		this.leftBound = leftBound;
		this.rightBound = rightBound;
	}
	
	public FlowObject getLeftBound() {
		return leftBound;
	}
	
	public FlowObject getRightBound() {
		return rightBound;
	}
	
	public Process getProcess() {
		if (leftBound != null) {
			return leftBound.getOwner();
		} else if (rightBound != null) {
			return rightBound.getOwner();
		}
		return null;
	}
}
