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

package de.d3web.shared.comparators.mc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.shared.comparators.IndividualComparator;

/**
 * Creation date: (07.08.2001 02:42:47)
 * 
 * @author: Norman Brümmer
 */
public class QuestionComparatorMCIndividual extends QuestionComparatorMC implements IndividualComparator {

	@Override
	public double compare(Value ans1, Value ans2) {
		List<ChoiceValue> proved = new LinkedList<ChoiceValue>();
		double compCount = 0;
		double succCount = 0;

		if (UndefinedValue.isUndefinedValue(ans1)
				&& UndefinedValue.isUndefinedValue(ans2)) {
			return 1;
		}
		else if (UndefinedValue.isUndefinedValue(ans1)) {
			return 0;
		}
		else if (UndefinedValue.isUndefinedValue(ans2)) {
			return 0;
		}
		List<ChoiceValue> ans1List = (List<ChoiceValue>) ((MultipleChoiceValue) ans1).getValue();
		List<ChoiceValue> ans2List = (List<ChoiceValue>) ((MultipleChoiceValue) ans2).getValue();

		Iterator<?> iter = ans1List.iterator();
		while (iter.hasNext()) {
			ChoiceValue ans = (ChoiceValue) iter.next();
			compCount++;
			if (ans2List.contains(ans)) {
				succCount++;
			}
			proved.add(ans);
		}

		iter = ans2List.iterator();
		while (iter.hasNext()) {
			ChoiceValue ans = (ChoiceValue) iter.next();
			if (!proved.contains(ans)) {
				compCount++;
			}
		}
		return succCount / compCount;
	}
}