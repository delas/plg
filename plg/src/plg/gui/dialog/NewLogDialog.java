package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import plg.generator.log.SimulationConfiguration;
import plg.generator.log.noise.NoiseConfiguration;
import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of the parameters for
 * the generation of a new log
 *
 * @author Andrea Burattin
 */
public class NewLogDialog extends GeneralDialog {

	private static final long serialVersionUID = -4781877672157619819L;
	protected static final String KEY_NO_TRACES = "NO_TRACES";
	protected static final String KEY_NOISE_INT_DATA = "NOISE_INT_DATA";
	protected static final String KEY_NOISE_INT_DATA_DELTA = "NOISE_INT_DATA_DELTA";
	protected static final String KEY_NOISE_STR_DATA = "NOISE_STR_DATA";
	protected static final String KEY_NOISE_ACT_NAME = "NOISE_ACT_NAME";
	protected static final String KEY_NOISE_TRACE_MISSING_HEAD = "NOISE_TRACE_MISSING_HEAD";
	protected static final String KEY_NOISE_TRACE_HEAD_SIZE = "NOISE_TRACE_HEAD_SIZE";
	protected static final String KEY_NOISE_TRACE_MISSING_TAIL = "NOISE_TRACE_MISSING_TAIL";
	protected static final String KEY_NOISE_TRACE_TAIL_SIZE = "NOISE_TRACE_TAIL_SIZE";
	protected static final String KEY_NOISE_TRACE_MISSING_EPISODE = "NOISE_TRACE_MISSING_EPISODE";
	protected static final String KEY_NOISE_TRACE_EPISODE_SIZE = "NOISE_TRACE_EPISODE_SIZE";
	protected static final String KEY_NOISE_PERTURBED_ORDER = "NOISE_PERTURBED_ORDER";
	protected static final String KEY_NOISE_DOUBLED_EVENT = "NOISE_DOUBLED_EVENT";
	protected static final String KEY_NOISE_ALIEN_EVENT = "NOISE_ALIEN_EVENT";
	
	protected final SimulationConfiguration DEFAULTS = new SimulationConfiguration(100);
	private SimulationConfiguration userConfiguration = null;
	
	protected JTextField nameField = null;
	protected JSpinner noOfTrace = null;
	protected JSpinner noiseIntegerData = null;
	protected JSpinner noiseIntegerDelta = null;
	protected JSpinner noiseStringData = null;
	protected JSpinner noiseActivityName = null;
	protected JSpinner noiseTraceMissingHead = null;
	protected JSpinner noiseTraceHeadSize = null;
	protected JSpinner noiseTraceMissingTail = null;
	protected JSpinner noiseTraceTailSize = null;
	protected JSpinner noiseTraceMissingEpisode = null;
	protected JSpinner noiseTraceEpisodeSize = null;
	protected JSpinner noiseTracePerturbedOrder = null;
	protected JSpinner noiseTraceDoubledEvent = null;
	protected JSpinner noiseTraceAlienEvent = null;
	
	protected JButton okButton = null;
	protected JButton resetButton = null;

