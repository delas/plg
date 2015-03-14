package plg.generator.log.noise;

import java.util.HashMap;
import java.util.Map;

import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.utils.Pair;
import plg.utils.Random;

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
	 * [Gunter09] Guenther, Christian W. (2009). <em>Process mining in Flexible
	 * Environments</em>. PhD Thesis, TU Eindhoven.
	 */
	public static enum NOISE_TYPE {
		/**
		 * This error consists in an integer attribute with an unexpected value.
		 * This noise is applied only to {@link IntegerDataObject} (i.e.,
		 * generated) attributes.
		 */
		DATA_INTEGER,
		/**
		 * This error consists in a string attribute with an unexpected value.
		 * This noise is applied only to {@link StringDataObject} (i.e.,
		 * generated) attributes.
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
	
	/** This is a test configuration with basic random values */
	public static final NoiseConfiguration BASIC_VALUES = new NoiseConfiguration(
			0.1, // integer data noise
			5, // delta new value
			0.1, // string data noise
			0.1, // activity name noise
			0.01, // trace missing head noise
			1, // head size
			0.01, // missing tail noise
			1, // tail size
			0.01, // missing episode noise
			1, // episode size
			0.01, // perturbed order noise
			0.01, // double event noise
			0.01 // alien event noise
		);
	
	/* Class' private fields */
	private Map<NOISE_TYPE, Pair<Double, ?>> config;
	
	/**
	 * This constructor builds a parameters configuration all parameters are
	 * required.
	 * 
	 * @param integerDataNoise
	 * @param deltaNewValue
	 * @param stringDataNoise
	 * @param activityNameNoise
	 * @param traceMissingHeadNoise
	 * @param headSize
	 * @param missingTailNoise
	 * @param tailSize
	 * @param missingEpisodeNoise
	 * @param episodeSize
	 * @param perturbedOrderNoise
	 * @param doubleEventNoise
	 * @param alienEventNoise
	 */
	public NoiseConfiguration(double integerDataNoise, int deltaNewValue,
			double stringDataNoise, double activityNameNoise,
			double traceMissingHeadNoise, int headSize,
			double missingTailNoise, int tailSize,
			double missingEpisodeNoise, int episodeSize,
			double perturbedOrderNoise, double doubleEventNoise,
			double alienEventNoise) {
		this.config = new HashMap<NOISE_TYPE, Pair<Double, ?>>();
		
		setIntegerDataNoise(integerDataNoise, deltaNewValue);
		setStringDataNoise(stringDataNoise);
		setActivityNameNoise(activityNameNoise);
		setTraceMissingHeadNoise(traceMissingHeadNoise, headSize);
		setTraceMissingTailNoise(missingTailNoise, tailSize);
		setTraceMissingEpisodeNoise(missingEpisodeNoise, episodeSize);
		setPerturbedOrderNoise(perturbedOrderNoise);
		setDoubleEventNoise(doubleEventNoise);
		setAlienEventNoise(alienEventNoise);
	}
	
	/**
	 * Sets the noise for integer data attributes. The modified value will be
	 * summed to a random value from the interval <tt>[-deltaNewValue, 
	 * deltaNewValue]</tt> (can also be 0).
	 * 
	 * @param probability the probability that an integer attribute is noisy
	 * @param deltaNewValue the range for the modification of the attribute
	 * @return the object after the modification
	 */
	public NoiseConfiguration setIntegerDataNoise(double probability, int deltaNewValue) {
		config.put(NOISE_TYPE.DATA_INTEGER, new Pair<Double, Integer>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.DATA_INTEGER).getFirst(),
				deltaNewValue));
		return this;
	}
	
	/**
	 * Get the current value of the integer data noise parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getIntegerDataNoiseProbability() {
		return config.get(NOISE_TYPE.DATA_INTEGER).getFirst();
	}
	
	/**
	 * Get the current value of the range for the modification of the attribute.
	 * 
	 * @return the value of the delta
	 */
	public int getIntegerDataNoiseDelta() {
		return (int) config.get(NOISE_TYPE.DATA_INTEGER).getSecond();
	}
	
	/**
	 * Sets the noise for string data attributes.
	 * 
	 * @param probability the probability that a string attribute is noisy
	 * @return the object after the modification
	 */
	public NoiseConfiguration setStringDataNoise(double probability) {
		config.put(NOISE_TYPE.DATA_STRING, new Pair<Double, Object>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.DATA_STRING).getFirst(),
				null));
		return this;
	}
	
	/**
	 * Get the current value of the string data noise parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getStringDataNoiseProbability() {
		return config.get(NOISE_TYPE.DATA_STRING).getFirst();
	}
	
	/**
	 * Sets the noise for an activity name.
	 * 
	 * @param probability the probability that an activity name is noisy
	 * @return the object after the modification
	 */
	public NoiseConfiguration setActivityNameNoise(double probability) {
		config.put(NOISE_TYPE.ACTIVITY_NAME, new Pair<Double, Object>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.ACTIVITY_NAME).getFirst(),
				null));
		return this;
	}
	
	/**
	 * Get the current value of the activity name parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getActivityNameNoiseProbability() {
		return config.get(NOISE_TYPE.ACTIVITY_NAME).getFirst();
	}
	
	/**
	 * Sets the noise for a trace missing its head.
	 * 
	 * @param probability the probability that a trace is missing its head
	 * @param headSize the size of a "trace head"
	 * @return the object after the modification
	 */
	public NoiseConfiguration setTraceMissingHeadNoise(double probability, int headSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_HEAD, new Pair<Double, Integer>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_MISSING_HEAD).getFirst(),
				headSize));
		return this;
	}
	
	/**
	 * Get the current value of the missing noise parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getTraceMissingHeadNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_MISSING_HEAD).getFirst();
	}
	
	/**
	 * Sets the noise for a trace missing its head.
	 * 
	 * @param probability the probability that a trace is missing its tail
	 * @param headSize the size of a "trace tail"
	 * @return the object after the modification
	 */
	public NoiseConfiguration setTraceMissingTailNoise(double probability, int tailSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_TAIL, new Pair<Double, Integer>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_MISSING_TAIL).getFirst(),
				tailSize));
		return this;
	}
	
	/**
	 * Get the current value of the missing tail parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getTraceMissingTailNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_MISSING_TAIL).getFirst();
	}
	
	/**
	 * Sets the noise for a trace missing an episode.
	 * 
	 * @param probability the probability that a trace is missing an episode
	 * @param headSize the size of a "trace episode"
	 * @return the object after the modification
	 */
	public NoiseConfiguration setTraceMissingEpisodeNoise(double probability, int episodeSize) {
		config.put(NOISE_TYPE.TRACE_MISSING_EPISODE, new Pair<Double, Integer>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_MISSING_EPISODE).getFirst(),
				episodeSize));
		return this;
	}
	
	/**
	 * Get the current value of the missing episode parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getTraceMissingEpisodeNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_MISSING_EPISODE).getFirst();
	}
	
	/**
	 * Sets the noise for perturbed events order.
	 * 
	 * @param probability the probability that a trace contains some perturbed
	 * events order
	 * @return the object after the modification
	 */
	public NoiseConfiguration setPerturbedOrderNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_PERTURBED_ORDER, new Pair<Double, Object>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_PERTURBED_ORDER).getFirst(),
				null));
		return this;
	}
	
	/**
	 * Get the current value of the perturbed order parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getPerturbedOrderNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_PERTURBED_ORDER).getFirst();
	}
	
	/**
	 * Sets the noise for a doubled event.
	 * 
	 * @param probability the probability that an trace contains some doubled
	 * events
	 * @return the object after the modification
	 */
	public NoiseConfiguration setDoubleEventNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_DOUBLE_EVENT, new Pair<Double, Object>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_DOUBLE_EVENT).getFirst(),
				null));
		return this;
	}
	
	/**
	 * Get the current value of the double event parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getDoubleEventNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_DOUBLE_EVENT).getFirst();
	}
	
	/**
	 * Sets the noise for an alien event.
	 * 
	 * @param probability the probability that an alien event is inserted
	 * @return the object after the modification
	 */
	public NoiseConfiguration setAlienEventNoise(double probability) {
		config.put(NOISE_TYPE.TRACE_ALIEN_EVENT, new Pair<Double, Object>(
				(probability >= 0 && probability <= 1)?
						probability :
						BASIC_VALUES.config.get(NOISE_TYPE.TRACE_ALIEN_EVENT).getFirst(),
				null));
		return this;
	}
	
	/**
	 * Get the current value of the alien event parameter
	 * 
	 * @return the value of the parameter
	 */
	public double getAlienEventNoiseProbability() {
		return config.get(NOISE_TYPE.TRACE_ALIEN_EVENT).getFirst();
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public boolean generateNoise(NOISE_TYPE type) {
		return Random.randomFromWeight(config.get(type).getFirst());
	}
}