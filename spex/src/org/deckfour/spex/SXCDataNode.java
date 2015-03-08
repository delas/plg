package org.deckfour.spex;

import java.io.IOException;
import java.io.Writer;

import org.deckfour.spex.SXTextNode;
import org.deckfour.spex.util.SXmlCharacterMethods;

/**
 * Bogus wrapper class for CDATA nodes.
 * 
 * @author Andrea Burattin
 */
public class SXCDataNode extends SXTextNode {

	/**
	 * Constructs a new CDATA node.
	 * 
	 * @param text text contained in this node
	 * @param aWriter output writer
	 * @param aTabLevel tabulator level
	 * @param aTabString tabulator encoding string
	 */
	public SXCDataNode(String text, Writer aWriter, int aTabLevel, String aTabString) throws IOException {
		super(text, aWriter, aTabLevel, aTabString);
		writer.write("<![CDATA[");
		writer.write(SXmlCharacterMethods.convertCharsToXml(text));
		writer.write("]]>");
	}
}
