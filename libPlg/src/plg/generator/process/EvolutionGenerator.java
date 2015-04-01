package plg.generator.process;

import java.util.ArrayList;
import java.util.List;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.utils.Logger;

/**
 * This class contains the procedures for the evolution of a business process
 * into a new [slightly different] one.
 * 
 * @author Andrea Burattin
 */
public class EvolutionGenerator extends ProcessGenerator {

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
	
	/**
	 * This public static method is the main interface for the process
	 * evolution. Specifically, this method performs some evolution on the
	 * provided process.
	 * 
	 * @param originalProcess the starting process
	 * @param parameters the randomization parameters to use
	 * @return the new process, after the evolutions
	 */
	public static Process evolveProcess(Process originalProcess, EvolutionConfiguration parameters) {
		Process process = (Process) originalProcess.clone();
		process.setName("Evolution of " + originalProcess.getName());
		
		List<Task> tasks = new ArrayList<Task>(process.getTasks());
		for (int i = 0; i < tasks.size(); i++) {
			Task randomTask = tasks.get(i);
			
			if (parameters.getEvolutionPresence()) {
				Logger.instance().debug("Evolution starting from " + randomTask);
				
				EvolutionGenerator eg = new EvolutionGenerator(process, parameters);
				PatternFrame evolution = eg.newInternalPattern(1, true, true);
				
				if (evolution == null) {
					try {
						for(FlowObject fo : randomTask.getIncomingObjects()) {
							for(FlowObject of : randomTask.getOutgoingObjects()) {
								process.newSequence(fo, of);
								process.removeComponent(process.getSequence(fo, randomTask));
								process.removeComponent(process.getSequence(randomTask, of));
							}
						}
						
						process.removeComponent(randomTask);
						process.check();
						
					} catch (IllegalSequenceException e) {
						e.printStackTrace();
					} catch (InvalidProcessException e) {
						e.printStackTrace();
					}
				} else {
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
				}
			}
		}
		
		// check if the new process contains any new stuff
		// the set of feature contains: the number of tasks, sequences,
		// gateways, and data objects
		int[] newProcessFeatures = {
				process.getTasks().size(),
				process.getSequences().size(),
				process.getGateways().size(),
				process.getDataObjects().size()
			};
		int[] originalProcessFeatures = {
				originalProcess.getTasks().size(),
				originalProcess.getSequences().size(),
				originalProcess.getGateways().size(),
				originalProcess.getDataObjects().size()
			};
		boolean match = true;
		for (int i = 0; i < newProcessFeatures.length; i++) {
			if (newProcessFeatures[i] != originalProcessFeatures[i]) {
				match = false;
				break;
			}
		}
		if (match) {
			// sadly, no new stuff, we have to run through the evolution again
			return evolveProcess(originalProcess, parameters);
		} else {
			return process;
		}
	}
}
