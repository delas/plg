package plg.stream.model;

import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.messages.query.TraceSessionMessage;

import plg.generator.engine.SimulationEngine;
import plg.model.Process;
import plg.stream.BroadcastService;
import plg.stream.configuration.StreamConfiguration;
import plg.utils.Logger;

public class Streamer implements Runnable {

	private int generatedInstances = 0;
	private StreamConfiguration configuration;
	private Process process;
	private BroadcastService broadcaster;
	
	private List<ConcurrentLinkedQueue<XTrace>> queue;
	
	private boolean enabled = true;
	private boolean running = false;
	private Timer updateQueue = new Timer();
	
	public Streamer(StreamConfiguration configuration, Process process) {
		this.configuration = configuration;
		this.process = process;
		this.queue = new CopyOnWriteArrayList<ConcurrentLinkedQueue<XTrace>>();
	}
	
	public synchronized void start() {
		running = true;
		
		// start the update queue
		updateQueue.schedule(new TimerTask() {
			@Override
			public void run() {
				populateEventsQueue();
			}
		}, 0, configuration.timeBetweenPopulateQueue);
		
		// wake up the current thread
		notify();
		
		Logger.instance().info("Streaming started");
	}
	
	public synchronized void pause() {
		running = false;
		updateQueue.cancel();
		Logger.instance().info("Streamimg paused");
	}
	
	public synchronized void stop() {
		enabled = false;
		Logger.instance().info("Streaming stopped");
	}
	
	protected synchronized void populateEventsQueue() {
//		TraceGenerator
	}
	
	protected synchronized void streamEvent() {
//		if (!queue.isEmpty()) {
//			broadcaster.send(queue.poll());
//		}
	}
	
	protected synchronized void updateProcess(Process process) {
		this.process = process;
	}
	
	@Override
	public void run() {
		while(enabled) {
			if (!running) {
				try {
					wait();
				} catch (InterruptedException e) { }
			}
			// check if the queue needs to be refilled
			if (queue.isEmpty()) {
				populateEventsQueue();
			}
			
			// stream next event
			streamEvent();
			
			// sleep for a while
//			try {
//				Thread.sleep(configuration.timeBetweenEachEvent);
//			} catch (InterruptedException e) { }
		}
	}
}
