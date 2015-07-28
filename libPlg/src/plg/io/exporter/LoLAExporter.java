package plg.io.exporter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import plg.annotations.Exporter;
import plg.generator.IProgressVisualizer;
import plg.generator.process.petrinet.Node;
import plg.generator.process.petrinet.PetriNet;
import plg.generator.process.petrinet.Place;
import plg.generator.process.petrinet.Transition;
import plg.model.Process;
import plg.utils.Pair;

/**
 * This class contains the exported module into a LoLA file, for the Petri net
 * version of the process
 * 
 * @author Andrea Burattin
 * @see http://service-technology.org/lola/
 */
@Exporter(
	name = "Petri net as LoLA file",
	fileExtension = "lola"
)
public class LoLAExporter extends FileExporter {

	int progress = 1;
	
	@Override
	public void exportModel(Process model, String filename, IProgressVisualizer progressVisualizer) {
		progressVisualizer.setIndeterminate(true);
		progressVisualizer.setText("Exporting LoLA file...");
		progressVisualizer.start();
		
		// export the model into a petri net
		PetriNet pn = new PetriNet(model);
		try {
			PrintWriter pw = new PrintWriter(filename);
			progressVisualizer.setMinimum(0);
			progressVisualizer.setMaximum(pn.getEdges().size() + pn.getTransitions().size());
			progressVisualizer.setIndeterminate(false);
			
			// export places
			int current = 0;
			pw.write("PLACE\n");
			for (Place p : pn.getPlaces()) {
				if (current++ > 0) {
					pw.write(", ");
				}
				pw.write(p.getComponentId());
				progressVisualizer.inc();
			}
			pw.write(";\n\n");
			
			// export marking
			current = 0;
			pw.write("MARKING\n");
			for (Place p : pn.getPlaces()) {
				if (p.getTokens() > 0) {
					if (current > 0) {
						pw.write(",\n");
					}
					pw.write("\t" + p.getComponentId() + ": 1");
					current++;
				}
			}
			pw.write(";\n\n");
			
			// export transitions
			for (Transition t : pn.getTransitions()) {
				// build incoming and outgoing connections
				Set<Node> in = new HashSet<Node>();
				Set<Node> out = new HashSet<Node>();
				for(Pair<Node, Node> e : pn.getEdges()) {
					if(e.getFirst().equals(t)) {
						out.add(e.getSecond());
					} else if (e.getSecond().equals(t)) {
						in.add(e.getFirst());
					}
				}
				// write the output
				pw.write("TRANSITION " + t.getComponentId() + "\n");
				current = 0;
				pw.write("\tCONSUME ");
				for (Node n : in) {
					if (current++ > 0) {
						pw.write(", ");
					}
					pw.write(n.getComponentId() + ": 1");
				}
				pw.write(";\n");
				current = 0;
				pw.write("\tPRODUCE ");
				for (Node n : out) {
					if (current++ > 0) {
						pw.write(", ");
					}
					pw.write(n.getComponentId() + ": 1");
				}
				pw.write(";\n\n");
				progressVisualizer.inc();
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		progressVisualizer.finished();
	}
}
