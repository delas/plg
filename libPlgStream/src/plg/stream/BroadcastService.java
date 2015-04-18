package plg.stream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.xml.OSXMLConverter;

import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.StreamEvent;
import plg.utils.Pair;

/**
 * This class is a broadcasting service. This class allows to keep connections
 * with many clients and to broadcast them messages. This service is also a
 * {@link Thread} because, as long as it is open, it must accept all the
 * incoming clients.
 * 
 * @author Andrea Burattin
 */
public class BroadcastService extends Thread {

	protected static OSXMLConverter converter = new OSXMLConverter();
	
	private StreamConfiguration confiuguration;
	private ServerSocket socket;
	private Set<Pair<Socket, OutputStreamWriter>> clients;
	private boolean running = true;
	
	/**
	 * Basic broadcast service constructor
	 * 
	 * @param configuration the stream configuration
	 */
	public BroadcastService(StreamConfiguration configuration) {
		this.confiuguration = configuration;
		this.clients = new HashSet<Pair<Socket, OutputStreamWriter>>();
	}
	
	/**
	 * Method to open the broadcasting service. This method wraps the call to
	 * the {@link #start()} method.
	 * 
	 * @throws IOException socket opening errors (e.g. port already used)
	 */
	public void open() throws IOException {
		socket = new ServerSocket(confiuguration.servicePort);
		socket.setSoTimeout(1000);
		start();
	}
	
	/**
	 * Method to close the current broadcasting service and shutdown the thread
	 * that listens for new clients. This method contains a call to
	 * {@link #join()}.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		running = false;
		try {
			join();
		} catch (InterruptedException e) { }
		
		for(Pair<Socket, OutputStreamWriter> p : clients) {
			p.getFirst().close();
		}
		socket.close();
	}
	
	/**
	 * This method sends a message to all the clients connected. This method can
	 * identify if a client is not connected anymore, and remove it from the
	 * connected clients list. 
	 * 
	 * @param message the message to be sent
	 */
	public void send(String message) {
		// data structure to collect all the possible disconnected clients
		Set<Pair<Socket, OutputStreamWriter>> toRemove = null;
		
		// send the message to all the clients
		for(Pair<Socket, OutputStreamWriter> p : clients) {
			try {
				p.getSecond().write(message);
				p.getSecond().flush();
			} catch (IOException e) {
				// we get here if the client is disconnected
				if (toRemove == null) {
					toRemove = new HashSet<Pair<Socket, OutputStreamWriter>>();
				}
				toRemove.add(p);
			}
		}
		// now remove all the disconnected clients
		if (toRemove != null) {
			for(Pair<Socket, OutputStreamWriter> p : toRemove) {
				clients.remove(p);
			}
		}
	}
	
	/**
	 * This method sends an {@link XTrace} to all the clients connected. This
	 * method can identify if a client is not connected anymore, and remove it
	 * from the connected clients list. 
	 * 
	 * @param trace the trace to send
	 * @see #send(String)
	 */
	public void send(XTrace trace) {
		send(converter.toXML(trace).replace('\n', ' ') + "\n");
	}
	
	/**
	 * This method sends an {@link XTrace} to all the clients connected. This
	 * method can identify if a client is not connected anymore, and remove it
	 * from the connected clients list. 
	 * 
	 * @param trace the trace to send
	 * @see #send(String)
	 */
	public void send(StreamEvent event) {
		send(converter.toXML(event.unwrap()).replace('\n', ' ') + "\n");
	}
	
	@Override
	public void run() {
		while (running && !socket.isClosed()) {
			try {
				Socket incoming = socket.accept();
				OutputStreamWriter osw = new OutputStreamWriter(incoming.getOutputStream());
				clients.add(new Pair<Socket, OutputStreamWriter>(incoming, osw));
			} catch (IOException e) {
				// here we have the socket timeout expiration, don't do anything
			}
		}
	}
}
