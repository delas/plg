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
		System.out.println("start");
		
		BPMNImporter importer = new BPMNImporter();
		System.out.print("importing... ");
		Process p = importer.importModel("/home/delas/desktop/model.xml");
		System.out.println("done");
		
		System.out.print("checking... ");
		p.check();
		System.out.println("done");
		
		System.out.print("generating log... ");
		LogGenerator generator = new LogGenerator(p);
		XLog log = generator.generateLog(1000);
		System.out.println("done");
		System.out.print("exporting log... ");
		XSerializer serializer = new XMxmlSerializer();
		serializer.serialize(log, new FileOutputStream("/home/delas/desktop/log.mxml"));
		System.out.println("done");
	}

}
