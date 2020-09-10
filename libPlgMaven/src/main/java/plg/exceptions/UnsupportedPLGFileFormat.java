package plg.exceptions;

/**
 * This class is an exception thrown when an invalid PLG file is presented.
 * 
 * @author Andrea Burattin
 */
public class UnsupportedPLGFileFormat extends Exception {

	private static final long serialVersionUID = 3207919091973781472L;

	/**
	 * Exception constructor
	 * 
	 * @param message the exception message
	 */
	public UnsupportedPLGFileFormat(String message) {
		super(message);
	}
}
