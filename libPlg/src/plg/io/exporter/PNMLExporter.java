package plg.io.exporter;

import java.io.File;
import java.io.IOException;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;

import plg.annotations.Exporter;
import plg.generator.IProgressVisualizer;
import plg.generator.process.petrinet.Node;
import plg.generator.process.petrinet.PetriNet;
import plg.generator.process.petrinet.Place;
import plg.generator.process.petrinet.Transition;
import plg.model.Process;
import plg.utils.Logger;
import plg.utils.Pair;
import plg.utils.PlgConstants;

/**
 * This class contains the exported module into a PNML file, for the Petri net
 * version of the process
 * 
 * @author Andrea Burattin
 * @see http://www.pnml.org/
 */
@Exporter(
	name = "Petri net as PNML file",
	fileExtension = "pnml"
)
public class PNMLExporter extends FileExporter {

	int progress = 1;
	
	@Override
	public void exportModel(Process model, String filename, IProgressVisualizer progressVisualizer) {
		progressVisualizer.setIndeterminate(true);
		progressVisualizer.setText("Exporting PNML file...");
		progressVisualizer.start();
		
		progress = 1;
		PetriNet pn = new PetriNet(model);
		try {
			
			File file = new File(filename);
			SXDocument doc = new SXDocument(file);
			
			SXTag pnml = doc.addNode("pnml");
			SXTag net = pnml.addChildNode("net");
			net.addAttribute("id", "net1");
			net.addAttribute("type", "http://www.pnml.org/version-2009/grammar/pnmlcoremodel");
			net.addChildNode("name").addChildNode("text").addTextNode(model.getName());
			SXTag page = net.addChildNode("page");
			page.addAttribute("id", "n0");
			page.addChildNode("name").addChildNode("text");
			
			// transitions
			for (Transition t : pn.getTransitions()) {
				addTransition(page, t.getComponentId(), t.getLabel(), t.isSilent());
				progress++;
			}
			
			// places
			for (Place p : pn.getPlaces()) {
				addPlace(page, p.getComponentId(), p.getTokens());
				progress++;
			}
			
			// edges
			for (Pair<Node, Node> s : pn.getEdges()) {
				Logger.instance().debug("Exporting " + s.getFirst() + " -> " + s.getSecond());
				String id = s.getFirst().getComponentId() + "-" + s.getSecond().getComponentId();
				addSequence(page, id, s.getFirst().getComponentId(), s.getSecond().getComponentId());
			}
			
			doc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		progressVisualizer.finished();
	}
	
	/**
	 * This method adds a transition to the current exported file
	 * 
	 * @param parent
	 * @param id
	 * @param name
	 * @param isSilent
	 * @return the tag generated for the transition
	 * @throws IOException
	 */
	private SXTag addTransition(SXTag parent, String id, String name, boolean isSilent) throws IOException {
		SXTag transition = parent.addChildNode("transition");
		transition.addAttribute("id", id);
		if (isSilent) {
			transition.addChildNode("name").addChildNode("text");
			SXTag toolspec = transition.addChildNode("toolspecific");
			toolspec.addAttribute("tool", "ProM");
			toolspec.addAttribute("version", "6.4");
			toolspec.addAttribute("activity", "$invisible$");
		} else {
			transition.addChildNode("name").addChildNode("text").addTextNode(name);
		}
		SXTag toolspecific = transition.addChildNode("toolspecific");
		toolspecific.addAttribute("tool", PlgConstants.LibPLG_NAME);
		toolspecific.addAttribute("version", PlgConstants.libPLG_VERSION);
		toolspecific.addAttribute("activity", name);
		SXTag graphics = transition.addChildNode("graphics");
		SXTag position = graphics.addChildNode("position");
		position.addAttribute("x", new Integer(progress * 50).toString());
		position.addAttribute("y", "50");
		SXTag dimension = graphics.addChildNode("dimension");
		dimension.addAttribute("x", "25");
		dimension.addAttribute("y", "20");
		SXTag fill = graphics.addChildNode("fill");
		fill.addAttribute("color", "#FFFFFF");
		return transition;
	}
	
	/**
	 * This method adds a place to the current exported file
	 * 
	 * @param parent
	 * @param id
	 * @param name
	 * @param isSilent
	 * @return the tag generated for the place
	 * @throws IOException
	 */
	private SXTag addPlace(SXTag parent, String id, int tokens) throws IOException {
		SXTag place = parent.addChildNode("place");
		place.addAttribute("id", id);
		place.addChildNode("name").addChildNode("text").addTextNode(id);
		SXTag graphics = place.addChildNode("graphics");
		SXTag position = graphics.addChildNode("position");
		position.addAttribute("x", new Integer(progress * 50).toString());
		position.addAttribute("y", "50");
		SXTag dimension = graphics.addChildNode("dimension");
		dimension.addAttribute("x", "12.5");
		dimension.addAttribute("y", "12.5");
		if (tokens > 0) {
			place.addChildNode("initialMarking").addChildNode("text").addTextNode(Integer.toString(tokens));
		}
		return place;
	}
	
	/**
	 * This method adds a sequence to the current exported file
	 * 
	 * @param parent
	 * @param id
	 * @param name
	 * @param isSilent
	 * @return the tag generated for the sequence
	 * @throws IOException
	 */
	private SXTag addSequence(SXTag parent, String id, String idSource, String idSink) throws IOException {
		SXTag arc = parent.addChildNode("arc");
		arc.addAttribute("id", id);
		arc.addAttribute("source", idSource);
		arc.addAttribute("target", idSink);
		arc.addChildNode("name").addChildNode("text").addTextNode("1");
		arc.addChildNode("graphics");
		arc.addChildNode("arctype").addChildNode("text").addTextNode("normal");
		return arc;
	}
}
