package plg.exceptions;

/**
 * This class is an exception thrown when an invalid script is provided.
 * 
 * @author Andrea Burattin
 */
public class InvalidScript extends Exception {

	private static final long serialVersionUID = 3207919091973781472L;

	/**
	 * Exception constructor
	 * 
	 * @param message the exception message
	 */
	public InvalidScript(String message) {
		super(message);
	}
}
