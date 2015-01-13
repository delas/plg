package plg.gui.controller;

import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.gui.config.ConfigurationSet;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.model.Process;

public class ProcessesController {

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
		Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES.setDepth(2));
	}
}
