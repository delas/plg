package plg.model.event;

import plg.model.FlowObject;
import plg.model.Process;

/**
 * This class represents a general process event
 * 
 * @author Andrea Burattin
 */
public abstract class Event extends FlowObject {

	/**
	 * This constructor creates a new event and register it to the given process
	 * owner
	 * 
	 * @param owner the process owner of the new event
	 */
	public Event(Process owner) {
		super(owner);
	}
}
