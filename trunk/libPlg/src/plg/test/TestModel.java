package plg.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.exporter.Exporter;
import plg.exporter.GraphvizExporter;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;

public class TestModel {

	public static void main(String[] args) throws
			IllegalSequenceException,
			InvalidProcessException,
			InvalidDataObject,
			FileNotFoundException,
			IOException,
			InterruptedException {
		
		final Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		System.out.println("Process randomization complete");
		
		File dotFile = File.createTempFile("model", ".dot");
		Exporter exporter = new GraphvizExporter();
		exporter.exportModel(p, dotFile.getAbsolutePath());
		
//		SimulationConfiguration sc = new SimulationConfiguration(1);
//		sc.setMultithreadingUsage(true);
//		LogGenerator generator = new LogGenerator(p, sc);
//		long startSimTime = System.currentTimeMillis();
//		XLog log = generator.generateLog();
//		System.out.println("Millisecs required: " + (System.currentTimeMillis() - startSimTime));
//		System.out.println("Log generated");
		
//		File xesFile = new File("/home/delas/desktop/log.xes"); //File.createTempFile("model", ".xes");
//		XesXmlSerializer serializer = new XesXmlSerializer();
//		try {
//			serializer.serialize(log, new FileOutputStream(xesFile.getAbsolutePath()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Log file: " + xesFile.getAbsolutePath());
//		System.out.println("Log serialized");
		
		File pdfFile = File.createTempFile("model", ".pdf");
		
		String[] env = {"PATH=/bin:/usr/bin/"};
		Runtime.getRuntime().exec("dot -Tpdf "+ dotFile.getAbsolutePath() + " -o " + pdfFile.getAbsolutePath(), env);
		Thread.sleep(500);
		new ProcessBuilder("evince", pdfFile.getAbsolutePath()).start();
	}
	
	@SuppressWarnings("unused")
	private static Process generateProcess() throws IllegalSequenceException, InvalidProcessException {
		Process p = new Process("test");
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		Gateway split = p.newParallelGateway();
		Gateway join = p.newParallelGateway();
		Gateway loop2_split = p.newExclusiveGateway();
		Gateway loop2_join = p.newExclusiveGateway();
		Task a = p.newTask("a");
		Task d = p.newTask("d");
		Task i = p.newTask("i");
		Task l = p.newTask("l");
		Task m = p.newTask("m");
		Task z = p.newTask("z");
		p.newSequence(start, a);
		p.newSequence(a, loop2_join);
		p.newSequence(loop2_join, m);
		p.newSequence(m, split);
		p.newSequence(split, d);
		p.newSequence(split, i);
		p.newSequence(d, join);
		p.newSequence(i, join);
		p.newSequence(join, z);
		p.newSequence(z, loop2_split);
		p.newSequence(loop2_split, l);
		p.newSequence(loop2_split, loop2_join);
		p.newSequence(l, end);
		p.check();
		return p;
	}
}
