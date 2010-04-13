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

import de.d3web.core.session.Value;
import de.d3web.core.session.values.Choice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparatorSymmetric;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

public class QuestionComparatorOCGroupedSymmetric extends
		QuestionComparatorOCGrouped implements GroupedComparatorSymmetric{
	
	@Override
	public double compare(Value answers1, Value answers2) {
		try {
			Choice ans1 = (Choice) answers1.getValue();
			Choice ans2 = (Choice) answers2.getValue();

			if (ans1.equals(ans2)) {
				return 1;
			}

			Iterator<PairRelation> iter = pairRelations.iterator();
			while (iter.hasNext()) {
				PairRelation r = (PairRelation) iter.next();
				if (r.containsAnswer(ans1) && r.containsAnswer(ans2)) {
					return r.getValue();
				}
			}
		} catch (Exception x) {
			System.err.println("OCGrouped: Exception while comparing: " + x);
			return 0;
		}
		return 0;
	}
}
