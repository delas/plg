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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import plg.stream.configuration.StreamConfiguration;
import plg.utils.CPUUtils;
import plg.utils.OSUtils;
import plg.utils.Pair;
import plg.utils.PlgConstants;

/**
 * 
 * @author Andrea Burattin
 */
public class RemoteLogger {

	protected static final String KEY_REMOTE_LOGGING_ENABLED = "REMOTE_LOGGING_ENABLED";
	
	/*
	 * These are the URLs for the remote logging
	 */
	public static final String NEW_SESSION_URL = "http://plg.processmining.it/log/cmd.php?cmd=newsession&plg_version=%s&os=%s&cpus=%s";
	public static final String CMD_URL = "http://plg.processmining.it/log/cmd.php?cmd=log&session_id=%s&token=%s&command=%s";
	
	/**
	 * The actual logging object.
	 */
	private static RemoteLogger logger = new RemoteLogger();
	
	private ConfigurationSet configuration;
	private boolean loggingEnabled = false;
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
	 * This method returns a new instance of the remote logger
	 * 
	 * @return a logger instance
	 */
	public static RemoteLogger instance() {
		return logger;
	}
	
	protected void initializeLogger() {
		if (!configuration.containsKey(KEY_REMOTE_LOGGING_ENABLED)) {
			try {
				// let's wait for some time, and hope the gui goes up in the meanwhile
				Thread.sleep(500);
			} catch (InterruptedException e) { }
			
			JLabel message = new JLabel("<html>Would you like to help us reporting anonymous usage statistics?<br>"
					+ "No information on actual processes or simulations will be reported (<a href=\"http://www.google.com\">info</a>).<br><br></html>");
			message.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI("http://plg.processmining.it/help/UsageStatistics"));
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
	
	protected void initializeRemoteSession() {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					initializeLogger();
					
					if (loggingEnabled) {
						// ask for a new session id
						String newSessionUrl = String.format(NEW_SESSION_URL,
								PlgConstants.libPLG_VERSION,
								OSUtils.determineOS(),
								CPUUtils.CPUAvailable());
						JSONObject sessionObj = httpRequestToJson(newSessionUrl);
						if (sessionObj != null) {
							RemoteLogger.this.sessionId = sessionObj.get("session_id").toString();
							RemoteLogger.this.token = sessionObj.get("token").toString();
						}
					}
					return null;
				}
				
				@Override
				protected void done() {
					// register open application
					log(REMOTE_MESSAGES.APPLICATION_STARTED).send();
				};
			}.execute();
	}
	
	protected void send(final RemoteLogBlob blob) {
		if (loggingEnabled) {
			if (sessionId != null && token != null) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						httpRequest(blob.getURL(), "parameters", blob.getParameters());
						return null;
					}
				}.execute();
			}
		}
	}
	
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
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	private String httpRequest(String url) {
		return httpRequest(url, null, null);
	}
	
	private JSONObject httpRequestToJson(String url) {
		JSONObject toReturn = null;
		String request = httpRequest(url);
		toReturn = (JSONObject) JSONValue.parse(request);
		if (!toReturn.get("response").equals("OK")) {
			return null;
		}
		return toReturn;
	}
	
	public RemoteLogBlob log(String activity) {
		return new RemoteLogBlob(REMOTE_MESSAGES.CUSTOM_MESSAGE).add("activity", activity);
	}
	
	public RemoteLogBlob log(REMOTE_MESSAGES activity) {
		return new RemoteLogBlob(activity);
	}
	
	/**
	 * 
	 */
	public class RemoteLogBlob extends ArrayList<Pair<String, String>> {
		
		private static final long serialVersionUID = -4194742473778496871L;
		protected REMOTE_MESSAGES message;
		
		public RemoteLogBlob(REMOTE_MESSAGES message) {
			this.message = message;
		}
		
		public void send() {
			instance().send(this);
		}
		
		public RemoteLogBlob add(String name, String value) {
			add(new Pair<String, String>(name, value));
			return this;
		}
		
		public RemoteLogBlob add(String name, boolean value) {
			return add(name, Boolean.toString(value));
		}
		
		public RemoteLogBlob add(String name, int value) {
			return add(name, Integer.toString(value));
		}
		
		public RemoteLogBlob add(String name, double value) {
			return add(name, Double.toString(value));
		}
		
		public RemoteLogBlob add(SimulationConfiguration configuration) {
			add("SM.A", configuration.useMultithreading());
			add("SM.B", configuration.getMaximumLoopCycles());
			add("SM.C", configuration.getNumberOfTraces());
			add(configuration.getNoiseConfiguration());
			return this;
		}
		
		public RemoteLogBlob add(NoiseConfiguration configuration) {
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

		public RemoteLogBlob add(StreamConfiguration configuration) {
			add("ST.A", configuration.servicePort);
			add("ST.B", configuration.maximumParallelInstances);
			add("ST.C", configuration.timeMultiplier);
			add("ST.D", configuration.timeFractionBeforeNewTrace);
			add("ST.E", configuration.markTraceBeginningEnd);
			return this;
		}

		public RemoteLogBlob add(RandomizationConfiguration configuration) {
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
		
		public String getURL() {
			return String.format(CMD_URL, sessionId, token, message.toString());
		}
		
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
