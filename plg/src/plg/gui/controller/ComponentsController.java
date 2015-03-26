package plg.gui.controller;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.TaskTime;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;

public class ComponentsController {

	private ApplicationController applicationController;
	
	public ComponentsController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	public void setTaskTime(Task task) {
		TaskTime ad = new TaskTime(applicationController.getMainFrame());
		if (task.getActivityScript() != null && !task.getActivityScript().getScript().isEmpty()) {
			ad.setScript(task.getActivityScript().getScript());
		}
		ad.setVisible(true);
		if (ad.returnedValue() == RETURNED_VALUES.SUCCESS) {
			task.setActivityScript(new IntegerScriptExecutor(ad.getScript()));
		}
	}
	
	public void removeDataObject(DataObject dataObject) {
		IDataObjectOwner owner = dataObject.getObjectOwner();
		owner.removeDataObject(dataObject);
	}
}
