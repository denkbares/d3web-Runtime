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

package de.d3web.shared.comparators.mc;

import java.util.Iterator;

import de.d3web.core.session.Value;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.shared.comparators.GroupedComparatorAsymmetric;
import de.d3web.shared.comparators.PairRelation;

public class QuestionComparatorMCGroupedAsymmetric extends
		QuestionComparatorMCGrouped implements GroupedComparatorAsymmetric{

	@Override
	public double compare(Value value1, Value value2) {
		try {
			MultipleChoiceValue ans1 = (MultipleChoiceValue) value1;
			MultipleChoiceValue ans2 = (MultipleChoiceValue) value2;

			if (ans1.equals(ans2)) {
				return 1;
			}

			Iterator<PairRelation> iter = pairRelations.iterator();
			while (iter.hasNext()) {
				PairRelation r = iter.next();
				if (r.getAnswer1().equals(ans1) && r.getAnswer2().equals(ans2)) {
					return r.getValue();
				}
			}
		} catch (Exception x) {
			System.err.println("MCCGroupedAsymmetric: Exception while comparing: " + x);
			return 0;
		}
		return 0;
	}
	
}
