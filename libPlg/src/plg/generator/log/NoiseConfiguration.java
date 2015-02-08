package plg.generator.log;

import java.util.HashMap;
import java.util.Map;

import plg.utils.Pair;

/**
 * This class describes the parameters of the noise generator. With this class
 * the user can control how the noise is added into the generated traces.
 * 
 * <p>
 * The actual noise types are reported into the {@link NOISE_TYPE} enumeration.
 * 
 * @author Andrea Burattin
 */
public class NoiseConfiguration {

	/**
	 * This enumeration describes the set of all types of noise.
	 * 
	 * <p>
	 * Reference: <br />
	 * [Gunter09] Günther, Christian W. (2009). <em>Process mining in Flexible
	 * Environments</em>. PhD Thesis, TU Eindhoven.
	 */
	public static enum NOISE_TYPE {
		/**
		 * This error consists in an integer attribute with an unexpected value
		 */
		DATA_INTEGER,
		/**
		 * This error consists in a string attribute with an unexpected value
		 */
		DATA_STRING,
		/**
		 * This error consists in an activity with an unexpected name
		 */
		ACTIVITY_NAME,
		/**
		 * This error corresponds to the "missing head error" reported in
		 * [Gunter09].
		 */
		TRACE_MISSING_HEAD,
		/**
		 * This error corresponds to the "missing tail error" reported in:
		 * [Gunter09].
		 */
		TRACE_MISSING_TAIL,
		/**
		 * This error corresponds to the "missing episode error" reported in:
		 * [Gunter09].
		 */
		TRACE_MISSING_EPISODE,
		/**
		 * This error corresponds to the "perturbed order error" reported in:
		 * [Gunter09].
		 */
		TRACE_PERTURBED_ORDER,
		/**
		 * This error corresponds to the "additional event error" reported in:
		 * [Gunter09].
		 */
		TRACE_DOUBLE_EVENT,
		/**
		 * This error corresponds to the "alien event error" reported in:
		 * [Gunter09].
		 */
		TRACE_ALIEN_EVENT
	}
	
	/* Class' private fields */
	private Map<NOISE_TYPE, Pair<Double, ?>> config = new HashMap<NOISE_TYPE, Pair<Double, ?>>();
	
	/**
	 * Sets the noise for integer data attributes. The modified value will be
	 * summed to a random value from the interval <tt>[-deltaNewValue, 
	 * deltaNewValue]</tt> (can also be 0).
	 * 
	 * @param probability the probability that an integer attribute is noisy
	 * @param deltaNewValue the 
	 */
	public void setIntegerDataNoise(double probability, int deltaNewValue) {
		config.put(NOISE_TYPE.DATA_INTEGER, new Pair<Double, Integer>(probability, deltaNewValue));
	}
	
	/**
	 * Sets the noise for string data attributes.
	 * 
	 * @param probability the probability that a string attribute is noisy
	 */
	public void setStringDataNoise(double probability) {
		config.put(NOISE_TYPE.DATA_STRING, new Pair<Double, Object>(probability, null));
	}
	
	/**
	 * Sets the noise for an activity name.
	 * 
	 * @param probability the probability that an activity name is noisy
	 */
	public void setActivityNameNoise(double probability) {
		config.put(NOISE_TYPE.ACTIVITY_NAME, new Pair<Double, Object>(probability, null));
	}
	
	/**
	 * Sets the noise for a trace missing its head.
	 * 
	 * @param probability the probability that a trace is missing its head
	 * @param headSize the size of a "trace head"
	 */
	public void setTraceMissingHeadNoise(double probability, int headSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_HEAD, new Pair<Double, Integer>(probability, headSize));
	}
	
	/**
	 * Sets the noise for a trace missing its head.
	 * 
	 * @param probability the probability that a trace is missing its tail
	 * @param headSize the size of a "trace tail"
	 */
	public void setTraceMissingTailNoise(double probability, int tailSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_TAIL, new Pair<Double, Integer>(probability, tailSize));
	}
	
	/**
	 * Sets the noise for a trace missing an episode.
	 * 
	 * @param probability the probability that a trace is missing an episode
	 * @param headSize the size of a "trace episode"
	 */
	public void setTraceMissingEpisodeNoise(double probability, int episodeSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_EPISODE, new Pair<Double, Integer>(probability, episodeSize));
	}
	
	/**
	 * Sets the noise for perturbed events order.
	 * 
	 * @param probability the probability that a trace contains some perturbed
	 * events order
	 */
	public void setPerturbedOrderNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_PERTURBED_ORDER, new Pair<Double, Object>(probability, null));
	}
	
	/**
	 * Sets the noise for a doubled event.
	 * 
	 * @param probability the probability that an trace contains some doubled
	 * events
	 */
	public void setDoubleEventNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_DOUBLE_EVENT, new Pair<Double, Object>(probability, null));
	}
	
	/**
	 * Sets the noise for an alien event.
	 * 
	 * @param probability the probability that an alien event is inserted
	 */
	public void setAlienEventNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_ALIEN_EVENT, new Pair<Double, Object>(probability, null));
	}
}
