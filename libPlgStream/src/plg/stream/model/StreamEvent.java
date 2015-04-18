package plg.stream.model;

import java.util.Date;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.buffered.XAttributeMapBufferedImpl;
import org.deckfour.xes.model.buffered.XAttributeMapSerializerImpl;
import org.deckfour.xes.model.buffered.XTraceBufferedImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;

import plg.utils.XLogHelper;

/**
 * 
 * @author Andrea Burattin
 */
public class StreamEvent extends XTraceBufferedImpl {

	private int internalChannel = -1;
	private XEvent internalEvent = null;
	
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
	
	public void setDate(Date date) {
		XLogHelper.setTimestamp(internalEvent, date);
		remove(0);
		add(internalEvent);
	}
	
	public Date getDate() {
		if (isEmpty()) {
			return null;
		}
		return XLogHelper.getTimestamp(get(0));
	}

	public int getInternalChannel() {
		return internalChannel;
	}

	public void setInternalChannel(int internalChannel) {
		this.internalChannel = internalChannel;
	}
}
