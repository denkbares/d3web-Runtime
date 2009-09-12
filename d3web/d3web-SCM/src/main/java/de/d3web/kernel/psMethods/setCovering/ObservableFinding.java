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

package de.d3web.kernel.psMethods.setCovering;

import java.util.Arrays;

/**
 * This represents a Finding that can be observed. It will be generated from
 * XPSCase-values
 * 
 * @author bruemmer
 */
public class ObservableFinding extends Finding {

	public ObservableFinding() {
		super();
	}

	public void setAnswers(Object[] answers) {
		super.setAnswers(answers);
	}

	public boolean isLeaf() {
		return true;
	}

	/**
	 * Calculates the similarity of this finding and the other (given) Finding
	 * 
	 * @param otherFinding
	 *            PredictedFinding to compare to
	 * @return the similarity value of the comparison
	 */
	public double calculateSimilarity(Finding otherFinding) {

		// if the NamedObjects are different, their
		// similarity is 0.
		if (!getNamedObject().getId().equals(otherFinding.getNamedObject().getId())) {
			return 0;
		}

		if (otherFinding instanceof PredictedFinding) {
			return otherFinding.calculateSimilarity(this);
		}

		return super.calculateSimilarity(Arrays.asList(getAnswers()), Arrays.asList(otherFinding
				.getAnswers()));

	}

	public String verbalize() {
		return Arrays.asList(getAnswers()).toString();
	}

	public String toString() {
		return getNamedObject().getId() + "=" + Arrays.asList(getAnswers()).toString();
	}
}
