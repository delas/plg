package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JSlider;

import plg.generator.process.EvolutionConfiguration;
import plg.generator.process.RandomizationConfiguration;
import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of the parameters for
 * the random evolution of an existing process model
 *
 * @author Andrea Burattin
 */
public class EvolutionDialog extends NewProcessDialog {

	/**
	 * This is a test configuration with basic random values for a process evolution
	 */
	public static final RandomizationConfiguration BASIC_VALUES_EVOLUTION = new RandomizationConfiguration(
			2, // max AND branches
			2, // max XOR branches
			0.01, // loop weight
			0.5, // single activity weight
			0.25, // skip weight
			0.5, // sequence weight
			0.01, // AND weight
			0.01, // XOR weight
			2, // maximum depth
			0.1 // data object probability
		);
	
	private static final long serialVersionUID = -6996256974716520028L;
	protected RandomizationConfiguration DEFAULTS = BASIC_VALUES_EVOLUTION;
	protected JSlider evolutionProbabilitySlider = null;

	/**
	 * Dialog constructor
	 * 
	 * @param owner the frame owning the dialog
	 * @param candidateName the candidate name for the new process
	 */
	public EvolutionDialog(JFrame owner, String candidateName) {
		super(candidateName, owner,
			"Process Evolution Configuration",
			"Use this dialog to set the parameters for the process random evolution.",
			ApplicationController.instance().getConfiguration(EvolutionDialog.class.getCanonicalName()));
		
		// creates widgets
		evolutionProbabilitySlider = new JSlider(0, 1000, 100);
		
		// style widgets
		evolutionProbabilitySlider.setPaintTicks(true);
		
		// add stuff
		bodyPanel.add(prepareFieldLabel("Evolution probability"));
		bodyPanel.add(evolutionProbabilitySlider);
		
		// layout everything
		layoutBody();
		
		reset();
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
	}
	
	@Override
	public EvolutionConfiguration getConfiguredValues() {
		return new EvolutionConfiguration(
				((double) evolutionProbabilitySlider.getValue()) / 1000d,
				super.getConfiguredValues());
	}
	
	protected void reset() {
		RandomizationConfiguration v = DEFAULTS;
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
}
