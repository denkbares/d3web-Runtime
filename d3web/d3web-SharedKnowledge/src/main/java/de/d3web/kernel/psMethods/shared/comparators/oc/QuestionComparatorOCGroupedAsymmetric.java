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

package de.d3web.kernel.psMethods.shared.comparators.oc;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparatorAsymmetric;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

public class QuestionComparatorOCGroupedAsymmetric extends
		QuestionComparatorOCGrouped implements GroupedComparatorAsymmetric{
	
	private static final long serialVersionUID = 3846583896907591804L;

	public double compare(List<?> answers1, List<?> answers2) {
		try {
			AnswerChoice ans1 = (AnswerChoice) answers1.get(0);
			AnswerChoice ans2 = (AnswerChoice) answers2.get(0);

			if (ans1.equals(ans2)) {
				return 1;
			}

			Iterator<PairRelation> iter = pairRelations.iterator();
			while (iter.hasNext()) {
				PairRelation r = (PairRelation) iter.next();
				if (r.getAnswer1().equals(ans1) && r.getAnswer2().equals(ans2)) {
					return r.getValue();
				}
			}
		} catch (Exception x) {
			System.err.println("OCGroupedAsymmetric: Exception while comparing: " + x);
			return 0;
		}
		return 0;
	}
}
