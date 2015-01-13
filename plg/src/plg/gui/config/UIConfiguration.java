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
import java.io.IOException;

import plg.gui.util.RuntimeUtils;

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 * @author Andrea Burattin (just for minor changes)
 */
public class UIConfiguration {
	
	private static final String FILE_NAME = "plg-config.xml";
	private static final UIConfiguration configuration = new UIConfiguration();
	
	public static ConfigurationSet master() {
		return configuration.configSet;
	}
	
	public static void save() throws IOException {
		String path = RuntimeUtils.getSupportFolder() + FILE_NAME;
		ConfigurationSerializer.serialize(configuration.configSet, new File(path));
	}
	
	private ConfigurationSet configSet;
	
	private UIConfiguration() {
		String path = RuntimeUtils.getSupportFolder() + FILE_NAME;
		try {
			configSet = ConfigurationParser.parse(new File(path));
		} catch (Exception e) {
			configSet = new ConfigurationSet("master");
			System.err.println("WARNING: No configuration found for PLG! (This is okay if you start the application for the first time)");
			System.err.println("Creating new configuration file at " + path + "...");
		}
	}
}
