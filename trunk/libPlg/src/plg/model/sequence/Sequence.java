package plg.model.sequence;

import plg.exceptions.IllegalSequenceException;
import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;

/**
 * 
 * @see <span>Table 7.3</span> of the
 * <a href="http://www.omg.org/cgi-bin/doc?formal/11-01-03.pdf">BPMN 2.0
 * standard definition</a> for the list of the allowed connection types
 */
public class Sequence extends Component {

	private FlowObject source;
	private FlowObject sink;
	
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
	
	public FlowObject getSource() {
		return source;
	}

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

	public FlowObject getSink() {
		return sink;
	}

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
	
	@Override
	public String toString() {
		return super.toString() + " - " + source.getComponentId() + " -> " + sink.getComponentId();
	}
	
	@Override
	public String getComponentName() {
		return "Sequence";
	}
}
