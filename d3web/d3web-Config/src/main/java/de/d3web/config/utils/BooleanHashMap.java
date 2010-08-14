/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

/*
 * Created on 28.11.2003
 */
package de.d3web.config.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author bannert
 */
public class BooleanHashMap extends HashMap<String, Boolean> {

	private List<String> keys = new LinkedList<String>();

	public Boolean put(String key, Object value) {
		if (value instanceof Boolean) return super.put(key, (Boolean) value);
		else return null;
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map t) {
		if (t instanceof BooleanHashMap) super.putAll(t);
	}

	public boolean isKey(String key) {
		return this.keys.contains(key);
	}

	public List getKeys() {
		return this.keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
}
