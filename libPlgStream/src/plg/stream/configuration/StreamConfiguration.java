package plg.stream.configuration;

/**
 * This class contains the stream configuration
 * 
 * @author Andrea Burattin
 */
public class StreamConfiguration {

	/**
	 * The TCP/IP port where the module can accept incoming connections
	 */
	public int servicePort = 1234;
	
	/**
	 * This method identifies the maximum number of parallel process instances
	 * running at the same time
	 */
	public int maximumParallelInstances = 10;
	
	/**
	 * Time multiplier. This parameter can be used to speed-up or slow down a
	 * trace. In particular, the length of a trace will be multiplied by this
	 * value in order to obtain the actual streaming duration.
	 * 
	 * <p>
	 * For example, if the value of this parameter is set to 1, then the
	 * simulation time will be preserved during the streaming (i.e., if a trace
	 * lasts for 10 days, the simulation will lasts 10 days as well).
	 */
	public double timeMultiplier = 0.01;
	
	/**
	 * This parameter can be used to tune the time before the beginning of a new
	 * trace. Specifically, the time to wait before the beginning of the new
	 * trace is given multiplying the length of the trace by this value.
	 */
	public double timeFractionBeforeNewTrace = 0.5;
	
	/**
	 * This parameter indicates whether the beginning/end of a trace should be
	 * marked or not.
	 * 
	 * <p>
	 * Such marking is done by adding an attribute
	 * <tt>stream:lifecycle:trace-transition</tt> to the first and to the last
	 * event of the trace, respectively with values <tt>start</tt> and
	 * <tt>complete</tt>.
	 */
	public boolean markTraceBeginningEnd = false;
}
