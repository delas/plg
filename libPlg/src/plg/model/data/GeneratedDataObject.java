package plg.model.data;

import plg.exceptions.InvalidScript;
import plg.generator.scriptexecuter.ScriptExecutor;
import plg.model.Process;

/**
 * This class describes a data object that can be associated to any flow object,
 * with a value automatically generated.
 * 
 * @author Andrea Burattin
 */
public abstract class GeneratedDataObject extends DataObject implements INoiseSensitiveDataObject {

	protected ScriptExecutor executor;
	protected Object originalValue = null;
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 * @param objectOwner the process owner of this data object
	 * @param generateInstance manually decide whether the instance value
	 * should be generated or not
	 */
	protected GeneratedDataObject(Process processOwner, IDataObjectOwner objectOwner) {
		super(processOwner, objectOwner);
	}
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 * @param generateInstance manually decide whether the instance value
	 * should be generated or not
	 */
	protected GeneratedDataObject(Process processOwner) {
		this(processOwner, null);
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
			originalValue = executor.getValue();
			setValue(originalValue);
		} catch (InvalidScript e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the script executor associated to the current data
	 * object
	 * 
	 * @return the script executor
	 */
	public ScriptExecutor getScriptExecutor() {
		return executor;
	}
	
	@Override
	public Object getOriginalValue() {
		return originalValue;
	}
}
