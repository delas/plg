package plg.exceptions;

/**
 * This class is an exception thrown when an invalid script is provided.
 * 
 * @author Andrea Burattin
 */
public class InvalidScript extends Exception {

	private static final long serialVersionUID = 3207919091973781472L;
	private String script;

	/**
	 * Exception constructor
	 * 
	 * @param message the exception message
	 * @param script the script that generated the error
	 */
	public InvalidScript(String message, String script) {
		super(message);
		this.script = script;
	}
	
	/**
	 * This method returns the script that generated the error
	 * 
	 * @return the script
	 */
	public String getScript() {
		return script;
	}
}
