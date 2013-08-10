package plg.model.event;

import plg.model.FlowObject;
import plg.model.Process;

public abstract class Event extends FlowObject {

	public Event(Process owner) {
		super(owner);
	}
}
