package plg.stream.test;

import plg.exceptions.IllegalSequenceException;
import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.Streamer;

public class Tester {

	/*public static void main(String args[]) {
		System.out.println("start");

		XTrace t1 = XLogHelper.createTrace("t1");
		XLogHelper.insertEvent(t1, "a", new Date(115, 1, 1));
		XLogHelper.insertEvent(t1, "b", new Date(115, 1, 2));
		XLogHelper.insertEvent(t1, "c", new Date(115, 1, 5));

		XTrace t2 = XLogHelper.createTrace("t2");
		XLogHelper.insertEvent(t2, "a", new Date(115, 0, 1));
		XLogHelper.insertEvent(t2, "b", new Date(115, 0, 2));
		XLogHelper.insertEvent(t2, "c", new Date(115, 0, 5));

		XTrace t3 = XLogHelper.createTrace("t3");
		XLogHelper.insertEvent(t3, "a", new Date(115, 0, 1));
		XLogHelper.insertEvent(t3, "b", new Date(115, 0, 2));
		XLogHelper.insertEvent(t3, "c", new Date(115, 0, 5));
		
		StreamConfiguration sc = new StreamConfiguration();
		sc.maximumParallelInstances = 1;
		sc.timeFractionBeforeNewTrace = 1;
		StreamBuffer sb = new StreamBuffer(sc);
		sb.enqueueTrace(t1);
		sb.enqueueTrace(t2);
		sb.enqueueTrace(t3);
		
		StreamEvent se = null;
		do {
			se = sb.getEventToStream();
			if (se != null) {
				System.out.println(XLogHelper.getName(se.get(0)) + " " + XLogHelper.getName(se) + " - " + se.getDate());
				System.out.flush();
			}
		} while (se != null);
		
		System.out.println("complete");
	}*/
	
	public static void main(String args[]) throws IllegalSequenceException {
		System.out.println("start");
		
		StreamConfiguration sc = new StreamConfiguration();
		sc.servicePort = 1234;
		sc.maximumParallelInstances = 10;
		sc.timeFractionBeforeNewTrace = 1;
		
		Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		
		Streamer s = new Streamer(sc, p, new SimulationConfiguration());
		s.startStream();
	}
}
