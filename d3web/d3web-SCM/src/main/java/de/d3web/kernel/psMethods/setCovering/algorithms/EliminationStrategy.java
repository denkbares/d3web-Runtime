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

package de.d3web.kernel.psMethods.setCovering.algorithms;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;

/**
 * This strategy decides if a finding has to be removed from the unexplained
 * list in the EliminationHypothesesGenerationAlgorithm
 * 
 * @author bruemmer
 */

public interface EliminationStrategy {
	/**
	 * Eliminates findings by the defined criterion
	 * 
	 * @param theCase
	 *            current case
	 * @param hyp
	 *            hypothesis to consider
	 * @param diag
	 *            SCDiagnosis to consider
	 * @param unexplained
	 *            observed findings that are not yet considered
	 * @return reduced findings-list without the eliminated findings
	 */
	public List eliminate(XPSCase theCase, SCDiagnosis diag, List unexplained);

	public String verbalize();
}
