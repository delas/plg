package plg.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plg.generator.log.SimulationConfiguration;
import plg.gui.controller.ApplicationController;
import plg.gui.controller.ProcessesController;
import plg.gui.controller.ProcessesController.ProcessesListener;
import plg.gui.remote.REMOTE_MESSAGES;
import plg.gui.remote.RemoteLogger;
import plg.gui.util.SpringUtilities;
import plg.gui.util.collections.ImagesCollection;
import plg.gui.widgets.StreamPreview;
import plg.model.Process;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.StreamBuffer;
import plg.stream.model.Streamer;
import plg.visualizer.BPMNVisualizer;
import plg.visualizer.GraphvizBPMNVisualizer;

/**
 * This class contains the dialog to manage the stream. This dialog is also a
 * {@link ProcessesListener} since it is capable of receiving updates from the
 * {@link ProcessesController}.
 * 
 * <p> This dialog is not modal: it is possible to change the underlying list of
 * processes in order to do perform concept drifts.
 *
 * @author Andrea Burattin
 */
public class StreamDialog extends GeneralDialog implements ProcessesController.ProcessesListener {

	private static final long serialVersionUID = -4781877672157619819L;
	
	protected StreamConfiguration streamConfiguration;
	protected SimulationConfiguration simulationConfiguration;
	protected Process process;
	protected Streamer streamer;
	protected Streamer streamerForPreview;
	protected StreamBuffer streamBuffer;
	
	protected JComboBox<ProcessSelector> processCombo;
	protected JLabel portLabel;
	protected JLabel parallelInstancesLabel;
	protected JLabel timeFractionLabel;
	protected JSlider timeMultiplierSlider;
	protected StreamPreview streamPreview;
	protected JPanel processPreviewContainer;
	protected JButton startButton;
	protected JButton stopButton;
	
