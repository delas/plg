package plg.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlGZIPSerializer;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.generator.log.LogGenerator;
import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.generator.process.petrinet.PetriNet;
import plg.io.exporter.GraphvizBPMNExporter;
import plg.io.exporter.GraphvizPetriNetExporter;
import plg.io.exporter.IFileExporter;
import plg.io.exporter.PNMLExporter;
import plg.io.importer.PLGImporter;
import plg.io.importer.SignavioBPMNImporter;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;
import plg.model.sequence.Sequence;

public class TestModel {

	public static void main(String[] args) throws
			IllegalSequenceException,
			InvalidProcessException,
			InvalidDataObject,
			FileNotFoundException,
			IOException,
			InterruptedException, ParserConfigurationException, TransformerException {
		
//		for (int i = 0; i < 200; i++) {
//			Process p = new Process("test");
//			ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES.setDepth(3).setDataObjectProbability(0.1));
//			
//			IFileExporter exporter = new GraphvizBPMNExporter();
//			exporter.exportModel(p, "C:\\Users\\Andrea\\Desktop\\test-files\\test-"+i+".dot");
//		}
//		
//		for (int i = 0; i < 200; i++) {
//			System.out.println("dot -Tpng test-"+ i + ".dot > test-" + i + ".png");
//		}
		
//		SignavioBPMNImporter i = new SignavioBPMNImporter();
		PLGImporter i = new PLGImporter();
//		Process p = generateProcess();
//		Process p2 = (Process) p.clone();
		Process p = i.importModel("C:\\Users\\Andrea\\Desktop\\test.plg");
//		Process p = i.importModel("C:\\Users\\Andrea\\Desktop\\Hybrid-no-time.bpmn");
		
//		LogGenerator g = new LogGenerator(p, new SimulationConfiguration(1000));
//		XLog l = g.generateLog();
//		XesXmlGZIPSerializer s = new XesXmlGZIPSerializer();
//		s.serialize(l, new FileOutputStream("C:\\Users\\Andrea\\Desktop\\testlog.xes.gz"));
		
		GraphvizBPMNExporter e = new GraphvizBPMNExporter();
		e.exportModel(p, "C:\\Users\\Andrea\\Desktop\\model.dot");
//		e.exportModel(p2, "C:\\Users\\Andrea\\Desktop\\model2.dot");
		System.out.println("done");
		
	}

	private static Process generateProcess() throws IllegalSequenceException,
			InvalidProcessException {
		Process p = new Process("test");
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		Gateway split = p.newParallelGateway();
		Gateway join = p.newParallelGateway();
		Task a = p.newTask("a");
		Task b = p.newTask("b");
		Task c = p.newTask("c");
		Task d = p.newTask("d");
		Task e = p.newTask("e");
		Task f = p.newTask("f");
		p.newSequence(start, a);
		p.newSequence(a, split);
		Sequence s = p.newSequence(split, b); p.newSequence(b, join);
		p.newSequence(split, c); p.newSequence(c, join);
		p.newSequence(split, d); p.newSequence(d, join);
		p.newSequence(split, e); p.newSequence(e, join);
		p.newSequence(e, join);
		p.newSequence(join, f);
		p.newSequence(f, end);
		
		new DataObject(p).set("d1", "v1");
		new DataObject(p, s).set("d2", "v2");
		new DataObject(p, c).set("d3", "v3");
		
		p.check();
		return p;
	}
}
