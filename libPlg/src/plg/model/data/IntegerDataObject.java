package plg.model.data;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.model.Process;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;

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
	 * @param processOwner the process owner of the data object
	 * @param objectOwner the owner of the data object
	 * @param direction the direction of the data object
	 * @param executor the integer script executor
	 */
	public IntegerDataObject(Process processOwner, IDataObjectOwner objectOwner, DATA_OBJECT_DIRECTION direction, IntegerScriptExecutor executor) {
		super(processOwner, objectOwner, direction);
		this.executor = executor;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param processOwner the process owner of the data object
	 * @param executor the integer script executor
	 */
	public IntegerDataObject(Process processOwner, IntegerScriptExecutor executor) {
		this(processOwner, null, null, executor);
	}
}
