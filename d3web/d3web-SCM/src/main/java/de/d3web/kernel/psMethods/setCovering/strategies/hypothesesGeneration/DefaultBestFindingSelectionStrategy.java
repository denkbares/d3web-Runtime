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

package de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.FindingByWeightComparator;

/**
 * @author bruemmer
 * 
 */
public class DefaultBestFindingSelectionStrategy implements BestFindingSelectionStrategy {

	private static DefaultBestFindingSelectionStrategy instance = null;
	private DefaultBestFindingSelectionStrategy() {
	}
	public static DefaultBestFindingSelectionStrategy getInstance() {
		if (instance == null) {
			instance = new DefaultBestFindingSelectionStrategy();
		}
		return instance;
	}

	public ObservableFinding selectMaxFinding(List unexplained, XPSCase theCase) {
		Comparator comparator = new FindingByWeightComparator(theCase);
		List findings = new LinkedList(unexplained);

		if ((findings != null) && !findings.isEmpty()) {
			Collections.sort(findings, comparator);
			return (ObservableFinding) findings.get(0);
		}
		return null;
	}

}
