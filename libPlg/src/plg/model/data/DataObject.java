package plg.model.data;

import plg.model.Component;
import plg.model.Displaceable;
import plg.model.Process;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.utils.Pair;

/**
 * This class describes a general data object. Each data object must be
 * associated to a process model and possibly to any flow object.
 * 
 * @author Andrea Burattin
 */
public class DataObject extends Component implements IDataObject, Displaceable {

	private IDataObjectOwner objectOwner;
	private DATA_OBJECT_DIRECTION ownerDirection;
	private String name;
	private Object value;
	
	// displacement attributes
	private Pair<Integer, Integer> location = new Pair<Integer, Integer>(0, 0);
	private Pair<Integer, Integer> dimension = new Pair<Integer, Integer>(0, 0);
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 */
	public DataObject(Process processOwner) {
		this(processOwner, null, null);
	}
	
	/**
	 * Class constructor that build a new data object associated to the current
	 * process.
	 * 
	 * @param processOwner the process owner of this data object
	 * @param objectOwner the flow object owner of this data object
	 * @param direction the direction of the data object
	 */
	public DataObject(Process processOwner, IDataObjectOwner objectOwner, DATA_OBJECT_DIRECTION direction) {
		super(processOwner);
		if (objectOwner != null) {
			setObjectOwner(objectOwner, direction);
		}
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
	 * This method returns the data object direction at the owner side
	 * 
	 * @return the direction of the data object
	 */
	public DATA_OBJECT_DIRECTION getDirectionOwner() {
		return ownerDirection;
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
	 * @param direction the direction of the data object
	 */
	public void setObjectOwner(IDataObjectOwner objectOwner, DATA_OBJECT_DIRECTION direction) {
		// why i added this recursive call? mmmm... :/
		/*if (this.objectOwner != null) {
			this.objectOwner.removeDataObject(this);
		}*/
		this.objectOwner = objectOwner;
		this.ownerDirection = direction;
		this.objectOwner.addDataObject(this, direction);
	}
	
	@Override
	public String getComponentName() {
		return "Data Object";
	}
	
	@Override
	public Pair<Integer, Integer> getLocation() {
		return location;
	}
	
	@Override
	public void setLocation(int x, int y) {
		location = new Pair<Integer, Integer>(x, y);
	}
	
	@Override
	public Pair<Integer, Integer> getDimensions() {
		return dimension;
	}
	
	@Override
	public void setDimensions(int width, int height) {
		dimension = new Pair<Integer, Integer>(width, height);
	}
}
