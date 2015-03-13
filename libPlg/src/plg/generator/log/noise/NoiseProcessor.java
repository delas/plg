package plg.generator.log.noise;

import java.math.BigInteger;
import java.util.Random;

import org.deckfour.xes.model.XTrace;

import plg.generator.log.noise.NoiseConfiguration.NOISE_TYPE;
import plg.model.data.INoiseSensitiveDataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;

/**
 * This class contains the noise processor. The noise processor is in charge of
 * the application of the noise to the different process components.
 * 
 * <p> It is important to note that each method of this class is going to
 * apply the noise only if necessary, according to the
 * {@link NoiseConfiguration} provided.
 * 
 * @author Andrea Burattin
 */
public class NoiseProcessor {

	private NoiseConfiguration noise;
	
	/**
	 * Basic class constructor
	 * 
	 * @param noise the noise configuration to use
	 */
	public NoiseProcessor(NoiseConfiguration noise) {
		this.noise = noise;
	}

	/**
	 * This method <strong>modifies</strong> the provided trace adding the
	 * trace-level noise.
	 * 
	 * <p> The trace-level noise takes into account:
	 * <ul>
	 * 	<li>{@link NOISE_TYPE#TRACE_ALIEN_EVENT}</li>
	 * 	<li>{@link NOISE_TYPE#TRACE_DOUBLE_EVENT}</li>
	 * 	<li>{@link NOISE_TYPE#TRACE_MISSING_EPISODE}</li>
	 * 	<li>{@link NOISE_TYPE#TRACE_MISSING_HEAD}</li>
	 * 	<li>{@link NOISE_TYPE#TRACE_MISSING_TAIL}</li>
	 * 	<li>{@link NOISE_TYPE#TRACE_PERTURBED_ORDER}</li>
	 * </ul>
	 * 
	 * @param trace the trace where the noise will be applied.
	 * <strong>Warning:</strong> this method modifies the provided trace.
	 */
	public void applyTraceNoise(XTrace trace) {
		
	}

	/**
	 * This method generates some string noise starting from the provided
	 * parameter. This method <strong>modifies</strong> the attribute in order
	 * to alter its value.
	 * 
	 * <p> This method uses the
	 * {@link INoiseSensitiveDataObject#getOriginalValue()} as starting point
	 * for the generation of noise. Therefore, several invocation of this method
	 * could generate different noises.
	 * 
	 * @see NOISE_TYPE#DATA_STRING
	 * @param dataObj the data object subject of the noise
	 */
	public void applyStringDataNoise(StringDataObject dataObj) {
		// String orginalValue = (String) dataObj.getOriginalValue();
		if (noise.generateNoise(NOISE_TYPE.DATA_STRING)) {
			dataObj.setValue(new BigInteger(65, new Random()).toString(32));
		}
	}

	/**
	 * This method generates some string noise starting from the provided
	 * parameter. This method <strong>modifies</strong> the attribute in order
	 * to alter its value.
	 * 
	 * <p> This method uses the
	 * {@link INoiseSensitiveDataObject#getOriginalValue()} as starting point
	 * for the generation of noise. Therefore, several invocation of this method
	 * could generate different noises.
	 * 
	 * @see NOISE_TYPE#DATA_INTEGER
	 * @param dataObj the data object subject of the noise
	 */
	public void applyIntegerDataNoise(IntegerDataObject dataObj) {
		Integer originalValue = (Integer) dataObj.getOriginalValue();
	}

	/**
	 * This method generates a new string representing the activity name, after
	 * the application of some noise.
	 * 
	 * @see NOISE_TYPE#ACTIVITY_NAME
	 * @param name the original activity name
	 * @return the noised activity name
	 */
	public String generateActivityNameNoise(String name) {
		return name;
	}
}
