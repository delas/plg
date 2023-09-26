package plg.gui.controller;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import plg.generator.process.EvolutionGenerator;
import plg.generator.process.ProcessGenerator;
import plg.gui.config.ConfigurationSet;
import plg.gui.dialog.EvolutionDialog;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.NewProcessDialog;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.gui.remote.REMOTE_MESSAGES;
import plg.gui.remote.RemoteLogger;
import plg.gui.util.FileFilterHelper;
import plg.gui.util.RuntimeUtils;
import plg.io.exporter.IFileExporter;
import plg.io.importer.IFileImporter;
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
	private static final String KEY_PROCESS_LOCATION = "PROCESS_LOCATION";
	
	private Set<ProcessesListener> listeners;
	private ApplicationController applicationController;
	private ProcessesList processesList;
	private SingleProcessVisualizer singleProcessVisualizer;
	private ConfigurationSet configuration;
	
	/**
	 * Controller constructor
	 * 
	 * @param applicationController the main application controller 
	 */
	protected ProcessesController(ApplicationController applicationController) {
		this.listeners = new HashSet<ProcessesController.ProcessesListener>();
		this.applicationController = applicationController;
		this.processesList = applicationController.getMainWindow().getProcessesList();
		this.singleProcessVisualizer = applicationController.getMainWindow().getSingleProcessVisualizer();
		this.configuration = applicationController.getConfiguration(ProcessesController.class.getCanonicalName());
		
		visualizeProcess(null);
	}
	
	/**
	 * This method causes the startup of the procedure for the creation of a
	 * new random process 
	 */
	public void randomProcess() {
		NewProcessDialog npd = new NewProcessDialog(
				ApplicationController.instance().getMainFrame(),
				"Process " + GENERATED_PROCESSES);
		npd.setVisible(true);
		if (RETURNED_VALUES.SUCCESS.equals(npd.returnedValue())) {
			Process p = new Process(npd.getNewProcessName());
			ProcessGenerator.randomizeProcess(p, npd.getConfiguredValues());
			
			GENERATED_PROCESSES++;
			processesList.storeNewProcess(GENERATED_PROCESSES, p.getName(), generateProcessSubtitle(p), p);
			notifyChangeProcessesList();
			
			// remote logging, if available
			RemoteLogger.instance().log(REMOTE_MESSAGES.PROCESS_RANDOMIZED).add(npd.getConfiguredValues()).send();
		}
	}
	
	/**
	 * This method causes the system to open a new file and show the process
	 * contained
	 */
	public void openProcess() {
		final JFileChooser fc = new JFileChooser(new File(configuration.get(KEY_PROCESS_LOCATION, RuntimeUtils.getHomeFolder())));
		FileFilterHelper.assignImportFileFilters(fc);
		int returnVal = fc.showOpenDialog(ApplicationController.instance().getMainFrame());
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final String fileName = fc.getSelectedFile().getAbsolutePath();
			final IFileImporter importer = FileFilterHelper.getImporterFromFileName((FileNameExtensionFilter) fc.getFileFilter());
			configuration.set(KEY_PROCESS_LOCATION, fileName.substring(0, fileName.lastIndexOf(File.separator)));
			
			SwingWorker<Process, Void> worker = new SwingWorker<Process, Void>() {
				@Override
				protected Process doInBackground() throws Exception {
					GENERATED_PROCESSES++;
					return importer.importModel(fileName, ApplicationController.instance().getMainWindow().getProgressStack().askForNewProgress());
				}
				
				@Override
				protected void done() {
					try {
						Process p = get();
						processesList.storeNewProcess(GENERATED_PROCESSES, p.getName(), generateProcessSubtitle(p), p);
						notifyChangeProcessesList();
						
						// remote logging, if available
						RemoteLogger.instance().log(REMOTE_MESSAGES.PROCESS_OPENED).add("filter", fc.getFileFilter().getDescription()).send();
					} catch (ExecutionException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			worker.execute();
		}
	}
	
	/**
	 * This method causes the system to save the process into a file
	 */
	public void saveProcess() {
		final JFileChooser fc = new JFileChooser(new File(configuration.get(KEY_PROCESS_LOCATION, RuntimeUtils.getHomeFolder())));
		FileFilterHelper.assignExportFileFilters(fc);
		int returnVal = fc.showSaveDialog(ApplicationController.instance().getMainFrame());
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fileName = fc.getSelectedFile().getAbsolutePath();
			final String file = FileFilterHelper.fixFileName(fileName, (FileNameExtensionFilter) fc.getFileFilter());
			configuration.set(KEY_PROCESS_LOCATION, fileName.substring(0, fileName.lastIndexOf(File.separator)));
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					IFileExporter exporter = FileFilterHelper.getExporterFromFileName((FileNameExtensionFilter) fc.getFileFilter());
					exporter.exportModel(
							singleProcessVisualizer.getCurrentlyVisualizedProcess(),
							file,
							ApplicationController.instance().getMainWindow().getProgressStack().askForNewProgress());
					
					// remote logging, if available
					RemoteLogger.instance().log(REMOTE_MESSAGES.PROCESS_SAVED).add("filter", fc.getFileFilter().getDescription()).send();
					return null;
				}
			};
			worker.execute();
		}
	}
	
	/**
	 * This method deletes a process from the process list
	 * 
	 * @param index the process index
	 */
	public void deleteProcess(int index) {
		if (index >= 0) {
			int confirmation = JOptionPane.showConfirmDialog(
					ApplicationController.instance().getMainFrame(),
					"Are you sure to delete the selected process?",
					"Confirm deletion",
					JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.NO_OPTION || confirmation == JOptionPane.CLOSED_OPTION) {
				return;
			}
			
			processesList.deleteProcess(index);
			notifyChangeProcessesList();
			
			// remote logging, if available
			RemoteLogger.instance().log(REMOTE_MESSAGES.PROCESS_DELETED).send();
		}
	}
	
	/**
	 * This method evolves the currently selected process
	 */
	public void evolveProcess() {
		Process p = singleProcessVisualizer.getCurrentlyVisualizedProcess();
		EvolutionDialog ed = new EvolutionDialog(ApplicationController.instance().getMainFrame(), "Evolution of " + p.getName());
		ed.setVisible(true);
		
		if (RETURNED_VALUES.SUCCESS.equals(ed.returnedValue())) {
			GENERATED_PROCESSES++;
			Process evolution = EvolutionGenerator.evolveProcess(p, ed.getConfiguredValues());
			processesList.storeNewProcess(GENERATED_PROCESSES, evolution.getName(), generateProcessSubtitle(evolution), evolution);
			notifyChangeProcessesList();
			
			// remote logging, if available
			RemoteLogger.instance().log(REMOTE_MESSAGES.PROCESS_EVOLVED).add(ed.getConfiguredValues()).send();
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
			processesList.setVisible(false);
			Logger.instance().info("No process to display");
		} else {
			singleProcessVisualizer.visualizeNewProcess(process);
			processesList.setVisible(true);
			Logger.instance().info("Selected process \"" + process.getName() + "\"");
		}
		
		// update toolbar buttons depending on the selected process or not
		applicationController.getMainWindow().getToolbar().setProcessSelected((process != null));
	}
	
	/**
	 * This method returns the list of processes
	 * 
	 * @return the list of processes
	 */
	public List<Process> getProcesses() {
		return processesList.getProcesses();
	}
	
	/**
	 * This method registers a new listers to the process controller
	 * 
	 * @param listener the listener to register
	 */
	public void registerNewListener(ProcessesListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * This method notifies to all listeners a change in processes list
	 */
	private void notifyChangeProcessesList() {
		for (ProcessesListener listener : listeners) {
			listener.processListChanged();
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
	
	public interface ProcessesListener {
		public void processListChanged();
	}
}
