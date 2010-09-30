/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.core.utilities.Pair;
import de.d3web.core.utilities.Triple;

public class DefaultInfoStore implements InfoStore {

	private final Map<Pair<String, Locale>, Object> entries =
			new HashMap<Pair<String, Locale>, Object>();

	@Override
	public Collection<Triple<String, Locale, Object>> entries() {
		Collection<Triple<String, Locale, Object>> result =
				new LinkedList<Triple<String, Locale, Object>>();
		for (Entry<Pair<String, Locale>, Object> entry : this.entries.entrySet()) {
			result.add(new Triple<String, Locale, Object>(
					entry.getKey().getA(),
					entry.getKey().getB(),
					entry.getValue()));
		}
		return result;
	}

	@Override
	public Object getValue(String key) {
		return getEntry(key, NO_LANGUAGE);
	}

	@Override
	public Object getValue(String key, Locale language) {
		Object value = getEntry(key, language);
		if (value != null) {
			return value;
		}
		return getEntry(key, NO_LANGUAGE);
	}

	private Object getEntry(String key, Locale language) {
		return this.entries.get(new Pair<String, Locale>(key, language));
	}

	@Override
	public void remove(String key) {
		remove(key, NO_LANGUAGE);
	}

	@Override
	public void remove(String key, Locale language) {
		this.entries.remove(new Pair<String, Locale>(key, language));
	}

}
