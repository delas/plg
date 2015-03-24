package plg.gui.controller;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XMxmlGZIPSerializer;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import plg.generator.log.LogGenerator;
import plg.gui.config.ConfigurationSet;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.NewLogDialog;
import plg.gui.panels.SingleProcessVisualizer;
import plg.gui.util.FileFilterHelper;
import plg.gui.util.RuntimeUtils;
import plg.model.Process;

/**
 * 
 * @author Andrea Burattin
 */
public class LogController {

	private static final String KEY_LOG_LOCATION = "LOG_LOCATION";
	
	private ApplicationController applicationController;
	private SingleProcessVisualizer singleProcessVisualizer;
	private ConfigurationSet configuration;

	/**
	 * Controller constructor
	 * 
	 * @param applicationController
	 * @param configuration
	 * @param processesList
	 * @param singleProcessVisualizer
	 */
	protected LogController(
			ApplicationController applicationController,
			SingleProcessVisualizer singleProcessVisualizer) {
		this.applicationController = applicationController;
		this.singleProcessVisualizer = singleProcessVisualizer;
		this.configuration = applicationController.getConfiguration(LogController.class.getCanonicalName());
	}
	
	/**
	 * 
	 */
	public void generateLog() {
		NewLogDialog nld = new NewLogDialog(applicationController.getMainFrame(),
				"Log for " + singleProcessVisualizer.getCurrentlyVisualizedProcess().getName());
		nld.setVisible(true);
		if (RETURNED_VALUES.SUCCESS.equals(nld.returnedValue())) {
			final JFileChooser fc = new JFileChooser(new File(configuration.get(KEY_LOG_LOCATION, RuntimeUtils.getHomeFolder())));

			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new FileNameExtensionFilter("Compressed XES file (*.xes.gz)", "xes.gz"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("XES file (*.xes)", "xes"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("Compressed MXML file (*.mxml.gz)", "mxml.gz"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("MXML file (*.mxml)", "mxml"));
			
			int returnVal = fc.showSaveDialog(applicationController.getMainFrame());
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = fc.getSelectedFile().getAbsolutePath();
				FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fc.getFileFilter();
				final String extension = selectedFilter.getExtensions()[0];
				final String file = FileFilterHelper.fixFileName(fileName, (FileNameExtensionFilter) selectedFilter);
				configuration.set(KEY_LOG_LOCATION, fileName.substring(0, fileName.lastIndexOf(File.separator)));
				
				Process process = singleProcessVisualizer.getCurrentlyVisualizedProcess();
				final LogGenerator lg = new LogGenerator(
						process,
						nld.getConfiguredValues(),
						applicationController.getMainWindow().getProgressStack().askForNewProgress());
				
				SwingWorker<XLog, Void> worker = new SwingWorker<XLog, Void>() {
					@Override
					protected XLog doInBackground() throws Exception {
						XSerializer serializer = null;
						if (extension.equals("xes")) {
							serializer = new XesXmlSerializer();
						} else if (extension.equals("xes.gz")) {
							serializer = new XesXmlGZIPSerializer();
						} else if (extension.equals("mxml")) {
							serializer = new XMxmlSerializer();
						} else if (extension.equals("mxml.gz")) {
							serializer = new XMxmlGZIPSerializer();
						}
						return lg.generateAndSerializeLog(serializer, new File(file));
					}
				};
				worker.execute();
			}
		}
	}
}
