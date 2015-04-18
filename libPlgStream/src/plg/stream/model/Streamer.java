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
		
		// populate the buffer
		populateBuffer();
		
		Logger.instance().info("Streaming started");
		
		// start the thread
		start();
		
		new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Buffer size: " + buffer.eventsInQueue());
			}
		}).start();;
	}
	
	public synchronized void endStream() {
		enabled = false;
		
		// stop the broadcast service
		try {
			broadcaster.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	}
	
	protected synchronized void streamEvent() {
		if (buffer.isEmpty()) {
			populateBuffer();
		}
		
		broadcaster.send(buffer.getEventToStream());
		
		if (buffer.needsTraces()) {
			populateBuffer();
		}
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
