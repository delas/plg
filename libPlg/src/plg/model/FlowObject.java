package plg.model;

import java.util.HashSet;
import java.util.Set;

import plg.model.event.EndEvent;

public abstract class FlowObject extends Component {

	private Set<FlowObject> incoming;
	private Set<FlowObject> outgoing;
	
	public FlowObject(Process owner) {
		super(owner);
		this.incoming = new HashSet<FlowObject>();
		this.outgoing = new HashSet<FlowObject>();
	}
	
	public boolean isIsolated() {
		return incoming.isEmpty() && outgoing.isEmpty();
	}
	
	public void addIncomingObject(FlowObject object) {
		incoming.add(object);
	}
	
	public void addOutgoing(FlowObject object) {
		outgoing.add(object);
	}
	
	public void removeIncomingObject(FlowObject object) {
		for(FlowObject o : incoming) {
			if (o.equals(object)) {
				incoming.remove(o);
				return;
			}
		}
	}
	
	public void removeOutgoingObject(FlowObject object) {
		for(FlowObject o : outgoing) {
			if (o.equals(object)) {
				outgoing.remove(o);
				return;
			}
		}
	}
	
	public Set<FlowObject> getIncomingObjects() {
		return incoming;
	}
	
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
}
