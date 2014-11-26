package plg.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This class is a helper that can be used to handle XLog objects. It is only
 * made of static methods.
 * 
 * For example, to create a log with two traces, each of them with two events,
 * you have to write:
 * <pre>
 * 	XLog l = XLogHelper.generateNewXLog("Process Name");
 * 
 * 	XTrace t1 = XLogHelper.insertTrace(l, "case1");
 * 	XEvent e11 = XLogHelper.insertEvent(t1, "A", new Date());
 * 	XEvent e12 = XLogHelper.insertEvent(t1, "B", new Date());
 * 
 * 	XTrace t2 = XLogHelper.insertTrace(l, "case2");
 * 	XEvent e21 = XLogHelper.insertEvent(t2, "A", new Date());
 * 	XEvent e22 = XLogHelper.insertEvent(t2, "B", new Date());
 * </pre>
 * 
 * It is also possible to "decorate" all the {@link XAttributable} elements,
 * such as {@link XLog}, {@link XTrace}, and {@link XEvent}:
 * <pre>
 * 	XLogHelper.decorateElement(t1, "cost", 1000.00); // with no extension
 * 	XLogHelper.decorateElement(e11, "org:group", "Department 1",
 * 		"Organizational"); // with "Organizational" extension
 * </pre>
 * 
 * There are <tt>decorateElement</tt> methods that can handle different element
 * types, such as:
 * <ul>
 *  <li>boolean values;</li>
 *  <li>date values;</li>
 *  <li>integer values;</li>
 *  <li>long values;</li>
 *  <li>string values.</li>
 * </ul>
 * 
 * @author Andrea Burattin
 */
public class XLogHelper {

	private static XFactory xesFactory = new XFactoryNaiveImpl();
	private static XExtensionManager xesExtensionManager = XExtensionManager.instance();

	/**
	 * This method generates a new {@link XLog} and returns it. By default,
	 * some of the standard extensions are registered, specifically:
	 * <ul>
	 * 	<li>{@link XConceptExtension};</li>
	 * 	<li>{@link XLifecycleExtension};</li>
	 * 	<li>{@link XOrganizationalExtension};</li>
	 * 	<li>{@link XTimeExtension}.</li>
	 * </ul>
	 * 
	 * @param logName the name of the new log
	 * @return the generated log
	 */
	public static XLog generateNewXLog(String logName) {
		XLog log;
		log = xesFactory.createLog();
		decorateElement(log, "concept:name", logName, "Concept");
		log.getExtensions().add(xesExtensionManager.getByName("Lifecycle"));
		log.getExtensions().add(xesExtensionManager.getByName("Organizational"));
		log.getExtensions().add(xesExtensionManager.getByName("Time"));
		log.getExtensions().add(xesExtensionManager.getByName("Concept"));
		return log;
	}
	
	/**
	 * This method creates a new {@link XTrace} with the given case id, and
	 * returns the new trace
	 * 
	 * @param caseId the case identifier of the new trace
	 * @return the new trace created, or <tt>null</tt> if the given log is not
	 * valid
	 */
	public static XTrace createTrace(String caseId) {
		XTrace trace = xesFactory.createTrace();
		decorateElement(trace, "concept:name", caseId, "Concept");
		return trace;
	}
	
	/**
	 * This method creates a new {@link XTrace} with the given case id, adds it
	 * to the given log object, and returns the new trace
	 * 
	 * @param log the log that is going to host the new trace
	 * @param caseId the case identifier of the new trace
	 * @return the new trace created, or <tt>null</tt> if the given log is not
	 * valid
	 */
	public static XTrace insertTrace(XLog log, String caseId) {
		if (log == null) {
			return null;
		}
		XTrace trace = xesFactory.createTrace();
		decorateElement(trace, "concept:name", caseId, "Concept");
		log.add(trace);
		return trace;
	}
	
	/**
	 * This method creates a new {@link XEvent} referring to the given activity
	 * name, occurred at the given time. The event is added to the given trace,
	 * and returned
	 * 
	 * @param trace the trace that is going to host the new event
	 * @param activityName the name of the activity the new event is going to
	 * refer to
	 * @param timestamp the time the new event occurred
	 * @return the new event created, or <tt>null</tt> if the given trace is
	 * not valid
	 */
	public static XEvent insertEvent(XTrace trace, String activityName, Date timestamp) {
		if (trace == null) {
			return null;
		}
		XEvent e = xesFactory.createEvent();
		decorateElement(e, "concept:name", activityName, "Concept");
		decorateElement(e, "time:timestamp", timestamp, "Time");
		trace.add(e);
		return e;
	}
	
