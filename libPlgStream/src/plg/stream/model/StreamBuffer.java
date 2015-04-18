package plg.stream.model;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.deckfour.xes.model.XTrace;

import plg.stream.configuration.StreamConfiguration;
import plg.utils.XLogHelper;

/**
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
	
	public synchronized boolean needsTraces() {
		for (int i = 0; i < size(); i++) {
			if (get(i).size() <= 2) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean isEmpty() {
		for (int i = 0; i < size(); i++) {
			if (!get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public synchronized int eventsInQueue() {
		int tot = 0;
		for (int i = 0; i < size(); i++) {
			tot += get(i).size();
		}
		return tot;
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
}
