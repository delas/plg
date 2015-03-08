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
 * This class represents regular tag nodes in XML.
 * 
 * <b>WARNING:</b> The usage contract for this class is as follows:
 * <ul>
 * <li>Attributes must be added immediately after creation of a tag, i.e.:</li>
 * <li>All attributes must have been added <b>before</b> adding the first child node.</li>
 * <li>Tags <b>must not be instantiated manually</b>, but by appending to another tag or document.</li>
 * <li>Tags <b>must not be closed manually</b>, but by closing their enclosing document.</li>
 * </ul>
 * @see org.deckfour.spex.SXDocument
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class SXTag extends SXNode {

	/**
	 * Name of this tag (contents between brackets)
	 */
	protected String name = null;
	/**
	 * The last opened child node of this tag.
	 */
	protected SXNode lastChildNode = null;
	/**
	 * Whether this tag ist still open (i.e., closing bracket not written yet)
	 */
	protected boolean isOpen = false;
	
	/**
	 * Creates a new tag instance.
	 * Will result in something like: <code>&lt;<i>aName</i>/&gt;</code>
	 * <b>WARNING:</b> Tags <b>must not be instantiated manually</b>, but by appending to another tag or document.
	 * 
	 * @param aName Name of the tag
	 * @param aWriter Writer used for writing the tag to
	 * @param aTabLevel Tabulator indentation level of this tag
	 * @param aTabString String containing the encoding of a tabulator
	 */
	public SXTag(String aName, Writer aWriter, int aTabLevel, String aTabString) throws IOException {
		super(aWriter, aTabLevel, aTabString);
		// initialize
		name = aName.trim();
		lastChildNode = null;
		// start writing tag
		synchronized(this) {
			indentLine();
			writer.write("<" + name);
			isOpen = true;
		}
	}
	
	/**
	 * Adds an attribute to this tag node.
	 * Will result in something like: <code><i>aName</i>=<i>aValue</i></code>
	 * <b>WARNING:</b> 
	 * <ul>
	 * <li>Attributes must be added immediately after creation of a tag, i.e.:</li>
	 * <li>All attributes must have been added <b>before</b> adding the first child node.</li>
	 * </ul>
	 * 
	 * @param aName Name, i.e. key, of this attribute
	 * @param aValue Value of this attribute
	 */
	public synchronized void addAttribute(String aName, String aValue) throws IOException {
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to add attribute '" + aName + "' to already closed tag '" + name + "'!");
		}
		// check for sane input
		if((aName==null) || 
				(aValue==null) || 
				(aName.trim().length()==0) || 
				(aValue.trim().length()==0)) {
			return;	// reject unnecessary attributes
		}
		// add attributes
		if(lastChildNode==null) {
			// encode and write attribute
			aName = aName.trim();
			aValue = SXmlCharacterMethods.convertCharsToXml(aValue.trim());
			writer.write(" " + aName + "=\"" + aValue + "\"");
		} else {
			// usage contract broken! (no adding of attributes after adding first child node)
			throw new IOException("No attributes can be added to a node "
					+ "after the first child has been added! ('" + name + "')");
		}
	}
	
	/**
	 * Adds a regular tag child node with the specified tag name to this node.
	 * <b>WARNING:</b> This will close the last added child node, if applicable!
	 * <b>NOTICE:</b> Use this method to add child nodes, do <b>NOT</b> attempt to instantiate them manually!
	 * 
	 * @param tagName Name of the tag node to be added
	 * @return The newly created tag
	 */
	public synchronized SXTag addChildNode(String tagName) throws IOException {
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to add child node '" + tagName + "' to already closed tag '" + name + "'!");
		}
		if(lastChildNode==null) {
			// no child nodes yet, close opening tag
			writer.write(">");
		} else {
			lastChildNode.close();
		}
		// regular child tags start in new line
		writer.write("\n");
		// create, register and return new child node
		SXTag childNode = new SXTag(tagName, writer, tabLevel+1, tabString);
		lastChildNode = childNode;
		return childNode;
	}
	
	/**
	 * Adds a text child node to this node. 
	 * <b>WARNING:</b> This will close the last added child node, if applicable!
	 * 
	 * @param text Text to be added
	 */
	public synchronized void addTextNode(String text) throws IOException {
		// check for non-empty string
		if(text == null || text.trim().length() == 0) {
			return;
		}
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to add child text node to already closed tag '" + name + "'!");
		}
		if(lastChildNode==null) {
			// no child nodes yet, close opening tag
			writer.write(">");
		} else {
			// close previous sibling and move to new line
			// FIXME: is this a legal situation, and if: how do we format this issue nicely?
			lastChildNode.close();
			writer.write("\n");
			indentLine();
		}
		SXTextNode textNode = new SXTextNode(text, writer, tabLevel, tabString);
		lastChildNode = textNode;
	}
	
	/**
	 * Adds a text child node to this node. 
	 * <b>WARNING:</b> This will close the last added child node, if applicable!
	 * 
	 * @param cdata CDATA text to be added
	 */
	public synchronized void addCDataNode(String cdata) throws IOException {
		// check for non-empty string
		if(cdata == null || cdata.trim().length() == 0) {
			return;
		}
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to add child text node to already closed tag '" + name + "'!");
		}
		if(lastChildNode==null) {
			// no child nodes yet, close opening tag
			writer.write(">");
		} else {
			// close previous sibling and move to new line
			// FIXME: is this a legal situation, and if: how do we format this issue nicely?
			lastChildNode.close();
			writer.write("\n");
			indentLine();
		}
		SXCDataNode cdataNode = new SXCDataNode(cdata, writer, tabLevel, tabString);
		lastChildNode = cdataNode;
	}
	
	/**
	 * Adds a comment to this node.
	 * <b>WARNING:</b> This will close the last added child node, if applicable!
	 * <b>NOTICE:</b> Syntactically spoken, comments act like child nodes.
	 * This means, they are treated just like child nodes with respect to indentation
	 * and embedding them into the document. Therefore, if you want to add a comment to
	 * a specific node, it is advised to add the comment to this node's supernode in 
	 * advance. This procedure will preserve correct formatting of the resulting document.
	 * 
	 * @param comment Text of the comment to be added (without leading and trailing 
	 * XML-style comment indicators!).
	 */
	public synchronized void addComment(String comment) throws IOException {
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to add comment child node to already closed tag '" + name + "'!");
		}
		if(lastChildNode==null) {
			// no child nodes yet, close opening tag
			writer.write(">");
		} else {
			lastChildNode.close();
		}
		// comment starts in new line
		writer.write("\n");
		SXCommentNode commentNode = new SXCommentNode(comment, writer, tabLevel+1, tabString);
		lastChildNode = commentNode;
	}
	
	/**
	 * Closes and finalizes this node.
	 * <b>WARNING:</b> Tags <b>must not be closed manually</b>, but by closing 
	 * their enclosing document.
	 */
	public synchronized void close() throws IOException {
		// reject modification of already closed node
		if(isOpen==false) {
			throw new IOException("Attempted to close already closed tag '" + name + "'!");
		}
		// close previously opened child node, if present
		if(lastChildNode!=null) {
			// close regular child node
			lastChildNode.close();
			// on regular nodes and comments, write closing tag into new line
			if((lastChildNode instanceof SXTextNode)==false) {
				writer.write("\n");
				indentLine();
			}
			// write closing text
			writer.write("</" + name + ">");
			lastChildNode = null;
		} else {
			// no siblings, close unique tag
			writer.write("/>");
		}
		isOpen = false;
	}
	
}
