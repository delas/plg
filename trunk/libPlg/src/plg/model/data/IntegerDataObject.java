package plg.model.data;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.model.FlowObject;

/**
 * This class is used to describe an integer data object that is used in
 * combination with an {@link IntegerScriptExecutor}.
 * 
 * @author Andrea Burattin
 */
public class IntegerDataObject extends GeneratedDataObject {

	/**
	 * Class constructor
	 * 
	 * @param objectOwner the owner of the data object
	 * @param executor the integer script executor
	 */
	public IntegerDataObject(FlowObject objectOwner, IntegerScriptExecutor executor) {
		super(objectOwner);
		this.executor = executor;
	}
}
