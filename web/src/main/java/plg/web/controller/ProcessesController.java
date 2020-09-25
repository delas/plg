package plg.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.io.exporter.BPMNExporter;
import plg.io.exporter.GraphvizBPMNExporter;
import plg.model.Process;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.Streamer;
import plg.web.utils.ProcessUtils;

@RestController
public class ProcessesController {

	private Set<String> streaming = new HashSet<String>();

	@GetMapping(path = RestAPIConfig.API_PREFIX + "/processes/randomize")
	public String generate() {
		Process p = new Process("");
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		String model = "";
		try {
			model = ProcessUtils.process2plg(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	@PostMapping(path = RestAPIConfig.API_PREFIX + "/processes/plg2bpmn")
	public String plg2bpmn(@RequestParam("plg") String plgModel) {
		String model = "";
		
		try {
			File fDot = File.createTempFile("model", "bpmn");
			Process p = ProcessUtils.plg2process(plgModel);
			BPMNExporter e = new BPMNExporter();
			e.exportModel(p, fDot.getAbsolutePath());
			model = new String(Files.readAllBytes(fDot.toPath()));
			fDot.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	@PostMapping(path = RestAPIConfig.API_PREFIX + "/processes/plg2dot")
	public String plg2graphviz(@RequestParam("plg") String plgModel) {
		String model = "";
		
		try {
			File fDot = File.createTempFile("model", "dot");
			Process p = ProcessUtils.plg2process(plgModel);
			GraphvizBPMNExporter e = new GraphvizBPMNExporter();
			e.exportModel(p, fDot.getAbsolutePath());
			model = new String(Files.readAllBytes(fDot.toPath()));
			fDot.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	@PostMapping(path = RestAPIConfig.API_PREFIX + "/processes/stream/{processName}")
	public String stream(@RequestParam("plg") String plgModel, @RequestParam("broker") String broker, @RequestParam("topic") String topic, @PathVariable String processName) {
		Process p;
		try {
			p = ProcessUtils.plg2process(plgModel);
			
			if (streaming.contains(p.getId())) {
				return "false";
			}
			
			StreamConfiguration sc = new StreamConfiguration();
			sc.brokerHost = broker;
			sc.topicBase = topic;
			sc.maximumParallelInstances = 20;
			sc.timeFractionBeforeNewTrace = 1;
			sc.markTraceBeginningEnd = true;
			sc.timeMultiplier = 0.0005;
			
			Streamer s = new Streamer(sc, processName, p, new SimulationConfiguration());
			s.startStream();
			streaming.add(p.getId());
			
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					s.endStream();
					streaming.remove(p.getId());
				}
			}, 1000 * 60 * 5);
			
		} catch (IOException e) {
			e.printStackTrace();
			return "false";
		}
		
		return "true";
	}
}
