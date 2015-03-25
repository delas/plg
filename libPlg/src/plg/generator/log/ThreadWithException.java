package plg.generator.log;

/**
 * This class represents a thread which execution can raise an exception. It is
 * fundamental, however, to use this kind of thread in combination with a
 * {@link ThreadContainer}.
 * 
 * @author Andrea Burattin
 * @param <T> the type returned by the computation of this thread
 */
public abstract class ThreadWithException<T> extends Thread {

	private ThreadContainer tc = null;
	private Exception thrownExeption = null;
	private T computedValue = null;
	
	/**
	 * This method can be used to set the thread container that will manage the
	 * eventual exceptions
	 * 
	 * @param tc the thread container
	 */
	public void setErrorListener(ThreadContainer tc) {
		this.tc = tc;
	}
	
	/**
	 * This method returns the exception thrown during the process generations
	 * 
	 * @return the thrown exception, or <tt>null</tt> if no problem arose
	 */
	public Exception getThrownExeption() {
		return thrownExeption;
	}
	
	/**
	 * This method returns the computed value
	 * 
	 * @return
	 */
	public T getComputedValue() {
		return computedValue;
	}
	
	/**
	 * This method is the actual executor of the computation
	 * 
	 * @return the final result
	 * @throws Exception
	 */
	protected abstract T runWithException() throws Exception;
	
	@Override
	public void run() {
		try {
			computedValue = runWithException();
		} catch (Exception e) {
			tc.exceptionReceived(e);
			thrownExeption = e;
		}
	}
}
