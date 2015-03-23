package plg.generator.log.noise;

import java.math.BigInteger;

import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import plg.generator.log.noise.NoiseConfiguration.NOISE_TYPE;
import plg.model.data.DataObject;
import plg.model.data.INoiseSensitiveDataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.utils.Random;
import plg.utils.XLogHelper;

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
	 * <strong>Attention:</strong> only one of these possible noise types could
	 * be applied on the same trace.
	 * 
	 * @param trace the trace where the noise will be applied.
	 * <strong>Warning:</strong> this method modifies the provided trace.
	 */
	public void applyTraceNoise(XTrace trace) {
		if (noise.generateNoise(NOISE_TYPE.TRACE_ALIEN_EVENT)) {
			generateTraceAlientEventNoise(trace);
		} else if (noise.generateNoise(NOISE_TYPE.TRACE_DOUBLE_EVENT)) {
			generateTraceDoubleEventNoise(trace);
		} else if (noise.generateNoise(NOISE_TYPE.TRACE_MISSING_EPISODE)) {
			generateTraceMissingEpisodeNoise(trace);
		} else if (noise.generateNoise(NOISE_TYPE.TRACE_MISSING_HEAD)) {
			generateTraceMissingHeadNoise(trace);
		} else if (noise.generateNoise(NOISE_TYPE.TRACE_MISSING_TAIL)) {
			generateTraceMissingTailNoise(trace);
		} else if (noise.generateNoise(NOISE_TYPE.TRACE_PERTURBED_ORDER)) {
			generateTracePerturbedOrderNoise(trace);
		}
	}

	/**
	 * This method generates some string noise starting from the provided
	 * parameter. This method <strong>modifies</strong> the attribute in order
	 * to alter its value.
	 * 
	 * <p> Several invocation of this method could generate different noises.
	 * 
	 * @see NOISE_TYPE#DATA_STRING
	 * @param dataObj the data object subject of the noise
	 */
	public void applyStringDataNoise(DataObject dataObj) {
		if ((dataObj instanceof StringDataObject) || !(dataObj instanceof IntegerDataObject)) {
			// String orginalValue = (String) dataObj.getOriginalValue();
			if (noise.generateNoise(NOISE_TYPE.DATA_STRING)) {
				dataObj.setValue(new BigInteger(65, Random.RANDOM).toString(32));
			}
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
		if (noise.generateNoise(NOISE_TYPE.DATA_STRING)) {
			int delta = noise.getIntegerDataNoiseDelta();
			dataObj.setValue(originalValue + Random.nextInt(-delta, delta));
		}
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
		if (noise.generateNoise(NOISE_TYPE.ACTIVITY_NAME)) {
			return new BigInteger(65, Random.RANDOM).toString(32);
		}
		return name;
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_ALIEN_EVENT}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTraceAlientEventNoise(XTrace trace) {
		if (trace.size() == 0) {
			XLogHelper.insertEvent(trace, new BigInteger(65, Random.RANDOM).toString(32));
		} else {
			int insertAfterEvent = Random.nextInt(0, trace.size() - 1);
			XAttributeTimestamp t = (XAttributeTimestamp) trace.get(insertAfterEvent).getAttributes().get("time:timestamp");
			XLogHelper.insertEvent(trace, new BigInteger(65, Random.RANDOM).toString(32), t.getValue());
		}
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_DOUBLE_EVENT}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTraceDoubleEventNoise(XTrace trace) {
		if (trace.size() > 0) {
			int indexToDouble = Random.nextInt(0, trace.size() - 1);
			XEvent e = XLogHelper.clone(trace.get(indexToDouble));
			trace.insertOrdered(e);
		}
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_MISSING_EPISODE}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTraceMissingEpisodeNoise(XTrace trace) {
		if (trace.size() > 0) {
			int firstPosition = Random.nextInt(0, trace.size() - 1);
			int episodeSize = Random.nextInt(1, Math.min(noise.getTraceMissingEpisodeSize(), trace.size() - firstPosition));
			for (int i = 0; i < episodeSize; i++) {
				trace.remove(firstPosition);
			}
		}
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_MISSING_HEAD}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTraceMissingHeadNoise(XTrace trace) {
		if (trace.size() > 0) {
			int headSize = Random.nextInt(1, Math.min(noise.getTraceMissingHeadSize(), trace.size()));
			for (int i = 0; i < headSize; i++) {
				trace.remove(0);
			}
		}
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_MISSING_TAIL}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTraceMissingTailNoise(XTrace trace) {
		if (trace.size() > 0) {
			int tailSize = Random.nextInt(1, Math.min(noise.getTraceMissingTailSize(), trace.size()));
			for (int i = 0; i < tailSize; i++) {
				trace.remove(trace.size() - 1);
			}
		}
	}
	
	/**
	 * This method is responsible to generate the noise for
	 * {@link NOISE_TYPE#TRACE_PERTURBED_ORDER}.
	 * 
	 * @param trace the trace that undergo the noise
	 */
	protected void generateTracePerturbedOrderNoise(XTrace trace) {
		if (trace.size() > 1) {
			int firstIndex = Random.nextInt(0, trace.size() - 2);
			int secondIndex = Random.nextInt(0, trace.size() - 1);
			while (firstIndex == secondIndex) {
				secondIndex = Random.nextInt(0, trace.size() - 1);
			}
			XEvent e1 = trace.get(firstIndex);
			XEvent e2 = trace.get(secondIndex);
			trace.set(firstIndex, e2);
			trace.set(secondIndex, e1);
		}
	}
}
