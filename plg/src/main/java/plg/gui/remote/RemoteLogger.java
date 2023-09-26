package plg.gui.remote;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import plg.generator.log.SimulationConfiguration;
import plg.generator.log.noise.NoiseConfiguration;
import plg.generator.process.RandomizationConfiguration;
import plg.gui.config.ConfigurationSet;
import plg.gui.config.UIConfiguration;
import plg.gui.controller.ApplicationController;
import plg.gui.util.collections.ImagesCollection;
import plg.stream.configuration.StreamConfiguration;
import plg.utils.CPUUtils;
import plg.utils.Logger;
import plg.utils.OSUtils;
import plg.utils.Pair;
import plg.utils.PlgConstants;

/**
 * This class encapsulates all the resources required for the remote logging.
 * This class is, as much as possible, independent of the actual program GUI.
 * The idea is that it should be fairly easy to use it into other projects.
 * 
 * @author Andrea Burattin
 */
public class RemoteLogger {

	protected static final String KEY_REMOTE_LOGGING_ENABLED = "REMOTE_LOGGING_ENABLED";
	
	/* These are the URLs for the remote logging */
	public static final String NEW_SESSION_URL = "http://plg.processmining.it/log/cmd.php?cmd=newsession&plg_version=%s&os=%s&cpus=%s";
	public static final String CMD_URL = "http://plg.processmining.it/log/cmd.php?cmd=log&session_id=%s&token=%s&command=%s";
	public static final String HELP_URL = "http://plg.processmining.it/help/UsageStatistics";
	
	/* The actual logging object */
	private static RemoteLogger logger = new RemoteLogger();
	
	private ConfigurationSet configuration;
	private boolean loggingEnabled = false;
	private boolean checkNewVersion = true;
	private String sessionId = null;
	private String token = null;
	
	/**
	 * Protected constructor. To use the logger use
	 * {@link RemoteLogger#instance()}. This constructor is used just to not
	 * allow new instances of the class.
	 */
	protected RemoteLogger() {
		this.configuration = UIConfiguration.master().getChild(RemoteLogger.class.getCanonicalName());
		
		initializeRemoteSession();
	}
	
	/**
	 * This method returns the current instance of the remote logger
	 * 
	 * @return a logger instance
	 */
	public static RemoteLogger instance() {
		return logger;
	}
	
