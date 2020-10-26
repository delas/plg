package plg.web.utils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import plg.io.exporter.PLGExporter;
import plg.io.importer.PLGImporter;
import plg.model.Process;

public class ProcessUtils {

	public static Process plg2process(String process) throws IOException {
		File fPlg = File.createTempFile("model", "plg");
		Files.write(fPlg.toPath(), process.getBytes());
		PLGImporter i = new PLGImporter();
		Process p = i.importModel(fPlg.getAbsolutePath());
		fPlg.delete();
		return p;
	}
	
	public static String process2plg(Process process) throws IOException {
		PLGExporter e = new PLGExporter();
		String model = "";
		File f = File.createTempFile("model", "plg");
		e.exportModel(process, f.getAbsolutePath());
		model = new String(Files.readAllBytes(f.toPath()));
		f.delete();
		return model;
	}
}
