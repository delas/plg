package plg.stream.configuration;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.deckfour.xes.model.XTrace;

import plg.model.Process;
import plg.stream.BroadcastService;

public class Streamer implements Runnable {

	private StreamConfiguration configuration;
	private Process process;
	private Queue<XTrace> queue;
	private BroadcastService broadcaster;
	private boolean enabled = true;
	private boolean running = false;
	private Timer updateQueue = new Timer();
	
	public Streamer(StreamConfiguration configuration, Process process) {
		this.configuration = configuration;
		this.process = process;
		this.queue = new ConcurrentLinkedQueue<XTrace>();
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
	}
	
	public synchronized void pause() {
		running = false;
		updateQueue.cancel();
	}
	
	public synchronized void stop() {
		enabled = false;
	}
	
	protected synchronized void populateEventsQueue() {
		
	}
	
	protected synchronized void streamEvent() {
		if (!queue.isEmpty()) {
			broadcaster.send(queue.poll());
		}
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
			try {
				Thread.sleep(configuration.timeBetweenEachEvent);
			} catch (InterruptedException e) { }
		}
	}
}
