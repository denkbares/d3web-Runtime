/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricaltesting.casevisualization.jung;

import java.util.Collection;

import de.d3web.empiricaltesting.RatedTestCase;

/**
 * This class is a heuristic which determines the vertical distance between two
 * vertices.
 * 
 * @author Sebastian Furth
 * 
 */
public class DistanceDeterminer {

	// Singleton Instance
	private static DistanceDeterminer instance = new DistanceDeterminer();

	/**
	 * Private constructor to ensure noninstantiability.
	 */
	private DistanceDeterminer() {
	}

	/**
	 * Returns an instance of DistanceDeterminer.
	 * 
	 * @return DistanceDeterminer instance
	 */
	public static DistanceDeterminer getInstance() {
		return instance;
	}

	/**
	 * Determines the distance between two vertices on basis of the maximum
	 * number of RatedSolutions.
	 * 
	 * @param cases Collection<RatedTestCase> all cases which are in the graph
	 * @return int vertical distance
	 */
	public int determineDistance(Collection<RatedTestCase> cases) {

		int result = 0;

		for (RatedTestCase rtc : cases) {
			if (rtc.getExpectedSolutions().size() > result) result = rtc.getExpectedSolutions().size();
		}

		return (result * 30 > 200) ? result * 30 : 200;

	}
}
