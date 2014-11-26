package plg.generator.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import plg.utils.Pair;
import plg.utils.SetUtils;
import cern.jet.random.engine.RandomEngine;

/**
 * This class describes the parameters of the process generator. With this class
 * the user can control the process randomization.
 *
 * @author Andrea Burattin
 */
public class RandomizationConfiguration {

	/** This is a random number generator */
	public static final RandomEngine RANDOM_GENERATOR = RandomEngine.makeDefault();
	/** This is a test configuration with basic random values */
	public static final RandomizationConfiguration BASIC_VALUES = new RandomizationConfiguration(
			5, // max AND branches
			5, // max XOR branches
			0.1, // loop probability
			0.2, // single activity probability
			0.1, // skip probability
			0.7, // sequence probability
			0.3, // AND probability
			0.3, // XOR probability
			3 // maximum depth
		);
	
	/** The maximum number of XOR branches (if wrong value is provided) */
	public static final int MAX_XOR_BRANCHES = 4;
	/** The maximum number of AND branches (if wrong value is provided) */
	public static final int MAX_AND_BRANCHES = 4;
	/** The minimum number of XOR branches */
	public static final int MIN_XOR_BRANCHES = 2;
	/** The minimum number of AND branches */
	public static final int MIN_AND_BRANCHES = 2;
	
	/* Class' private fields */
	private int ANDBranches;
	private int XORBranches;
	private Map<RANDOMIZATION_PATTERN, Double> probabilites;
	private int maxDepth;
	
	/**
	 * This enumeration describes the set of all possible patterns
	 */
	public static enum RANDOMIZATION_PATTERN {
		/** Single activity pattern */
		SINGLE_ACTIVITY,
		/** Sequence activities pattern */
		SEQUENCE,
		/** AND pattern */
		PARALLEL_EXECUTION,
		/** XOR pattern */
		MUTUAL_EXCLUSION,
		/** XOR pattern */
		LOOP,
		/** Empty: no activity pattern */
		SKIP
	}
	
	/**
	 * This method generates boolean values with respect to the given
	 * probability.
	 * 
	 * @param successProbability the probability of success
	 * @return <tt>true</tt> with probability <tt>successPercent/100</tt>, <tt>false</tt> otherwise
	 */
	public static boolean randomFromProbability(double successProbability) {
		return (successProbability > RANDOM_GENERATOR.nextDouble());
	}
	
	/**
	 * This method generates a new random number with respect to the current
	 * probability distribution and in a particular range.
	 *
	 * @param min the minimal value (excluded)
	 * @param max the maximal value (excluded)
	 * @return the random integer
	 */
	public static Integer nextInt(int min, int max) {
		Float v = RANDOM_GENERATOR.nextFloat();
		int range = max - min;
		range = Math.round(range * v) + min;
		return range;
	}
	
	/**
	 * This constructor builds a parameters configuration all parameters are
	 * required.
	 * 
	 * @param ANDBranches the maximum number of AND branches (must be > 1)
	 * @param XORBranches the maximum number of XOR branches (must be > 1)
	 * @param loopProbability the loop probability (must be in [0, 1])
	 * @param singleActivityProbability the probability of single activity (must
	 * be in <tt>[0,1]</tt>)
	 * @param skupProbability the probability of a skip (must be in
	 * <tt>[0,1]</tt>)
	 * @param sequenceProbability he probability of sequence activity (must be
	 * in <tt>[0,1]</tt>)
	 * @param ANDProbability the probability of AND split-join (must be in
	 * <tt>[0,1]</tt>)
	 * @param XORProbability the probability of XOR split-join (must be in
	 * <tt>[0,1]</tt>)
	 * @param emptyPercent the probability of an empty pattern (must be in
	 * <tt>[0,1]</tt>)
	 * @param maxDepth the maximum network deep
	 */
	public RandomizationConfiguration(int ANDBranches, int XORBranches,
			double loopProbability, double singleActivityProbability, double skipProbability,
			double sequenceProbability, double ANDProbability, double XORProbability,
			int maxDepth) {
		this.probabilites = new HashMap<RandomizationConfiguration.RANDOMIZATION_PATTERN, Double>();
		
		setAndBranches(ANDBranches);
		setXorBranches(XORBranches);
		setLoopProbability(loopProbability);
		setSingleActivityProbability(singleActivityProbability);
		setSkipProbability(skipProbability);
		setSequenceProbability(sequenceProbability);
		setANDProbability(ANDProbability);
		setXORProbability(XORProbability);
		setDepth(maxDepth);
	}
	
