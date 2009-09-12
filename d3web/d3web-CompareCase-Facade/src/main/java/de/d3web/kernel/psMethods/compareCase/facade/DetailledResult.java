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

package de.d3web.kernel.psMethods.compareCase.facade;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.psMethods.compareCase.comparators.ComparatorResult;

/**
 * Insert the type's description here. Creation date: (22.08.01 01:45:07)
 * 
 * @author: Norman Brümmer
 */
public class DetailledResult {
	private QContainer container = null;
	private double similarity = 0;
	private double maxPoints = 0;
	private double reachedPoints = 0;

	private List comparatorResults = null;

	/**
	 * ContainerResult constructor comment.
	 */
	public DetailledResult(QContainer cont, List results) {
		super();
		initialize(cont, results);
	}

	public void initialize(QContainer cont, List results) {
		container = cont;
		comparatorResults = results;
		calcContainerResult();
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 01:53:00)
	 */
	private void calcContainerResult() {
		Iterator iter = comparatorResults.iterator();
		while (iter.hasNext()) {
			ComparatorResult cres = (ComparatorResult) iter.next();
			maxPoints += cres.getMaxPoints();
			reachedPoints += cres.getReachedPoints();
		}

		similarity = reachedPoints / maxPoints;
	}

	/**
	 * Insert the method's description here. Creation date: (23.08.2001
	 * 17:19:17)
	 * 
	 * @return java.lang.String
	 */
	public String getContainerId() {
		return container.getId();
	}

	/**
	 * Insert the method's description here. Creation date: (23.08.2001
	 * 17:19:17)
	 * 
	 * @return java.lang.String
	 */
	public String getContainerName() {
		return container.getText();
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 01:52:24)
	 * 
	 * @return double
	 */
	public double getContainerSimilarity() {
		return similarity;
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 03:25:53)
	 * 
	 * @return java.util.List
	 */
	public List getDetailledQuestionResults() {
		return comparatorResults;
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 01:52:24)
	 * 
	 * @return double
	 */
	public double getMaxContainerPoints() {
		return maxPoints;
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 01:52:24)
	 * 
	 * @return double
	 */
	public double getReachedContainerPoints() {
		return reachedPoints;
	}

	/**
	 * Insert the method's description here. Creation date: (22.08.01 17:16:13)
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return container.getId() + ": " + reachedPoints + " = " + similarity
				+ " * " + maxPoints + "::::" + comparatorResults;
	}
}