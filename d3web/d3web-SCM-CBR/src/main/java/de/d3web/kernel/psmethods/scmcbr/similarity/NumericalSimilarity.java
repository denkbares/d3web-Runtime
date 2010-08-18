/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.kernel.psmethods.scmcbr.similarity;

import java.util.List;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko Created: 25.09.2009
 * 
 */
public class NumericalSimilarity implements ISimilarityMeasurer {

	private final QuestionNum question;
	private final List<NumericalInterval> intervals;
	private final List<Double> similarities;

	/**
	 * @param question
	 * @param intervals
	 * @param similarities
	 */
	public NumericalSimilarity(QuestionNum question,
			List<NumericalInterval> intervals, List<Double> similarities) {
		this.question = question;
		this.intervals = intervals;
		this.similarities = similarities;

		if (intervals.size() != similarities.size()) throw new IllegalArgumentException();

	}

	@Override
	public double computeSimilarity(Session session) {

		// TODO

		return 0;
	}

}
