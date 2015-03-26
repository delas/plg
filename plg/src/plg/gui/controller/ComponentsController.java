package plg.gui.controller;

import javax.swing.JOptionPane;

import plg.generator.process.ProcessGenerator;
import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.gui.dialog.GeneralDialog.RETURNED_VALUES;
import plg.gui.dialog.PlainDataObjectDialog;
import plg.gui.dialog.ScriptDataObjectDialog;
import plg.gui.dialog.TaskTime;
import plg.gui.panels.SingleProcessVisualizer;
import plg.model.Process;
import plg.model.activity.Activity;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;
import plg.model.data.IntegerDataObject;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.data.StringDataObject;
import plg.utils.Logger;

public class ComponentsController {

	private SingleProcessVisualizer singleProcessVisualizer;
	
	public ComponentsController() {
		this.singleProcessVisualizer = ApplicationController.instance().getMainWindow().getSingleProcessVisualizer();
	}

	public void setTaskTime(Task task) {
		TaskTime ad = new TaskTime(ApplicationController.instance().getMainFrame());
		if (task.getActivityScript() != null && !task.getActivityScript().getScript().isEmpty()) {
			ad.setScript(task.getActivityScript().getScript());
		}
		ad.setVisible(true);
		if (ad.returnedValue() == RETURNED_VALUES.SUCCESS) {
			task.setActivityScript(new IntegerScriptExecutor(ad.getScript()));
		}
	}
	
	public void addDataObject(Activity activity, DATA_OBJECT_DIRECTION direction, Class<?> type) {
		Process owner = activity.getOwner();
		String candidateName = String.format(
				ProcessGenerator.DATA_OBJECT_NAME_PATTERN,
				ProcessGenerator.numberToAlpha(owner.getDataObjects().size() + 1).toLowerCase());
		DataObject newDataObject = null;
		
		if (type.equals(StringDataObject.class)) {
			ScriptDataObjectDialog sdod = new ScriptDataObjectDialog(
					ApplicationController.instance().getMainFrame(), 
					candidateName, 
					type);
			sdod.setVisible(true);
			if (sdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				newDataObject = new StringDataObject(owner, new StringScriptExecutor(sdod.getScript()));
				newDataObject.setName(sdod.getDataObjectName());
			}
		} else if (type.equals(IntegerDataObject.class)) {
			ScriptDataObjectDialog sdod = new ScriptDataObjectDialog(
					ApplicationController.instance().getMainFrame(), 
					candidateName, 
					type);
			sdod.setVisible(true);
			if (sdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				newDataObject = new IntegerDataObject(owner, new IntegerScriptExecutor(sdod.getScript()));
				newDataObject.setName(sdod.getDataObjectName());
			}
		} else if (type.equals(DataObject.class)) {
			PlainDataObjectDialog pdod = new PlainDataObjectDialog(ApplicationController.instance().getMainFrame(), candidateName);
			pdod.setVisible(true);
			if (pdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				newDataObject = new DataObject(owner);
				newDataObject.set(pdod.getDataObjectName(), pdod.getDataObjectValue());
			}
		}
		
		if (newDataObject != null) {
			// set new owner
			newDataObject.setObjectOwner(activity, direction);
			// logging
			Logger.instance().info("Addded new data object (`" + newDataObject.getName() + "')");
			// refresh the view
			singleProcessVisualizer.refreshCurrentProcess();
		}
	}
	
	public void editDataObject(DataObject dataObject) {
		boolean modified = false;
		if (dataObject instanceof StringDataObject) {
			ScriptDataObjectDialog sdod = new ScriptDataObjectDialog(
					ApplicationController.instance().getMainFrame(),
					dataObject.getName(),
					StringDataObject.class);
			sdod.setScript(((StringDataObject) dataObject).getScriptExecutor().getScript());
			sdod.setVisible(true);
			if (sdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				dataObject.setName(sdod.getDataObjectName());
				((StringDataObject) dataObject).getScriptExecutor().setScript(sdod.getScript());
				modified = true;
			}
		} else if (dataObject instanceof IntegerDataObject) {
			ScriptDataObjectDialog sdod = new ScriptDataObjectDialog(
					ApplicationController.instance().getMainFrame(), 
					dataObject.getName(), 
					IntegerDataObject.class);
			sdod.setScript(((IntegerDataObject) dataObject).getScriptExecutor().getScript());
			sdod.setVisible(true);
			if (sdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				dataObject.setName(sdod.getDataObjectName());
				((IntegerDataObject) dataObject).getScriptExecutor().setScript(sdod.getScript());
				modified = true;
			}
		} else if (dataObject instanceof DataObject) {
			PlainDataObjectDialog pdod = new PlainDataObjectDialog(
					ApplicationController.instance().getMainFrame(), 
					dataObject.getName());
			pdod.setDataObjectValue((String) dataObject.getValue());
			pdod.setVisible(true);
			if (pdod.returnedValue() == RETURNED_VALUES.SUCCESS) {
				dataObject.set(pdod.getDataObjectName(), pdod.getDataObjectValue());
				modified = true;
			}
		}
		
		if (modified) {
			// logging
			Logger.instance().info("Modified data object (`" + dataObject.getName() + "')");
			// refresh the view
			singleProcessVisualizer.refreshCurrentProcess();
		}
	}
	
	public void removeDataObject(DataObject dataObject) {
		int confirmation = JOptionPane.showConfirmDialog(
				ApplicationController.instance().getMainFrame(),
				"Are you sure to delete the data object?",
				"Confirm deletion",
				JOptionPane.YES_NO_OPTION);
		if (confirmation == JOptionPane.NO_OPTION || confirmation == JOptionPane.CLOSED_OPTION) {
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
