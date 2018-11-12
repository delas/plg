package plg.visualizer.model.edges;

import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotDataObjectConnection extends DotEdge {

	public DotDataObjectConnection(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("color", "#303030");
		setOption("style", "dashed");
		setOption("arrowhead", "open");
	}
}
