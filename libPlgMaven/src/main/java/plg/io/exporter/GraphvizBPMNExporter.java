package plg.io.exporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import plg.annotations.Exporter;
import plg.generator.IProgressVisualizer;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
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
public class GraphvizBPMNExporter extends FileExporter {

	/**
	 * The following comment contains the basic string with the Graphviz Dot
	 * baseline for the process. We used the Multiline annotation
	 * (https://github.com/benelog/multiline) in order to populate our variable
	 * with a very long and multiline string.
	 */
	private String basic = "digraph G {\r\n" + 
			"	graph [splines=\"ortho\", nodesep=\"0.5\"];\r\n" + 
			"	rankdir=\"LR\";\r\n" + 
			"	\r\n" + 
			"	// start events\r\n" + 
			"	node [\r\n" + 
			"		label=\"\",\r\n" + 
			"		shape=\"circle\",\r\n" + 
			"		width=\"0.3\",\r\n" + 
			"		style=\"filled\",\r\n" + 
			"		fillcolor=\"#5dbd5a:#bafcc2\",\r\n" + 
			"		gradientangle=270,\r\n" + 
			"		color=\"#20962f\",\r\n" + 
			"		fontcolor=\"#20962f\",\r\n" + 
			"		fontname=\"sans-serif\",\r\n" + 
			"		fontsize=\"12.0\",\r\n" + 
			"		penwidth=2\r\n" + 
			"	];\r\n" + 
			"### START EVENTS ###\r\n" + 
			"	\r\n" + 
			"	// end events\r\n" + 
			"	node [\r\n" + 
			"		label=\"\",\r\n" + 
			"		shape=\"circle\",\r\n" + 
			"		width=\"0.3\",\r\n" + 
			"		style=\"filled\",\r\n" + 
			"		fillcolor=\"#e46e60:#ffc5c1\",\r\n" + 
			"		gradientangle=270,\r\n" + 
			"		color=\"#630000\",\r\n" + 
			"		fontcolor=\"#630000\",\r\n" + 
			"		fontname=\"sans-serif\",\r\n" + 
			"		fontsize=\"12.0\",\r\n" + 
			"		penwidth=2\r\n" + 
			"	];\r\n" + 
			"### END EVENTS ###\r\n" + 
			"\r\n" + 
			"	// activities\r\n" + 
			"	node [\r\n" + 
			"		shape=\"box\",\r\n" + 
			"		style=\"filled\",\r\n" + 
			"		fillcolor=\"#cedeef:#ffffff\",\r\n" + 
			"		gradientangle=270,\r\n" + 
			"		color=\"#5a677b\",\r\n" + 
			"		width=\"0.5\",\r\n" + 
			"		fontcolor=\"#5a677b\",\r\n" + 
			"		fontname=\"sans-serif\",\r\n" + 
			"		fontsize=\"14.0\",\r\n" + 
			"		penwidth=1\r\n" + 
			"	];\r\n" + 
			"### ACTIVITIES ###\r\n" + 
			"\r\n" + 
			"	// data objects\r\n" + 
			"	node [\r\n" + 
			"		shape=\"note\",\r\n" + 
			"		fontsize=10,\r\n" + 
			"		width=\"0.5\",\r\n" + 
			"		height=\"0.5\",\r\n" + 
			"		style=\"filled\",\r\n" + 
			"		fillcolor=\"#ffffff\",\r\n" + 
			"		color=\"#666666\",\r\n" + 
			"		fontcolor=\"#666666\",\r\n" + 
			"		fontname=\"sans-serif\"\r\n" + 
			"	];\r\n" + 
			"### DATAOBJECTS ###\r\n" + 
			"	\r\n" + 
			"	// gateways\r\n" + 
			"	node [\r\n" + 
			"		shape=\"diamond\",\r\n" + 
			"		//fixedsize=\"true\",\r\n" + 
			"		width=\"0.5\",\r\n" + 
			"		height=\"0.5\",\r\n" + 
			"		fontsize=20,\r\n" + 
			"		style=\"filled\",\r\n" + 
			"		fillcolor=\"#ffff84:#ffffbd\",\r\n" + 
			"		gradientangle=270,\r\n" + 
			"		color=\"#a6a855\",\r\n" + 
			"		fontcolor=\"#708041\",\r\n" + 
			"		fontname=\"sans-serif\"\r\n" + 
			"	];\r\n" + 
			"### GATEWAYS ###\r\n" + 
			"\r\n" + 
			"	// edges\r\n" + 
			"	edge[\r\n" + 
			"		color=\"#5a677b\"\r\n" + 
			"	];\r\n" + 
			"### EDGES ###\r\n" + 
			"\r\n" + 
			"	// data object connections\r\n" + 
			"	edge[\r\n" + 
			"		color=\"#666666\",\r\n" + 
			"		style=\"dotted\",\r\n" + 
			"		arrowhead=\"open\"\r\n" + 
			"	];\r\n" + 
			"### DATAOBJECTSCONNECTIONS ###\r\n" + 
			"}";
	
	@Override
	public void exportModel(Process model, String filename, IProgressVisualizer progress) {
		progress.setIndeterminate(true);
		progress.setText("Exporting Graphviz BPMN file...");
		progress.start();
		Logger.instance().info("Starting process exportation");
		preprocessString(model);
		try {
			FileUtils.writeStringToFile(new File(filename), basic);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.instance().info("Process exportation complete");
		progress.finished();
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
				buffer += "\tc_" + o.getId() + " [label=\"" + o.getName() + " = "+ o.getValue() +"\"];\n";
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
			if (o.getObjectOwner() != null) {
				if (o.getDirectionOwner() == DATA_OBJECT_DIRECTION.REQUIRED) {
					String left = "c_" + o.getId();
					String right = "c_" + o.getObjectOwner().getId();
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
