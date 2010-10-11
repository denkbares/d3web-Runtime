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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.knowledge.InfoStore;

/**
 * Interface for knowledge base objects, which provide an ID for retrieval.
 * Nearly all knowledge base objects should extend this class.
 * 
 * @author joba, Christian Betz
 * @see NamedObject
 */
public interface IDObject {

	// --- header information ---
	/**
	 * Returns the id of the terminology object
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * Returns the name of the terminology object
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Returns the InfoStore of the {@link IDObject}
	 * 
	 * @created 07.10.2010
	 * @return {@link InfoStore}
	 */
	InfoStore getInfoStore(); // formerly known as PropertyContainer

}
