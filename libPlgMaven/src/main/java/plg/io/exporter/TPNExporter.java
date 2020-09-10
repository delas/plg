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
 * This class contains the exported module into a TPN file, for the Petri net
 * version of the process
 * 
 * @author Andrea Burattin
 * @see http://www.win.tue.nl/~hverbeek/doku.php?id=projects:prom:plug-ins:import:tpn
 */
@Exporter(
	name = "Petri net as TPN file",
	fileExtension = "tpn"
)
public class TPNExporter extends FileExporter {

	int progress = 1;
	
	@Override
	public void exportModel(Process model, String filename, IProgressVisualizer progressVisualizer) {
		progressVisualizer.setIndeterminate(true);
		progressVisualizer.setText("Exporting TPN file...");
		progressVisualizer.start();
		
		// export the model into a petri net
		PetriNet pn = new PetriNet(model);
		try {
			PrintWriter pw = new PrintWriter(filename);
			progressVisualizer.setMinimum(0);
			progressVisualizer.setMaximum(pn.getEdges().size() + pn.getTransitions().size());
			progressVisualizer.setIndeterminate(false);
			
			// export places
			for (Place p : pn.getPlaces()) {
				pw.write("place \"" + p.getComponentId() + "\"");
				if (p.getTokens() > 0) {
					pw.write(" init " + p.getTokens());
				}
				pw.write(";\n");
				progressVisualizer.inc();
			}
			
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
				pw.write("trans \"" + t.getComponentId() + "\"~\"" + t.getLabel() + "\" in ");
				for (Node n : in) {
					pw.write("\"" + n.getComponentId() + "\" ");
				}
				pw.write("out ");
				for (Node n : out) {
					pw.write("\"" + n.getComponentId() + "\" ");
				}
				pw.write(";\n");
				progressVisualizer.inc();
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		progressVisualizer.finished();
	}
}
