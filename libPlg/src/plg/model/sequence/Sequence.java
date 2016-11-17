package plg.model.sequence;

import java.util.LinkedList;
import java.util.List;

import plg.exceptions.IllegalSequenceException;
import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.utils.Pair;

/**
 * This class represents a sequence connection between two flow objects.
 * 
 * @see <span>Table 7.3</span> of the
 * <a href="http://www.omg.org/cgi-bin/doc?formal/11-01-03.pdf">BPMN 2.0
 * standard definition</a> for the list of the allowed connection types
 */
public class Sequence extends Component {

	private FlowObject source;
	private FlowObject sink;
	
	// displacement attributes
	private List<Pair<Integer, Integer>> sequenceOfPoints = new LinkedList<Pair<Integer, Integer>>();
	
	/**
	 * This constructor creates a new sequence connection and register it to the
	 * given process owner
	 * 
	 * @param owner the process owner of the new sequence
	 * @param source the source flow object
	 * @param sink the destination flow object of the sequence
	 * @throws IllegalSequenceException this exception is thrown if the source
	 * or the sink are illegal
	 */
	public Sequence(Process owner, FlowObject source, FlowObject sink) throws IllegalSequenceException {
		super(owner);
		if ((source instanceof StartEvent && sink instanceof StartEvent) ||
				(source instanceof EndEvent) ||
				(sink instanceof StartEvent)) {
			owner.removeComponent(this);
			throw new IllegalSequenceException("Illegal sequence: requested connection " +
					"from `" + source.getComponentName() + "'" +
					"to `" + sink.getComponentName() + "'.");
		} else {
			setSource(source);
			setSink(sink);
		}
	}
	
	/**
	 * This method to obtain the source of the sequence
	 * 
	 * @return the source flow object
	 */
	public FlowObject getSource() {
		return source;
	}

	/**
	 * This method to change the source of the current sequence
	 * 
	 * @param newSource the new flow object to be used as source
	 */
	public void setSource(FlowObject newSource) {
		if (source != null && sink != null) {
			source.removeOutgoingObject(sink);
			sink.removeIncomingObject(source);
		}
		source = newSource;
		if (sink != null) {
			source.addOutgoing(sink);
			sink.addIncomingObject(source);
		}
	}

	/**
	 * This method to obtain the source of the sequence
	 * 
	 * @return the destination flow object
	 */
	public FlowObject getSink() {
		return sink;
	}

	/**
	 * This method to change the sink of the current sequence
	 * 
	 * @param newSink the new flow object to be used as sink
	 */
	public void setSink(FlowObject newSink) {
		if (source != null && sink != null) {
			source.removeOutgoingObject(sink);
			sink.removeIncomingObject(source);
		}
		sink = newSink;
		if (source != null) {
			source.addOutgoing(sink);
			sink.addIncomingObject(source);
		}
	}
	
	public void addPoint(int x, int y) {
		sequenceOfPoints.add(new Pair<Integer, Integer>(x, y));
	}
	
	public List<Pair<Integer, Integer>> getPoints() {
		return sequenceOfPoints;
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + source.getId() + " -> " + sink.getId();
	}
	
	@Override
	public String getComponentName() {
		return "Sequence";
	}
}