	/**
	 * This method initializes the logger. In particular, this method is
	 * responsible of:
	 * <ol>
	 * 	<li>checking whether there is already a setting for enabling or not the
	 * 		remote logging;</li>
	 * 	<li>in case such configuration is not there, showing a dialog with the
	 * 		request to the user;</li>
	 * 	<li>store (or retrieve) the user-provided configuration.</li>
	 * </ol>
	 */
	protected void initializeLogger() {
		if (!configuration.containsKey(KEY_REMOTE_LOGGING_ENABLED)) {
			try {
				// let's wait for some time, and hope the gui goes up in the meanwhile
				Thread.sleep(500);
			} catch (InterruptedException e) { }
			
			JLabel message = new JLabel("<html>Would you like to help us reporting anonymous usage statistics?<br>"
					+ "No information on actual processes or simulations will be reported (<a href=\"http://"+ HELP_URL +"\">info</a>).<br><br></html>");
			message.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(HELP_URL));
						} catch (IOException | URISyntaxException ex) { }
					}
				}
			});
			message.setToolTipText("Open URL with more information");
			message.setCursor(new Cursor(Cursor.HAND_CURSOR));
			JCheckBox checkbox = new JCheckBox("Do not show this message again.");
			Object[] params = {message, checkbox};
			
			int result = JOptionPane.showConfirmDialog(ApplicationController.instance().getMainFrame(), params, "PLG Usage", JOptionPane.YES_NO_OPTION);
			boolean response = (result == JOptionPane.YES_OPTION);
			boolean permanent = checkbox.isSelected();
			
			// set the values
			loggingEnabled = response;
			if (permanent) {
				configuration.setBoolean(KEY_REMOTE_LOGGING_ENABLED, response);
			}
		}
		loggingEnabled = configuration.getBoolean(KEY_REMOTE_LOGGING_ENABLED);
	}
	
	/**
	 * This method is responsible of retrieving the new session id and the
	 * token, and to send the {@link REMOTE_MESSAGES#APPLICATION_STARTED}
	 * message.
	 */
	protected void initializeRemoteSession() {
		new SwingWorker<JSONObject, Void>() {
			@Override
			protected JSONObject doInBackground() throws Exception {
//				initializeLogger();
				JSONObject sessionObj = null;
				
				if (loggingEnabled) {
					// ask for a new session id
					String newSessionUrl = String.format(NEW_SESSION_URL,
							PlgConstants.libPLG_VERSION,
							OSUtils.determineOS(),
							CPUUtils.CPUAvailable());
					sessionObj = httpRequestToJson(newSessionUrl);
					if (sessionObj != null) {
						RemoteLogger.this.sessionId = sessionObj.get("session_id").toString();
						RemoteLogger.this.token = sessionObj.get("token").toString();
					}
				}
				return sessionObj;
			}
			
			@Override
			protected void done() {
				// register open application
				log(REMOTE_MESSAGES.APPLICATION_STARTED).send();
				
				try {
					// check version
					if (get() != null && checkNewVersion) {
						parseLastVersion((JSONObject) get().get("last_version"));
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			};
		}.execute();
	}
	
	/**
	 * This method parses the version information passed and shows a dialog if
	 * there is a new version of PLG available
	 * 
	 * @param versionInfo the JSON object with the updated information
	 */
	protected void parseLastVersion(JSONObject versionInfo) {
		final String version = versionInfo.get("version").toString();
		final String date = versionInfo.get("date").toString();
		final String info = versionInfo.get("info").toString();
		final String url = versionInfo.get("url").toString();
		
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (!version.equals(PlgConstants.libPLG_VERSION)) {
					JLabel message = new JLabel("<html>New version available: <b>" + version + "</b> (released on " + date + ").</html>");
					JLabel releaseInfo = new JLabel("<html>Release info:<br>" + info + "</html>");
					JLabel urlMessage = new JLabel("<html>Download URL: <a href=\"" + url + "\">" + url + "</a>.</html>");
					urlMessage.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							if (Desktop.isDesktopSupported()) {
								try {
									Desktop.getDesktop().browse(new URI(url));
								} catch (IOException | URISyntaxException ex) { }
							}
						}
					});
					urlMessage.setToolTipText("Open " + url);
					urlMessage.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					Object[] params = {message, releaseInfo, urlMessage};
					
					JOptionPane.showMessageDialog(ApplicationController.instance().getMainFrame(),
							params,
							"New version of PLG available",
							JOptionPane.INFORMATION_MESSAGE,
							ImagesCollection.UPDATES_ICON);
					
					Logger.instance().debug("A new version of PLG (" + version + ") is available");
					
				} else {
					Logger.instance().debug("Your version of PLG (" + PlgConstants.libPLG_VERSION + ") is the latest available");
				}
				
				return null;
			}
		}.execute();
	}
	
	/**
	 * This method, if the remote logging is enabled, will send the provided
	 * {@link RemoteLogEntity} with all its parameters.
	 * 
	 * @param blob the remote log to send
	 */
	protected void send(final RemoteLogEntity blob) {
		if (loggingEnabled) {
			if (sessionId != null && token != null) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						String url = String.format(CMD_URL, sessionId, token, blob.message.toString());
						httpRequest(url, "parameters", blob.getParameters());
						return null;
					}
				}.execute();
			}
		}
	}
	
	/**
	 * This method runs the provided configuration with the provided post
	 * variable name and configuration. If <tt>postName</tt> or
	 * <tt>postVariable</tt> are <tt>null</tt> the the connection will perform
	 * a GET request.
	 * 
	 * @param url the remote url
	 * @param postName the name of the post variable
	 * @param postValue the value for the post variable
	 * @return a string representation of the first line of the result
	 */
	private String httpRequest(String url, String postName, String postValue) {
		String toReturn = "";
		try {
			URI uri = new URI(url);
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest request = null;
			if (postName == null || postValue == null) {
				request = RequestBuilder
						.get(uri)
						.setHeader("client", "plg-gui")
						.build();
			} else {
				request = RequestBuilder
						.post(uri)
						.setHeader("client", "plg-gui")
						.addParameter(postName, postValue)
						.build();
			}
			HttpResponse response = client.execute(request);
			Scanner s = new Scanner(response.getEntity().getContent());
			toReturn = s.nextLine();
			s.close();
		} catch (IOException | URISyntaxException e) {
			System.err.println("Network error (" + e.toString() + "), too bad but I can live with it...");
		}
		return toReturn;
	}
	
	/**
	 * Perform a GET request to the provided url
	 * 
	 * @param url the url target of the GET request
	 * @return a string representation of the first line of the result
	 */
	private String httpRequest(String url) {
		return httpRequest(url, null, null);
	}
	
	/**
	 * This method performs a GET request to the provided url and returns a
	 * {@link JSONObject} representation of the answer. This method also checks
	 * whether the result contains an attribute named <tt>response</tt> with
	 * value <tt>OK</tt>
	 * 
	 * @param url the url target of the GET request
	 * @return a JSON representation of the answer
	 */
	private JSONObject httpRequestToJson(String url) {
		JSONObject toReturn = null;
		String request = httpRequest(url);
		toReturn = (JSONObject) JSONValue.parse(request);
		if (!toReturn.get("response").equals("OK")) {
			return null;
		}
		return toReturn;
	}
	
	/**
	 * This method builds a new {@link RemoteLogEntity} starting from a
	 * {@link REMOTE_MESSAGES#CUSTOM_MESSAGE}.
	 * 
	 * @param activity the name of the activity for the custom message
	 * @return the generated remote log entity
	 */
	public RemoteLogEntity log(String activity) {
		return new RemoteLogEntity(REMOTE_MESSAGES.CUSTOM_MESSAGE).add("activity", activity);
	}
	
	/**
	 * This method builds a new {@link RemoteLogEntity} with the provided
	 * message
	 * 
	 * @param activity the activity to log
	 * @return the generated remote log entity
	 */
	public RemoteLogEntity log(REMOTE_MESSAGES activity) {
		return new RemoteLogEntity(activity);
	}
	
	/**
	 * This methods sets whether the remote logger should also check is a new
	 * version of the software is available
	 * 
	 * @param checkNewVersion
	 */
	public void checkNewVersion(boolean checkNewVersion) {
		this.checkNewVersion = checkNewVersion;
	}
	
	/**
	 * This class represents a remote log entry. Each remote log entry is made
	 * of a main message and a list of parameters (each parameter is a name-
	 * value pair).
	 */
	public class RemoteLogEntity extends ArrayList<Pair<String, String>> {
		
		private static final long serialVersionUID = -4194742473778496871L;
		protected REMOTE_MESSAGES message;
		
		/**
		 * Protected constructor. To create a new {@link RemoteLogEntity}, you
		 * should use {@link RemoteLogger#log(String)} or
		 * {@link RemoteLogger#log(REMOTE_MESSAGES)}.
		 * 
		 * @param message the message for the new remote log entity
		 */
		protected RemoteLogEntity(REMOTE_MESSAGES message) {
			this.message = message;
		}
		
		/**
		 * This method sends the current remote log entity
		 */
		public void send() {
			instance().send(this);
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param name the name of the new parameter
		 * @param value the value of the new parameter
		 * @return the current entity
		 */
		public RemoteLogEntity add(String name, String value) {
			add(new Pair<String, String>(name, value));
			return this;
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param name the name of the new parameter
		 * @param value the value of the new parameter
		 * @return the current entity
		 */
		public RemoteLogEntity add(String name, boolean value) {
			return add(name, Boolean.toString(value));
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param name the name of the new parameter
		 * @param value the value of the new parameter
		 * @return the current entity
		 */
		public RemoteLogEntity add(String name, int value) {
			return add(name, Integer.toString(value));
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param name the name of the new parameter
		 * @param value the value of the new parameter
		 * @return the current entity
		 */
		public RemoteLogEntity add(String name, double value) {
			return add(name, Double.toString(value));
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param configuration the configuration to log
		 * @return the current entity
		 */
		public RemoteLogEntity add(SimulationConfiguration configuration) {
			add("SM.A", configuration.useMultithreading());
			add("SM.B", configuration.getMaximumLoopCycles());
			add("SM.C", configuration.getNumberOfTraces());
			add(configuration.getNoiseConfiguration());
			return this;
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param configuration the configuration to log
		 * @return the current entity
		 */
		public RemoteLogEntity add(NoiseConfiguration configuration) {
			add("NS.A", configuration.getIntegerDataNoiseProbability() + "," + configuration.getIntegerDataNoiseDelta());
			add("NS.B", configuration.getStringDataNoiseProbability());
			add("NS.C", configuration.getActivityNameNoiseProbability());
			add("NS.D", configuration.getTraceMissingHeadNoiseProbability() + "," + configuration.getTraceMissingHeadSize());
			add("NS.E", configuration.getTraceMissingTailNoiseProbability() + "," + configuration.getTraceMissingTailSize());
			add("NS.F", configuration.getTraceMissingEpisodeNoiseProbability() + "," + configuration.getTraceMissingEpisodeSize());
			add("NS.G", configuration.getPerturbedOrderNoiseProbability());
			add("NS.H", configuration.getDoubleEventNoiseProbability());
			add("NS.I", configuration.getAlienEventNoiseProbability());
			return this;
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param configuration the configuration to log
		 * @return the current entity
		 */
		public RemoteLogEntity add(StreamConfiguration configuration) {
//			add("ST.A", configuration.servicePort);
			add("ST.B", configuration.maximumParallelInstances);
			add("ST.C", configuration.timeMultiplier);
			add("ST.D", configuration.timeFractionBeforeNewTrace);
			add("ST.E", configuration.markTraceBeginningEnd);
			return this;
		}
		
		/**
		 * This method adds a parameter to the current remote log entity
		 * 
		 * @param configuration the configuration to log
		 * @return the current entity
		 */
		public RemoteLogEntity add(RandomizationConfiguration configuration) {
			add("RP.A", configuration.getAndBranches());
			add("RP.B", configuration.getXorBranches());
			add("RP.C", configuration.getLoopWeight());
			add("RP.D", configuration.getSingleActivityWeight());
			add("RP.E", configuration.getSkipWeight());
			add("RP.F", configuration.getSequenceWeight());
			add("RP.G", configuration.getANDWeight());
			add("RP.H", configuration.getXORWeight());
			add("RP.I", configuration.getMaximumDepth());
			add("RP.L", configuration.getDataObjectProbability());
			return this;
		}
		
		/**
		 * This method returns a string representation (JSON based) of the
		 * current entity parameters
		 * 
		 * @return a string representation of the parameters
		 */
		@SuppressWarnings("unchecked")
		public String getParameters() {
			JSONObject obj = new JSONObject();
			for(Pair<String, String> p : this) {
				obj.put(p.getFirst(), p.getSecond());
			}
			return obj.toJSONString();
		}
	}
}
