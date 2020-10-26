package plg.web.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import plg.generator.log.SimulationConfiguration;
import plg.model.Process;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.Streamer;
import plg.web.model.Process4Web;

@RestController
@RequestMapping("/api/v2/streamer/")
public class StreamController {

	private Set<String> streaming = new HashSet<String>();
	
	@GetMapping("/{processId}")
	public @ResponseBody String streaming(@PathVariable String processId) {
		return streaming.contains(processId)? "true" : "false";
	}
	
	@PostMapping("/processes/stream/{processName}")
	public @ResponseBody String stream(@RequestParam("plg") Process4Web process, @RequestParam("broker") String broker, @RequestParam("topic") String topic, @PathVariable String processName) {
		try {
			if (streaming.contains(process.getId())) {
				return "false";
			}
			
			Process p = process.getPlgProcess();
			
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
