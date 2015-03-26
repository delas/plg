package plg.generator.engine;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import plg.generator.IProgressVisualizer;

/**
 * Class which represents a queue of concurrent threads.
 * 
 * @author Mirko Polato (main author)
 * @author Andrea Burattin
 */
public class SimulationEngine {

	private BlockingQueue<ThreadWithException<?>> taskQueue = null;
	private ArrayList<Work> threads = null;
	private IProgressVisualizer progress;
	
	/**
	 * Creates a new queue with a maximum number of concurrent threads
	 * given by the parameter
	 * 
	 * @param noOfThreads maximum number of concurrent threads
	 * @param maxNoOfTasks maximum number of tasks to assign to the engine
	 */
	public SimulationEngine(int noOfThreads, int maxNoOfTasks, IProgressVisualizer progress) {
		this.threads = new ArrayList<Work>();
		this.taskQueue = new LinkedBlockingQueue<ThreadWithException<?>>(maxNoOfTasks);
		this.progress = progress;
		
		for(int i = 0; i < noOfThreads; i++) {
			threads.add(new Work(taskQueue));
		}
	}

	/**
	 * Executes the given task in a new thread. Please note that if the engine
	 * has completed its current assignments, the enqueued tasks will not be
	 * executed.
	 * 
	 * @param task The task to execute
	 */
	public synchronized void enqueue(ThreadWithException<?> task) {
		try {
			this.taskQueue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts all the task in concurrent threads.
	 * 
	 * @param tasks The tasks to execute
	 */
	public void start() throws Exception {
		// start all the threads
		for(Work thread : threads) {
			thread.start();
		}
		// wait until all threads are done
		for(Work thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// check for any exception
		for(Work thread : threads) {
			if (thread.getCollectedException() != null) {
				throw thread.getCollectedException();
			}
		}
	}
	
	/**
	 * Class which represent e single task. This class is actually a
	 * {@link ThreadContainer}.
	 * 
	 * @author Mirko Polato
	 * @author Andrea Burattin
	 */
	public class Work extends Thread implements ThreadContainer {

		private BlockingQueue<ThreadWithException<?>> taskQueue = null;
		private Exception collectedException = null;
		
		/**
		 * Constructor
		 * 
		 * @param queue The tasks queue
		 */
		public Work(BlockingQueue<ThreadWithException<?>> queue) {
			taskQueue = queue;
		}
		
		/**
		 * This method returns the exception captured during the execution
		 * 
		 * @return the exception captured, or <tt>null</tt> if no exception has
		 * been thrown
		 */
		public Exception getCollectedException() {
			return collectedException;
		}
		
		@Override
		public void run() {
			while(!taskQueue.isEmpty()) {
				try {
					ThreadWithException<?> t = taskQueue.poll();
					t.setErrorListener(this);
					t.start();
					t.join();
					progress.inc();
				} catch(Exception e){
					// log or otherwise report exception, but keep pool thread alive
					e.printStackTrace();
				}
			}
		}

		@Override
		public void exceptionReceived(Exception e) {
			taskQueue.clear();
			collectedException = e;
		}
	}
}
