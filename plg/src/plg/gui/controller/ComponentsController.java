package plg.gui.controller;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.TaskTime;
import plg.model.activity.Task;

public class ComponentsController {

	private ApplicationController applicationController;
	
	public ComponentsController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	public void setTaskTime(Task task) {
		TaskTime ad = new TaskTime(applicationController.getMainFrame());
		if (task.getActivityScript() != null) {
			ad.setScript(task.getActivityScript().getScript());
		}
		ad.setVisible(true);
		if (ad.returnedValue() == RETURNED_VALUES.SUCCESS) {
			task.setActivityScript(new IntegerScriptExecutor(ad.getScript()));
		}
	}
}
