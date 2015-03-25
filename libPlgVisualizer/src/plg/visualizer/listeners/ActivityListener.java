package plg.visualizer.listeners;

import plg.model.activity.Activity;

/**
 * This class represents a listener for events on an activity visualized
 * 
 * @author Andrea Burattin
 */
public interface ActivityListener {

	/**
	 * This method is called every time the visualizer asks to set the activity
	 * duration
	 * 
	 * @param activity the activity involved
	 */
	public void setActivityDuration(Activity activity);
	
	/**
	 * This method is called every time the visualizer asks to set the time to
	 * wait after the execution of the activity
	 * 
	 * @param activity the activity involved
	 */
	public void setTimeAfterActivity(Activity activity);
}
