package plg.gui.remote;

/**
 * This enumeartion collects all the possible messages that might be remotely
 * logged by the {@link RemoteLogger}.
 * 
 * @author Andrea Burattin
 */
public enum REMOTE_MESSAGES {
	
	/** A customized message. This value should be used if the action does not
	 * fit into any of the other available options */
	CUSTOM_MESSAGE,
	
	/** the application is started */
	APPLICATION_STARTED,
	/** The application has been closed */
	APPLICATION_CLOSED,
	
	/** A random process has been created */
	PROCESS_RANDOMIZED,
	/** A process has been opened */
	PROCESS_OPENED,
	/** A process has been saved */
	PROCESS_SAVED,
	/** A process has been evolved */
	PROCESS_EVOLVED,
	/** A process has been deleted */
	PROCESS_DELETED,
	
	/** A new log has been generated */
	LOG_GENERATED,
	/** The stream has been configured */
	STREAM_CONFIGURED,
	/** The stream has been started */
	STREAM_STARTED,
	/** The stream has been stopped */
	STREAM_STOPPED,
	
	/** The console visibility has been modified */
	CHANGED_CONSOLE_VISIBILITY;
}
