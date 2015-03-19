package plg.io.exporter;

import java.io.File;
import java.io.IOException;

import org.adrianwalker.multilinestring.Multiline;
import org.apache.commons.io.FileUtils;

import plg.annotations.Exporter;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;
import plg.utils.Logger;

/**
 * This class contains the export module for Graphviz Dot file (of the BPMN),
 * which can be converted, for example, into a PDF or SVG or PNG file.
 * 
 * @author Andrea Burattin
 * @see http://www.graphviz.org/content/dot-language
 */
@Exporter(
	name = "BPMN Model as Graphviz Dot",
	fileExtension = "dot"
)
public class GraphvizBPMNExporter implements IFileExporter {

	/**
	 * The following comment contains the basic string with the Graphviz Dot
	 * baseline for the process. We used the Multiline annotation
	 * (https://github.com/benelog/multiline) in order to populate our variable
	 * with a very long and multiline string.
	 */
	/**
digraph G {
	graph [splines=ortho, nodesep="0.5"];
	rankdir=LR;
	
	// start events
	node [
		label="",
		shape=circle,
		width="0.3",
		style=filled,
		fillcolor="#5dbd5a:#bafcc2",
		gradientangle=270,
		color="#20962f",
		fontcolor="#20962f",
		fontname="sans-serif",
		fontsize="12.0",
		penwidth=2
	];
### START EVENTS ###
	
	// end events
	node [
		label="",
		shape=circle,
		width="0.3",
		style=filled,
		fillcolor="#e46e60:#ffc5c1",
		gradientangle=270,
		color="#630000",
		fontcolor="#630000",
		fontname="sans-serif",
		fontsize="12.0",
		penwidth=2
	];
### END EVENTS ###

	// activities
	node [
		shape=box,
		style="filled",
		fillcolor="#cedeef:#ffffff",
		gradientangle=270,
		color="#5a677b",
		width="0.5",
		fontcolor="#5a677b",
		fontname="sans-serif",
		fontsize="14.0",
		penwidth=1
	];
### ACTIVITIES ###

	// data objects
	node [
		shape=note,
		fontsize="10",
		width="0.5",
		height="0.5",
		style="filled",
		fillcolor="#ffffff",
		color="#666666",
		fontcolor="#666666",
		fontname="sans-serif"
	];
### DATAOBJECTS ###
	
	// gateways
	node [
		shape=diamond,
		fixedsize=true,
		width="0.5",
		height="0.5",
		fontsize="20.0",
		style="filled",
		fillcolor="#ffff84:#ffffbd",
		gradientangle=270,
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

	// data object connections
	edge[
		color="#666666",
		style="dotted",
		arrowhead="open"
	];
### DATAOBJECTSCONNECTIONS ###
}
*/
	@Multiline
	private String basic;
	
	@Override
	public void exportModel(Process model, String filename) {
		Logger.instance().info("Starting process exportation");
		preprocessString(model);
		try {
			FileUtils.writeStringToFile(new File(filename), basic);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.instance().info("Process exportation complete");
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
			buffer += "\tc_" + e.getId() + " [xlabel=\"Start\"];\n";
		}
		basic = basic.replace("### START EVENTS ###", buffer);

		// end events
		buffer = "";
		for(EndEvent e : model.getEndEvents()) {
			buffer += "\tc_" + e.getId() + " [xlabel=\"End\"];\n";
		}
		basic = basic.replace("### END EVENTS ###", buffer);
		
		// activities
		buffer = "";
		for(Task e : model.getTasks()) {
			buffer += "\tc_" + e.getId() + " [label=\"" + e.getName() + "\"];\n";
		}
		basic = basic.replace("### ACTIVITIES ###", buffer);

		// gateways
		buffer = "";
		for(Gateway e : model.getGateways()) {
			if (e instanceof ParallelGateway) {
				buffer += "\tc_" + e.getId() + " [label=\"+\"];\n";
			} else if (e instanceof ExclusiveGateway) {
				buffer += "\tc_" + e.getId() + " [label=\"&times;\"];\n";
			}
		}
		basic = basic.replace("### GATEWAYS ###", buffer);
		
		// data objects
		buffer = "";
		for(DataObject o : model.getDataObjects()) {
			if (o instanceof StringDataObject) {
				buffer += "\tc_" + o.getId() + " [label=\"" + o.getName() + " (string)\"];\n";
			} else if (o instanceof IntegerDataObject) {
				buffer += "\tc_" + o.getId() + " [label=\"" + o.getName() + " (integer)\"];\n";
			} else {
				buffer += "\tc_" + o.getId() + " [label=\"" + o.getName() + " = \\\""+ o.getValue() +"\\\"\"];\n";
			}
		}
		basic = basic.replace("### DATAOBJECTS ###", buffer);

		// edges
		buffer = "";
		for(Sequence s : model.getSequences()) {
			String left = "c_" + s.getSource().getId();
			String right = "c_" + s.getSink().getId();
			
			buffer +=  "\t" + left + " -> " + right + ";\n";
		}
		basic = basic.replace("### EDGES ###", buffer);
		
		// data object connections
		buffer = "";
		for(DataObject o : model.getDataObjects()) {
			if (o.getProcessOwner() != null) {
				if (o.getObjectOwner() instanceof Sequence) { 
					String left = "c_" + o.getId();
					String right = "c_" + ((Sequence) o.getObjectOwner()).getSink().getId();
					buffer +=  "\t" + left + " -> " + right + ";\n";
				} else {
					String left = "c_" + o.getObjectOwner().getId();
					String right = "c_" + o.getId();
					buffer +=  "\t" + left + " -> " + right + ";\n";
				}
			}
		}
		basic = basic.replace("### DATAOBJECTSCONNECTIONS ###", buffer);
	}
}
