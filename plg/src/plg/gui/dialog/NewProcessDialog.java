package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import plg.generator.process.RandomizationConfiguration;
import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of the parameters for
 * the random creation of a new process model
 *
 * @author Andrea Burattin
 */
public class NewProcessDialog extends GeneralDialog {

	private static final long serialVersionUID = -4781877672157619819L;
	protected static final String KEY_MAX_DEPTH = "MAX_DEPTH";
	protected static final String KEY_SEQUENCE_WEIGHT = "SEQUENCE_WEIGHT";
	protected static final String KEY_SINGLE_WEIGHT = "SINGLE_WEIGHT";
	protected static final String KEY_SKIP_WEIGHT = "SKIP_WEIGHT";
	protected static final String KEY_AND_WEIGHT = "AND_WEIGHT";
	protected static final String KEY_XOR_WEIGHT = "WEIGHT";
	protected static final String KEY_LOOP_WEIGHT = "LOOP_WEIGHT";
	protected static final String KEY_AND_BRANCHES = "AND_BRANCHES";
	protected static final String KEY_XOR_BRANCHES = "XOR_BRANCHES";
	protected static final String KEY_DATA_OBJECTS = "DATA_OBJECTS";
	
	protected final RandomizationConfiguration DEFAULTS = RandomizationConfiguration.BASIC_VALUES;
	private RandomizationConfiguration userConfiguration = null;
	
