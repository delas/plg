package plg.gui.dialog;

import javax.swing.JFrame;

import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of the parameters for
 * the simulation of traces for streaming
 *
 * @author Andrea Burattin
 */
public class StreamNoiseDialog extends NewLogDialog {

	private static final long serialVersionUID = -4781877672157619819L;
	
	/**
	 * Dialog constructor
	 * 
	 * @param owner the frame owning the dialog
	 */
	public StreamNoiseDialog(JFrame owner) {
		super(null, owner,
				"Stream Simulation Configuration",
				"Use this dialog to configure the stream simulation of a process.",
				ApplicationController.instance().getConfiguration(StreamNoiseDialog.class.getCanonicalName()),
				false);
	}
}
