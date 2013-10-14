package plg.model.data;

import plg.exceptions.InvalidScript;
import plg.generator.scriptexecuter.ScriptExecutor;
import plg.model.FlowObject;

/**
 * This class describes a data object that can be associated to any flow object,
 * with a value automatically generated.
 * 
 * @author Andrea Burattin
 */
public abstract class GeneratedDataObject extends DataObject {

	protected ScriptExecutor executor;
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param owner the process owner of this data object
	 * @param generateInstance manually decide whether the instance value
	 * should be generated or not
	 */
	protected GeneratedDataObject(FlowObject objectOwner) {
		super(objectOwner);
	}

	/**
	 * This method will call a new generation of the value for the current data
	 * object instance. Different invocations of this method may generate
	 * different values.
	 * 
	 * @param caseId the case identifier of the current process instance
	 */
	public void generateInstance(String caseId) {
		try {
			executor.execute(caseId);
			setValue(executor.getValue());
		} catch (InvalidScript e) {
			e.printStackTrace();
		}
	}
}