	protected JTextField nameField = null;
	protected JSpinner depthSpinner = null;
	protected JSlider sequenceWeightSlider = null;
	protected JSlider singleWeightSlider = null;
	protected JSlider skipWeightSlider = null;
	protected JSlider andWeightSlider = null;
	protected JSlider xorWeightSlider = null;
	protected JSlider loopWeightSlider = null;
	protected JSlider dataObjectsSlider = null;
	protected JSpinner andBranchesSpinner = null;
	protected JSpinner xorBranchesSpinner = null;
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
	protected NewProcessDialog(String candidateProcessName, JFrame owner, String title, String help, final ConfigurationSet configuration) {
		super(owner,title, help, configuration);
		
		// creates widgets
		nameField = new JTextField();
		depthSpinner = new JSpinner(new SpinnerNumberModel(
				configuration.getInteger(KEY_MAX_DEPTH, DEFAULTS.getMaximumDepth()), 1, 20, 1));
		sequenceWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_SEQUENCE_WEIGHT, DEFAULTS.getSequenceWeight()) * 100));
		singleWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_SINGLE_WEIGHT, DEFAULTS.getSingleActivityWeight()) * 100));
		skipWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_SKIP_WEIGHT, DEFAULTS.getSkipWeight()) * 100));
		andWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_AND_WEIGHT, DEFAULTS.getANDWeight()) * 100));
		xorWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_XOR_WEIGHT, DEFAULTS.getXORWeight()) * 100));
		loopWeightSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_LOOP_WEIGHT, DEFAULTS.getLoopWeight()) * 100));
		dataObjectsSlider = new JSlider(0, 100,
				(int) (configuration.getDouble(KEY_DATA_OBJECTS, DEFAULTS.getDataObjectProbability()) * 100));
		andBranchesSpinner = new JSpinner(new SpinnerNumberModel(
				configuration.getInteger(KEY_AND_BRANCHES, DEFAULTS.getAndBranches()), 2, 10, 1));
		xorBranchesSpinner = new JSpinner(new SpinnerNumberModel(
				configuration.getInteger(KEY_XOR_BRANCHES, DEFAULTS.getXorBranches()), 2, 10, 1));
		
		okButton = new JButton("OK");
		resetButton = new JButton("Reset values");
		
		// style widgets
		sequenceWeightSlider.setPaintTicks(true);
		singleWeightSlider.setPaintTicks(true);
		skipWeightSlider.setPaintTicks(true);
		andWeightSlider.setPaintTicks(true);
		xorWeightSlider.setPaintTicks(true);
		loopWeightSlider.setPaintTicks(true);
		dataObjectsSlider.setPaintTicks(true);
		
		// insert footer buttons
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configuration.setInteger(KEY_MAX_DEPTH, Integer.parseInt(depthSpinner.getValue().toString()));
				configuration.setDouble(KEY_SEQUENCE_WEIGHT, (double)(sequenceWeightSlider.getValue() / 100d));
				configuration.setDouble(KEY_SINGLE_WEIGHT, (double)(singleWeightSlider.getValue() / 100d));
				configuration.setDouble(KEY_SKIP_WEIGHT, (double)(skipWeightSlider.getValue() / 100d));
				configuration.setDouble(KEY_AND_WEIGHT, (double)(andWeightSlider.getValue() / 100d));
				configuration.setDouble(KEY_XOR_WEIGHT, (double)(xorWeightSlider.getValue() / 100d));
				configuration.setDouble(KEY_LOOP_WEIGHT, (double)(loopWeightSlider.getValue() / 100d));
				configuration.setInteger(KEY_AND_BRANCHES, Integer.parseInt(andBranchesSpinner.getValue().toString()));
				configuration.setInteger(KEY_XOR_BRANCHES, Integer.parseInt(xorBranchesSpinner.getValue().toString()));
				
				userConfiguration = new RandomizationConfiguration(
						Integer.parseInt(andBranchesSpinner.getValue().toString()),
						Integer.parseInt(xorBranchesSpinner.getValue().toString()),
						(double)(loopWeightSlider.getValue() / 100d),
						(double)(singleWeightSlider.getValue() / 100d),
						(double)(skipWeightSlider.getValue() / 100d),
						(double)(sequenceWeightSlider.getValue() / 100d),
						(double)(andWeightSlider.getValue() / 100d),
						(double)(xorWeightSlider.getValue() / 100d),
						Integer.parseInt(depthSpinner.getValue().toString()),
						(double)(dataObjectsSlider.getValue() / 100d));
				returnedValue = RETURNED_VALUES.SUCCESS;
				NewProcessDialog.this.dispose();
			}
		});
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RandomizationConfiguration v = RandomizationConfiguration.BASIC_VALUES;
				andBranchesSpinner.setValue(v.getAndBranches());
				xorBranchesSpinner.setValue(v.getXorBranches());
				loopWeightSlider.setValue((int) (v.getLoopWeight() * 100));
				singleWeightSlider.setValue((int) (v.getSingleActivityWeight() * 100));
				skipWeightSlider.setValue((int) (v.getSkipWeight() * 100));
				sequenceWeightSlider.setValue((int) (v.getSequenceWeight() * 100));
				andWeightSlider.setValue((int) (v.getANDWeight() * 100));
				xorWeightSlider.setValue((int) (v.getXORWeight() * 100));
				depthSpinner.setValue(v.getMaximumDepth());
				dataObjectsSlider.setValue((int) (v.getDataObjectProbability() * 100));
			}
		});
		
		addFooterButton(resetButton, false);
		addFooterButton(okButton, true);
		
		// new process name
		nameField.setText(candidateProcessName);
		bodyPanel.add(prepareFieldLabel("New process name"));
		bodyPanel.add(nameField);
		insertBodySeparator(10);
		
		// max depth
		bodyPanel.add(prepareFieldLabel("Maximum depth"));
		bodyPanel.add(depthSpinner);
		
		// beanches
		bodyPanel.add(prepareFieldLabel("Max AND branches"));
		bodyPanel.add(andBranchesSpinner);
		bodyPanel.add(prepareFieldLabel("Max XOR branches"));
		bodyPanel.add(xorBranchesSpinner);
		insertBodySeparator(10);
		
		// add weights
		bodyPanel.add(prepareFieldLabel("Sequence weight"));
		bodyPanel.add(sequenceWeightSlider);
		bodyPanel.add(prepareFieldLabel("Single activity weight"));
		bodyPanel.add(singleWeightSlider);
		bodyPanel.add(prepareFieldLabel("Skip weight"));
		bodyPanel.add(skipWeightSlider);
		bodyPanel.add(prepareFieldLabel("AND weight"));
		bodyPanel.add(andWeightSlider);
		bodyPanel.add(prepareFieldLabel("XOR weight"));
		bodyPanel.add(xorWeightSlider);
		bodyPanel.add(prepareFieldLabel("Loop weight"));
		bodyPanel.add(loopWeightSlider);
		insertBodySeparator(10);
		
		// data objects
		bodyPanel.add(prepareFieldLabel("Data objects probability"));
		bodyPanel.add(dataObjectsSlider);
		
		// layout everything
		layoutBody();
	}
	
	/**
	 * Dialog constructor
	 * 
	 * @param owner the frame owning the dialog
	 * @param candidateName the candidate name for the new process
	 */
	public NewProcessDialog(JFrame owner, String candidateName) {
		this(candidateName, owner,
			"New Random Process Configuration",
			"Use this dialog to set the new process parameters.",
			ApplicationController.instance().getConfiguration(NewProcessDialog.class.getCanonicalName()));
	}
	
	/**
	 * This method returns the configuration set up by the user
	 * 
	 * @return the configuration set
	 */
	public RandomizationConfiguration getConfiguredValues() {
		return userConfiguration;
	}
	
	/**
	 * This method returns the name, set by the user, of the new process
	 * 
	 * @return the name of the new process
	 */
	public String getNewProcessName() {
		return nameField.getText();
	}
}
