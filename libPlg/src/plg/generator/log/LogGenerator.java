package plg.generator.log;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;

import plg.generator.IProgressVisualizer;
import plg.model.Process;
import plg.model.event.StartEvent;
import plg.utils.CPUUtils;
import plg.utils.Logger;
import plg.utils.XLogHelper;

/**
 * This class describes a general log generator
 * 
 * @author Andrea Burattin
 */
public class LogGenerator {

	private Process process;
	private SimulationConfiguration parameters;
	private IProgressVisualizer progress;
	
	/**
	 * Basic constructor for a log generator
	 * 
	 * @param process the process to use for the log generation
	 * @param parameters the simulation configuration
	 * @param progress the progress visualizer
	 */
	public LogGenerator(Process process, SimulationConfiguration parameters, IProgressVisualizer progress) {
		this.process = process;
		this.parameters = parameters;
		this.progress = progress;
	}
	
	/**
	 * This method returns an {@link XLog} object with the generated log.
	 * 
	 * <p> If more than one {@link StartEvent} is available, the simulator picks
	 * one randomly.
	 * 
	 * @return the generated log
	 */
	public XLog generateLog() {
		// configure progress
		progress.setMinimum(0);
		progress.setMaximum(parameters.getNumberOfTraces());
		
		// define the number of CPU cores to use
		int coresToUse = (parameters.useMultithreading())? CPUUtils.CPUAvailable() : 1;
		Logger.instance().info("Starting simulation with " + coresToUse + " cores");
		
		// prepare the engine
		Set<TraceGenerator> traceGenerators = new HashSet<TraceGenerator>();
		SimulationEngine se = new SimulationEngine(coresToUse, parameters.getNumberOfTraces(), progress);
		
		for (int i = 0; i < parameters.getNumberOfTraces(); i++) {
			// the actual trace generator
			TraceGenerator tg = new TraceGenerator(
					process,
					String.format(parameters.getCaseIdPattern(), i),
					parameters);
			traceGenerators.add(tg);
			se.enqueue(tg);
		}
		
		// power on!
		se.start();
		
		// collect all traces into the generated log
		XLog log = XLogHelper.generateNewXLog("tmp-process");
		for(TraceGenerator tg : traceGenerators) {
			log.add(tg.getGeneratedTrace());
		}
		
		return log;
	}
}
