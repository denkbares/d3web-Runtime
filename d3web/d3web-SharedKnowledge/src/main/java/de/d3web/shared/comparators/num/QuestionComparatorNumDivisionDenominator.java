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
 * Insert the type's description here. Creation date: (07.08.2001 02:02:34)
 * 
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumDivisionDenominator extends QuestionComparatorNum {

	private double denominator = 0;

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

		if (denominator == 0) {
			denominator = Math.abs(x1 - x2);
		}
		return 1 - (Math.abs(x1 - x2) / denominator);

	}

	/**
	 * Insert the method's description here. Creation date: (07.08.2001
	 * 02:05:42)
	 * 
	 * @param newDenominator double
	 */
	public void setDenominator(double newDenominator) {
		denominator = newDenominator;
	}

	/**
	 * @return
	 */
	public double getDenominator() {
		return denominator;
	}

}