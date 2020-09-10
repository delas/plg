package plg.model.event;

import plg.model.Process;

/**
 * This class represents a new end event
 * 
 * @author Andrea Burattin
 */
public class EndEvent extends Event {

	/**
	 * This constructor creates a new end event and register it to the given
	 * process owner
	 * 
	 * @param owner the process owner of the new end event
	 */
	public EndEvent(Process owner) {
		super(owner);
	}
	
	@Override
	public String getComponentName() {
		return "End Event";
	}
}
