package plg.gui.controller;

import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.gui.config.ConfigurationSet;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.model.Process;
import plg.utils.Logger;

public class ProcessesController {

	private static int GENERATED_PROCESSES = 0;
	private ApplicationController applicationController;
	private ConfigurationSet configuration;
	private ProcessesList processesList;
	private SingleProcessVisualizer singleProcessVisualizer;
	
	protected ProcessesController(
			ApplicationController applicationController,
			ConfigurationSet configuration,
			ProcessesList processesList,
			SingleProcessVisualizer singleProcessVisualizer) {
		this.applicationController = applicationController;
		this.configuration = configuration;
		this.processesList = processesList;
		this.singleProcessVisualizer = singleProcessVisualizer;		
		
	}
	
	public void randomProcess() {
		GENERATED_PROCESSES++;
		Process p = new Process("Process " + GENERATED_PROCESSES);
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES.setDepth(1));
		newProcessReady(p);
	}
	
	private void newProcessReady(Process p) {
		processesList.storeNewProcess(GENERATED_PROCESSES, p.getName(), getProcessSecondLine(p), p);
	}
	
	public void visualizeProcess(Process p) {
		if (p == null) {
			singleProcessVisualizer.generateProcessPlaceholder();
		} else {
			singleProcessVisualizer.visualizeNewProcess(p);
			Logger.instance().debug("Selected process \"" + p.getName() + "\"");
		}
	}
	
	private String getProcessSecondLine(Process p) {
		String sLine = "";
		sLine += p.getTasks().size() + " ";
		sLine += (p.getTasks().size() > 1)? "activities" : "activity";
		sLine += ", ";
		if (p.getGateways().size() == 0) {
			sLine += "no gateways";
		} else {
			sLine += p.getGateways().size() + " ";
			sLine += (p.getGateways().size() > 1)? "gateways" : "gateway";
		}
		return sLine;
	}
}
