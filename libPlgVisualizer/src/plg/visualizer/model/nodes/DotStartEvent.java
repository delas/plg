package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotStartEvent extends DotNode {

	public DotStartEvent() {
		super("", null);
		
		setSelectable(true);
		
		setOption("shape", "circle");
		setOption("style", "filled");
		setOption("fillcolor", "#5dbd5a:#bafcc2");
		setOption("gradientangle", "270");
		setOption("color", "#20962f");
		setOption("width", ".3");
		setOption("fontcolor", "#20962f");
		setOption("fontname", "sans-serif");
		setOption("fontsize", "12.0");
		setOption("penwidth", "2");
	}

}
