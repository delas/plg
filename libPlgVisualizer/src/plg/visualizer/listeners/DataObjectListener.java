package plg.visualizer.listeners;

import plg.model.activity.Activity;
import plg.model.data.DataObject;

/**
 * This class represents a listener for events on a data object visualized
 * 
 * @author Andrea Burattin
 */
public interface DataObjectListener {

	/**
	 * This method is called every time the visualizer asks to edit the data
	 * object
	 * 
	 * @param dataObject the data object involved
	 */
	public void editDataObjects(DataObject dataObject);
	
	/**
	 * This method is called every time the visualizer asks to remove the data
	 * object
	 * 
	 * @param dataObject the data object involved
	 */
	public void removeDataObjects(DataObject dataObject);
	
	/**
	 * This method is called every time the visualizer asks to add a data object
	 * incoming into the provided activity
	 * 
	 * @param activity the activity involved
	 */
	public void addIncomingDataObjects(Activity activity);
	
	/**
	 * This method is called every time the visualizer asks to add a data object
	 * to the provided activity
	 * 
	 * @param activity the activity involved
	 */
	public void addActivityDataObjects(Activity activity);
}
