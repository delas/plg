package plg.exceptions;

/**
 * This class is an exception that can be thrown when an illegal sequence is
 * reported. Illegal sequences are reported in <em>Table 7.3</em> of the
 * <a href="http://www.omg.org/cgi-bin/doc?formal/11-01-03.pdf">BPMN 2.0
 * standard definition</a>.
 * 
 * @author Andrea Burattin
 */
public class IllegalSequenceException extends Exception {
	
	private static final long serialVersionUID = 1215436066655948513L;
	
	/**
	 * Exception constructor
	 * 
	 * @param message the exception message
	 */
	public IllegalSequenceException(String message) {
		super(message);
	}
}
