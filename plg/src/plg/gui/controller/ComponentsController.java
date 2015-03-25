package plg.gui.controller;

import plg.gui.dialog.ActivityDuration;
import plg.gui.dialog.TimeAfterActivity;
import plg.model.activity.Activity;

public class ComponentsController {

	private ApplicationController applicationController;
	
	public ComponentsController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	public void setActivityDuration(Activity activity) {
		ActivityDuration ad = new ActivityDuration(applicationController.getMainFrame());
		ad.setVisible(true);
	}

	public void setTimeAfterActivity(Activity activity) {
		TimeAfterActivity ad = new TimeAfterActivity(applicationController.getMainFrame());
		ad.setVisible(true);
	}
}
