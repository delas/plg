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
		setOption("style", "filled");
		setOption("fillcolor", "#e46e60:#ffc5c1");
		setOption("gradientangle", "270");
		setOption("color", "#630000");
		setOption("width", ".3");
		setOption("fontcolor", "#630000");
		setOption("fontname", "sans-serif");
		setOption("fontsize", "12.0");
		setOption("penwidth", "2");
	}

}
