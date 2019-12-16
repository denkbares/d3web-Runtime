/*
 * Copyright (C) 2019 denkbares GmbH, Germany
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

package de.d3web.costbenefit.blackboard;

import de.d3web.core.session.Session;

/**
 * Interface to mark a session to be derived from an other existing session.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 16.12.2019
 */
public interface DerivedSession extends Session {
	/**
	 * Returns the underlying root session this session is derived from. If a derived session is also derived from
	 * another derived session, this method should directly return the original non-derived session.
	 *
	 * @return the most original session this session is derived from
	 */
	Session getRootSession();
}