	/**
	 * This method creates a new {@link XEvent} referring to the given activity
	 * name, occurred at the given time. The event is added to the given trace,
	 * and returned
	 * 
	 * @param trace the trace that is going to host the new event
	 * @param activityName the name of the activity the new event is going to
	 * refer to
	 * @return the new event created, or <tt>null</tt> if the given trace is
	 * not valid
	 */
	public static XEvent insertEvent(XTrace trace, String activityName) {
		if (trace == null) {
			return null;
		}
		XEvent e = xesFactory.createEvent();
		decorateElement(e, "concept:name", activityName, "Concept");
		trace.add(e);
		return e;
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a string
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, String value, String extensionName) {
		if (element == null) {
			return;
		}
		XAttributeLiteral attribute = xesFactory.createAttributeLiteral(attributeName, value, xesExtensionManager.getByName(extensionName));
		XAttributeMap attributes = element.getAttributes();
		if (attributes == null || attributes.isEmpty()) {
			attributes = xesFactory.createAttributeMap();
		}
		attributes.put(attributeName, attribute);
		element.setAttributes(attributes);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a string
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, String value) {
		decorateElement(element, attributeName, value, null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a double
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, Double value, String extensionName) {
		if (element == null) {
			return;
		}
		XAttributeContinuous attribute = xesFactory.createAttributeContinuous(attributeName, value, xesExtensionManager.getByName(extensionName));
		XAttributeMap attributes = element.getAttributes();
		if (attributes == null || attributes.isEmpty()) {
			attributes = xesFactory.createAttributeMap();
		}
		attributes.put(attributeName, attribute);
		element.setAttributes(attributes);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a double
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, Double value) {
		decorateElement(element, attributeName, value, null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a date
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, Date value, String extensionName) {
		if (element == null) {
			return;
		}
		XAttributeTimestamp attribute = xesFactory.createAttributeTimestamp(attributeName, value, xesExtensionManager.getByName(extensionName));
		XAttributeMap attributes = element.getAttributes();
		if (attributes == null) {
			attributes = xesFactory.createAttributeMap();
		}
		attributes.put(attributeName, attribute);
		element.setAttributes(attributes);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a date
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, Date value) {
		decorateElement(element, attributeName, value, null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a boolean
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, Boolean value, String extensionName) {
		if (element == null) {
			return;
		}
		XAttributeBoolean attribute = xesFactory.createAttributeBoolean(attributeName, value, xesExtensionManager.getByName(extensionName));
		XAttributeMap attributes = element.getAttributes();
		if (attributes == null) {
			attributes = xesFactory.createAttributeMap();
		}
		attributes.put(attributeName, attribute);
		element.setAttributes(attributes);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a boolean
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, Boolean value) {
		decorateElement(element, attributeName, value, null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a long
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, Long value, String extensionName) {
		if (element == null) {
			return;
		}
		XAttributeDiscrete attribute = xesFactory.createAttributeDiscrete(attributeName, value, xesExtensionManager.getByName(extensionName));
		XAttributeMap attributes = element.getAttributes();
		if (attributes == null) {
			attributes = xesFactory.createAttributeMap();
		}
		attributes.put(attributeName, attribute);
		element.setAttributes(attributes);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with a boolean
	 * value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, Long value) {
		decorateElement(element, attributeName, value, null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with an
	 * integer value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 * @param extensionName the extension name
	 */
	public static void decorateElement(XAttributable element, String attributeName, Integer value, String extensionName) {
		decorateElement(element, attributeName, value.longValue(), null);
	}
	
	/**
	 * This method can be used to decorate an {@link XAttributable} element
	 * (such as {@link XLog}, {@link XTrace}, and {@link XEvent}) with an
	 * integer value
	 * 
	 * @param element the element to decorate
	 * @param attributeName the attribute name
	 * @param value the attribute value
	 */
	public static void decorateElement(XAttributable element, String attributeName, Integer value) {
		decorateElement(element, attributeName, value.longValue(), null);
	}
	
	/**
	 * This method returns the value of the attribute <tt>concept:name</tt> for
	 * the given attributable element
	 * 
	 * @param element the element to analyze
	 * @return the value of the <tt>concept:name</tt> attribute
	 */
	public static String getName(XAttributable element) {
		XAttributeLiteral name = (XAttributeLiteral) element.getAttributes().get("concept:name");
		return name.getValue();
	}
	
	/**
	 * This method returns the value of the attribute <tt>time:timestamp</tt>
	 * for the given attributable element
	 * 
	 * @param element the element to analyze
	 * @return the value of the <tt>time:timestamp</tt> attribute
	 */
	public static Date getTimestamp(XAttributable element) {
		XAttributeTimestamp time = (XAttributeTimestamp) element.getAttributes().get("time:timestamp");
		return time.getValue();
	}
	
	/**
	 * This method sorts the given trace according to the timestamps of its
	 * events
	 * 
	 * @param trace the trace to be sorted
	 */
	public static void sort(XTrace trace) {
		Collections.sort(trace, new Comparator<XEvent>() {
			@Override
			public int compare(XEvent e1, XEvent e2) {
				Date d1 = getTimestamp(e1);
				Date d2 = getTimestamp(e2);
				return d1.compareTo(d2);
			}
		});
	}
}
