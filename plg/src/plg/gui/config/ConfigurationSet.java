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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Christian W. Guenther (christian@deckfour.org)
 * @author Andrea Burattin (just for minor changes)
 */
public class ConfigurationSet extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	private final String name;
	private HashMap<String,ConfigurationSet> children;
	
	public ConfigurationSet(String name) {
		this.name = name;
		this.children = new HashMap<String,ConfigurationSet>();
	}
	
	public Collection<ConfigurationSet> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}
	
	public ConfigurationSet getChild(String name) {
		ConfigurationSet child = children.get(name);
		if(child == null) {
			child = new ConfigurationSet(name);
			children.put(name, child);
		}
		return child;
	}
	
	public void addChild(ConfigurationSet child) {
		children.put(child.name(), child);
	}
	
	public void set(String key, String value) {
		this.put(key, value);
	}
	
	public String get(String key, String defaultValue) {
		String value = this.get(key);
		if(value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}
	
	public void setInteger(String key, int value) {
		this.put(key, Integer.toString(value));
	}
	
	public int getInteger(String key) {
		return getInteger(key, 0);
	}
	
	public int getInteger(String key, int defaultValue) {
		String value = get(key);
		if(value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}
	
	public void setDouble(String key, double value) {
		this.put(key, Double.toString(value));
	}
	
	public double getDouble(String key) {
		return getDouble(key, 0);
	}
	
	public double getDouble(String key, double defaultValue) {
		String value = get(key);
		if(value == null) {
			return defaultValue;
		} else {
			return Double.parseDouble(value);
		}
	}
	
	public void setBoolean(String key, boolean value) {
		this.put(key, Boolean.toString(value));
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = get(key);
		if(value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}
	
	public String name() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof ConfigurationSet) {
			ConfigurationSet other = (ConfigurationSet)o;
			return other.name.equals(name);
		} else {
			return false;
		}
	}

}
