package plg.exceptions;

/**
 * This class is an exception thrown when a data object is somehow illegal.
 * 
 * @author Andrea Burattin
 */
public class InvalidDataObject extends Exception {

	private static final long serialVersionUID = -5262973171881262133L;

	/**
	 * Exception constructor
	 * 
	 * @param message the exception message
	 */
	public InvalidDataObject(String message) {
		super(message);
	}
}
