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
import java.util.regex.Pattern;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.records.SessionRecord;

/**
 * Matches a String property (optionally specified with a Locale) with a regular
 * expression
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 08.03.2011
 */
public class StringPropertyFilter implements Filter {

	private final Property<String> property;
	private final Pattern p;
	private final Locale locale;

	public StringPropertyFilter(Property<String> property, String regexp) {
		this(property, null, regexp);
	}

	public StringPropertyFilter(Property<String> property, Locale locale, String regexp) {
		this.property = property;
		this.locale = locale;
		p = Pattern.compile(regexp);
	}

	@Override
	public boolean accept(SessionRecord record) {
		String value;
		if (locale != null) {
			value = record.getInfoStore().getValue(property, locale);
		}
		else {
			value = record.getInfoStore().getValue(property);
		}
		if (value == null) return false;
		return p.matcher(value).matches();
	}
}
