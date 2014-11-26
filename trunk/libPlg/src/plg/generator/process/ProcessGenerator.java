package plg.generator.process;

import plg.generator.process.RandomizationConfiguration.RANDOMIZATION_PATTERN;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.Event;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;

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
	protected static final String ACTIVITY_NAME_PATTERN = "Activity %s";
	
	private Process process;
	private RandomizationConfiguration parameters;
	private int generatedActivities = 0;
	
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
		Event start = process.newStartEvent();
		Event end = process.newEndEvent();
		PatternFrame p = newInternalPattern(1, true, false);
		System.out.println(p);
		PatternFrame.connect(start, p).connect(end);
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
			System.out.println(nextAction);

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
			System.out.print("\tFORCING ");
			if (canSkip) {
				RANDOMIZATION_PATTERN nextAction = parameters.getRandomPattern(RANDOMIZATION_PATTERN.SKIP, RANDOMIZATION_PATTERN.SINGLE_ACTIVITY);
				if (nextAction == RANDOMIZATION_PATTERN.SKIP) {
					System.out.println("SKIP");
					return null;
				}
			}
			System.out.println("ACTIVITY");
			return newActivity();
		}
	}
	
	/**
	 * This method generates a new activity.
	 * 
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newActivity() {
		String activityName = askNewName();
		return new PatternFrame(process.newTask(activityName));
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
		PatternFrame beforeSplit = newActivity();
		Gateway split = process.newExclusiveGateway();
		Gateway join = process.newExclusiveGateway();
		PatternFrame afterJoin = newActivity();
		int branchesToGenerate = parameters.getRandomXORBranches();
		
		for(int i = 0; i < branchesToGenerate; i++) {
			PatternFrame p = newInternalPattern(currentDepth, loopAllowed, canSkip);
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
	 * @return the frame containing the generated pattern
	 */
	protected PatternFrame newLoopBranch(int currentDepth) {
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
	protected String askNewName() {
		generatedActivities++;
		String result = "";
		int num = generatedActivities;
		while (num > 0) {
			num--;
			int remainder = num % 26;
			char digit = (char) (remainder + 'A');
			result = digit + result;
			num = (num - remainder) / 26;
		}
		return String.format(ACTIVITY_NAME_PATTERN, result);
	}
}
