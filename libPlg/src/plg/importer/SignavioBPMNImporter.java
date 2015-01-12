package plg.importer;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import com.google.common.base.CharMatcher;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.DataObjectOwner;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

/**
 * This importing module works with process models represented as BPMN. In
 * particular, this class imports from XML files expresses using the
 * {@link http://schema.omg.org/spec/BPMN/2.0}.
 * 
 * This class imports:
 * <ul>
 * 	<li>start and end events;</li>
 * 	<li>tasks;</li>
 * 	<li>AND and XOR gateways;</li>
 * 	<li>data objects.</li>
 * </ul>
 * 
 * @author Andrea Burattin
 */
public class SignavioBPMNImporter implements FileImporter {

	private static final Namespace ns = Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");
	private static final Pattern REGEX_SIMPLE = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");
	private static final Pattern REGEX_INTEGER_SCRIPT = Pattern.compile("(?i)\\s*(\\S+)\\s*\\(\\s*Integer\\s*\\)\\s*");
	private static final Pattern REGEX_STRING_SCRIPT = Pattern.compile("(?i)\\s*(\\S+)\\s*\\(\\s*String\\s*\\)\\s*");
	
	@Override
	public Process importModel(String filename) {
		HashMap<String, Component> inverseKey = new HashMap<String, Component>();
		HashMap<String, Set<Task>> taskToDataObject = new HashMap<String, Set<Task>>();
		HashMap<String, Set<Task>> dataObjectToTask = new HashMap<String, Set<Task>>();
		
		Process p = null;
		try {
			FileInputStream input = new FileInputStream(filename);
			
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(input);
			Element definitions = document.getRootElement();
			Element process = definitions.getChild("process", ns);
			p = new Process(process.getAttributeValue("id"));
			
			// Events (start and end)
			for (Element ss : process.getChildren("startEvent", ns)) {
				StartEvent s = new StartEvent(p);
				inverseKey.put(ss.getAttributeValue("id"), s);
			}
			for (Element es : process.getChildren("endEvent", ns)) {
				EndEvent e = new EndEvent(p);
				inverseKey.put(es.getAttributeValue("id"), e);
			}
			// Tasks
			for (Element ts : process.getChildren("task", ns)) {
				Task t = new Task(p, ts.getAttributeValue("name"));
				String script = ts.getChildText("documentation", ns);
				if (script != null) {
					t.setActivityScript(new IntegerScriptExecutor(script));
				}
				inverseKey.put(ts.getAttributeValue("id"), t);
				for (Element doa : ts.getChildren("dataOutputAssociation", ns)) {
					Set<Task> s = new HashSet<Task>();
					if (taskToDataObject.containsKey(doa.getChildText("targetRef", ns))) {
						s = taskToDataObject.get(doa.getChildText("targetRef", ns));
					}
					s.add(t);
					taskToDataObject.put(doa.getChildText("targetRef", ns), s);
				}
				for (Element doa : ts.getChildren("dataInputAssociation", ns)) {
					Set<Task> s = new HashSet<Task>();
					if (dataObjectToTask.containsKey(doa.getChildText("sourceRef", ns))) {
						s = dataObjectToTask.get(doa.getChildText("sourceRef", ns));
					}
					s.add(t);
					dataObjectToTask.put(doa.getChildText("sourceRef", ns), s);
				}
			}
			// Gateways
			for (Element gs : process.getChildren("exclusiveGateway", ns)) {
				ExclusiveGateway g = new ExclusiveGateway(p);
				inverseKey.put(gs.getAttributeValue("id"), g);
			}
			for (Element gs : process.getChildren("parallelGateway", ns)) {
				ParallelGateway g = new ParallelGateway(p);
				inverseKey.put(gs.getAttributeValue("id"), g);
			}
			// Sequences
			for (Element ss : process.getChildren("sequenceFlow", ns)) {
				FlowObject source = (FlowObject) inverseKey.get(ss.getAttributeValue("sourceRef"));
				FlowObject sink = (FlowObject) inverseKey.get(ss.getAttributeValue("targetRef"));
				new Sequence(p, source, sink);
			}
			// Data Objects
			for (Element ds : process.getChildren("dataObjectReference", ns)) {
				String doId = ds.getAttributeValue("id");
				if (taskToDataObject.containsKey(doId)) {
					for(Task t : taskToDataObject.get(doId)) {
						parseDataObject(ds, t, p);
					}
				} else if (dataObjectToTask.containsKey(doId)) {
					for(Task t : dataObjectToTask.get(doId)) {
						for (FlowObject fo : t.getIncomingObjects()) {
							Sequence s = p.getSequence(fo, t);
							parseDataObject(ds, s, p);
						}
					}
				} else {
					parseDataObject(ds, null, p);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}
	
	/**
	 * Method that parses a data object XML structure to generate the logic
	 * business object
	 * 
	 * @param dataObjectElement
	 * @param owner
	 * @return
	 */
	private DataObject parseDataObject(Element dataObjectElement, DataObjectOwner owner, Process process) {
		DataObject dataObject = null;
		String name = dataObjectElement.getAttributeValue("name");
		Matcher matcherSimple = REGEX_SIMPLE.matcher(name);
		Matcher matcherIntegerScript = REGEX_INTEGER_SCRIPT.matcher(name);
		Matcher matcherStringScript = REGEX_STRING_SCRIPT.matcher(name);
		
		if (matcherSimple.matches()) {
			dataObject = new DataObject(process);
			dataObject.setName(matcherSimple.group(1));
			dataObject.setValue(matcherSimple.group(2));
		} else if (matcherIntegerScript.matches()) {
			String script = CharMatcher.ASCII.retainFrom(dataObjectElement.getChildText("documentation", ns));
			dataObject = new IntegerDataObject(process, new IntegerScriptExecutor(script));
			dataObject.setName(matcherIntegerScript.group(1));
		} else if (matcherStringScript.matches()) {
			String script = CharMatcher.ASCII.retainFrom(dataObjectElement.getChildText("documentation", ns));
			dataObject = new StringDataObject(process, new StringScriptExecutor(script));
			dataObject.setName(matcherStringScript.group(1));
		}
		
		if (owner != null) {
			dataObject.setObjectOwner(owner);
		}
		
		return dataObject;
	}
}
