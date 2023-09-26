package plg.gui.dialog;

import javax.swing.JFrame;

import plg.gui.controller.ApplicationController;
import plg.gui.util.collections.ScriptsCollection;

/**
 * This dialog can be used to configure the script for the simulation of the
 * times relationed information
 * 
 * @author Andrea Burattin
 */
public class TaskTime extends CodeDialog {

	private static final long serialVersionUID = -1273109278694623349L;

	public TaskTime(JFrame owner) {
		super(owner,
			"Activity Time Setter",
			"In the area below you can insert the Python script to setup the task time scripts",
			ApplicationController.instance().getConfiguration(TaskTime.class.getCanonicalName()));
	}

	@Override
	protected String standardScript() {
		return ScriptsCollection.TIME_SCRIPT;
	}
}
