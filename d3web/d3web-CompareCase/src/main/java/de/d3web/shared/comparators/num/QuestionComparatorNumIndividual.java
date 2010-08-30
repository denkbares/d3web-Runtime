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

package de.d3web.shared.comparators.num;

import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.shared.comparators.IndividualComparator;

/**
 * Creation date: (10.08.2001 22:55:40)
 * 
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumIndividual extends QuestionComparatorNum implements IndividualComparator {

	@Override
	public double compare(Value ans1, Value ans2) {
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
		Double d1 = (Double) ((NumValue) ans1).getValue();
		Double d2 = (Double) ((NumValue) ans2).getValue();
		return (d1.equals(d2)) ? 1 : 0;
	}
}