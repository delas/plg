package plg.model.activity;

import plg.exceptions.InvalidScript;
import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.model.Process;

/**
 * This class represents a task of a process.
 * 
 * @author Andrea Burattin
 */
public class Task extends Activity {

	/**
	 * Name of the Python function that generate the duration of the activity,
	 * in seconds
	 */
	private static final String FUNCTION_DURATION = "time_lasted";
	/**
	 * Name of the Python function that generate the waiting time after the
	 * activity execution, in seconds
	 */
	private static final String FUNCTION_TIME_AFTER = "time_after";
	
	private String name;
	private IntegerScriptExecutor activityScript;
	
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
	
	/**
	 * Sets the script executor
	 * 
	 * @param activityScript the scripts associated to the activity
	 */
	public void setActivityScript(IntegerScriptExecutor activityScript) {
		this.activityScript = activityScript;
	}
	
	/**
	 * This method, given the case id, returns the duration of the activity. The
	 * duration is calculated executing the {@link #FUNCTION_DURATION} function.
	 * 
	 * @param caseId the case identifier of the current execution
	 * @return the duration of the activity, if the script is available, 0
	 * otherwise
	 */
	public long getDutarion(String caseId) {
		if (activityScript == null) {
			return 0;
		}
		try {
			activityScript.execute(FUNCTION_DURATION, caseId);
			return activityScript.getValue();
		} catch (InvalidScript e) { }
		return 0;
	}
	
	/**
	 * This method, given the case id, returns the time to wait after the
	 * execution of the activity. This time is calculated executing the
	 * {@link #FUNCTION_TIME_AFTER} function.
	 * 
	 * @param caseId the case identifier of the current execution
	 * @return the time to be waited after the execution of the activity, if
	 * the script is available, 3600 otherwise (1 hour)
	 */
	public long getTimeAfter(String caseId) {
		if (activityScript == null) {
			return 3600;
		}
		try {
			activityScript.execute(FUNCTION_TIME_AFTER, caseId);
			return activityScript.getValue();
		} catch (InvalidScript e) { }
		return 3600;
	}
	
	/**
	 * This method returns the activity script associated to the current task.
	 * This script is used to compute time statistics.
	 * 
	 * @return the activity script
	 */
	public IntegerScriptExecutor getActivityScript() {
		return activityScript;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Task `").append(getName()).append("'");
		buffer.append(" (id: ").append(getId()).append(") ");
//		buffer.append(" -- incoming: ");
//		for(FlowObject fo : getIncomingObjects()) {
//			buffer.append(fo.getComponentId()).append(" ");
//		}
//		buffer.append(" | outgoing: ");
//		for(FlowObject fo : getOutgoingObjects()) {
//			buffer.append(fo.getComponentId()).append(" ");
//		}
		return buffer.toString();
	}
	
	@Override
	public String getComponentName() {
		return "Task";
	}
}
