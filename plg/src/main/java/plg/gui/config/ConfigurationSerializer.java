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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeSet;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 * @author Andrea Burattin (just for minor changes)
 */
public class ConfigurationSerializer {
	
	public static void serialize(ConfigurationSet set, File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		serialize(set, os);
	}
	
	public static void serialize(ConfigurationSet set, OutputStream out) throws IOException {
		SXDocument doc = new SXDocument(out);
		doc.addComment("PLG configuration file");
		doc.addComment("(c) 2009 by Christian W. Guenther (christian@deckfour.org)");
		doc.addComment("(c) 2015 by Andrea Burattin");
		doc.addComment("WARNING: Do not manually edit this file, unless you know what you are doing!");
		SXTag root = doc.addNode("configuration");
		root.addAttribute("timestamp", Long.toString(System.currentTimeMillis()));
		serialize(set, root);
		doc.close();
	}
	
	private static void serialize(ConfigurationSet set, SXTag parent) throws IOException {
		SXTag tag = parent.addChildNode("set");
		tag.addAttribute("name", set.name());
		TreeSet<String> keys = new TreeSet<String>(set.keySet());
		for(String key : keys) {
			String value = set.get(key);
			if(value != null) {
				SXTag attribute = tag.addChildNode("attribute");
				attribute.addAttribute("key", key);
				attribute.addAttribute("value", value);
			}
		}
		for(ConfigurationSet child : set.getChildren()) {
			serialize(child, tag);
		}
	}

}
