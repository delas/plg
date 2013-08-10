package plg.model;

import java.util.ArrayList;
import java.util.List;

public abstract class FlowObject extends Component {

	private List<FlowObject> incoming;
	private List<FlowObject> outgoing;
	
	public FlowObject(Process owner) {
		super(owner);
		this.incoming = new ArrayList<FlowObject>();
		this.outgoing = new ArrayList<FlowObject>();
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
	
	public List<FlowObject> getIncomingObjects() {
		return incoming;
	}
	
	public List<FlowObject> getOutgoingObjects() {
		return outgoing;
	}
}
