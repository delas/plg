package plg.model.data;

import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.model.FlowObject;

/**
 * This class is used to describe an integer data object that is used in
 * combination with an {@link StringScriptExecutor}.
 * 
 * @author Andrea Burattin
 */
public class StringDataObject extends GeneratedDataObject {

	/**
	 * Class constructor
	 * 
	 * @param objectOwner the owner of the data object
	 * @param executor the string script executor
	 */
	public StringDataObject(FlowObject objectOwner, StringScriptExecutor executor) {
		super(objectOwner);
		this.executor = executor;
	}
}
