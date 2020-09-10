package plg.generator.process.petrinet;

import java.util.HashSet;
import java.util.Set;

import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;
import plg.utils.Logger;
import plg.utils.Pair;

/**
 * This class represents a Petri net generated starting from a {@link Process}
 * specified within PLG.
 * 
 * @author Andrea Burattin
 */
public class PetriNet {

	protected Process originalProcess;
	protected Set<Node> components = new HashSet<Node>();
	protected Set<Transition> transitions = new HashSet<Transition>();
	protected Set<Place> places = new HashSet<Place>();
	protected Set<Pair<Node, Node>> edges = new HashSet<Pair<Node, Node>>();
	
	/**
	 * Petri net constructor. This constructor also converts the provided
	 * process as Petri net.
	 * 
	 * @param originalProcess the original process to convert
	 */
	public PetriNet(Process originalProcess) {
		this.originalProcess = originalProcess;
		convert();
		optimize();
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public Set<Place> getPlaces() {
		return places;
	}
	
	public Set<Pair<Node, Node>> getEdges() {
		return edges;
	}
	
	public Set<Node> getIncomingNodes(Node target) {
		Set<Node> in = new HashSet<Node>();
		for(Pair<Node, Node> edge : edges) {
			if (edge.getSecond().equals(target)) {
				in.add(edge.getFirst());
			}
		}
		return in;
	}
	
	public Set<Node> getOutgoingNodes(Node source) {
		Set<Node> out = new HashSet<Node>();
		for(Pair<Node, Node> edge : edges) {
			if (edge.getFirst().equals(source)) {
				out.add(edge.getSecond());
			}
		}
		return out;
	}
	
	/**
	 * This method, given a component id returns the corresponding node object
	 * associated.
	 * 
	 * @param componentId the id of the component to search
	 * @return the node object with the required component id
	 */
	private Node getComponent(String componentId) {
		for(Node n : components) {
			if (n.getReferenceId().equals(componentId)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * This method connects two nodes
	 * 
	 * @param source the source node
	 * @param sink the sink node
	 */
	private void connect(Node source, Node sink) {
		Logger.instance().debug("Adding connection " + source + " -> " + sink);
		edges.add(new Pair<Node, Node>(source, sink));
	}
	
	/**
	 * This method generates a new place
	 * 
	 * @return the new place generated
	 */
	private Place newPlace(String referenceId) {
		Place p = new Place(this);
		p.setReferenceId(referenceId);
		places.add(p);
		components.add(p);
		return p;
	}
	
	/**
	 * This method generates a new transition
	 * 
	 * @return the new transition generated
	 */
	private Transition newTransition(String referenceId) {
		Transition t = new Transition(this);
		t.setReferenceId(referenceId);
		transitions.add(t);
		components.add(t);
		return t;
	}
	
	/**
	 * This method is responsible of converting the provided business process
	 * into a Petri net.
	 */
	private void convert() {
		Logger.instance().debug("Starting conversion to Petri net");
		
		// add places for start events
		for(StartEvent e : originalProcess.getStartEvents()) {
			Logger.instance().debug("Adding place for " + e);
			Place p = newPlace(e.getId());
			p.setTokens(1);
		}
		
		// add places for end events
		for(EndEvent e : originalProcess.getEndEvents()) {
			Logger.instance().debug("Adding place for " + e);
			newPlace(e.getId());
		}
		
		// add all activities
		for(Task t : originalProcess.getTasks()) {
			Logger.instance().debug("Adding transition for " + t);
			Transition trans = newTransition(t.getId());
			trans.setSilent(false);
			trans.setLabel(t.getName());
		}
		
		// add all gateways
		for(Gateway g : originalProcess.getGateways()) {
			if (g instanceof ParallelGateway) {
				Logger.instance().debug("Adding silent transition for " + g);
				Transition trans = newTransition(g.getId());
				trans.setSilent(true);
			} else {
				Logger.instance().debug("Adding place for " + g);
				newPlace(g.getId());
			}
		}
		
		// add all sequences
		Set<Sequence> gatewayToGateway = new HashSet<Sequence>();
		
		// add all sequences except for the gateway to gateway
		for(Sequence s : originalProcess.getSequences()) {
			FlowObject source = s.getSource();
			FlowObject sink = s.getSink();
			Logger.instance().debug("Processing " + source + " -> " + sink);
			
			if (source instanceof Task && sink instanceof Task) {
				// task -> task
				Place connector = newPlace("undef_" + source.getId() + "-" + sink.getId());
				connect(getComponent(source.getId()), connector);
				connect(connector, getComponent(sink.getId()));
				
			} else if (source instanceof Task && sink instanceof Gateway) {
				// task -> gateway
				Gateway g = (Gateway) sink;
				if (g instanceof ParallelGateway) {
					Place p = newPlace("undef_" + source.getId() + "-" + sink.getId());
					connect(getComponent(source.getId()), p);
					connect(p, getComponent(sink.getId()));
					
				} else if (g instanceof ExclusiveGateway) {
					connect(getComponent(source.getId()), getComponent(sink.getId()));
				}
				
			} else if (source instanceof Gateway && sink instanceof Task) {
				// gateway -> task
				Gateway g = (Gateway) source;
				if (g instanceof ParallelGateway) {
					Place p = newPlace("undef_" + source.getId() + "-" + sink.getId());
					connect(getComponent(g.getId()), p);
					connect(p, getComponent(sink.getId()));
					
				} else if (g instanceof ExclusiveGateway) {
					connect(getComponent(source.getId()), getComponent(sink.getId()));
				}
				
			} else if (source instanceof StartEvent) {
				// start -> ?
				connect(getComponent(source.getId()), getComponent(sink.getId()));
				
			} else if (sink instanceof EndEvent) {
				// ? -> end
				connect(getComponent(source.getId()), getComponent(sink.getId()));
				
			} else if (source instanceof Gateway && sink instanceof Gateway) {
				// gateway -> gateway
				gatewayToGateway.add(s);
				
			}
		}
		
		// add the sequences gateway to gateway
		for(Sequence s : gatewayToGateway) {
			FlowObject source = s.getSource();
			FlowObject sink = s.getSink();
			
			if (source instanceof ExclusiveGateway && sink instanceof ExclusiveGateway) {
				Transition t = newTransition("undef_" + source.getId() + "-" + sink.getId());
				t.setSilent(true);
				connect(getComponent(source.getId()), t);
				connect(t, getComponent(sink.getId()));
				
			} else if (source instanceof ParallelGateway && sink instanceof ParallelGateway) {
				Place p = newPlace("undef_" + source.getId() + "-" + sink.getId());
				connect(getComponent(source.getId()), p);
				connect(p, getComponent(sink.getId()));
				
			} else {
				connect(getComponent(source.getId()), getComponent(sink.getId()));
			}
		}
		
		Logger.instance().debug("Conversion to Petri net complete");
	}
	
	/**
	 * This method removes useless double silent transitions
	 */
	private void optimize() {
		Logger.instance().debug("Starting Petri net optimization");
		
		// merge sequences of silent transitions
		Set<Place> toRemove = new HashSet<Place>();
		for(Place p : places) {
			Set<Node> inPlace = getIncomingNodes(p);
			Set<Node> outPlace = getOutgoingNodes(p);
			if (inPlace.size() == 1 && outPlace.size() == 1){
				Transition before = (Transition) inPlace.iterator().next();
				Transition after = (Transition) outPlace.iterator().next();
				if (before.isSilent() && after.isSilent()) {
					Set<Node> inTrans = getIncomingNodes(before);
					Set<Node> outTrans = getOutgoingNodes(after);
					if (inTrans.size() == 1 && outTrans.size() == 1) {
						// let's go for merge
						// new hidden transition with connections
						Transition t = newTransition("merge_" + before.getReferenceId() + "-" + after.getReferenceId());
						t.setSilent(true);
						connect(inTrans.iterator().next(), t);
						connect(t, outTrans.iterator().next());
						// remove old sequences
						edges.remove(new Pair<Node, Node>(inTrans.iterator().next(), before));
						edges.remove(new Pair<Node, Node>(before, p));
						edges.remove(new Pair<Node, Node>(p, after));
						edges.remove(new Pair<Node, Node>(after, outTrans.iterator().next()));
						// remove old transitions
						transitions.remove(before);
						transitions.remove(after);
						toRemove.add(p);
					}
				}
			}
		}
		places.removeAll(toRemove);
		
		Logger.instance().debug("Petri net optimization complete");
	}
}
