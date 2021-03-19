/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.core.knowledge.terminology.info;

/**
 * Specified how solutions may be displayed to the user.
 */
public enum SolutionDisplay {
	/**
	 * Specifies a solution that is normally displayed to the user.
	 */
	normal,

	/**
	 * Specifies a solution that is a context solution. This means the solution should not be
	 * displayed to the user, but only used for knowledge-base internal purposes.
	 */
	context,

	/**
	 * Specifies that the solution is a user-relevant group of sub-solutions. For these
	 * groups, the grouping solution is more relevant for the user. The group solution should be
	 * displayed if any of the sub-solutions are derived, instead of the particular derived
	 * sub-solutions. These derived sub-solution(s) of the group may be displayed to the user
	 * additionally (if they are not context solutions) to explain the grouping solution.
	 */
	group
}
