package plg.generator;

/**
 * This interface defines the basic contract that all progress visualizers are
 * required to implement
 * 
 * @author Andrea Burattin
 */
public interface IProgressVisualizer {

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
}
