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
package org.deckfour.spex.util;

/**
 * This class provides a set of convenience methods for handling strings that 
 * are to be written to or read from XML documents.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class SXmlCharacterMethods {
	
	/**
	 * Convenience method to convert XML reserved chars.
	 * 
	 * @param input String to be converted.
	 * @return Converted string.
	 */
	public static String convertCharsToXml(String input) {
		StringBuffer result = new StringBuffer();
		for(int i=0; i<input.length(); i++) {
			switch(input.charAt(i)) {
				case '<':	result.append("&lt;"); break;
				case '>':	result.append("&gt;"); break;
				case '"':	result.append("&quot;"); break;
				case '\'':	result.append("&apos;"); break;
				case '&':	result.append("&amp;"); break;
				default:	result.append(input.charAt(i));
			}
		}
		return result.toString().trim();
	}
	
	/**
	 * Convenience method for backward conversion of XML encoded chars.
	 * 
	 * @param input XML encoded string to be converted.
	 * @return Converted string.
	 */
	public static String convertCharsFromXml(String input) {
		StringBuffer result = new StringBuffer();
		for(int i=0; i<input.length();) {
			if(input.charAt(i)=='&') {
				if(input.substring(i, i+3).equals("&lt;")) {
					result.append('<');
					i += 4;
				} else if(input.substring(i, i+3).equals("&gt;")) {
					result.append('>');
					i += 4;
				} else if(input.substring(i, i+4).equals("&amp;")) {
					result.append('&');
					i += 5;
				} else if(input.substring(i, i+5).equals("&quot;")) {
					result.append('"');
					i += 6;
				} else if(input.substring(i, i+5).equals("&apos;")) {
					result.append('\'');
					i += 6;
				} else {
					result.append(input.charAt(i));
					i++;
				}
			} else {
				result.append(input.charAt(i));
				i++;
			}
		}
		return result.toString();
	}

}
