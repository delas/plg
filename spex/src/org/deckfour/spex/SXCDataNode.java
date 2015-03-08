package org.deckfour.spex;

import java.io.IOException;
import java.io.Writer;

import org.deckfour.spex.util.SXmlCharacterMethods;

/**
 * Bogus wrapper class for CDATA nodes.
 * 
 * @author Andrea Burattin
 */
public class SXCDataNode extends SXNode {

	/**
	 * Constructs a new CDATA node.
	 * 
	 * @param text text contained in this node
	 * @param aWriter output writer
	 * @param aTabLevel tabulator level
	 * @param aTabString tabulator encoding string
	 */
	public SXCDataNode(String text, Writer aWriter, int aTabLevel, String aTabString) throws IOException {
		super(aWriter, aTabLevel, aTabString);
		writer.write("\n");
		indentLine();
		writer.write("<![CDATA[");
		writer.write("\n");
		writer.write(SXmlCharacterMethods.convertCharsToXml(text));
		writer.write("\n");
		indentLine();
		writer.write("]]>");
	}

	/* (non-Javadoc)
	 * @see org.processmining.lib.xml.Node#close()
	 */
	public void close() throws IOException {
		// ignore this event
	}
}
