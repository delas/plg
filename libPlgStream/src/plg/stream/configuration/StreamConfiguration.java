package plg.stream.configuration;

/**
 * This class represents the stream configuration
 * 
 * @author Andrea Burattin
 */
public class StreamConfiguration {

	/**
	 * The TCP/IP port where the module can accept incoming connections
	 */
	public int servicePort = 1234;
	
	public int maximumParallelInstances = 10;
	
	/**
	 * Time multiplier. This parameter can be used to speed-up or slow down a
	 * trace. In particular, the length of a trace will be multiplied by this
	 * value in order to obtain the actual length. Then, a value of 1 will
	 * preserve the duration of the generated trace.
	 */
	public double timeMultiplier = 0.5;
	
	public double timeFractionBeforeNewTrace = 0.5;
}
