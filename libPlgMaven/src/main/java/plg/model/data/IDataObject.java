package plg.model.data;

/**
 * This interface describes a general data object.
 * 
 * @author Andrea Burattin
 */
public interface IDataObject {

	/**
	 * This method returns the attribute name of the data object
	 * 
	 * @return the name of the current data object
	 */
	public String getName();
	
	/**
	 * This method returns the attribute value of the data object
	 * 
	 * @return the value of the current data object
	 */
	public Object getValue();
}
