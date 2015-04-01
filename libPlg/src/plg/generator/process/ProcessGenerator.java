package plg.generator.process;

import java.math.BigInteger;
import java.util.Random;

import plg.generator.process.RandomizationConfiguration.RANDOMIZATION_PATTERN;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.event.EndEvent;
import plg.model.event.Event;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;
import plg.utils.Logger;
import plg.utils.SetUtils;

/**
 * This class contains the random generator of processes. Actually, this class
 * is responsible for the randomization of a process.
 * 
 * @author Andrea Burattin
 */
public class ProcessGenerator {

	/**
	 * This string contains the pattern for the generation of activities. The
	 * pattern requires one parameter, which will be replaced with a progressive
	 * letter, such as "A", ..., "Z", "AA", "AB", ...
	 */
	public static final String ACTIVITY_NAME_PATTERN = "Activity %s";
	
	/**
	 * This string contains the pattern for the generation of data objects. The
	 * pattern requires one parameter, which will be replaced with a progressive
	 * letter, such as "A", ..., "Z", "AA", "AB", ...
	 */
	public static final String DATA_OBJECT_NAME_PATTERN = "variable_%s";
	
	private Process process;
	private RandomizationConfiguration parameters;
	protected int generatedActivities = 0;
	protected int generatedDataObjects = 0;
	
	/**
	 * This public static method is the main interface for the process
	 * randomization. Specifically, this method adds to the provided process a
	 * control-flow structure, which starts from a {@link StartEvent}, and
	 * finishes into an {@link EndEvent}.
	 * 
	 * <p> If the provided process is not empty, the new control-flow is added
	 * to the existing process.
	 * 
	 * @param process the process to randomize
	 * @param parameters the randomization parameters to use
	 */
	public static void randomizeProcess(Process process, RandomizationConfiguration parameters) {
		new ProcessGenerator(process, parameters).begin();
	}
	
	/**
	 * Protected class constructor. This method is not publicly available since
	 * we would like to interact only through the
	 * {@link ProcessGenerator#randomizeProcess(Process)} method.
	 * 
	 * @param process the process to randomize
	 * @param parameters the randomization parameters to use
	 */
	protected ProcessGenerator(Process process, RandomizationConfiguration parameters) {
		this.process = process;
		this.parameters = parameters;
	}
	
	/**
	 * This method initialize the randomization process, first by adding start
	 * and end events, and then populating the internal structure.
	 */
	protected void begin() {
		Logger.instance().info("Starting process randomization");
		Event start = process.newStartEvent();
		Event end = process.newEndEvent();
		PatternFrame p = newInternalPattern(1, true, false);
		PatternFrame.connect(start, p).connect(end);
		Logger.instance().info("Process randomization complete");
	}
	
	/**
	 * This method generates a new internal pattern. The pattern to generate is
	 * randomly selected with respect to the provided random generation policy.
	 * 
	 * @param currentDepth the current depth of the generation
	 * @param canLoop specifies whether a loop is allowed here or not
	 * @param canSkip specifies whether the pattern can be a skip
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newInternalPattern(int currentDepth, boolean canLoop, boolean canSkip) {
		if (currentDepth <= parameters.getMaximumDepth()) {
			
			RANDOMIZATION_PATTERN nextAction = parameters.getRandomPattern(canLoop, canSkip);
			PatternFrame generatedFrame = null;
			
			switch (nextAction) {
			case SEQUENCE:
				generatedFrame = newSequence(currentDepth + 1, canLoop, canSkip);
				break;
			case PARALLEL_EXECUTION:
				generatedFrame = newAndBranches(currentDepth + 1, canLoop);
				break;
			case MUTUAL_EXCLUSION:
				generatedFrame = newXorBranches(currentDepth + 1, canLoop, canSkip);
				break;
			case LOOP:
				generatedFrame = newLoopBranch(currentDepth + 1);
				break;
			case SKIP:
				generatedFrame = null;
				break;
			default:
				generatedFrame = newActivity();
				break;
			}
			
			return generatedFrame;
			
		} else {
			if (canSkip) {
				RANDOMIZATION_PATTERN nextAction = parameters.getRandomPattern(RANDOMIZATION_PATTERN.SKIP, RANDOMIZATION_PATTERN.SINGLE_ACTIVITY);
				if (nextAction == RANDOMIZATION_PATTERN.SKIP) {
					Logger.instance().debug("Skip forced");
					return null;
				}
			}
			Logger.instance().debug("Activity forced");
			return newActivity();
		}
	}
	
	/**
	 * This method generates a new activity.
	 * 
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newActivity() {
		String activityName = askNewActivityName();
		Logger.instance().debug("New activity created (`" + activityName + "')");
		Task t = process.newTask(activityName);
		if (parameters.generateDataObject()) {
			newDataObject().setObjectOwner(t, SetUtils.getRandom(DATA_OBJECT_DIRECTION.values()));
		}
		return new PatternFrame(t);
	}
	
	/**
	 * This method generates a new data object and returns it.
	 * 
	 * @return the new data object generated
	 */
	protected DataObject newDataObject() {
		DataObject d = new DataObject(process);
		d.setName(askNewDataObjectName());
		d.setValue(new BigInteger(65, new Random()).toString(32));
		Logger.instance().debug("New data object created (`" + d.getName() + "' = `" + d.getValue() + "')");
		return d;
	}
	
