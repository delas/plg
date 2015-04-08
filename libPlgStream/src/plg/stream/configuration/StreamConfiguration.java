package plg.stream.configuration;

/**
 * This class represents the basic stream configuration
 * 
 * @author Andrea Burattin
 */
public class StreamConfiguration {

	/**
	 * The TCP/IP port where the module can accept incoming connections
	 */
	public int servicePort = 1337;
	
	public int maximumParallelInstances = 10;
	
	/**
	 * Time in milliseconds
	 */
	public int timeBetweenEachEvent = 250;
	
	/**
	 * Time in milliseconds.
	 */
	public int timeBetweenPopulateQueue = 1000;
}
