package plg.gui.dialog;

import javax.swing.JFrame;

import plg.generator.process.RandomizationConfiguration;
import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of the parameters for
 * the random evolution of an existing process model
 *
 * @author Andrea Burattin
 */
public class EvolutionDialog extends NewProcessDialog {

	private static final long serialVersionUID = -6996256974716520028L;
	protected RandomizationConfiguration DEFAULTS = RandomizationConfiguration.BASIC_VALUES_EVOLUTION;

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
	}
}
