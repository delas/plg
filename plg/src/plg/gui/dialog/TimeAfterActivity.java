package plg.gui.dialog;

import javax.swing.JFrame;

import plg.gui.controller.ApplicationController;

public class TimeAfterActivity extends CodeDialog {

	private static final long serialVersionUID = -1273109278694623349L;

	public TimeAfterActivity(JFrame owner) {
		super(owner,
			"Time After Activity",
			"In the area below you can insert the Python script to setup the time to wait before starting the following activity",
			ApplicationController.instance().getConfiguration(TimeAfterActivity.class.getCanonicalName()));
	}

	@Override
	protected String standardScript() {
		return getFile("/plg/resources/scripts/timeAfterActivityTemplate.py");
	}
}
