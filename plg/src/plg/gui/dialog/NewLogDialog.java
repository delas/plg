package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
	
	protected static final String KEY_SELECTED_PRESET = "SELECTED_PRESET";
	protected static final String NAME_COMPLETE_NOISE = "Complete noise";
	protected static final String NAME_NO_NOISE = "No noise";
	protected static final String NAME_ONLY_NAMES_NOISE = "Noise only activity names";
	protected static final String NAME_ONLY_DO_NOISE = "Noise only on data objects";
	protected static final String NAME_ONLY_CONTROL_FLOW_NOISE = "Noise only on the control-flow";
	protected static final String NAME_USER_SETTING = "User setting";
	
	protected Map<String, SimulationConfiguration> CONFIGURATIONS = new HashMap<String, SimulationConfiguration>();
	protected SimulationConfiguration currentConfiguration = null;
	
	protected JComboBox<PresetConfiguration> presetConfigurations = null;
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
		
		CONFIGURATIONS.put(NAME_COMPLETE_NOISE, new SimulationConfiguration(1000, NoiseConfiguration.COMPLETE_NOISE));
		CONFIGURATIONS.put(NAME_NO_NOISE, new SimulationConfiguration(1000, NoiseConfiguration.NO_NOISE));
		CONFIGURATIONS.put(NAME_ONLY_NAMES_NOISE, new SimulationConfiguration(1000, NoiseConfiguration.ONLY_NAMES_NOISE));
		CONFIGURATIONS.put(NAME_ONLY_DO_NOISE, new SimulationConfiguration(1000, NoiseConfiguration.ONLY_DO_NOISE));
		CONFIGURATIONS.put(NAME_ONLY_CONTROL_FLOW_NOISE, new SimulationConfiguration(1000, NoiseConfiguration.ONLY_CONTROL_FLOW_NOISE));
		
		SimulationConfiguration DEFAULTS = CONFIGURATIONS.get(NAME_NO_NOISE);
		
		// creates widgets
		presetConfigurations = new JComboBox<PresetConfiguration>();
		for (String k : CONFIGURATIONS.keySet()) {
			PresetConfiguration pc = new PresetConfiguration(k, CONFIGURATIONS.get(k));
			presetConfigurations.addItem(pc);
			if (k.equals(configuration.get(KEY_SELECTED_PRESET, NAME_NO_NOISE))) {
				presetConfigurations.setSelectedItem(pc);
				currentConfiguration = pc.configuration;
				DEFAULTS = pc.configuration;
			}
		}
		
		nameField = new JTextField();
		noOfTrace = new JSpinner(new SpinnerNumberModel(
				DEFAULTS.getNumberOfTraces(), 1, 1000000, 1));
		noiseIntegerData = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getIntegerDataNoiseProbability() * 1000), 0, 1000, 1));
		noiseIntegerDelta = new JSpinner(new SpinnerNumberModel(
				(int) DEFAULTS.getNoiseConfiguration().getIntegerDataNoiseDelta(), 1, 5, 1));
		noiseStringData = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getStringDataNoiseProbability() * 1000), 0, 1000, 1));
		noiseActivityName = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getActivityNameNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceMissingHead = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getTraceMissingHeadNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceHeadSize = new JSpinner(new SpinnerNumberModel(
				(int) DEFAULTS.getNoiseConfiguration().getTraceMissingHeadSize(), 1, 20, 1));
		noiseTraceMissingTail = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getTraceMissingTailNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceTailSize = new JSpinner(new SpinnerNumberModel(
				(int) DEFAULTS.getNoiseConfiguration().getTraceMissingTailSize(), 1, 20, 1));
		noiseTraceMissingEpisode = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getTraceMissingEpisodeNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceEpisodeSize = new JSpinner(new SpinnerNumberModel(
				(int) DEFAULTS.getNoiseConfiguration().getTraceMissingEpisodeSize(), 1, 20, 1));
		noiseTracePerturbedOrder = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getPerturbedOrderNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceDoubledEvent = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getDoubleEventNoiseProbability() * 1000), 0, 1000, 1));
		noiseTraceAlienEvent = new JSpinner(new SpinnerNumberModel(
				(double) (DEFAULTS.getNoiseConfiguration().getAlienEventNoiseProbability() * 1000), 0, 1000, 1));
		
		okButton = new JButton("OK");
		
		// insert footer buttons
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NoiseConfiguration noise = new NoiseConfiguration (
						(double) noiseIntegerData.getValue() / 1000d,
						(int) noiseIntegerDelta.getValue(),
						(double) noiseStringData.getValue() / 1000d,
						(double) noiseActivityName.getValue() / 1000d,
						(double) noiseTraceMissingHead.getValue() / 1000d,
						(int) noiseTraceHeadSize.getValue(),
						(double) noiseTraceMissingTail.getValue() / 1000d,
						(int) noiseTraceTailSize.getValue(),
						(double) noiseTraceMissingEpisode.getValue() / 1000d,
						(int) noiseTraceEpisodeSize.getValue(),
						(double) noiseTracePerturbedOrder.getValue() / 1000d,
						(double) noiseTraceDoubledEvent.getValue() / 1000d,
						(double) noiseTraceAlienEvent.getValue() / 1000d);
				
				currentConfiguration = new SimulationConfiguration(Integer.parseInt(noOfTrace.getValue().toString()), noise);
				returnedValue = RETURNED_VALUES.SUCCESS;
				NewLogDialog.this.dispose();
			}
		});
		presetConfigurations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PresetConfiguration c = (PresetConfiguration) presetConfigurations.getSelectedItem();
				configuration.set(KEY_SELECTED_PRESET, c.name);
				NoiseConfiguration n = c.configuration.getNoiseConfiguration();
				// noOfTrace.setValue(s.getNumberOfTraces());
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
		
		addFooterButton(okButton, true);
		
		// presets
		bodyPanel.add(prepareFieldLabel("Configuration presets"));
		bodyPanel.add(presetConfigurations);
		insertBodySeparator(10);
		
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
		return currentConfiguration;
	}
	
	/**
	 * This method returns the name, set by the user, of the new log
	 * 
	 * @return the name of the new log
	 */
	public String getNewLogName() {
		return nameField.getText();
	}
	
	/**
	 * This class contains the values on the preset combo box
	 */
	private class PresetConfiguration {
		public String name;
		public SimulationConfiguration configuration;
		
		public PresetConfiguration(String name, SimulationConfiguration configuration) {
			this.name = name;
			this.configuration = configuration;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
