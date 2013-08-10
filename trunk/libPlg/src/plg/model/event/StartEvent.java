package plg.model.event;

import plg.model.Process;


public class StartEvent extends Event {
	
	public StartEvent(Process owner) {
		super(owner);
	}
	
	@Override
	public String getComponentName() {
		return "Start Event";
	}
}
