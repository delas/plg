package plg.model;

import java.util.HashSet;
import java.util.Set;

import plg.model.data.DataObject;
import plg.model.data.DataObjectOwner;
import plg.model.event.EndEvent;
import plg.model.sequence.Sequence;

/**
 * This class represents a "flowing object" of the process. A flowing object is
 * a process {@link Component} that is part of the work-flow perspective of the
 * process. For example, a task or a gateway are flow objects.
 * 
 * The main characteristic of a flow object is that it can be connected to other
 * flow object using {@link Sequence} connectors.
 * 
 * @author Andrea Burattin
 */
public abstract class FlowObject extends Component implements DataObjectOwner {

	private Set<DataObject> dataObjects;
	private Set<FlowObject> incoming;
	private Set<FlowObject> outgoing;
	
	/**
	 * This constructor creates a new flow object
	 * 
	 * @param owner the process owner of the flow object
	 */
	public FlowObject(Process owner) {
		super(owner);
		this.dataObjects = new HashSet<DataObject>();
		this.incoming = new HashSet<FlowObject>();
		this.outgoing = new HashSet<FlowObject>();
	}
	
	/**
	 * This method analyzes the current object and determines if it is somehow
	 * connected to other flow objects.
	 * 
	 * @return <tt>true</tt> if the object has no connection, <tt>false</tt>
	 * otherwise
	 */
	public boolean isIsolated() {
		return incoming.isEmpty() && outgoing.isEmpty();
	}
	
	/**
	 * This method register a flow object as an incoming one 
	 * 
	 * @param object the incoming flow object
	 */
	public void addIncomingObject(FlowObject object) {
		incoming.add(object);
	}
	
	/**
	 * This method register a flow object as an outgoing one
	 * 
	 * @param object the outgoing flow object
	 */
	public void addOutgoing(FlowObject object) {
		outgoing.add(object);
	}
	
	/**
	 * This method unregister a flow object from the list of incoming one
	 * 
	 * @param object the object to remove
	 */
	public void removeIncomingObject(FlowObject object) {
		for(FlowObject o : incoming) {
			if (o.equals(object)) {
				incoming.remove(o);
				return;
			}
		}
	}
	
	/**
	 * This method unregister a flow object from the list of outgoing one
	 * 
	 * @param object the object to remove
	 */
	public void removeOutgoingObject(FlowObject object) {
		for(FlowObject o : outgoing) {
			if (o.equals(object)) {
				outgoing.remove(o);
				return;
			}
		}
	}
	
	/**
	 * This method to get the set of all incoming objects
	 * 
	 * @return the set of flow objects
	 */
	public Set<FlowObject> getIncomingObjects() {
		return incoming;
	}
	
	/**
	 * This method to get the set of all outgoing objects
	 * 
	 * @return the set of flow objects
	 */
	public Set<FlowObject> getOutgoingObjects() {
		return outgoing;
	}
	
	/**
	 * This method can be used to know whether the current object can reach at
	 * least one {@link EndEvent}.
	 * 
	 * @return <tt>true</tt> if the current object can reach and end event,
	 * <tt>false</tt> otherwise
	 */
	public boolean canReachEndEvent() {
		return canReachEndEvent(new HashSet<FlowObject>());
	}
	
	/**
	 * This private method is the actual recursive method that implements the
	 * depth-first search.
	 * 
	 * @param visitedObjects the list of objects already visited
	 * @return <tt>true</tt> if the current object can reach and end event,
	 * <tt>false</tt> otherwise
	 */
	private boolean canReachEndEvent(Set<FlowObject> visitedObjects) {
		if (this instanceof EndEvent) {
			return true;
		}
		for(FlowObject o : outgoing) {
			if (!visitedObjects.contains(o)) {
				visitedObjects.add(o);
				if (o.canReachEndEvent(visitedObjects)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void addDataObject(DataObject data) {
		if (!dataObjects.contains(data)) {
			dataObjects.add(data);
		}
	}
	
	@Override
	public void removeDataObject(DataObject data) {
		if (dataObjects.contains(data)) {
			dataObjects.remove(data);
		}
	}
	
	@Override
	public Set<DataObject> getDataObjects() {
		return dataObjects;
	}
}
