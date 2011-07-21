/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.core.io.utilities;

import java.util.Comparator;

import de.d3web.core.knowledge.Resource;

/**
 * Compares {@link Resource}s by their path names.
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 21.07.2011
 */
public class ResourceComparator implements Comparator<Resource> {

	@Override
	public int compare(Resource r1, Resource r2) {
		return r1.getPathName().compareTo(r2.getPathName());
	}

}
