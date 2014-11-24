package plg.exporter;

import java.io.File;
import java.io.IOException;

import org.adrianwalker.multilinestring.Multiline;
import org.apache.commons.io.FileUtils;

import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

/**
 * This class contains the exported module into a Graphviz Dot file, which can
 * be converted, for example, into a PDF or SVG or PNG file.
 * 
 * @author Andrea Burattin
 * @see http://www.graphviz.org/content/dot-language
 */
public class GraphvizExporter implements Exporter {

	/**
	 * The following comment contains the basic string with the Graphviz Dot
	 * baseline for the process. We used the Multiline annotation
	 * (https://github.com/benelog/multiline) in order to populate our variable
	 * with a very long and multiline string.
	 */
	/**
digraph G {
	rankdir=LR;
	
	// start events
	node [
		label="",
		shape=circle,
		width="0.3",
		style=filled,
		fillcolor="#a5e69c",
		color="#20962f"
	];
### START EVENTS ###
	
	// end events
	node [
		label="",
		shape=circle,
		width="0.3",
		style=filled,
		fillcolor="#ff9c9c",
		color="#630000",
		penwidth=2
	];
### END EVENTS ###

	// activities
	node [
		shape=box,
		style="filled",
		fillcolor="#e7efff",
		color="#5a677b",
		fontcolor="#5a677b",
		fontname="sans-serif",
		penwidth=1
	];
### ACTIVITIES ###
	
	// gateways
	node [
		shape=diamond,
		fixedsize=true,
		width="0.5",
		height="0.5",
		fontsize="20.0",
		style="filled",
		fillcolor="#f7f794",
		color="#a6a855",
		fontcolor="#708041",
		fontname="sans-serif"
	];
### GATEWAYS ###
	
	// edges
	edge[
		color="#5a677b"
	];
### EDGES ###
}
*/
	@Multiline
	private String basic;
	
	@Override
	public void exportModel(Process model, String filename) {
		preprocessString(model);
		try {
			FileUtils.writeStringToFile(new File(filename), basic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method preprocesses the basic file in order to generate the final
	 * Graphviz Dot string.
	 * 
	 * @param model the model to save
	 */
	private void preprocessString(Process model) {
		// start events
		String buffer = "";
		for(StartEvent e : model.getStartEvents()) {
			buffer += "\tc_" + e.getComponentId() + ";\n";
		}
		basic = basic.replace("### START EVENTS ###", buffer);

		// end events
		buffer = "";
		for(EndEvent e : model.getEndEvents()) {
			buffer += "\tc_" + e.getComponentId() + ";\n";
		}
		basic = basic.replace("### END EVENTS ###", buffer);
		
		// activities
		buffer = "";
		for(Task e : model.getTasks()) {
			buffer += "\tc_" + e.getComponentId() + " [label=\"" + e.getName() + "\"];\n";
		}
		basic = basic.replace("### ACTIVITIES ###", buffer);

		// gateways
		buffer = "";
		for(Gateway e : model.getGateways()) {
			if (e instanceof ParallelGateway) {
				buffer += "\tc_" + e.getComponentId() + " [label=\"+\"];\n";
			} else if (e instanceof ExclusiveGateway) {
				buffer += "\tc_" + e.getComponentId() + " [label=\"×\"];\n";
			}
		}
		basic = basic.replace("### GATEWAYS ###", buffer);
		
		// edges
		buffer = "";
		for(Sequence s : model.getSequences()) {
			buffer +=  "\tc_" + s.getSource().getComponentId() + " -> c_" + s.getSink().getComponentId() + ";\n";
		}
		basic = basic.replace("### EDGES ###", buffer);
	}
}
