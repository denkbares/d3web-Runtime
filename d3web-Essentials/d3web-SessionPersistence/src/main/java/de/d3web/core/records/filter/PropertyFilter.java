/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records.filter;

import java.util.Locale;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.records.SessionRecord;

/**
 * Matches the value of a property (optionally specified with a locale)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.03.2011
 */
public class PropertyFilter<T> implements Filter {

	private final Property<T> property;
	private final T value;
	private final Locale locale;

	public PropertyFilter(Property<T> property, T value) {
		this(property, null, value);
	}

	public PropertyFilter(Property<T> property, Locale locale, T value) {
		this.property = property;
		this.locale = locale;
		this.value = value;
	}

	@Override
	public boolean accept(SessionRecord record) {
		T valueFromStore;
		if (locale != null) {
			valueFromStore = record.getInfoStore().getValue(property, locale);
		}
		else {
			valueFromStore = record.getInfoStore().getValue(property);
		}
		if (value == null && valueFromStore == null) {
			return true;
		}
		else if (valueFromStore == null) {
			return false;
		}
		else {
			return valueFromStore.equals(value);
		}
	}

}
