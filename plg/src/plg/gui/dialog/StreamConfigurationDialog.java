package plg.gui.dialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;

public class StreamConfigurationDialog extends GeneralDialog {

	private static final long serialVersionUID = 4780160624995326441L;
	
	protected JSpinner portSpinner = null;
	protected JSpinner parallelInstancesSpinner = null;
	protected JSpinner timeFractionSpinner = null;
	protected JCheckBox markBeginningEndCheckBox = null;
	protected JButton okButton = null;

	public StreamConfigurationDialog(JFrame owner, String title, String help, ConfigurationSet configuration) {
		super(owner, title, help, configuration);
		
		portSpinner = new JSpinner(new SpinnerNumberModel(1234, 1, 9999, 1));
		parallelInstancesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
		timeFractionSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0, 10, 0.01));
		markBeginningEndCheckBox = new JCheckBox("Mark beginning and end of traces");
		
		okButton = new JButton("OK");
		
		bodyPanel.add(prepareFieldLabel("Network port"));
		bodyPanel.add(portSpinner);
		insertBodySeparator(10);
		
		bodyPanel.add(prepareFieldLabel("Maximum number of parallel instances"));
		bodyPanel.add(parallelInstancesSpinner);
		bodyPanel.add(prepareFieldLabel("Time fraction before new trace"));
		bodyPanel.add(timeFractionSpinner);
		bodyPanel.add(prepareFieldLabel(""));
		bodyPanel.add(markBeginningEndCheckBox);

		insertBodySeparator(175);
		
		addFooterButton(okButton, true);
		
		// layout everything
		layoutBody();
	}
	
	public StreamConfigurationDialog(JFrame owner) {
		this(owner,
			"Stream Configuration",
			"Configuration of the stream parameters.",
			ApplicationController.instance().getConfiguration(NewProcessDialog.class.getCanonicalName()));
	}

}
