package plg.stream.model;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.deckfour.xes.model.XTrace;

import plg.stream.configuration.StreamConfiguration;
import plg.utils.Random;
import plg.utils.XLogHelper;

/**
 * This class represents a stream buffer. A stream buffer is capable of keeping
 * the space usage as low as possible, even when simulating streams of large
 * processes.
 * 
 * <p>
 * Its internal structure consists of a list of queue of {@link StreamEvent}s.
 * In particular, the outer list contains the total number of allowed "channels"
 * (i.e., the number of parallel traces). A queue, which belongs to a specific
 * channel contains the actual events to be sent over the network.
 * 
 * @author Andrea Burattin
 */
public class StreamBuffer extends CopyOnWriteArrayList<ConcurrentLinkedDeque<StreamEvent>> {

	private static final long serialVersionUID = -2148968724588635829L;
	private StreamConfiguration configuration;
	
	/**
	 * Buffer constructor
	 * 
	 * @param configuration the stream configuration
	 */
	public StreamBuffer(StreamConfiguration configuration) {
		this.configuration = configuration;
		
		// initialize all channels
		for(int i = 0; i < configuration.maximumParallelInstances; i++) {
			add(new ConcurrentLinkedDeque<StreamEvent>());
		}
	}
	
	/**
	 * This method returns the new event to be streamed
	 * 
	 * @return the stream event to be sent, or <tt>null</tt> if no event is
	 * available
	 */
	public synchronized StreamEvent getEventToStream() {
		int channel = channelWithEventToStream();
		if (channel != -1) {
			// remove the event and give it back to the caller
			return get(channel).poll();
		}
		// exceptional case: the stream buffer has no events are stored
		return null;
	}
	
	/**
	 * This method enqueues the provided trace into the buffer. Specifically,
	 * the trace is added to the channel which is going to end the sooner.
	 * 
	 * <p>
	 * If the trace has no duration (i.e., it is composed of just one event),
	 * then the activity duration is set to 1 hour.
	 * 
	 * @param trace the new trace to add
	 */
	public synchronized void enqueueTrace(XTrace trace) {
		List<XTrace> events = XLogHelper.traceToEventsForStream(trace);
		if (events.size() > 0) {
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
				if (traceDuration == 0) {
					traceDuration = 1000*60*60;
				}
				long timeToWait = (long) (traceDuration * configuration.timeFractionBeforeNewTrace);
				long timeLastEventOnChannel = get(targetChannel).peekLast().getDate().getTime();
				timeShift = (timeLastEventOnChannel + timeToWait) - startEvent;
				// add some more randomization to the trace beginning
				timeShift += Random.nextInt(0, (int) traceDuration);
			}
			
			// set the actual time of all events
			int progress = 0;
			for(XTrace t : events) {
				// create the actual stream event
				StreamEvent se = StreamEvent.wrap(t);
				se.setInternalChannel(targetChannel);
				
				// add trace start/end marking, if necessary
				if (configuration.markTraceBeginningEnd) {
					if (progress == 0) {
						se.setTraceLifecycle("start");
					} else if (progress == t.size()) {
						se.setTraceLifecycle("complete");
					}
				}
				
				// fix the time of the event
				long eventTime = XLogHelper.getTimestamp(t.get(0)).getTime();
				if (progress > 0) {
					// add some more randomization to the actual event
					long prevEventTime = XLogHelper.getTimestamp(events.get(progress - 1).get(0)).getTime();
					eventTime += Random.nextInt(0, (int) (prevEventTime - eventTime) / 2);
				}
				se.setDate(new Date(eventTime + timeShift));
				
				// enqueue the stream event into the buffer
				get(targetChannel).add(se);
				progress++;
			}
		}
	}
	
	/**
	 * This method can be used to check whether the buffer needs to be refilled
	 * 
	 * @return <tt>true</tt> if the buffer has to be refilled, <tt>false</tt>
	 * otherwise
	 */
	public synchronized boolean needsTraces() {
		for (int i = 0; i < size(); i++) {
			if (get(i).size() <= 2) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean isEmpty() {
		for (int i = 0; i < size(); i++) {
			if (!get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method returns the number of events currently in the buffer queue
	 * 
	 * @return the number of enqueued events
	 */
	public synchronized int eventsInQueue() {
		int tot = 0;
		for (int i = 0; i < size(); i++) {
			tot += get(i).size();
		}
		return tot;
	}
	
	/**
	 * This method clears all the queues of the buffer
	 */
	public synchronized void clearQueues() {
		for (int i = 0; i < size(); i++) {
			get(i).clear();
		}
	}
	
	/**
	 * This method returns the channel with the event to be removed and sent
	 * 
	 * @return
	 */
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
	
	/**
	 * This method returns the channel which will host the new trace to be
	 * enqueued
	 * 
	 * @return
	 */
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
