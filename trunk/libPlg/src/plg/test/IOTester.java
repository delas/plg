package plg.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;

import plg.exceptions.InvalidProcessException;
import plg.generator.log.LogGenerator;
import plg.importer.BPMNImporter;
import plg.model.Process;

public class IOTester {

	public static void main(String[] args) throws InvalidProcessException, FileNotFoundException, IOException {
		if (args.length != 3) {
			System.err.println("Please use: java -jar ProcessGenerator.jar MODEL_FILE LOG_DESTINATION NO_TRACES");
			System.exit(-1);
		}
		
		String modelFile = args[0];
		String logDestination = args[1];
		Integer noTraces = Integer.parseInt(args[2]);
		
		System.out.println("Welcome!");
		
		System.out.println("Model: " + modelFile);
		System.out.println("Log destination: " + logDestination);
		System.out.println("No. of traces: " + noTraces);
		
		System.out.print("1. Importing model... ");
		BPMNImporter importer = new BPMNImporter();
		Process p = importer.importModel(modelFile);
		System.out.println("done!");
		
		System.out.print("2. Model checking... ");
		p.check();
		System.out.println("done!");
		
		System.out.print("3. Generating log... ");
		LogGenerator generator = new LogGenerator(p);
		XLog log = generator.generateLog(noTraces);
		System.out.println("done!");
		
		System.out.print("4. Exporting log... ");
		XSerializer serializer = new XMxmlSerializer();
		serializer.serialize(log, new FileOutputStream(logDestination));
		System.out.println("done!");
	}

}
