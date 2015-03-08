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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * This class represents an XML document. It is a
 * convenient wrapper for writing XML documents sequentially,
 * while preserving a quasi-object-oriented interface.
 * @see org.deckfour.spex.SXTag
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class SXDocument extends SXNode {
	
	/**
	 * Tabulator string.
	 */
	protected String tabString = "\t";
	/**
	 * Last opened child node of this document.
	 */
	protected SXNode lastChildNode = null;
	/**
	 * Whether this document is still open.
	 */
	protected boolean isOpen = true;

	
	/**
	 * Creates a new document.
	 * 
	 * @param aWriter writer instance to write document to
	 * @param aCharset charset used for encoding in the provided Writer
	 */
	public SXDocument(Writer writer, Charset charset) throws IOException {
		super(writer, 0, "\t");
		lastChildNode = null;
		synchronized(this) {
			isOpen = true;
			writer.write("<?xml version=\"1.0\" encoding=\"" + charset.name()+ "\" ?>");
		}
	}
	
	/**
	 * Creates a new document.
	 * 
	 * @param aStream output stream to write document to
	 * @param aCharset charset to be used for encoding
	 */
	public SXDocument(OutputStream aStream, Charset aCharset) throws IOException {
		super(new OutputStreamWriter(aStream, aCharset), 0, "\t");
		lastChildNode = null;
		synchronized(this) {
			isOpen = true;
			writer.write("<?xml version=\"1.0\" encoding=\"" + aCharset.name()+ "\" ?>");
		}
	}

	/**
	 * Creates a new document.
	 * 
	 * @param aStream output stream to write document to
	 * @param aCharsetName standard name of the charset to be used for encoding
	 */
	public SXDocument(OutputStream aStream, String aCharsetName) throws IOException {
		this(aStream, Charset.forName(aCharsetName));
	}
	
	/**
	 * Creates a new document with standard UTF-8 encoding.
	 * 
	 * @param aStream output stream to write document to
	 */
	public SXDocument(OutputStream aStream) throws IOException {
		this(aStream, Charset.forName("UTF-8"));
	}
	
	/**
	 * Creates a new document with standard UTF-8 encoding.
	 * 
	 * @param aFile file to write the document to
	 * @throws IOException 
	 */
	public SXDocument(File aFile) throws IOException {
		this(new BufferedOutputStream(new FileOutputStream(aFile)), 
				Charset.forName("UTF-8"));
	}
	
	/**
	 * Internal abstraction method;
	 * prepares the document for inserting a new child tag of any type.
	 */
	protected void prepareToAddChildNode() throws IOException {
		// reject modification of already closed document
		if(isOpen==false) {
			throw new IOException("Attempting to write to a closed document!");
		}
		// close previous tag, if applicable
		if(lastChildNode!=null) {
			lastChildNode.close();
			lastChildNode = null;
		}
		writer.write("\n");
	}
	
	/**
	 * Adds a regular, named node to this document (usually one single root node)
	 * <b>WARNING:</b> This will close the last added tag, if applicable!
	 * 
	 * @param tagName The name of the tag to add (contents between brackets)
	 * @return A Node abstraction.
	 */
	public SXTag addNode(String tagName) throws IOException {
		prepareToAddChildNode();
		SXTag node = new SXTag(tagName, writer, 0, tabString);
		lastChildNode = node;
		return node;
	}

	/**
	 * Adds a comment to this document.
	 * <b>WARNING:</b> This will close the last added tag, if applicable!
	 * 
	 * @param comment The comment line to be added.
	 */
	public void addComment(String comment) throws IOException {
		prepareToAddChildNode();
		SXCommentNode commentNode = new SXCommentNode(comment, writer, 0, tabString);
		lastChildNode = commentNode;
	}
	
	/**
	 * Closes this document.
	 * <b>NOTICE:</b> It is <b>absolutely necessary</b> to call this method in the end,
	 * as otherwise the last added child node will not be appropriately finished!
	 */
	public void close() throws IOException {
		// close last tag, if present
		if(lastChildNode!=null) {
			lastChildNode.close();
			lastChildNode = null;
		}
		writer.write("\n");
		writer.flush();
		isOpen = false;
	}
	
	/**
	 * Sets this document's tabulator encoding.
	 * 
	 * @param aTabString Tabulator string to be used.
	 */
	public void setTabString(String aTabString) {
		tabString = aTabString;
	}
	
	/**
	 * Returns this document's tabulator encoding.
	 * 
	 * @return This document's tabulator encoding.
	 */
	public String getTabString() {
		return tabString;
	}
	
}