	/**
	 * Main dialog constructor
	 * 
	 * @param owner the owner of the dialog
	 * @param streamConfiguration the stream configuration
	 * @param simulationConfiguration the simulation configuration
	 * @param process the initial process to stream
	 */
	public StreamDialog(JFrame owner, StreamConfiguration streamConfiguration, SimulationConfiguration simulationConfiguration, Process process) {
		super(owner,
			"Stream Process",
			"This dialog can be used to handle the streaming process",
			ApplicationController.instance().getConfiguration(StreamDialog.class.getCanonicalName()),
			true, false);
		
		this.streamConfiguration = streamConfiguration;
		this.simulationConfiguration = simulationConfiguration;
		this.process = process;
		
		this.streamerForPreview = new Streamer(streamConfiguration, process.getName(), process, simulationConfiguration);
		this.streamBuffer = streamerForPreview.getBuffer();
		
		// widgets
		processCombo = new JComboBox<ProcessSelector>();
		portLabel = prepareFieldLabel("");
		parallelInstancesLabel = prepareFieldLabel("");
		timeFractionLabel = prepareFieldLabel("");
		timeMultiplierSlider = new JSlider(1, 2000);
		
		// buttons
		startButton = new JButton("Start stream");
		stopButton = new JButton("Stop stream");
		stopButton.setEnabled(false);
		cancelButton.setVisible(false);
		
		addFooterButton(stopButton, false);
		addFooterButton(startButton, true);
		
		startButton.setIcon(ImagesCollection.ICON_PLAY);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				streamer = new Streamer(
						StreamDialog.this.streamConfiguration,
						StreamDialog.this.process.getName(),
						StreamDialog.this.process,
						StreamDialog.this.simulationConfiguration);
				streamer.startStream();
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
				
				// remote logging, if available
				RemoteLogger.instance().log(REMOTE_MESSAGES.STREAM_STARTED).send();
			}
		});
		stopButton.setIcon(ImagesCollection.ICON_STOP);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton.setEnabled(false);
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						streamer.endStream();
						startButton.setEnabled(true);
						
						// remote logging, if available
						RemoteLogger.instance().log(REMOTE_MESSAGES.STREAM_STOPPED).send();
					}
				});
			}
		});
		
		// widgets configuration
		// time slider
		timeMultiplierSlider.setValue((int) (streamConfiguration.timeMultiplier * 100000d));
		timeMultiplierSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = (double) timeMultiplierSlider.getValue() / 100000d;
				StreamDialog.this.streamConfiguration.timeMultiplier = val;
				streamPreview.updateUI();
				StreamDialog.this.streamConfiguration.timeMultiplier = val;
			}
		});
		// process selector
		processCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (processCombo.isEnabled()) {
					ProcessSelector ps = (ProcessSelector) processCombo.getSelectedItem();
					Process selectedProcess = ps.process;
					updateProcess(selectedProcess);
				}
			}
		});
		
		// let the body panel grow vertically and horizontally
		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints parentConstraint = ((GridBagLayout) bodyPanel.getParent().getLayout()).getConstraints(bodyPanel);
		parentConstraint.fill = GridBagConstraints.BOTH;
		((GridBagLayout) bodyPanel.getParent().getLayout()).setConstraints(bodyPanel, parentConstraint);
		
		// parameters summary panel
		JPanel parameters = new JPanel(new SpringLayout());
		parameters.add(prepareFieldLabel("Process streamed:"));
		parameters.add(processCombo);
		parameters.add(prepareFieldLabel("Network port:"));
		parameters.add(portLabel);
		parameters.add(prepareFieldLabel("Parallel instances:"));
		parameters.add(parallelInstancesLabel);
		parameters.add(prepareFieldLabel("Frac. before trace:"));
		parameters.add(timeFractionLabel);
		parameters.add(prepareFieldLabel("Time multiplier:"));
		parameters.add(timeMultiplierSlider);
		SpringUtilities.makeCompactGrid(parameters,
				(parameters.getComponentCount() / 2), 2, // rows, cols
				0, 0, // initX, initY
				5, 10); //xPad, yPad
		
		// stream preview
		streamPreview = new StreamPreview(streamBuffer, streamConfiguration);
		
		// process preview
		processPreviewContainer = new JPanel(new GridBagLayout());
		
		// add everything to the body panel
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 5);
		bodyPanel.add(parameters, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 0, 0);
		bodyPanel.add(streamPreview, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		c.weightx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		bodyPanel.add(processPreviewContainer, c);
		
		bodyPanel.updateUI();
		bodyPanel.repaint();
		
		// unregister listeners
		getRootPane().getActionMap().remove("ESC_KEY_PRESSED");
		
		// register for processes list controller
		ApplicationController.instance().processes().registerNewListener(this);
	}
	
	/**
	 * This method updates the dialog labels
	 */
	protected void updateLabels() {
//		portLabel.setText("" + streamConfiguration.servicePort);
		parallelInstancesLabel.setText("" + streamConfiguration.maximumParallelInstances);
		timeFractionLabel.setText("" + streamConfiguration.timeFractionBeforeNewTrace);
	}
	
	/**
	 * This method updates the process to be streamed
	 * 
	 * @param process the new process to use for the streaming
	 */
	protected void updateProcess(Process process) {
		// update the preview
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		GraphvizBPMNVisualizer v = new GraphvizBPMNVisualizer(process);
		processPreviewContainer.removeAll();
		processPreviewContainer.add(v, c);
		processPreviewContainer.updateUI();
//		v.fit();
		
		// update the actual process
		this.process = process;
		if (streamer != null) {
			streamer.updateProcess(process);
		}
		
		// update the streamer preview
		streamerForPreview.updateProcess(process);
		streamerForPreview.clearBuffer();
		streamerForPreview.initialBufferPopulation();
		streamPreview.updateUI();
	}
	
	@Override
	protected void afterShowing() {
		updateLabels();
		updateProcess(process);
		processListChanged();
	}

	@Override
	public void processListChanged() {
		processCombo.setEnabled(false);
		processCombo.removeAllItems();
		for (Process p : ApplicationController.instance().processes().getProcesses()) {
			ProcessSelector ps = new ProcessSelector(p);
			processCombo.addItem(ps);
			if (p.equals(process)) {
				processCombo.setSelectedItem(ps);
			}
		}
		processCombo.setEnabled(true);
	}
	
	/**
	 * This class contains the values on the processes combo box
	 */
	private class ProcessSelector {
		public static final int MAX_NAME_LENGTH = 25;
		public String name;
		public Process process;
		
		public ProcessSelector(Process process) {
			this.name = process.getName().substring(0, Math.min(process.getName().length(), MAX_NAME_LENGTH));
			if (process.getName().length() > MAX_NAME_LENGTH) {
				this.name += "...";
			}
			this.process = process;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
