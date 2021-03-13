/*
 * Copyright (C) 2021 denkbares GmbH, Germany
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

package de.d3web.core.inference;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.Session;

/**
 * This interface defines the interview support of an problem solving method ( {@link PSMethod}). Interview support
 * allows the PSMethodInterview to access some (dynamically changing) information about how the questions and their
 * ranges should be offered to the user.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.2021
 */
public interface InterviewSupport {

	/**
	 * Returns true if the choice can be offered to the user in the specified session. If false, the option should be
	 * hidden or disabled by the interview, not allow the user to select this option.
	 *
	 * @param session the session to check the choice for
	 * @param choice  the choice to check for availability
	 * @return true if the choice is available
	 */
	boolean isAvailable(Session session, Choice choice);
}
