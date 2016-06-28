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
package de.d3web.core.knowledge;

import java.util.Comparator;
import java.util.Locale;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.utils.Triple;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 11.10.2010
 */
public class InfoStoreUtil {

	private static final TripleComparator tripleComparator = new TripleComparator();

	public static void copyEntries(InfoStore source, InfoStore target) {
		for (Triple<Property<?>, Locale, ?> entry : source.entries()) {
			target.addValue(entry.getA(), entry.getB(), entry.getC());
		}
	}

	public static TripleComparator getTrimpleComparator() {
		return tripleComparator;
	}

	/**
	 * Comparator for {@link Triple}s used by the {@link InfoStore}. They are
	 * first compared by the name of the {@link Property}, then by the
	 * verbalization of the {@link Locale} and then by the toString method of
	 * the object.
	 * 
	 * @author Albrecht Striffler (denkbares GmbH)
	 * @created 15.06.2011
	 */
	private static class TripleComparator implements Comparator<Triple<Property<?>, Locale, Object>> {

		@Override
		public int compare(Triple<Property<?>, Locale, Object> o1, Triple<Property<?>, Locale, Object> o2) {
			int prop = o1.getA().getName().compareTo(o2.getA().getName());
			if (prop != 0) return prop;
			if (!(o1.getB() == null && o2.getB() == null)) {
				if (o1.getB() == null) return -1;
				if (o2.getB() == null) return 1;
				int locale = o1.getB().toString().compareTo(o2.getB().toString());
				if (locale != 0) return locale;
			}
			return String.valueOf(o1.getC()).compareTo(String.valueOf(o2.getC()));
		}

	}
}