	/**
	 * Set the AND branches parameter
	 * 
	 * @param andBranches the maximum number of AND branches
	 */
	public void setAndBranches(int andBranches) {
		ANDBranches = (andBranches > 1)? andBranches : MAX_AND_BRANCHES;
	}
	
	/**
	 * Get the AND branches parameter
	 * 
	 * @return the maximum number of AND branches
	 */
	public int getAndBranches() {
		return ANDBranches;
	}
	
	/**
	 * Set the XOR branches parameter
	 * 
	 * @param xorBranches the maximum number of XOR branches
	 */
	public void setXorBranches(int xorBranches) {
		XORBranches = (xorBranches > 1)? xorBranches : MAX_XOR_BRANCHES;
	}
	
	/**
	 * Get the XOR branches parameter
	 * 
	 * @return the maximum number of XOR branches
	 */
	public int getXorBranches() {
		return XORBranches;
	}
	
	/**
	 * Set the loop probability parameter
	 * 
	 * @param loopProbability
	 */
	public void setLoopProbability(double loopProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.LOOP,
				(loopProbability >= 0.0 && loopProbability <= 1.0)?
					loopProbability :
					BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.LOOP));
	}
	
	/**
	 * Get the current value of the loop probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getLoopProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.LOOP);
	}
	
	/**
	 * Set the single activity probability parameter
	 * 
	 * @param singleActivityProbability
	 */
	public void setSingleActivityProbability(double singleActivityProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.SINGLE_ACTIVITY,
				(singleActivityProbability >= 0.0 && singleActivityProbability <= 1.0)?
						singleActivityProbability :
						BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.SINGLE_ACTIVITY));
	}
	
	/**
	 * Get the current value of the single activity probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getSingleActivityProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.SINGLE_ACTIVITY);
	}
	
	/**
	 * Set the skip probability parameter
	 * 
	 * @param skipProbability
	 */
	public void setSkipProbability(double skipProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.SKIP,
				(skipProbability >= 0.0 && skipProbability <= 1.0)?
						skipProbability :
							BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.SKIP));
	}
	
	/**
	 * Get the current value of the skip probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getSkipProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.SKIP);
	}
	
	/**
	 * Set the sequence probability parameter
	 * 
	 * @param sequenceProbability
	 */
	public void setSequenceProbability(double sequenceProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.SEQUENCE,
				(sequenceProbability >= 0.0 && sequenceProbability <= 1.0)?
						sequenceProbability :
							BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.SEQUENCE));
	}
	
	/**
	 * Get the current value of the sequence probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getSequenceProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.SEQUENCE);
	}
	
	/**
	 * Set the AND probability parameter
	 * 
	 * @param ANDProbability
	 */
	public void setANDProbability(double ANDProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.PARALLEL_EXECUTION,
				(ANDProbability >= 0.0 && ANDProbability <= 1.0)?
						ANDProbability :
							BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.PARALLEL_EXECUTION));
	}
	
	/**
	 * Get the current value of the AND probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getANDProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.PARALLEL_EXECUTION);
	}
	
	/**
	 * Set the XOR probability parameter
	 * 
	 * @param XORProbability
	 */
	public void setXORProbability(double XORProbability) {
		probabilites.put(RANDOMIZATION_PATTERN.MUTUAL_EXCLUSION,
				(XORProbability >= 0.0 && XORProbability <= 1.0)?
						XORProbability :
							BASIC_VALUES.probabilites.get(RANDOMIZATION_PATTERN.MUTUAL_EXCLUSION));
	}
	
	/**
	 * Get the current value of the XOR probability parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getXORProbability() {
		return probabilites.get(RANDOMIZATION_PATTERN.MUTUAL_EXCLUSION);
	}
	
	/**
	 * Set the maximum depth parameter
	 * 
	 * @param depth
	 */
	public void setDepth(int depth) {
		this.maxDepth = (depth > 0)? depth : BASIC_VALUES.maxDepth;
	}
	
	/**
	 * Get the current value of the maximum depth parameter
	 * 
	 * @return the value of the parameter
	 */
	public int getMaximumDepth() {
		return maxDepth;
	}
	
	/**
	 * This method return the number of AND branches to generate, according to
	 * the given probability
	 * 
	 * @return the number of AND branches to generate
	 */
	public int getRandomANDBranches() {
		return nextInt(MIN_AND_BRANCHES, getAndBranches() - 1);
	}
	
	
	/**
	 * This method return the number of XOR branches to generate, according to
	 * the given probability
	 * 
	 * @return the number of XOR branches to generate
	 */
	public int getRandomXORBranches() {
		return nextInt(MIN_XOR_BRANCHES, getXorBranches() - 1);
	}
	
	
	/**
	 * This method is used for the definition of the presence of a loop
	 * 
	 * @return true if a loop must be inserted, false otherwise
	 */
	public boolean getLoopPresence() {
		return randomFromProbability(getLoopProbability());
	}
	
	/**
	 * This method returns a pattern, randomly selected between:
	 * <ul>
	 * 	<li>Single activity</li>
	 * 	<li>Sequence pattern</li>
	 * 	<li>AND pattern</li>
	 * 	<li>XOR pattern</li>
	 * 	<li>Skip (according to the parameter)</li>
	 * 	<li>Loop (according to the parameter)</li>
	 * </ul>
	 * 
	 * <p> The selection is done according to the given probabilities
	 * 
	 * @param canLoop specifies whether the pattern can be a loop
	 * @param canSkip specifies whether the pattern can be a skip
	 * @return the random pattern
	 */
	public RANDOMIZATION_PATTERN getRandomPattern(boolean canLoop, boolean canSkip) {
		Set<RANDOMIZATION_PATTERN> options = new HashSet<RANDOMIZATION_PATTERN>();
		options.add(RANDOMIZATION_PATTERN.SINGLE_ACTIVITY);
		options.add(RANDOMIZATION_PATTERN.SEQUENCE);
		options.add(RANDOMIZATION_PATTERN.PARALLEL_EXECUTION);
		options.add(RANDOMIZATION_PATTERN.MUTUAL_EXCLUSION);
		if (canSkip) {
			options.add(RANDOMIZATION_PATTERN.SKIP);
		}
		if (canLoop) {
			options.add(RANDOMIZATION_PATTERN.LOOP);
		}
		
		return getRandomPattern(options.toArray(new RANDOMIZATION_PATTERN[options.size()]));
	}
	
	/**
	 * This method returns a random pattern, selected between the provided ones
	 * 
	 * <p> The selection is done according to the given probabilities
	 * 
	 * @param patterns the patterns to choose from
	 * @return the random pattern
	 */
	public RANDOMIZATION_PATTERN getRandomPattern(RANDOMIZATION_PATTERN ... patterns) {
		Set<Pair<RANDOMIZATION_PATTERN, Double>> options = new HashSet<Pair<RANDOMIZATION_PATTERN, Double>>();
		for(RANDOMIZATION_PATTERN p : patterns) {
			options.add(new Pair<RANDOMIZATION_PATTERN, Double>(p, probabilites.get(p)));
		}
		return SetUtils.getRandomWeighted(options);
	}
}
