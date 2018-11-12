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
		setOption("width", "0.4");
		setOption("height", "0.4");
		setOption("fontsize", "30");
		setOption("fontcolor", "#000000");
		setOption("fontname", "sans-serif");
		setOption("fixedsize", "true");
		
		if (TYPE.PARALLEL.equals(type)) {
			setLabel("<<table border='0'><tr><td></td></tr><tr><td valign='top'><font point-size='5' color='white'>_</font>+</td></tr></table>>");
		} else {
			setLabel("<<table border='0'><tr><td></td></tr><tr><td valign='top'><font point-size='7.5' color='white'>_</font>&times;</td></tr></table>>");
		}
	}

}
