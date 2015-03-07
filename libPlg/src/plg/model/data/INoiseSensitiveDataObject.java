package plg.model.data;

/**
 * This class describes a noise-sensitive data object. A noise sensitive data
 * object is a data object which values can be altered by some noise. The actual
 * correct value is still available.
 * 
 * @author Andrea Burattin
 */
public interface INoiseSensitiveDataObject extends IDataObject {

	/**
	 * This method returns the original value generated for this data object,
	 * prior to the application of any noise.
	 * 
	 * @return the original value of the current data object, with no noise
	 * application
	 */
	public Object getOriginalValue();
}
