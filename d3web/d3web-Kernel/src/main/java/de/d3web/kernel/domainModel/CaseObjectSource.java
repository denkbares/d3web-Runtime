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

package de.d3web.kernel.domainModel;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;

/**
 * States the ability that the implementing object are able to create an session
 * object to store session depending data into.
 */
public interface CaseObjectSource {

	/**
	 * Create a session object for itself, to store all dynamic values (session
	 * dependent values) of this instance into.
	 * <p>
	 * <b>Do not call this method directly.</b> It is created by the {@link XPSCase}
	 * implementations to create the dynamic objects. Use
	 * {@link XPSCase#getCaseObject(CaseObjectSource)} instead.
	 * 
	 * @param session
	 *            the session instance this object is created for
	 * 
	 * @return the created session object for this instance
	 */
	public XPSCaseObject createCaseObject(XPSCase session);
}
