package plg.generator.engine;

/**
 * A thread container is just a computational container of
 * {@link ThreadWithException}. The main characteristic of this container is
 * the possibility to asynchronously receive an exception during computation.
 * 
 * @author Andrea Burattin
 */
public interface ThreadContainer {

	/**
	 * This method is invoked by the {@link ThreadWithException} when an
	 * exception is thrown
	 * 
	 * @param exception the thrown exception
	 */
	public void exceptionReceived(Exception exception);
}
