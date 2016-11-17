package plg.model;

import plg.utils.Pair;

/**
 * This interface defines the properties that all implementing classes can be
 * displaced in a canvas.
 * 
 * @author Andrea Burattin
 */
public interface Displaceable {

	/**
	 * This method returns the location coordinates of the element.
	 * 
	 * @return
	 */
	public Pair<Integer, Integer> getLocation();

	/**
	 * This method sets the location coordinates of the element.
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y);

	/**
	 * This method returns the dimensions of the element.
	 * @return
	 */
	public Pair<Integer, Integer> getDimensions();

	/**
	 * This method sets the dimensions of the element.
	 * 
	 * @param width
	 * @param height
	 */
	public void setDimensions(int width, int height);
}
