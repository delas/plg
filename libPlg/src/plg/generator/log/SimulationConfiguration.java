package plg.generator.log;

/**
 * This class describes the parameters of the process simulation. With this
 * class the user can control the process simulation.
 * 
 * @author Andrea Burattin
 */
public class SimulationConfiguration {

	private boolean useMultithreading = true;
	private int maximumLoopCycles = 3;
	private String caseIdPattern = "case_%d";
	private String newLogName = "tmp-log";
	private int numberOfTraces;
	private NoiseConfiguration noise;
	
	/**
	 * Basic class constructor. The only required parameter is the number of
	 * traces to simulate. For all other parameters, standard values are
	 * available
	 * 
	 * @param numberOfTraces the number of traces to simulate
	 */
	public SimulationConfiguration(int numberOfTraces) {
		this(numberOfTraces, NoiseConfiguration.BASIC_VALUES);
	}
	
	/**
	 * Basic class constructor. It requires the number of traces to simulate and
	 * the noise configuration.
	 * 
	 * @param numberOfTraces the number of traces to simulate
	 * @param noise the noise configuration for the simulation
	 */
	public SimulationConfiguration(int numberOfTraces, NoiseConfiguration noise) {
		this.numberOfTraces = numberOfTraces;
		this.noise = noise;
	}
	
	/**
	 * This method returns the maximum number of cycles repetitions allowed for
	 * each process loop
	 * 
	 * @return the maximum number of loops
	 */
	public int getMaximumLoopCycles() {
		return maximumLoopCycles;
	}
	
	/**
	 * This method sets the maximum number of cycles repetitions allowed for
	 * each process loop
	 * 
	 * @param maximumLoopCycles the maximum number of loops
	 */
	public void setMaximumLoopCycles(int maximumLoopCycles) {
		this.maximumLoopCycles = maximumLoopCycles;
	}
	
	/**
	 * This method returns the number of traces to generate
	 * 
	 * @return the number of traces
	 */
	public int getNumberOfTraces() {
		return numberOfTraces;
	}
	
	/**
	 * This method returns the noise configuration for this simulation
	 * 
	 * @return the noise configuration
	 */
	public NoiseConfiguration getNoiseConfiguration() {
		return noise;
	}
	
	/**
	 * This method sets the number of traces to generate
	 * 
	 * @param numberOfTraces the number of traces
	 */
	public void setNumberOfTraces(int numberOfTraces) {
		this.numberOfTraces = numberOfTraces;
	}
	
	/**
	 * This method returns the name of the new log
	 * 
	 * @return the name of the new log
	 */
	public String getNewLogName() {
		return newLogName;
	}
	
	/**
	 * This method sets the name of the new log
	 * 
	 * @param newLogName the name of the new log
	 */
	public void setNewLogName(String newLogName) {
		this.newLogName = newLogName;
	}
	
	/**
	 * This method returns whether the simulation engine is supposed to run in
	 * a multithreading fashion
	 * 
	 * @return <tt>true</tt> if the engine can use multithreading,
	 * <tt>false</tt> otherwise
	 */
	public boolean useMultithreading() {
		return useMultithreading;
	}
	
	/**
	 * This method sets whether the simulation engine is supposed to run in a
	 * multithreading fashion
	 * 
	 * @param useMultithreading whether the simulation can use multithreading
	 */
	public void setMultithreadingUsage(boolean useMultithreading) {
		this.useMultithreading = useMultithreading;
	}

	/**
	 * This method returns the string pattern for the case id
	 * 
	 * @return the case id pattern
	 */
	public String getCaseIdPattern() {
		return caseIdPattern;
	}

	/**
	 * This method sets the string pattern for the case id. The string is
	 * supposed to contain one decimal flag (<tt>%d</tt>).
	 * 
	 * @param caseIdPattern the pattern to set
	 * @see String#format(String, Object...)
	 */
	public void setCaseIdPattern(String caseIdPattern) {
		this.caseIdPattern = caseIdPattern;
	}
	
	/**
	 * This method sets the noise configuration for the current simulation.
	 * 
	 * @param noise the noise configuration
	 */
	public void setNoiseConfiguration(NoiseConfiguration noise) {
		this.noise = noise;
	}
}
