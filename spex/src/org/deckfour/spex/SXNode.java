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
 * Abstract superclass for nodes in an XML document.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public abstract class SXNode {
	
	/**
	 * Tabulator indentation level of this node.
	 */
	protected int tabLevel = 0;
	/**
	 * Tabulator string.
	 */
	protected String tabString = null;
	/**
	 * Writer used for serializing document.
	 */
	protected Writer writer = null;
	
	/**
	 * Creates a new node.
	 * 
	 * @param aWriter The writer used for serializing output.
	 * @param aTabLevel Indentation level of this node.
	 * @param aTabString Tabulator string used in document.
	 */
	protected SXNode(Writer aWriter, int aTabLevel, String aTabString) {
		writer = aWriter;
		tabLevel = aTabLevel;
		tabString = aTabString;
	}
	
	/**
	 * Explicitly closes this node.
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Returns the indentation level of this node.
	 * 
	 * @return The tabulator indentation level of this node.
	 */
	public int getTabLevel() {
		return tabLevel;
	}
	
	/**
	 * Convenience method for indenting a line according to this 
	 * node's tabulator depth level.
	 */
	protected synchronized void indentLine() throws IOException {
		for(int i=0; i<tabLevel; i++) {
			writer.write(tabString);
		}
	}
	

}
