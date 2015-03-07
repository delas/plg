package plg.io.exporter;

import java.io.File;
import java.io.IOException;

import org.adrianwalker.multilinestring.Multiline;
import org.apache.commons.io.FileUtils;

import plg.annotations.Exporter;
import plg.generator.process.petrinet.Node;
import plg.generator.process.petrinet.PetriNet;
import plg.generator.process.petrinet.Place;
import plg.generator.process.petrinet.Transition;
import plg.model.Process;
import plg.utils.Logger;
import plg.utils.Pair;

/**
 * This class contains the export module for Graphviz Dot file (of the Petri
 * net), which can be converted, for example, into a PDF or SVG or PNG file.
 * 
 * @author Andrea Burattin
 * @see http://www.graphviz.org/content/dot-language
 */
@Exporter(
	name = "Graphviz exporter for a Petri net version of the process",
	fileExtension = "dot"
)
public class GraphvizPetriNetExporter implements IFileExporter {

	/**
	 * The following comment contains the basic string with the Graphviz Dot
	 * baseline for the process. We used the Multiline annotation
	 * (https://github.com/benelog/multiline) in order to populate our variable
	 * with a very long and multiline string.
	 */
	/**
digraph G {
	ranksep=".3";
	fontsize="10";
	remincross=true;
	margin="0.0,0.0";
 	fontname="Arial";
	rankdir="LR";
	
	edge [arrowsize="0.5"];
	node [height=".2",width=".2",fontname="Arial",fontsize="10"];

	### TRANSITIONS ###

	### PLACES ###

	### ARCS ###
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
		PetriNet pn = new PetriNet(model);
		
		// transitions
		String buffer = "";
		for(Transition t : pn.getTransitions()) {
			if (t.isSilent()) {
				buffer += "\t" + t.getComponentId() + " [shape=\"box\",label=\"\",height=\".2\",width=\".2\",style=\"filled\",fillcolor=\"black\"];\n";
			} else {
				buffer += "\t" + t.getComponentId() + " [shape=\"box\",label=\""+ t.getLabel() +"\"];\n";
			}
		}
		basic = basic.replace("### TRANSITIONS ###", buffer);
		
		// places
		buffer = "";
		for(Place p : pn.getPlaces()) {
			if (p.getTokens() > 0) {
				buffer += "\t" + p.getComponentId() + " [shape=\"circle\",label=\"\",height=\".1\",width=\".1\",style=\"filled\",fillcolor=\"black\",peripheries=\"2\"]\n";
			} else {
				buffer += "\t" + p.getComponentId() + " [shape=\"circle\",label=\"\"]\n";
			}
		}
		basic = basic.replace("### PLACES ###", buffer);
		
		// edges
		buffer = "";
		for(Pair<Node, Node> s : pn.getEdges()) {
			String left = s.getFirst().getComponentId();
			String right = "" + s.getSecond().getComponentId();
			buffer +=  "\t" + left + " -> " + right + " [label=\"\"];\n";
		}
		basic = basic.replace("### ARCS ###", buffer);
	}
}
