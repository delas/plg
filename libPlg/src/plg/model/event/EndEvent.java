package plg.model.event;

import plg.model.Process;

public class EndEvent extends Event {

	public EndEvent(Process owner) {
		super(owner);
	}
	
	@Override
	public String getComponentName() {
		return "End Event";
	}
}
