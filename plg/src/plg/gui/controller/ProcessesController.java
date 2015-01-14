package plg.gui.controller;

import plg.generator.process.ProcessGenerator;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.NewProcessDialog;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.model.Process;
import plg.utils.Logger;

/**
 * This class described the controller of the processes. It is in charge of
 * managing new and existing processes, display and coordinate their
 * interaction.
 *
 * @author Andrea Burattin
 */
public class ProcessesController {

	private static int GENERATED_PROCESSES = 1;
	private ApplicationController applicationController;
	private ProcessesList processesList;
	private SingleProcessVisualizer singleProcessVisualizer;
	
	/**
	 * Controller constructor
	 * 
	 * @param applicationController
	 * @param configuration
	 * @param processesList
	 * @param singleProcessVisualizer
	 */
	protected ProcessesController(
			ApplicationController applicationController,
			ProcessesList processesList,
			SingleProcessVisualizer singleProcessVisualizer) {
		this.applicationController = applicationController;
		this.processesList = processesList;
		this.singleProcessVisualizer = singleProcessVisualizer;
		
	}
	
	/**
	 * This method causes the startup of the procedure for the creation of a
	 * new random process 
	 */
	public void randomProcess() {
		NewProcessDialog npd = new NewProcessDialog(applicationController.getMainFrame(), "Process " + GENERATED_PROCESSES);
		npd.setVisible(true);
		if (RETURNED_VALUES.SUCCESS.equals(npd.returnedValue())) {
			GENERATED_PROCESSES++;
			Process p = new Process(npd.getNewProcessName());
			ProcessGenerator.randomizeProcess(p, npd.getConfiguredValues());
			
			processesList.storeNewProcess(GENERATED_PROCESSES, p.getName(), generateProcessSubtitle(p), p);
		}
	}
	
	/**
	 * This method is used to display a specific process model
	 * 
	 * @param process the process model to visualize
	 */
	public void visualizeProcess(Process process) {
		if (process == null) {
			singleProcessVisualizer.generateProcessPlaceholder();
			Logger.instance().info("No process to show");
		} else {
			singleProcessVisualizer.visualizeNewProcess(process);
			Logger.instance().info("Selected process \"" + process.getName() + "\"");
		}
	}
	
	/**
	 * This method generates the "subtitle" of a process
	 * 
	 * @param p
	 * @return
	 */
	private String generateProcessSubtitle(Process p) {
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
