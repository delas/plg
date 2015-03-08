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

/**
 * Wraps an XML comment within a syntactical node, to allow for correct
 * code formatting / indentation and sufficiently complex document layouts.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class SXCommentNode extends SXNode {

	/**
	 * Constructs a new comment node.
	 * 
	 * @param comment Comment string, <b>without</b> leading 
	 * ("<code>&lt;!--</code>") and trailing ("<code>--&gt;</code>") 
	 * XML-style comment delimiters!
	 * @param aWriter output writer
	 * @param aTabLevel indentation level
	 * @param aTabString tabulator encoding
	 */
	public SXCommentNode(String comment, Writer aWriter, int aTabLevel, String aTabString) throws IOException {
		super(aWriter, aTabLevel, aTabString);
		indentLine();
		writer.write("<!-- " + comment + " -->");
	}

	/* (non-Javadoc)
	 * @see org.processmining.lib.xml.Node#close()
	 */
	public void close() throws IOException {
		// ignore this event
	}

}
