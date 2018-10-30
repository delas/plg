package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotGateway extends DotNode {

	public enum TYPE {
		PARALLEL, EXCLUSIVE
	}
	
	public DotGateway(TYPE type) {
		super("", null);
		
		setSelectable(true);
		
		setOption("shape", "diamond");
		setOption("style", "filled");
		setOption("width", "0.4");
		setOption("height", "0.4");
		setOption("fontsize", "20");
		setOption("fillcolor", "#ffff84:#ffffbd");
		setOption("gradientangle", "270");
		setOption("color", "#a6a855");
		setOption("fontcolor", "#708041");
		setOption("fontname", "sans-serif");
		setOption("fixedsize", "true");
		
		if (TYPE.PARALLEL.equals(type)) {
			setLabel("+");
		} else {
			setLabel("&times;");
		}
	}

}
