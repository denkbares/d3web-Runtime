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

/**
 * Creation date: (07.08.2001 02:02:18)
 * 
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumDivision extends QuestionComparatorNum {

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
		double x1 = ((Double) ((NumValue) ans1).getValue()).doubleValue();
		double x2 = ((Double) ((NumValue) ans2).getValue()).doubleValue();

		double max = Math.max(x1, x2);
		double min = Math.min(x1, x2);

		if (max != 0) {
			double quotient = min / max;
			if (quotient < 0) {
				// one of the numbers was negative!
				return 0;
			}
			else if (quotient > 1) {
				// both of the numbers were negative!
				return max / min;
			}
			else return quotient;
		}
		else {
			return (min == 0) ? 1 : 0;
		}
	}
}