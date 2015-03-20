package plg.generator;

/**
 * This interface defines the basic contract that all progress visualizers are
 * required to implement
 * 
 * @author Andrea Burattin
 */
public interface IProgressVisualizer {

	/**
	 * Some waiting sentences (from Slickerbox) :)
	 */
	public static String[] waitingSentences = new String[] {
		"Patience is a virtue...",
		"Hold tight...",
		"Waiting is the hardest part...",
		"Can't be much longer...",
		"Just a little longer...",
		"Hold on there...",
		"Almost there...",
		"Just a second...",
		"A little patience..."
	};
	
	/**
	 * This method sets the minimum value of the current progress
	 * 
	 * @param minimum the minimum value
	 */
	public void setMinimum(int minimum);
	
	/**
	 * This method sets the maximum value of the current progress
	 * 
	 * @param maximum the maximum value
	 */
	public void setMaximum(int maximum);
	
	/**
	 * This method increments the current status of the visualizer
	 */
	public void inc();
	
	/**
	 * This method sets the indetermination of the current progress
	 * 
	 * @param indeterminate whether there indetermination on the progress
	 */
	public void setIndeterminate(boolean indeterminate);
	
	/**
	 * This method updates the status of the current progress
	 * 
	 * @param status the new status text
	 */
	public void setText(String status);
	
	/**
	 * This method indicates that the current operation is actually started
	 */
	public void start();
	
	/**
	 * This method indicates that the current operation is finished
	 */
	public void finished();
}
