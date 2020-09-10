package plg.utils;

import cern.jet.random.engine.RandomEngine;

/**
 * This class collects several utilities methods related to the generation of
 * random stuff.
 * 
 * @author Andrea Burattin
 */
public class Random {

	/**
	 * This is a random number generator
	 */
	public static final RandomEngine RANDOM_GENERATOR = RandomEngine.makeDefault();
	
	/**
	 * This is a standard Java-based random generator
	 */
	public static final java.util.Random RANDOM = new java.util.Random();
	
	/**
	 * This method generates boolean values with respect to the given
	 * weight.
	 * 
	 * @param successWeight the weight of success
	 * @return <tt>true</tt> with weight <tt>successPercent/100</tt>, <tt>false</tt> otherwise
	 */
	public static boolean randomFromWeight(double successWeight) {
		return (successWeight > RANDOM_GENERATOR.nextDouble());
	}
	
	/**
	 * This method generates a new random number with respect to the current
	 * weight distribution and in a particular range.
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
}
