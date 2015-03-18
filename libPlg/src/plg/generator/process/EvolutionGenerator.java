package plg.generator.process;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.utils.Logger;
import plg.utils.SetUtils;

/**
 * This class contains the procedures for the evolution of a business process
 * into a new [slightly different] one.
 * 
 * @author Andrea Burattin
 */
public class EvolutionGenerator extends ProcessGenerator {

	/**
	 * This public static method is the main interface for the process
	 * evolution. Specifically, this method performs some evolution on the
	 * provided process.
	 * 
	 * @param originalProcess the starting process
	 * @param parameters the randomization parameters to use
	 * @return the new process, after the evolutions
	 */
	public static Process evolveProcess(Process originalProcess, RandomizationConfiguration parameters) {
		Process process = (Process) originalProcess.clone();
		process.setName("Evolution of " + originalProcess.getName());
		
		Task randomTask = SetUtils.getRandom(process.getTasks());

		EvolutionGenerator eg = new EvolutionGenerator(process, parameters);
		PatternFrame evolution = eg.newInternalPattern(1, true, false);
		
		Logger.instance().debug("Evolution starting from " + randomTask);
		FlowObject entry = evolution.getLeftBound();
		FlowObject exit = evolution.getRightBound();
		
		try {
			for(FlowObject fo : randomTask.getIncomingObjects()) {
				process.newSequence(fo, entry);
				process.removeComponent(process.getSequence(fo, randomTask));
			}
			for(FlowObject fo : randomTask.getOutgoingObjects()) {
				process.newSequence(exit, fo);
				process.removeComponent(process.getSequence(randomTask, fo));
			}
			
			process.removeComponent(randomTask);
			process.check();
			
		} catch (IllegalSequenceException e) {
			e.printStackTrace();
		} catch (InvalidProcessException e) {
			e.printStackTrace();
		}
		
		return process;
	}
	
	/**
	 * Protected class constructor. This method is not publicly available since
	 * we would like to interact only through the
	 * {@link EvolutionGenerator#evolveProcess(Process, RandomizationConfiguration)}
	 * method.
	 * 
	 * @param process the process to randomize
	 * @param parameters the randomization parameters to use
	 */
	protected EvolutionGenerator(Process process, RandomizationConfiguration parameters) {
		super(process, parameters);
		generatedActivities = process.getTasks().size();
		generatedDataObjects = process.getDataObjects().size();
	}
}
