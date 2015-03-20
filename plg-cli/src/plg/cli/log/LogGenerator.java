package plg.cli.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import plg.exceptions.InvalidProcessException;
import plg.generator.ProgressAdapter;
import plg.generator.log.SimulationConfiguration;
import plg.io.importer.SignavioBPMNImporter;
import plg.model.Process;
import plg.utils.PlgConstants;

/**
 * This class contains the main program to simulate a process
 * 
 * @author Andrea Burattin
 */
public class LogGenerator {

	/**
	 * This class contains the command line parameters
	 *
	 * @author Andrea Burattin
	 */
	private class CLIOptions {
	
		@Option(
			name = "--version",
			aliases = "-v",
			usage = "check the current version of the application"
		)
		public Boolean printVersion = false;
		
		@Option(
			name = "--model",
			aliases = "-m",
			usage = "the process model (as a Signavio BPMN) to use"
		)
		public File modelFile = null;
		
		@Option(
			name = "--log",
			aliases = "-l",
			usage = "the destination log file"
		)
		public String logDestination = null;
		
		@Option(
			name = "--traces",
			aliases = "-c",
			usage = "the number of traces to generate"
		)
		public Integer noTraces = null;
	}
	
	/**
	 * 
	 * @param args
	 * @throws InvalidProcessException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InvalidProcessException, FileNotFoundException, IOException {
		
		// CLI parameters parsing
		CLIOptions parameters = new LogGenerator().new CLIOptions();
		CmdLineParser parser = new CmdLineParser(parameters);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("");
			System.err.println("java -jar LogGenerator.jar -m <model file> -l <destination log> -c <no of traces>");
			parser.printUsage(System.err);
			System.exit(1);
		}
		
		// check parameters
		if (!parameters.printVersion &&
			(parameters.modelFile == null || parameters.logDestination == null || parameters.noTraces == null)) {
			System.err.println("java -jar LogGenerator.jar -m <model file> -l <destination log> -c <no of traces>");
			parser.printUsage(System.err);
			System.exit(1);
		}
		
		// version menu
		if (parameters.printVersion) {
			System.out.println("PLG-CLI (" + PlgConstants.libPLG_SIGNATURE + ")");
			System.exit(0);
		}
		
		// parameters summary
		System.out.println("PLG-CLI (" + PlgConstants.libPLG_SIGNATURE + ")");
		System.out.println("");
		System.out.println("Model: " + parameters.modelFile);
		System.out.println("Log destination: " + parameters.logDestination);
		System.out.println("No. of traces: " + parameters.noTraces);
		System.out.println("");
		
		// model import
		System.out.print("1. Importing model... ");
		SignavioBPMNImporter importer = new SignavioBPMNImporter();
		Process p = importer.importModel(parameters.modelFile.getAbsolutePath());
		System.out.println("done!");
		
		// model checking
		System.out.print("2. Model checking... ");
		p.check();
		System.out.println("done!");
		
		// log generations
		System.out.print("3. Generating log... ");
		SimulationConfiguration sc = new SimulationConfiguration(parameters.noTraces);
		plg.generator.log.LogGenerator generator = new plg.generator.log.LogGenerator(p, sc, new ProgressAdapter());
		XLog log = generator.generateLog();
		System.out.println("done!");
		
		// log export
		System.out.print("4. Exporting log... ");
		XSerializer serializer = new XMxmlSerializer();
		if (parameters.logDestination.substring(parameters.logDestination.length() - 3).equals("xes")) {
			serializer = new XesXmlSerializer();
		}
		serializer.serialize(log, new FileOutputStream(parameters.logDestination));
		System.out.println("done!");
	}
}
