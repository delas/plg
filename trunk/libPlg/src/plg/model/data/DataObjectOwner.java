package plg.model.data;

import java.util.Set;

/**
 * This interface models the possible owners of data objects.
 * 
 * @author Andrea Burattin
 */
public interface DataObjectOwner {

	/**
	 * This method to add a new data object to the current flow object
	 * 
	 * @param data the data object to be added to the current flow object
	 */
	public void addDataObject(DataObject data);
	
	/**
	 * This method to remove the given data object from the current flow object
	 * 
	 * @param data the data object to be removed from the current flow object
	 */
	public void removeDataObject(DataObject data);
	
	/**
	 * This method returns the set of all the data objects associated to the
	 * current flow object
	 * 
	 * @return the set of data objects
	 */
	public Set<DataObject> getDataObjects();
}
