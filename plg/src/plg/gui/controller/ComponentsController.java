package plg.gui.controller;

import javax.swing.JOptionPane;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.TaskTime;
import plg.gui.panels.SingleProcessVisualizer;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;
import plg.utils.Logger;

public class ComponentsController {

	private ApplicationController applicationController;
	private SingleProcessVisualizer singleProcessVisualizer;
	
	public ComponentsController(
			ApplicationController applicationController,
			SingleProcessVisualizer singleProcessVisualizer) {
		this.applicationController = applicationController;
		this.singleProcessVisualizer = singleProcessVisualizer;
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
		if (JOptionPane.showConfirmDialog(ApplicationController.instance().getMainFrame(),
				"Are you sure to delete the data object?",
				"Confirm deletion",
				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
			return;
		}
		
		Logger.instance().info("Removed data object (`" + dataObject.getName() + "')");
		
		// remove the data object from the process
		Process p = dataObject.getOwner();
		p.removeComponent(dataObject);
		
		// remove the dependency for the data object
		IDataObjectOwner owner = dataObject.getObjectOwner();
		owner.removeDataObject(dataObject);
		
		// refresh the view
		singleProcessVisualizer.refreshCurrentProcess();
	}
}
