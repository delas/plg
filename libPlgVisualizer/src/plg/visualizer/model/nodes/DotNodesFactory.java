package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

import plg.model.Component;
import plg.model.data.DataObject;
import plg.visualizer.model.nodes.DotGateway.TYPE;

/**
 * 
 * @author Andrea Burattin
 */
public class DotNodesFactory {

	public static DotNode construct(Component node) {
		if (node instanceof plg.model.activity.Task) {
			return new DotTask(((plg.model.activity.Task) node).getName());
		}
		if (node instanceof plg.model.event.StartEvent) {
			return new DotStartEvent();
		}
		if (node instanceof plg.model.event.EndEvent) {
			return new DotEndEvent();
		}
		if (node instanceof plg.model.gateway.ParallelGateway) {
			return new DotGateway(TYPE.PARALLEL);
		}
		if (node instanceof plg.model.gateway.ExclusiveGateway) {
			return new DotGateway(TYPE.EXCLUSIVE);
		}
		if (node instanceof plg.model.data.DataObject) {
			return new DotDataObject((DataObject) node);
		}
		return null;
	}
}
