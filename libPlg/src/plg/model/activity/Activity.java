package plg.model.activity;

import plg.model.FlowObject;
import plg.model.Process;

/**
 * This class represents a general process activity
 * 
 * @author Andrea Burattin
 */
public abstract class Activity extends FlowObject {

	/**
	 * This constructor creates a new activity and register it to the given
	 * process owner
	 * 
	 * @param owner the process owner of the new activity
	 */
	public Activity(Process owner) {
		super(owner);
	}
}
