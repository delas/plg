package plg.gui.controller;

import java.io.File;
import java.util.concurrent.ExecutionException;

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
import plg.gui.dialog.ErrorDialog;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.NewLogDialog;
import plg.gui.dialog.StreamConfigurationDialog;
import plg.gui.dialog.StreamDialog;
import plg.gui.dialog.StreamNoiseDialog;
import plg.gui.panels.Progress;
import plg.gui.panels.SingleProcessVisualizer;
import plg.gui.util.FileFilterHelper;
import plg.gui.util.RuntimeUtils;
import plg.model.Process;

/**
 * This class represents the log controller, and is in charge of managing the
 * logs.
 * 
 * @author Andrea Burattin
 */
public class LogController {

	private static final String KEY_LOG_LOCATION = "LOG_LOCATION";
	
	private SingleProcessVisualizer singleProcessVisualizer;
	private ConfigurationSet configuration;

	/**
	 * Controller constructor
	 * 
	 * @param applicationController the main application controller
	 */
	protected LogController(ApplicationController applicationController) {
		this.singleProcessVisualizer = applicationController.getMainWindow().getSingleProcessVisualizer();
		this.configuration = applicationController.getConfiguration(LogController.class.getCanonicalName());
	}
	
	/**
	 * This method is responsible of generating a new log
	 */
	public void generateLog() {
		NewLogDialog nld = new NewLogDialog(
				ApplicationController.instance().getMainFrame(),
				"Log for " + singleProcessVisualizer.getCurrentlyVisualizedProcess().getName());
		nld.setVisible(true);
		if (RETURNED_VALUES.SUCCESS.equals(nld.returnedValue())) {
			final JFileChooser fc = new JFileChooser(new File(configuration.get(KEY_LOG_LOCATION, RuntimeUtils.getHomeFolder())));

			fc.setAcceptAllFileFilterUsed(false);
			fc.addChoosableFileFilter(new FileNameExtensionFilter("Compressed XES file (*.xes.gz)", "xes.gz"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("XES file (*.xes)", "xes"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("Compressed MXML file (*.mxml.gz)", "mxml.gz"));
			fc.addChoosableFileFilter(new FileNameExtensionFilter("MXML file (*.mxml)", "mxml"));
			
			int returnVal = fc.showSaveDialog(ApplicationController.instance().getMainFrame());
			
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
						ApplicationController.instance().getMainWindow().getProgressStack().askForNewProgress());
				
				SwingWorker<XLog, Void> worker = new SwingWorker<XLog, Void>() {
					@Override
					protected XLog doInBackground() {
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
						try {
							return lg.generateAndSerializeLog(serializer, new File(file));
						} catch (Exception e) {
							new ErrorDialog(ApplicationController.instance().getMainFrame(), e).setVisible(true);
						}
						return null;
					}
				};
				worker.execute();
			}
		}
	}
	
	/**
	 * This method is responsible of generating streams
	 */
	public void generateStream() {
		final StreamNoiseDialog nld = new StreamNoiseDialog(ApplicationController.instance().getMainFrame());
		nld.setVisible(true);
		
		if (RETURNED_VALUES.SUCCESS.equals(nld.returnedValue())) {
			
			// ok, we now have a configuration for the generation of the noise of the stream
			final StreamConfigurationDialog scd = new StreamConfigurationDialog(ApplicationController.instance().getMainFrame());
			scd.setVisible(true);
			
			final Progress progress = ApplicationController.instance().getMainWindow().getProgressStack().askForNewProgress();
			
			if (RETURNED_VALUES.SUCCESS.equals(scd.returnedValue())) {
				SwingWorker<StreamDialog, Void> worker = new SwingWorker<StreamDialog, Void>() {
					@Override
					protected StreamDialog doInBackground() throws Exception {
						progress.start();
						progress.setIndeterminate(true);
						return new StreamDialog(
								ApplicationController.instance().getMainFrame(),
								scd.getConfiguredValues(),
								nld.getConfiguredValues(),
								singleProcessVisualizer.getCurrentlyVisualizedProcess());
					}
					
					@Override
					protected void done() {
						progress.finished();
						try {
							get().setVisible(true);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				};
				worker.execute();
			}
		}
	}
}
