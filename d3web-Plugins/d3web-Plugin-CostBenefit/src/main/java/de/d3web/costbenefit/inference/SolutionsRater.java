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

import de.d3web.core.knowledge.terminology.Solution;

/**
 * This class is used to check, if a calculation should continue or abort
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.12.2013
 */
public interface SolutionsRater {

	/**
	 * Checks if a calculation with the specified undiscriminted solutions
	 * should be started
	 * 
	 * @created 06.12.2013
	 * @param undiscriminateSolutions solutions
	 * @return true if the calculation should be started, false otherwise
	 */
	boolean check(Collection<Solution> undiscriminateSolutions);

}
