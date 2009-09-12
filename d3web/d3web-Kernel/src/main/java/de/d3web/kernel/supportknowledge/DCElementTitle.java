/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.supportknowledge;

import java.util.ArrayList;
import java.util.Collection;

public enum DCElementTitle {

	SHORT_DESCRIPTION("shortDescription"),
	LONG_DESCRIPTION("longDescription"),
	EXAMPLES("examples"),
	REALISATION("realisation"),
	EXPLANATION("explanation");
	
	private final String tag;
	
	DCElementTitle(String tag) {
		this.tag = tag;
	}
	
	public String getTitle() {
		return tag;
	}
	
	public static String[] getAllTitles() {
		Collection<String> result = new ArrayList<String>();
		for (DCElementTitle element : values()) {
			result.add(element.getTitle());
		}
		return result.toArray(new String[0]);
	}
	
	public static DCElementTitle getDCElementTitle(String title) {
		for (DCElementTitle element : values()) {
			if(element.getTitle().equals(title)) {
				return element;
			}
		}
		return null;
	}
	
}
