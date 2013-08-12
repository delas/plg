package plg.model.event;

import plg.model.Process;

/**
 * This class represents a new start event
 * 
 * @author Andrea Burattin
 */
public class StartEvent extends Event {

	/**
	 * This constructor creates a new start event and register it to the given
	 * process owner
	 * 
	 * @param owner the process owner of the new start event
	 */
	public StartEvent(Process owner) {
		super(owner);
	}
	
	@Override
	public String getComponentName() {
		return "Start Event";
	}
}
