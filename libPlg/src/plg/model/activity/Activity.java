package plg.model.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import plg.model.FlowObject;
import plg.model.Process;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;

/**
 * This class represents a general process activity
 * 
 * @author Andrea Burattin
 */
public abstract class Activity extends FlowObject implements IDataObjectOwner {

	private Map<DATA_OBJECT_DIRECTION, Set<DataObject>> dataObjects;
	
	/**
	 * This constructor creates a new activity and register it to the given
	 * process owner
	 * 
	 * @param owner the process owner of the new activity
	 */
	public Activity(Process owner) {
		super(owner);
		this.dataObjects = new HashMap<DATA_OBJECT_DIRECTION, Set<DataObject>>();
		for (DATA_OBJECT_DIRECTION d : DATA_OBJECT_DIRECTION.values()) {
			this.dataObjects.put(d, new HashSet<DataObject>());
		}
	}
	
	@Override
	public void addDataObject(DataObject data, DATA_OBJECT_DIRECTION direction) {
		Set<DataObject> s = dataObjects.get(direction);
		if (!s.contains(data)) {
			s.add(data);
			data.setObjectOwner(this, direction);
		}
	}
	
	@Override
	public void removeDataObject(DataObject data) {
		for (DATA_OBJECT_DIRECTION d : DATA_OBJECT_DIRECTION.values()) {
			Set<DataObject> s = dataObjects.get(d);
			if (s.contains(data)) {
				s.remove(data);
			}
		}
	}
	
	@Override
	public Set<DataObject> getDataObjects() {
		Set<DataObject> s = new HashSet<DataObject>();
		for (DATA_OBJECT_DIRECTION d : DATA_OBJECT_DIRECTION.values()) {
			s.addAll(dataObjects.get(d));
		}
		return s;
	}
	
	@Override
	public Set<DataObject> getDataObjects(DATA_OBJECT_DIRECTION direction) {
		return dataObjects.get(direction);
	}
}
