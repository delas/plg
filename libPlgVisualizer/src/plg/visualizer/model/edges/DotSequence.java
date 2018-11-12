package plg.visualizer.model.edges;

import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotSequence extends DotEdge {

	public DotSequence(DotNode source, DotNode target) {
		super(source, target);
		
		setOption("color", "#303030");
	}
}
