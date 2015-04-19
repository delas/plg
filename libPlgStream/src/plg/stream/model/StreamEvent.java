package plg.stream.model;

import java.util.Date;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.buffered.XAttributeMapBufferedImpl;
import org.deckfour.xes.model.buffered.XAttributeMapSerializerImpl;
import org.deckfour.xes.model.buffered.XTraceBufferedImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import plg.utils.XLogHelper;

/**
 * This class represents an event which can be streamed over the network
 * 
 * @author Andrea Burattin
 */
public class StreamEvent extends XTraceBufferedImpl {

	private int internalChannel = -1;
	private XEvent internalEvent = null;
	
	/**
	 * Basic constructor
	 */
	public StreamEvent() {
		super(
			new XAttributeMapLazyImpl<XAttributeMapBufferedImpl>(XAttributeMapBufferedImpl.class),
			new XAttributeMapSerializerImpl());
	}
	
	/**
	 * This method wraps the attributes which, however it still refers to the
	 * same internal objects.
	 * 
	 * @param trace
	 * @return
	 */
	public static StreamEvent wrap(XTrace trace) {
		StreamEvent streamEvent = new StreamEvent();
		
		// map attributes
		XAttributeMap am = trace.getAttributes();
		XAttributeMap newAm = streamEvent.getAttributes();
		for (String key : am.keySet()) {
			newAm.put(key, am.get(key));
		}
		streamEvent.setAttributes(newAm);
		
		// map events
		streamEvent.add(trace.get(0));
		streamEvent.internalEvent = trace.get(0);
		return streamEvent;
	}
	
	/**
	 * This method unwraps the event, in order to obtain the trace it contains
	 * 
	 * @return the {@link XTrace} object contained into this {@link StreamEvent}
	 */
	public XTrace unwrap() {
		XTrace t = new XTraceImpl(new XAttributeMapLazyImpl<XAttributeMapBufferedImpl>(XAttributeMapBufferedImpl.class));
		XAttributeMap am = getAttributes();
		XAttributeMap newAm = t.getAttributes();
		for (String key : am.keySet()) {
			newAm.put(key, am.get(key));
		}
		t.setAttributes(newAm);
		t.add(internalEvent);
		return t;
	}
	
	/**
	 * This method sets the date of the event
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		XLogHelper.setTimestamp(internalEvent, date);
		remove(0);
		add(internalEvent);
	}
	
	/**
	 * This method gets the date of the event
	 * 
	 * @return
	 */
	public Date getDate() {
		if (isEmpty()) {
			return null;
		}
		return XLogHelper.getTimestamp(get(0));
	}
	
	/**
	 * This method gets the buffer channel of the event
	 * 
	 * @return
	 */
	public int getInternalChannel() {
		return internalChannel;
	}

	/**
	 * This method sets the buffer channel of the event. This method will be
	 * called by the {@link StreamBuffer#enqueueTrace(XTrace)}.
	 * 
	 * @param internalChannel
	 */
	protected void setInternalChannel(int internalChannel) {
		this.internalChannel = internalChannel;
	}
}
