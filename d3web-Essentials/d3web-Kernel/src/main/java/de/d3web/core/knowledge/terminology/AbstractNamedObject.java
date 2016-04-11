/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.core.knowledge.terminology;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.InfoStore;

/**
 * 
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class AbstractNamedObject implements NamedObject {

	/**
	 * Representing a short name of the object.
	 */
	protected final String name;
	private final InfoStore infoStore = new DefaultInfoStore();

	/**
	 * 
	 */
	public AbstractNamedObject(String name) {
		this.name = name;
	}

	/**
	 * The text of a {@link AbstractTerminologyObject} is the name or a short
	 * description of the object. Please keep it brief and use other fields for
	 * longer content (e.g., prompt for {@link Question}, and comments for
	 * {@link Solution}).
	 * 
	 * @return the name of this object
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}

	@Override
	@Deprecated
	public String getId() {
		return getName();
	}

}