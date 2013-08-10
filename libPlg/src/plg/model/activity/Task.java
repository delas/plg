package plg.model.activity;

import plg.model.FlowObject;
import plg.model.Process;

public class Task extends Activity {

	private String name;
	
	public Task(Process owner, String name) {
		super(owner);
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Task `").append(getName()).append("'");
		buffer.append(" -- incoming: ");
		for(FlowObject fo : getIncomingObjects()) {
			buffer.append(fo.getComponentId()).append(" ");
		}
		buffer.append(" | outgoing: ");
		for(FlowObject fo : getOutgoingObjects()) {
			buffer.append(fo.getComponentId()).append(" ");
		}
		return buffer.toString();
	}
	
	@Override
	public String getComponentName() {
		return "Task";
	}
}
