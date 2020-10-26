package plg.web.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.io.exporter.BPMNExporter;
import plg.io.exporter.GraphvizBPMNExporter;
import plg.io.exporter.IFileExporter;
import plg.io.exporter.PNMLExporter;
import plg.io.exporter.TPNExporter;
import plg.model.Process;
import plg.web.model.Process4Web;
import plg.web.utils.ProcessUtils;

@RestController
@RequestMapping("/api/v2/process/")
public class ProcessGeneratorController {

	@GetMapping("/randomize")
	public Process4Web generate() {
		Process p = new Process("");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		String model = "";
		try {
			model = ProcessUtils.process2plg(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Process4Web(p.getId(), model);
	}
	
	@PostMapping("/plg2bpmn")
	public String plg2bpmn(@RequestParam Process4Web process) throws IOException {
		return exporter(process.getPlgProcess(), new BPMNExporter());
	}
	
	@PostMapping("/plg2tpn")
	public String plg2tpn(@RequestParam Process4Web process) throws IOException {
		return exporter(process.getPlgProcess(), new TPNExporter());
	}
	
	@PostMapping("/plg2pnml")
	public String plg2pnml(@RequestParam Process4Web process) throws IOException {
		return exporter(process.getPlgProcess(), new PNMLExporter());
	}
	
	@PostMapping("/plg2dot")
	public String plg2graphviz(@RequestParam Process4Web process) throws IOException {
		return exporter(process.getPlgProcess(), new GraphvizBPMNExporter());
	}
	
	private String exporter(Process process, IFileExporter exporter) throws IOException {
		String model = "";
		File fDot = File.createTempFile("model", "out");
		exporter.exportModel(process, fDot.getAbsolutePath());
		model = new String(Files.readAllBytes(fDot.toPath()));
		fDot.delete();
		return model;
	}
}
