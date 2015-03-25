package plg.visualizer.listeners;

import plg.model.activity.Task;

/**
 * This class represents a listener for events on an activity visualized
 * 
 * @author Andrea Burattin
 */
public interface TaskListener {

	/**
	 * This method is called every time the visualizer asks to set the activity
	 * duration and the time to wait after the execution of the activity
	 * 
	 * @param task the activity involved
	 */
	public void setTaskTime(Task task);
}