	/**
	 * This constructor can be used to subclass the dialog
	 * 
	 * @param candidateProcessName the candidate process name
	 * @param owner the owner of the dialog
	 * @param title the title of the dialog
	 * @param help a short text describing the dialog content
	 * @param configuration the configuration set to use for the dialog
	 */
	protected NewLogDialog(String candidateProcessName, JFrame owner, String title, String help, final ConfigurationSet configuration) {
		super(owner,title, help, configuration);
		
		// creates widgets
		nameField = new JTextField();
		noOfTrace = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getInteger(KEY_NO_TRACES, DEFAULTS.getNumberOfTraces()), 1, 1000000, 1));
		noiseIntegerData = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_INT_DATA, DEFAULTS.getNoiseConfiguration().getIntegerDataNoiseProbability()) * 1000), 0, 1000, 1));
		noiseIntegerDelta = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getInteger(KEY_NOISE_INT_DATA_DELTA, DEFAULTS.getNoiseConfiguration().getIntegerDataNoiseDelta()), 1, 5, 1));
		noiseStringData = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_STR_DATA, DEFAULTS.getNoiseConfiguration().getStringDataNoiseProbability()) * 1000), 0, 1000, 1));
		
		noiseActivityName = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getDouble(KEY_NOISE_ACT_NAME, (int) (DEFAULTS.getNoiseConfiguration().getActivityNameNoiseProbability()) * 1000), 0, 1000, 1));
		
		noiseTraceMissingHead = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_TRACE_MISSING_HEAD, DEFAULTS.getNoiseConfiguration().getTraceMissingHeadNoiseProbability()) * 1000), 0, 1000, 1));
		noiseTraceHeadSize = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getInteger(KEY_NOISE_TRACE_HEAD_SIZE, DEFAULTS.getNoiseConfiguration().getTraceMissingHeadSize()), 1, 20, 1));
		noiseTraceMissingTail = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_TRACE_MISSING_TAIL, DEFAULTS.getNoiseConfiguration().getTraceMissingTailNoiseProbability()) * 1000), 0, 1000, 1));
		noiseTraceTailSize = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getInteger(KEY_NOISE_TRACE_TAIL_SIZE, DEFAULTS.getNoiseConfiguration().getTraceMissingTailSize()), 1, 20, 1));
		noiseTraceMissingEpisode = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_TRACE_MISSING_EPISODE, DEFAULTS.getNoiseConfiguration().getTraceMissingEpisodeNoiseProbability()) * 1000), 0, 1000, 1));
		noiseTraceEpisodeSize = new JSpinner(new SpinnerNumberModel(
				(int) configuration.getInteger(KEY_NOISE_TRACE_EPISODE_SIZE, DEFAULTS.getNoiseConfiguration().getTraceMissingEpisodeSize()), 1, 20, 1));
		noiseTracePerturbedOrder = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_PERTURBED_ORDER, DEFAULTS.getNoiseConfiguration().getPerturbedOrderNoiseProbability()) * 1000), 0, 1000, 1));
		noiseTraceDoubledEvent = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_DOUBLED_EVENT, DEFAULTS.getNoiseConfiguration().getDoubleEventNoiseProbability()) * 1000), 0, 1000, 1));
		noiseTraceAlienEvent = new JSpinner(new SpinnerNumberModel(
				(int) (configuration.getDouble(KEY_NOISE_ALIEN_EVENT, DEFAULTS.getNoiseConfiguration().getAlienEventNoiseProbability()) * 1000), 0, 1000, 1));
		
		okButton = new JButton("OK");
		resetButton = new JButton("Reset values");
		
		// style widgets
		
		// insert footer buttons
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configuration.setInteger(KEY_NO_TRACES, Integer.parseInt(noOfTrace.getValue().toString()));
				configuration.setDouble(KEY_NOISE_INT_DATA, new Double((Integer) noiseIntegerData.getValue()) / 1000d);
				configuration.setInteger(KEY_NOISE_INT_DATA_DELTA, Integer.parseInt(noiseIntegerDelta.getValue().toString()));
				configuration.setDouble(KEY_NOISE_STR_DATA, new Double((Integer) noiseStringData.getValue()) / 1000d);
				configuration.setDouble(KEY_NOISE_ACT_NAME, new Double((Integer) noiseActivityName.getValue()) / 1000d);
				configuration.setDouble(KEY_NOISE_TRACE_MISSING_HEAD, new Double((Integer) noiseTraceMissingHead.getValue()) / 1000d);
				configuration.setInteger(KEY_NOISE_TRACE_HEAD_SIZE, Integer.parseInt(noiseTraceHeadSize.getValue().toString()));
				configuration.setDouble(KEY_NOISE_TRACE_MISSING_TAIL, new Double((Integer) noiseTraceMissingTail.getValue()) / 1000d);
				configuration.setInteger(KEY_NOISE_TRACE_TAIL_SIZE, Integer.parseInt(noiseTraceTailSize.getValue().toString()));
				configuration.setDouble(KEY_NOISE_TRACE_MISSING_EPISODE, new Double((Integer) noiseTraceMissingEpisode.getValue()) / 1000d);
				configuration.setInteger(KEY_NOISE_TRACE_EPISODE_SIZE, Integer.parseInt(noiseTraceEpisodeSize.getValue().toString()));
				configuration.setDouble(KEY_NOISE_PERTURBED_ORDER, new Double((Integer) noiseTracePerturbedOrder.getValue()) / 1000d);
				configuration.setDouble(KEY_NOISE_DOUBLED_EVENT, new Double((Integer) noiseTraceDoubledEvent.getValue()) / 1000d);
				configuration.setDouble(KEY_NOISE_ALIEN_EVENT, new Double((Integer) noiseTraceAlienEvent.getValue()) / 1000d);
				
				NoiseConfiguration noise = new NoiseConfiguration (
						new Double((Integer) noiseIntegerData.getValue()) / 1000d,
						Integer.parseInt(noiseIntegerDelta.getValue().toString()),
						new Double((Integer) noiseStringData.getValue()) / 1000d,
						new Double((Integer) noiseActivityName.getValue()) / 1000d,
						new Double((Integer) noiseTraceMissingHead.getValue()) / 1000d,
						Integer.parseInt(noiseTraceHeadSize.getValue().toString()),
						new Double((Integer) noiseTraceMissingTail.getValue()) / 1000d,
						Integer.parseInt(noiseTraceTailSize.getValue().toString()),
						new Double((Integer) noiseTraceMissingEpisode.getValue()) / 1000d,
						Integer.parseInt(noiseTraceEpisodeSize.getValue().toString()),
						new Double((Integer) noiseTracePerturbedOrder.getValue()) / 1000d,
						new Double((Integer) noiseTraceDoubledEvent.getValue()) / 1000d,
						new Double((Integer) noiseTraceAlienEvent.getValue()) / 1000d);
				
				userConfiguration = new SimulationConfiguration(Integer.parseInt(noOfTrace.getValue().toString()), noise);
				returnedValue = RETURNED_VALUES.SUCCESS;
				NewLogDialog.this.dispose();
			}
		});
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimulationConfiguration s = DEFAULTS;
				NoiseConfiguration n = s.getNoiseConfiguration();
				
				noOfTrace.setValue(s.getNumberOfTraces());
				noiseIntegerData.setValue(n.getIntegerDataNoiseProbability() * 1000);
				noiseIntegerDelta.setValue(n.getIntegerDataNoiseDelta());
				noiseStringData.setValue(n.getStringDataNoiseProbability() * 1000);
				noiseActivityName.setValue(n.getActivityNameNoiseProbability() * 1000);
				noiseTraceMissingHead.setValue(n.getTraceMissingHeadNoiseProbability() * 1000);
				noiseTraceHeadSize.setValue(n.getTraceMissingHeadSize());
				noiseTraceMissingTail.setValue(n.getTraceMissingTailNoiseProbability() * 1000);
				noiseTraceTailSize.setValue(n.getTraceMissingTailSize());
				noiseTraceMissingEpisode.setValue(n.getTraceMissingEpisodeNoiseProbability() * 1000);
				noiseTraceEpisodeSize.setValue(n.getTraceMissingEpisodeSize());
				noiseTracePerturbedOrder.setValue(n.getPerturbedOrderNoiseProbability() * 1000);
				noiseTraceDoubledEvent.setValue(n.getDoubleEventNoiseProbability() * 1000);
				noiseTraceAlienEvent.setValue(n.getAlienEventNoiseProbability() * 1000);
			}
		});
		
		addFooterButton(resetButton, false);
		addFooterButton(okButton, true);
		
		// new log name and no of traces
		nameField.setText(candidateProcessName);
		bodyPanel.add(prepareFieldLabel("New log name"));
		bodyPanel.add(nameField);
		bodyPanel.add(prepareFieldLabel("Number of traces"));
		bodyPanel.add(noOfTrace);
		insertBodySeparator(10);
		
		// noise
		bodyPanel.add(prepareFieldLabel("Change activity name prob. (‰)"));
		bodyPanel.add(noiseActivityName);
		bodyPanel.add(prepareFieldLabel("Trace missing head prob. (‰)"));
		bodyPanel.add(noiseTraceMissingHead);
		bodyPanel.add(prepareFieldLabel("Trace missing tail prob. (‰)"));
		bodyPanel.add(noiseTraceMissingTail);
		bodyPanel.add(prepareFieldLabel("Trace missing episode prob. (‰)"));
		bodyPanel.add(noiseTraceMissingEpisode);
		bodyPanel.add(prepareFieldLabel("Perturbed event order prob. (‰)"));
		bodyPanel.add(noiseTracePerturbedOrder);
		bodyPanel.add(prepareFieldLabel("Doubled event prob. (‰)"));
		bodyPanel.add(noiseTraceDoubledEvent);
		bodyPanel.add(prepareFieldLabel("Alient event prob. (‰)"));
		bodyPanel.add(noiseTraceAlienEvent);
		bodyPanel.add(prepareFieldLabel("Integer data object error prob. (‰)"));
		bodyPanel.add(noiseIntegerData);
		bodyPanel.add(prepareFieldLabel("String data object error prob. (‰)"));
		bodyPanel.add(noiseStringData);
		insertBodySeparator(10);
		
		bodyPanel.add(prepareFieldLabel("Head max size"));
		bodyPanel.add(noiseTraceHeadSize);
		bodyPanel.add(prepareFieldLabel("Tail max size"));
		bodyPanel.add(noiseTraceTailSize);
		bodyPanel.add(prepareFieldLabel("Episode max size"));
		bodyPanel.add(noiseTraceEpisodeSize);
		bodyPanel.add(prepareFieldLabel("Integer data object error delta"));
		bodyPanel.add(noiseIntegerDelta);
		
		// layout everything
		layoutBody();
	}
	
	/**
	 * Dialog constructor
	 * 
	 * @param owner the frame owning the dialog
	 * @param candidateName the candidate name for the new process
	 */
	public NewLogDialog(JFrame owner, String candidateName) {
		this(candidateName, owner,
			"Simulation Configuration",
			"Use this dialog to configure the simulation of a process.",
			ApplicationController.instance().getConfiguration(NewLogDialog.class.getCanonicalName()));
	}
	
	/**
	 * This method returns the configuration set up by the user
	 * 
	 * @return the configuration set
	 */
	public SimulationConfiguration getConfiguredValues() {
		return userConfiguration;
	}
	
	/**
	 * This method returns the name, set by the user, of the new log
	 * 
	 * @return the name of the new log
	 */
	public String getNewLogName() {
		return nameField.getText();
	}
}
