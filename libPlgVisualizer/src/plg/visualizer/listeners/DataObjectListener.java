package plg.visualizer.listeners;

import plg.model.activity.Activity;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;

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
	 * for the provided activity
	 * 
	 * @param activity the activity involved
	 * @param direction the data object direction
	 * @param type the type of the data object to generate (one of
	 * {@link DataObject}<tt>.class</tt>,
	 * {@link IntegerDataObject}<tt>.class</tt> and
	 * {@link StringDataObject}<tt>.class</tt> is expected)
	 */
	public void addDataObjects(Activity activity, DATA_OBJECT_DIRECTION direction, Class<?> type);
}
