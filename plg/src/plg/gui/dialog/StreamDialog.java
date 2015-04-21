package plg.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plg.generator.log.SimulationConfiguration;
import plg.gui.controller.ApplicationController;
import plg.gui.util.SpringUtilities;
import plg.gui.widgets.StreamPreview;
import plg.model.Process;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.StreamBuffer;
import plg.stream.model.Streamer;
import plg.visualizer.BPMNVisualizer;

/**
 * This class contains the dialog to manage the stream
 *
 * @author Andrea Burattin
 */
public class StreamDialog extends GeneralDialog {

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
	
	public StreamDialog(JFrame owner, StreamConfiguration streamConfiguration, SimulationConfiguration simulationConfiguration, Process process) {
		super(owner,
			"Stream Process",
			"This dialog can be used to handle the streaming process",
			ApplicationController.instance().getConfiguration(StreamDialog.class.getCanonicalName()),
			true, false);
		
		this.streamConfiguration = streamConfiguration;
		this.simulationConfiguration = simulationConfiguration;
		this.process = process;
		
		this.streamerForPreview = new Streamer(streamConfiguration, process, simulationConfiguration);
		this.streamBuffer = streamerForPreview.getBuffer();
		
		// widgets
		processCombo = new JComboBox<ProcessSelector>();
		portLabel = prepareFieldLabel("");
		parallelInstancesLabel = prepareFieldLabel("");
		timeFractionLabel = prepareFieldLabel("");
		timeMultiplierSlider = new JSlider(1, 200);
		
		// buttons
		startButton = new JButton("Start stream");
		stopButton = new JButton("Stop stream");
		stopButton.setEnabled(false);
		cancelButton.setVisible(false);
		
		addFooterButton(stopButton, false);
		addFooterButton(startButton, true);
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				streamer = new Streamer(
						StreamDialog.this.streamConfiguration,
						StreamDialog.this.process,
						StreamDialog.this.simulationConfiguration);
				streamer.startStream();
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
			}
		});
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				streamer.endStream();
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
			}
		});
		
		// widgets configuration
		// time slider
		timeMultiplierSlider.setValue((int) (streamConfiguration.timeMultiplier * 10000d));
		timeMultiplierSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = (double) timeMultiplierSlider.getValue() / 10000d;
				StreamDialog.this.streamConfiguration.timeMultiplier = val;
				streamPreview.updateUI();
				StreamDialog.this.streamConfiguration.timeMultiplier = val;
			}
		});
		// process selector
		for (Process p : ApplicationController.instance().processes().getProcesses()) {
			ProcessSelector ps = new ProcessSelector(p);
			processCombo.addItem(ps);
			if (p.equals(process)) {
				processCombo.setSelectedItem(ps);
			}
		}
		processCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProcessSelector ps = (ProcessSelector) processCombo.getSelectedItem();
				Process selectedProcess = ps.process;
				updateProcess(selectedProcess);
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
	}
	
	protected void updateLabels() {
		portLabel.setText("" + streamConfiguration.servicePort);
		parallelInstancesLabel.setText("" + streamConfiguration.maximumParallelInstances);
		timeFractionLabel.setText("" + streamConfiguration.timeFractionBeforeNewTrace);
	}
	
	protected void updateProcess(Process process) {
		// update the preview
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		BPMNVisualizer v = new BPMNVisualizer(process);
		processPreviewContainer.removeAll();
		processPreviewContainer.add(v, c);
		processPreviewContainer.updateUI();
		v.fit();
		
		// update the actual process
		this.process = process;
		
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
	}
	
	private class ProcessSelector {
		public String name;
		public Process process;
		
		public ProcessSelector(Process process) {
			this.name = process.getName();
			this.process = process;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