	/**
	 * This method generates a new sequence pattern. A sequence is connecting
	 * two internal frames, generate using
	 * {@link ProcessGenerator#newInternalPattern()}.
	 * 
	 * @param currentDepth the current depth of the generation
	 * @param canLoop specifies whether a loop is allowed here or not
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newSequence(int currentDepth, boolean canLoop, boolean canSkip) {
		Logger.instance().debug("New sequence pattern to create");
		PatternFrame p1 = newInternalPattern(currentDepth, canLoop, canSkip);
		PatternFrame p2 = newInternalPattern(currentDepth, canLoop, canSkip);
		return PatternFrame.connect(p1, p2);
	}
	
	/**
	 * This method generates a new AND pattern. Each branch is populated using
	 * the generate using {@link ProcessGenerator#newInternalPattern()} method.
	 * 
	 * @param currentDepth the current depth of the generation
	 * @param loopAllowed specifies whether a loop is allowed here or not
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newAndBranches(int currentDepth, boolean loopAllowed) {
		Logger.instance().debug("New AND pattern to create");
		PatternFrame beforeSplit = newActivity();
		Gateway split = process.newParallelGateway();
		Gateway join = process.newParallelGateway();
		PatternFrame afterJoin = newActivity();
		int branchesToGenerate = parameters.getRandomANDBranches();
		
		for(int i = 0; i < branchesToGenerate; i++) {
			PatternFrame p = newInternalPattern(currentDepth, loopAllowed, false);
			PatternFrame.connect(split, p).connect(join);
		}
		
		PatternFrame.connect(beforeSplit, split);
		PatternFrame.connect(join, afterJoin);
		
		return new PatternFrame(beforeSplit.getLeftBound(), afterJoin.getRightBound());
	}
	
	/**
	 * This method generates a new XOR pattern. Each branch is populated using
	 * the generate using {@link ProcessGenerator#newInternalPattern()} method.
	 * 
	 * @param currentDepth the current depth of the generation
	 * @param loopAllowed specifies whether a loop is allowed here or not
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newXorBranches(int currentDepth, boolean loopAllowed, boolean canSkip) {
		Logger.instance().debug("New XOR pattern to create");
		PatternFrame beforeSplit = newActivity();
		Gateway split = process.newExclusiveGateway();
		Gateway join = process.newExclusiveGateway();
		PatternFrame afterJoin = newActivity();
		int branchesToGenerate = parameters.getRandomXORBranches();
		
		for(int i = 0; i < branchesToGenerate; i++) {
			PatternFrame p = newInternalPattern(currentDepth, loopAllowed, canSkip);
			PatternFrame.connect(split, p).connect(join);
			if (parameters.generateDataObject() && p.getLeftBound() instanceof IDataObjectOwner) {
				newDataObject().setObjectOwner((IDataObjectOwner) p.getLeftBound(), DATA_OBJECT_DIRECTION.REQUIRED);
			}
		}
		
		PatternFrame.connect(beforeSplit, split);
		PatternFrame.connect(join, afterJoin);
		
		return new PatternFrame(beforeSplit.getLeftBound(), afterJoin.getRightBound());
	}
	
	/**
	 * This method generates a new XOR pattern. Each branch is populated using
	 * the generate using {@link ProcessGenerator#newInternalPattern()} method.
	 * 
	 * @param currentDepth the current depth of the generation
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newLoopBranch(int currentDepth) {
		Logger.instance().debug("New loop pattern to create");
		PatternFrame beforeSplit = newActivity();
		Gateway split = process.newExclusiveGateway();
		Gateway join = process.newExclusiveGateway();
		PatternFrame afterJoin = newActivity();
		
		PatternFrame body = newInternalPattern(currentDepth, false, false);
		PatternFrame rollback = newInternalPattern(currentDepth, false, true);
		
		PatternFrame.connect(split, body).connect(join);
		PatternFrame.connect(join, rollback).connect(split);
		
		PatternFrame.connect(beforeSplit, split);
		PatternFrame.connect(join, afterJoin);
		
		return new PatternFrame(beforeSplit.getLeftBound(), afterJoin.getRightBound());
	}
	
	/**
	 * This method returns a new activity name, based on a progressive pattern.
	 * 
	 * @return a new name for an activity
	 * @see ProcessGenerator#ACTIVITY_NAME_PATTERN
	 */
	protected String askNewActivityName() {
		generatedActivities++;
		String candidateActivityName = String.format(ACTIVITY_NAME_PATTERN, numberToAlpha(generatedActivities));
		
		// check whether the candidate activity name is already present in the
		// process
		boolean activityAlreadyPresent = false;
		for (Task t : process.getTasks()) {
			if (t.getName().equals(candidateActivityName)) {
				activityAlreadyPresent = true;
				break;
			}
		}
		// if the activity is already there, then generate a new one, otherwise
		// the candidate activity name is returned
		if (activityAlreadyPresent) {
			return askNewActivityName();
		} else {
			return candidateActivityName;
		}
	}
	
	/**
	 * This method returns a new data object name, based on a progressive
	 * pattern.
	 * 
	 * @return a new name for a data object
	 * @see ProcessGenerator#DATA_OBJECT_NAME_PATTERN
	 */
	protected String askNewDataObjectName() {
		generatedDataObjects++;
		return String.format(DATA_OBJECT_NAME_PATTERN, numberToAlpha(generatedDataObjects).toLowerCase());
	}
	
	/**
	 * This method converts a number into a string representation
	 * 
	 * @param num the number to convert
	 * @return a string equivalent to the number, e.g., 1 becomes "A", 2 becomes
	 * "B", etc.
	 */
	public static String numberToAlpha(int num) {
		String result = "";
		while (num > 0) {
			num--;
			int remainder = num % 26;
			char digit = (char) (remainder + 'A');
			result = digit + result;
			num = (num - remainder) / 26;
		}
		return result;
	}
}
