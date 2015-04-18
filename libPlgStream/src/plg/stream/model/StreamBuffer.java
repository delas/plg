package plg.stream.model;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.deckfour.xes.model.XTrace;

import plg.stream.configuration.StreamConfiguration;
import plg.utils.XLogHelper;

/**
 * 
 * List<Queue<Xtrace>>
 * parallel traces
 *     - queue traces
 *     - queue traces
 *     - queue traces
 *     - queue traces
 * 
 * @author Andrea Burattin
 */
public class StreamBuffer extends CopyOnWriteArrayList<ConcurrentLinkedDeque<StreamEvent>> {

	private static final long serialVersionUID = -2148968724588635829L;
	private StreamConfiguration configuration;
	
	public StreamBuffer(StreamConfiguration configuration) {
		this.configuration = configuration;
		
		// initialize all channels
		for(int i = 0; i < configuration.maximumParallelInstances; i++) {
			add(new ConcurrentLinkedDeque<StreamEvent>());
		}
	}
	
	public synchronized StreamEvent getEventToStream() {
		int channel = channelWithEventToStream();
		if (channel != -1) {
			// remove the event and give it back to the caller
			return get(channel).poll();
		}
		// exceptional case: the stream buffer has no events are stored
		return null;
	}
	
	public synchronized void enqueueTrace(XTrace trace) {
		List<XTrace> events = XLogHelper.traceToEventsForStream(trace);
		int targetChannel = channelToEnqueueEvents();
		boolean noEvents = (targetChannel == -1) || get(targetChannel).isEmpty();
		
		if (targetChannel == -1) {
			targetChannel = 0;
		}
		
		// compute shifting information for the time
		long timeShift = 0;
		if (!noEvents) {
			long startEvent = XLogHelper.getTimestamp(trace.get(0)).getTime();
			long endEvent = XLogHelper.getTimestamp(trace.get(trace.size() - 1)).getTime();
			long traceDuration = endEvent - startEvent;
			long timeToWait = (long) (traceDuration * configuration.timeFractionBeforeNewTrace);
			long timeLastEventOnChannel = get(targetChannel).peekLast().getDate().getTime();
			timeShift = (timeLastEventOnChannel + timeToWait) - startEvent;
		}
		
		// set the actual time of all events
		for(XTrace t : events) {
			StreamEvent se = StreamEvent.wrap(t);
			se.setInternalChannel(targetChannel);
			// fix the time of the event
			long eventTime = XLogHelper.getTimestamp(t.get(0)).getTime();
			se.setDate(new Date(eventTime + timeShift));
			// enqueue the stream event into the buffer
			get(targetChannel).add(se);
		}
	}

	protected int channelWithEventToStream() {
		// search for the nearest event through all the channels
		int channelToUse = -1;
		Date oldestEvent = new Date();
		for (int i = 0; i < size(); i++) {
			if (!get(i).isEmpty()) {
				Date currentEventDate = get(i).peek().getDate();
				// check the actual dates
				if (currentEventDate.before(oldestEvent)) {
					oldestEvent = currentEventDate;
					channelToUse = i;
				}
			}
		}
		// return the oldest back to the caller
		return channelToUse;
	}
	
	protected int channelToEnqueueEvents() {
		// search for the most distant event through all the channels
		int channelToUse = -1;
		Date oldestEvent = new Date();
		for (int i = 0; i < size(); i++) {
			if (get(i).isEmpty()) {
				channelToUse = i;
				oldestEvent = new Date(0);
			} else {
				Date currentEventDate = get(i).peekLast().getDate();
				// check the actual dates
				if (currentEventDate.before(oldestEvent)) {
					oldestEvent = currentEventDate;
					channelToUse = i;
				}
			}
		}
		// return the oldest back to the caller
		return channelToUse;
	}
	
	public static void main(String args[]) {
		System.out.println("start");

		XTrace t1 = XLogHelper.createTrace("t1");
		XLogHelper.insertEvent(t1, "a", new Date(115, 1, 1));
		XLogHelper.insertEvent(t1, "b", new Date(115, 1, 2));
		XLogHelper.insertEvent(t1, "c", new Date(115, 1, 5));

		XTrace t2 = XLogHelper.createTrace("t2");
		XLogHelper.insertEvent(t2, "a", new Date(115, 0, 1));
		XLogHelper.insertEvent(t2, "b", new Date(115, 0, 2));
		XLogHelper.insertEvent(t2, "c", new Date(115, 0, 5));

//		XTrace t3 = XLogHelper.createTrace("t3");
//		XLogHelper.insertEvent(t3, "a", new Date(115, 0, 1));
//		XLogHelper.insertEvent(t3, "b", new Date(115, 0, 2));
//		XLogHelper.insertEvent(t3, "c", new Date(115, 0, 5));
		
		StreamConfiguration sc = new StreamConfiguration();
		sc.maximumParallelInstances = 1;
		sc.timeFractionBeforeNewTrace = 1;
		StreamBuffer sb = new StreamBuffer(sc);
		sb.enqueueTrace(t1);
		sb.enqueueTrace(t2);
//		sb.enqueueTrace(t3);
		
		StreamEvent se = null;
		do {
			se = sb.getEventToStream();
			if (se != null) {
				System.out.println(XLogHelper.getName(se.get(0)) + " " + XLogHelper.getName(se) + " - " + se.getDate());
				System.out.flush();
			}
		} while (se != null);
		
		System.out.println("complete");
	}
}
