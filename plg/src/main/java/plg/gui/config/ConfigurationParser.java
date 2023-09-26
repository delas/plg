/*
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * License to link and use is also granted to open source programs which
 * are not licensed under the terms of the GPL, given that they satisfy one
 * or more of the following conditions:
 * 1) Explicit license is granted to the ProM and ProMimport programs for
 *    usage, linking, and derivative work.
 * 2) Carte blance license is granted to all programs developed at
 *    Eindhoven Technical University, The Netherlands, or under the
 *    umbrella of STW Technology Foundation, The Netherlands.
 * For further exemptions not covered by the above conditions, please
 * contact the author of this code.
 * 
 */
package plg.gui.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class ConfigurationParser {
	
	public static ConfigurationSet parse(File file)
			throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new FileInputStream(file);
		return parse(is);
	}
	
	public static ConfigurationSet parse(InputStream is) 
			throws ParserConfigurationException, SAXException, IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		// set up a specialized SAX2 handler to fill the container
		ConfigurationHandler handler = (new ConfigurationParser()).new ConfigurationHandler();
		// set up SAX parser and parse provided log file into the container
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(false);
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(bis, handler);
		bis.close();
		return handler.getResult();
	}
	
	protected class ConfigurationHandler extends DefaultHandler {
		
		private Stack<ConfigurationSet> stack;
		private ConfigurationSet master;
		
		protected ConfigurationHandler() {
			stack = new Stack<ConfigurationSet>();
			master = null;
		}
		
		public ConfigurationSet getResult() {
			return master;
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// resolve tag name
			String tagName = localName.trim();
			if (tagName.length() == 0) {
				tagName = qName;
			}
			// evaluate
			if(tagName.equalsIgnoreCase("set")) {
				String name = attributes.getValue("name");
				ConfigurationSet set = new ConfigurationSet(name);
				if(master ==  null) {
					master = set;
				} else {
					stack.peek().addChild(set);
				}
				stack.push(set);
			} else if(tagName.equalsIgnoreCase("attribute")) {
				String key = attributes.getValue("key");
				String value = attributes.getValue("value");
				stack.peek().put(key, value);
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// resolve tag name
			String tagName = localName.trim();
			if (tagName.length() == 0) {
				tagName = qName;
			}
			// check if end of set
			if(tagName.equalsIgnoreCase("set")) {
				stack.pop();
			}
		}
	}
}
