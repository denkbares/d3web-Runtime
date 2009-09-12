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

package de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation;

import java.util.Set;

import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * This interface describes a strategy for calculating the transitive covering
 * strength of paths from a diagnosis to a finding
 * 
 * @author bruemmer
 * 
 */
public interface StrengthCalculationStrategy {
	/**
	 * 
	 * @param diag
	 *            start node
	 * @param f
	 *            target node
	 * @return the calculated set of strengths for the paths
	 */
	public Set calculateTransitiveStrengths(TransitiveClosure closure, SCDiagnosis diag,
			PredictedFinding f);
}
