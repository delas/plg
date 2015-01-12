package plg.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.exporter.FileExporter;
import plg.exporter.GraphvizBPMNExporter;
import plg.exporter.GraphvizPetriNetExporter;
import plg.exporter.PNMLExporter;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.generator.process.petrinet.PetriNet;
import plg.model.Process;

public class TestModel {

	public static void main(String[] args) throws
			IllegalSequenceException,
			InvalidProcessException,
			InvalidDataObject,
			FileNotFoundException,
			IOException,
			InterruptedException, ParserConfigurationException, TransformerException {
		
		Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES.setDepth(2));
		
		// process simulation
//		SimulationConfiguration sc = new SimulationConfiguration(1);
//		sc.setMultithreadingUsage(true);
//		LogGenerator generator = new LogGenerator(p, sc);
//		long startSimTime = System.currentTimeMillis();
//		XLog log = generator.generateLog();
//		System.out.println("Millisecs required: " + (System.currentTimeMillis() - startSimTime));
//		System.out.println("Log generated");
		
		// log serialization
//		File xesFile = new File("/home/delas/desktop/log.xes"); //File.createTempFile("model", ".xes");
//		XesXmlSerializer serializer = new XesXmlSerializer();
//		try {
//			serializer.serialize(log, new FileOutputStream(xesFile.getAbsolutePath()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Log file: " + xesFile.getAbsolutePath());
//		System.out.println("Log serialized");
		
		
		// output and pdf visualization
		File dotFile = File.createTempFile("model", ".dot");
		FileExporter exporter = new GraphvizBPMNExporter();
		exporter.exportModel(p, dotFile.getAbsolutePath());
		File pdfFile = File.createTempFile("model", ".pdf");
		String[] env = {"PATH=/bin:/usr/bin/"};
		Runtime.getRuntime().exec("dot -Tpdf "+ dotFile.getAbsolutePath() + " -o " + pdfFile.getAbsolutePath(), env);
		
		File dotFile2 = File.createTempFile("model", ".dot");
		FileExporter exporter2 = new GraphvizPetriNetExporter();
		exporter2.exportModel(p, dotFile2.getAbsolutePath());
		File pdfFile2 = File.createTempFile("model", ".pdf");
		Runtime.getRuntime().exec("dot -Tpdf "+ dotFile2.getAbsolutePath() + " -o " + pdfFile2.getAbsolutePath(), env);
		Thread.sleep(500);
		
		new ProcessBuilder("evince", pdfFile.getAbsolutePath()).start();
		new ProcessBuilder("evince", pdfFile2.getAbsolutePath()).start();
	}
}
