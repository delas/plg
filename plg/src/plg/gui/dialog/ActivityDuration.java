package plg.gui.dialog;

import javax.swing.JFrame;

import plg.gui.controller.ApplicationController;

public class ActivityDuration extends CodeDialog {

	private static final long serialVersionUID = -1273109278694623349L;

	public ActivityDuration(JFrame owner) {
		super(owner,
			"Activity Duration Setter",
			"In the area below you can insert the Python script to setup the activity duration",
			ApplicationController.instance().getConfiguration(ActivityDuration.class.getCanonicalName()));
	}

	@Override
	protected String standardScript() {
		return getFile("/plg/resources/scripts/activityDurationTemplate.py");
	}
}
