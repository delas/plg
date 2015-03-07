package plg.model.data;

import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;

/**
 * This class describes a general data object. Each data object must be
 * associated to a process model and possibly to any flow object.
 * 
 * @author Andrea Burattin
 */
public class DataObject extends Component implements IDataObject {

	private IDataObjectOwner objectOwner;
	private String name;
	private Object value;
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 * @param objectOwner the flow object owner owner of this data object
	 */
	public DataObject(Process processOwner, IDataObjectOwner objectOwner) {
		super(processOwner);
		if (objectOwner != null) {
			setObjectOwner(objectOwner);
		}
	}
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 */
	public DataObject(Process processOwner) {
		this(processOwner, null);
	}
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param objectOwner the flow object owner owner of this data object
	 */
	public DataObject(FlowObject objectOwner) {
		this(objectOwner.getOwner(), objectOwner);
	}
	
	/**
	 * This method sets the data object attribute name and value
	 * 
	 * @param name the name to set
	 * @param value the value to set
	 */
	public void set(String name, Object value) {
		setName(name);
		setValue(value);
	}
	
	@Override
	public String getName() {
		return name;
	}

	/**
	 * This method sets the attribute name of the data object
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * This method sets the attribute value of the data object
	 * 
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * This method returns the object owner of the data object
	 * 
	 * @return the object owner
	 */
	public IDataObjectOwner getObjectOwner() {
		return objectOwner;
	}

	/**
	 * This method returns the object owner of the data object
	 * 
	 * @return the object owner
	 */
	public Process getProcessOwner() {
		return getOwner();
	}

	/**
	 * This method sets the object owner of the data object
	 * 
	 * @param objectOwner the object owner to set
	 */
	public void setObjectOwner(IDataObjectOwner objectOwner) {
		if (this.objectOwner != null) {
			this.objectOwner.removeDataObject(this);
		}
		this.objectOwner = objectOwner;
		this.objectOwner.addDataObject(this);
	}
	
	@Override
	public String getComponentName() {
		return "Data Object";
	}
}
