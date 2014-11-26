package plg.generator.log;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class which represents a queue of concurrent threads.
 * 
 * @author Mirko Polato (main author)
 * @author Andrea Burattin
 */
public class SimulationEngine {

	private BlockingQueue<Runnable> taskQueue = null;
	private ArrayList<Work> threads = null;
	
	/**
	 * Creates a new queue with a maximum number of concurrent threads
	 * given by the parameter
	 * 
	 * @param noOfThreads maximum number of concurrent threads
	 * @param maxNoOfTasks maximum number of tasks to assign to the engine
	 */
	public SimulationEngine(int noOfThreads, int maxNoOfTasks){
		threads = new ArrayList<Work>();
		taskQueue = new LinkedBlockingQueue<Runnable>(maxNoOfTasks);
		
		for(int i = 0; i < noOfThreads; i++){
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
	public synchronized void enqueue(Runnable task){
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
	public void start() {
		for(Work thread : threads){
			thread.start();
		}
		
		for(Work thread : threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Class which represent e single task
	 * 
	 * @author Mirko Polato
	 */
	public class Work extends Thread {

		private BlockingQueue<Runnable> taskQueue = null;
		
		/**
		 * Constructor
		 * @param queue The tasks queue
		 */
		public Work(BlockingQueue<Runnable> queue){
			taskQueue = queue;
		}
		
		public void run(){
			while(!taskQueue.isEmpty()){
				try{
					Thread t = new Thread(taskQueue.poll());
					t.start();
					t.join();
				} catch(Exception e){
					//log or otherwise report exception,
					//but keep pool thread alive.
					e.printStackTrace();
				}
			}
		}
	}
}
