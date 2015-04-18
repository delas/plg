package plg.stream.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TimerTask;

import javax.swing.Timer;

import plg.model.Process;
import plg.generator.log.SimulationConfiguration;
import plg.generator.log.TraceGenerator;
import plg.stream.BroadcastService;
import plg.stream.configuration.StreamConfiguration;
import plg.utils.Logger;

public class Streamer extends Thread {

	private int generatedInstances = 0;
	private StreamConfiguration configuration;
	private Process process;
	private SimulationConfiguration simulationParameters;
	private BroadcastService broadcaster;
	
	private StreamBuffer buffer;
	
	private boolean enabled = true;
	private Timer infoTimer = null;
	
	public Streamer(StreamConfiguration configuration, Process process, SimulationConfiguration simulationParameters) {
		this.configuration = configuration;
		this.process = process;
		this.simulationParameters = simulationParameters;
		this.buffer = new StreamBuffer(configuration);
		this.broadcaster = new BroadcastService(configuration);
	}
	
	public synchronized void startStream() {
		enabled = true;

		// start the broadcast service
		try {
			broadcaster.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// start the stream info thread
		infoTimer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.instance().debug("Stream Buffer size: " + buffer.eventsInQueue());
			}
		});
		infoTimer.start();
		
		// populate the buffer
		populateBuffer();
		
		Logger.instance().info("Streaming started");
		
		// start the thread
		start();
	}
	
	public synchronized void endStream() {
		enabled = false;
		
		// stop the broadcast service
		try {
			broadcaster.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// end the stream info thread
		infoTimer.stop();
		
		Logger.instance().info("Streaming stopped");
	}
	
	protected synchronized void populateBuffer() {
		TraceGenerator th = new TraceGenerator(process,
				String.format(simulationParameters.getCaseIdPattern(), generatedInstances++),
				simulationParameters);
		try {
			th.start();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		buffer.enqueueTrace(th.getGeneratedTrace());
		
		Logger.instance().debug("Generated new traces (" + generatedInstances + " so far)");
	}
	
	protected synchronized void streamEvent() {
		if (buffer.isEmpty()) {
			populateBuffer();
		}
		
		broadcaster.send(buffer.getEventToStream());
		
		if (buffer.needsTraces()) {
			populateBuffer();
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) { }
	}
	
	protected synchronized void updateProcess(Process process) {
		this.process = process;
	}
	
	
	@Override
	public void run() {
		while(enabled) {
			// stream next event
			streamEvent();
		}
	}
}
