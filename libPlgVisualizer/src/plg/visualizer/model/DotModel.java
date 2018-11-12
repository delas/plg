package plg.visualizer.model;

import java.util.HashMap;
import java.util.Map;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.sequence.Sequence;
import plg.visualizer.model.edges.DotDataObjectConnection;
import plg.visualizer.model.edges.DotSequence;
import plg.visualizer.model.nodes.DotNodesFactory;

/**
 * 
 * @author Andrea Burattin
 */
public class DotModel extends Dot {

	private Process model;
	
	public DotModel(Process model) {
		this.model = model;
		
//		setOption("splines", "ortho");
//		setOption("nodesep", "1.0");
//		setOption("sep", "+4");
		setOption("rankdir", "LR");
		
		realize();
	}

	private void realize() {
		Map<String, DotNode> idToNodes = new HashMap<String, DotNode>();
		
		// adding all nodes
		for (Component node : model.getComponents()) {
			if (node instanceof FlowObject || node instanceof DataObject) {
				DotNode dotNode = DotNodesFactory.construct(node);
				idToNodes.put(node.getId(), dotNode);
				addNode(dotNode);
			}
		}
		
		// adding all edges
		for (Sequence sequence : model.getSequences()) {
			DotNode source = idToNodes.get(sequence.getSource().getId());
			DotNode sink = idToNodes.get(sequence.getSink().getId());
			addEdge(new DotSequence(source, sink));
		}
		
		// connecting all data objects
		for (DataObject dobj : model.getDataObjects()) {
			if (dobj.getObjectOwner() != null) {
				DotNode source = null;
				DotNode sink = null;
				if (dobj.getDirectionOwner() == DATA_OBJECT_DIRECTION.GENERATED) {
					source = idToNodes.get(dobj.getObjectOwner().getId());
					sink = idToNodes.get(dobj.getId());
				} else {
					source = idToNodes.get(dobj.getId());
					sink = idToNodes.get(dobj.getObjectOwner().getId());
				}
				addEdge(new DotDataObjectConnection(source, sink));
			}
		}
	}
}
