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
package de.d3web.diaFlux.flow;

/**
 * @author Reinhard Hatko
 * @created 08.03.2012
 */
public interface DiaFluxElement {

	/**
	 * Returns the if of this element. The id should be unique, at least within the same flow.
	 *
	 * @return the ID of this element
	 */
	String getID();

	/**
	 * Returns the flow this element is part of. It may return null if the element is not yet added to a flowchart.
	 *
	 * @return the containing flow of this element
	 */
	Flow getFlow();

	/**
	 * Removes this instance from the flow and unlinks it from all terminology objects. After this method is called, the
	 * object should not be reused any longer.
	 */
	void destroy();
}