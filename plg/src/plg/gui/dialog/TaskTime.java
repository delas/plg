package plg.gui.dialog;

import javax.swing.JFrame;

import plg.gui.controller.ApplicationController;

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
		return getFile("/plg/resources/scripts/taskTime.py");
	}
}
