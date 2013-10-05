package plg.model.activity;

import plg.model.FlowObject;
import plg.model.Process;

/**
 * This class represents a task of a process.
 * 
 * @author Andrea Burattin
 */
public class Task extends Activity {

	private String name;
	
	/**
	 * This constructor creates a new task and registers it to the process owner
	 * 
	 * @param owner the process owner of the new activity
	 * @param name the name of the new task
	 */
	public Task(Process owner, String name) {
		super(owner);
		this.setName(name);
	}
	
	/**
	 * Returns the name of the task
	 * 
	 * @return the task name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the task
	 * 
	 * @param name the new task name
	 */
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
