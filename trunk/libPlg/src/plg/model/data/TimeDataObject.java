package plg.model.data;

import java.util.Random;

import plg.exceptions.InvalidDataObject;
import plg.model.activity.Activity;

/**
 * This class describes a {@link DataObject} that refers to timing information.
 * Specifically, when associated to an activity, it describes the typical
 * duration of the given task.
 * 
 * It is possible to associate only one time data object for the same activity.
 * 
 * The duration of an activity will be a random value (uniformly distributed)
 * between the minimum and the maximum number of seconds specified.
 * 
 * @author Andrea Burattin
 */
public class TimeDataObject extends DataObject {

	private static Random randomGenerator = new Random();
	private Integer minDuration = 0;
	private Integer maxDuration = 0;
	
	/**
	 * Class constructor that build a new time data object associated to a given
	 * activity.
	 * 
	 * @param objectOwner the activity associated to this time data object.
	 * There can be only one time data object for one activity.
	 * @param minDuration the minimum number of seconds required to perform
	 * the given activity
	 * @param maxDuration the maximum number of seconds required to perform
	 * the given activity
	 * @throws InvalidDataObject this exception is thrown when there is already
	 * a time data object associated to the given activity
	 */
	public TimeDataObject(Activity objectOwner, Integer minDuration, Integer maxDuration) throws InvalidDataObject {
		super(objectOwner);
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		
		for (DataObject d : objectOwner.getDataObjects()) {
			if (d instanceof TimeDataObject && !d.equals(this)) {
				objectOwner.getDataObjects().remove(this);
				throw new InvalidDataObject("Activity `" + objectOwner + "'" +
					" has already a TimeDataObject.");
			}
		}
		
		generateInstanceDuration();
	}
	
	/**
	 * This method returns the duration of the current activity instance.
	 * Different invocations of this method may return different values.
	 * 
	 * @return the duration (in seconds) of the current instance
	 */
	public void generateInstanceDuration() {
		setValue(minDuration + randomGenerator.nextInt(maxDuration - minDuration));
	}
	
	@Override
	public Integer getValue() {
		return (Integer) super.getValue();
	}
}
