package plg.model.data;

import plg.model.Component;
import plg.model.FlowObject;

/**
 * This class describes a general data object that can be associated to any flow
 * object
 * 
 * @author Andrea Burattin
 */
public class DataObject extends Component {

	private FlowObject objectOwner;
	private String name;
	private Object value;
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param owner the process owner of this data object
	 */
	public DataObject(FlowObject objectOwner) {
		super(objectOwner.getOwner());
		setObjectOwner(objectOwner);
	}
	
	/**
	 * This method returns the attribute name of the data object
	 * 
	 * @return the name of the current data object
	 */
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

	/**
	 * This method returns the attribute value of the data object
	 * 
	 * @return the value of the current data object
	 */
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
	public FlowObject getObjectOwner() {
		return objectOwner;
	}

	/**
	 * This method sets the object owner of the data object
	 * 
	 * @param objectOwner the object owner to set
	 */
	public void setObjectOwner(FlowObject objectOwner) {
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
