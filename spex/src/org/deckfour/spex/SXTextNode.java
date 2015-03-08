/*
 * Spex
 * 
 * An efficient API and implementation for the serial processing
 * and serialization of XML documents.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.spex;

import java.io.IOException;
import java.io.Writer;

import org.deckfour.spex.util.SXmlCharacterMethods;

/**
 * Bogus wrapper class for text nodes.
 * Text nodes can only be appended to tag nodes and represent tag-enclosed
 * text fragements in an XML document.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class SXTextNode extends SXNode {
	
	/**
	 * Constructs a new text node.
	 * 
	 * @param text text contained in this node
	 * @param aWriter output writer
	 * @param aTabLevel tabulator level
	 * @param aTabString tabulator encoding string
	 */
	public SXTextNode(String text, Writer aWriter, int aTabLevel, String aTabString) throws IOException {
		super(aWriter, aTabLevel, aTabString);
		writer.write(SXmlCharacterMethods.convertCharsToXml(text));
	}

	/* (non-Javadoc)
	 * @see org.processmining.lib.xml.Node#close()
	 */
	public void close() throws IOException {
		// ignore this event
	}

}
