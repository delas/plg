package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import plg.generator.process.RandomizationConfiguration;
import plg.gui.util.SpringUtilities;

/**
 * 
 *
 * @author Andrea Burattin
 */
public class NewProcessDialog extends GeneralDialog {

	private static final long serialVersionUID = -4781877672157619819L;
	private static final RandomizationConfiguration DEFAULTS = RandomizationConfiguration.BASIC_VALUES;
	
	protected JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(DEFAULTS.getMaximumDepth(), 1, 20, 1));
	protected JSlider sequenceWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getSequenceWeight() * 100));
	protected JSlider singleWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getSingleActivityWeight() * 100));
	protected JSlider skipWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getSkipWeight() * 100));
	protected JSlider andWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getANDWeight() * 100));
	protected JSlider xorWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getXORWeight() * 100));
	protected JSlider loopWeightSlider = new JSlider(0, 100, (int) (DEFAULTS.getLoopWeight() * 100));
	
	protected JSpinner andBranchesSpinner = new JSpinner(new SpinnerNumberModel(DEFAULTS.getAndBranches(), 2, 10, 1));
	protected JSpinner xorBranchesSpinner = new JSpinner(new SpinnerNumberModel(DEFAULTS.getXorBranches(), 2, 10, 1));
	
	protected JButton okButton = new JButton("OK");

	/**
	 * 
	 * @param owner
	 */
	public NewProcessDialog(JFrame owner) {
		super(owner, "New Process", "Use this dialog to set the new process parameters.");
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configuredValues();
//				NewProcessDialog.this.dispose();
			}
		});
		
		addFooterButton(okButton, true);
		
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
		
		// layout everything
		layoutBody();
	}
	
	/**
	 * 
	 * @return
	 */
	public RandomizationConfiguration configuredValues() {
		RandomizationConfiguration configuration = new RandomizationConfiguration(
				Integer.parseInt(andBranchesSpinner.getValue().toString()),
				Integer.parseInt(xorBranchesSpinner.getValue().toString()),
				(double)(loopWeightSlider.getValue() / 100d),
				(double)(singleWeightSlider.getValue() / 100d),
				(double)(skipWeightSlider.getValue() / 100d),
				(double)(sequenceWeightSlider.getValue() / 100d),
				(double)(andWeightSlider.getValue() / 100d),
				(double)(xorWeightSlider.getValue() / 100d),
				Integer.parseInt(depthSpinner.getValue().toString()));
		System.out.println(configuration);
		return null;
	}




	public static void main(String args[]) {
		JFrame f = new JFrame("test");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setLocation(300, 300);
		f.setVisible(true);
		GeneralDialog d = new NewProcessDialog(f);
		d.setVisible(true);
	}

}
