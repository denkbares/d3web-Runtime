/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.model;

import de.d3web.core.session.Session;

/**
 * Interface to represent a single check on a specified session created from a
 * {@link TestCase} at a specific time.
 * 
 * @author Volker Belli & Markus Friedrich (denkbares GmbH)
 * @created 23.01.2012
 */
public interface Check {

	/**
	 * Applies this check to the specified session and returns whether the was
	 * successful or not.
	 * 
	 * @created 23.01.2012
	 * @param session Session to be checked
	 * @return true if the Check was successful, false otherwise
	 */
	boolean check(Session session);

	/**
	 * Returns a user readable String representation of the condition to be
	 * evaluated by this check.
	 * 
	 * @created 23.01.2012
	 * @return String representation of the condition
	 */
	String getCondition();

}
