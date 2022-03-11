/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import java.util.Collection;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.model.Target;

/**
 * This class is used to check, if a calculation should continue or abort
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.12.2013
 */
public interface SolutionsRater {

	/**
	 * Checks if a calculation with the specified undiscriminted solutions
	 * should be started -> Returns true if further targets are to be calculated
	 *
	 * @param undiscriminatedSolutions solutions
	 * @param targets                 the remaining test steps with benefit
	 * @return true if the calculation should be started, false otherwise
	 * @created 06.12.2013
	 */
	boolean check(Session session, Collection<Solution> undiscriminatedSolutions, Set<Target> targets);
}
