package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotEndEvent extends DotNode {

	public DotEndEvent() {
		super("", null);
		
		setSelectable(true);
		
		setOption("shape", "circle");
		setOption("width", ".3");
		setOption("fontcolor", "#000000");
		setOption("fontname", "sans-serif");
		setOption("fontsize", "12.0");
		setOption("penwidth", "2");
	}

}
