package plg.gui.remote;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

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

	/**
	 * This field can be used to turn on and off the logging mechanism.
	 */
	public static final boolean LOGGING_ENABLED = true;
	
	public static final String NEW_SESSION_URL = "http://plg.processmining.it/log/cmd.php?cmd=newsession&plg_version=%s&os=%s&cpus=%s";
	public static final String CMD_URL = "http://plg.processmining.it/log/cmd.php?cmd=log&session_id=%s&token=%s&command=%s";
	
	/**
	 * The actual logging object.
	 */
	private static RemoteLogger logger = new RemoteLogger();
	
	private String sessionId = null;
	private String token = null;
	
	/**
	 * Protected constructor. To use the logger use
	 * {@link RemoteLogger#instance()}. This constructor is used just to not
	 * allow new instances of the class.
	 */
	protected RemoteLogger() {
		if (LOGGING_ENABLED) {
			// ask for a new session id
			String newSessionUrl = String.format(NEW_SESSION_URL,
					PlgConstants.libPLG_VERSION,
					OSUtils.determineOS(),
					CPUUtils.CPUAvailable());
			JSONObject sessionObj = httpRequestToJson(newSessionUrl);
			if (sessionObj != null) {
				this.sessionId = sessionObj.get("session_id").toString();
				this.token = sessionObj.get("token").toString();
			}
		}
	}
	
	protected void send(RemoteLogBlob blob) {
		if (LOGGING_ENABLED) {
			if (sessionId != null && token != null) {
				httpRequest(blob.getURL(), "parameters", blob.getParameters());
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
	
	/**
	 * This method returns a new instance of the remote logger
	 * 
	 * @return a logger instance
	 */
	public static RemoteLogger instance() {
		return logger;
	}
	
	public RemoteLogBlob log(String activity) {
		return new RemoteLogBlob(REMOTE_MESSAGES.CUSTOM_MESSAGE).add("activity", activity);
	}
	
	public RemoteLogBlob log(REMOTE_MESSAGES activity) {
		return new RemoteLogBlob(activity);
	}
	
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
