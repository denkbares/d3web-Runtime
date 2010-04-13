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

package de.d3web.core.session.blackboard;
import de.d3web.core.session.CaseObjectSource;
/**
 * Superclass for any dynamic, session specific object.
 * 
 * @author Christian Betz, joba
 */
public abstract class SessionObject {
	private CaseObjectSource sourceObject;

	public SessionObject(CaseObjectSource theSourceObject) {
		super();
		setSourceObject(theSourceObject);
	}

	/**
	 * Creation date: (23.05.2001 15:35:43)
	 * @return the IDObject this dynamic SessionObject has been created for
	 */
	protected CaseObjectSource getSourceObject() {
		return sourceObject;
	}

	private void setSourceObject(
		CaseObjectSource newSourceObject) {
		sourceObject = newSourceObject;
	}
}