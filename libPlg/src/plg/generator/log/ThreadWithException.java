package plg.generator.log;

/**
 * 
 * @author Andrea Burattin
 * @param <T>
 */
public abstract class ThreadWithException<T> extends Thread {

	private ThreadContainer tc = null;
	private Exception thrownExeption = null;
	private T computedValue = null;
	
	public void setErrorListener(ThreadContainer tc) {
		this.tc = tc;
	}
	
	public void exception(Exception e) {
		tc.exceptionReceived(e);
	}
	
	/**
	 * This method returns the exception thrown during the process generations
	 * 
	 * @return the thrown exception, or <tt>null</tt> if no problem arose
	 */
	public Exception getThrownExeption() {
		return thrownExeption;
	}
	
	public T getComputedValue() {
		return computedValue;
	}
	
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
