package plg.stream.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.Timer;

import plg.generator.log.SimulationConfiguration;
import plg.generator.log.TraceGenerator;
import plg.model.Process;
import plg.stream.BroadcastService;
import plg.stream.configuration.StreamConfiguration;
import plg.utils.Logger;

/**
 * This class provides the main access to the stream capabilities. It is
 * possible to construct a new streamer with the provided constructor, and then
 * use the {@link #startStream()} and {@link #endStream()} methods in order to
 * activate or stop the events streaming.
 * 
 * <p>
 * The method {@link #updateProcess(Process)} can be called without stopping the
 * running stream. This can be used to simulate <em>concept drifts</em>.
 * 
 * @author Andrea Burattin
 */
public class Streamer extends Thread {

	private Process process;
	private SimulationConfiguration simulationParameters;
	private StreamConfiguration configuration;
	
	private BroadcastService broadcaster;
	private StreamBuffer buffer;
	private long generatedInstances = 0;
	private long timeLastEvent = -1;
	
	private boolean enabled = false;
	private Timer infoTimer = null;
	
	/**
	 * Basic streamer constructor
	 * 
	 * @param configuration the stream configuration
	 * @param process the process to use for the first stream
	 * @param simulationParameters parameters for the simulation of the traces
	 */
	public Streamer(StreamConfiguration configuration, Process process, SimulationConfiguration simulationParameters) {
		this.configuration = configuration;
		this.process = process;
		this.simulationParameters = simulationParameters;
		this.buffer = new StreamBuffer(configuration);
		this.broadcaster = new BroadcastService(configuration);
	}
	
	/**
	 * This method starts the actual streaming of the provided process
	 */
	public synchronized void startStream() {
		if (!enabled) {
			enabled = true;
			
			// start the broadcast service
			try {
				broadcaster.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// start the stream info thread
			infoTimer = new Timer(2500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Logger.instance().debug("Stream buffer size: " + buffer.eventsInQueue() + ", traces generated: " + generatedInstances);
				}
			});
			infoTimer.start();
			
			// populate the buffer
			populateBuffer();
			
			Logger.instance().info("Streaming started");
			
			// start the thread
			start();
		}
	}
	
	/**
	 * This method ends the currently ongoing streaming
	 */
	public synchronized void endStream() {
		if (enabled) {
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
	}
	
	/**
	 * This method is responsible of the generation of new traces to populate
	 * the {@link StreamBuffer}
	 */
	protected synchronized void populateBuffer() {
		// define the trace generator
		TraceGenerator th = new TraceGenerator(process,
				String.format(simulationParameters.getCaseIdPattern(), generatedInstances++),
				simulationParameters);
		try {
			// start up the trace generator
			th.start();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// enqueue the new trace
		buffer.enqueueTrace(th.getGeneratedTrace());
	}
	
	/**
	 * This method is responsible for the actual streaming of an event fetched
	 * from the {@link StreamBuffer}
	 */
	protected synchronized void streamEvent() {
		// extract the new event to stream
		StreamEvent toStream = buffer.getEventToStream();
		
		// define the amount of time to wait
		long timeToWait = 0;
		if (timeLastEvent > 0) {
			long timeNewEvent = toStream.getDate().getTime();
			timeToWait = (long) ((timeNewEvent - timeLastEvent) * configuration.timeMultiplier);
			timeLastEvent = timeNewEvent;
		}
		
		// update the event time to use the current time
		toStream.setDate(new Date());
		broadcaster.send(toStream);
		
		// if necessary, update the buffer
		if (buffer.needsTraces()) {
			populateBuffer();
		}
		
		// now we have to sleep for the provided amount of time
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e1) { }
	}
	
	/**
	 * This method can be used to update the current process. This method can be
	 * called while the streaming is ongoing.
	 * 
	 * @param process the new process
	 */
	protected synchronized void updateProcess(Process process) {
		if (process != null) {
			this.process = process;
		}
	}
	
	@Override
	public void run() {
		while(enabled) {
			// stream next event
			streamEvent();
		}
	}
}
