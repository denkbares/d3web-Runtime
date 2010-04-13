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

package de.d3web.shared.comparators.num;
import java.util.Iterator;
import java.util.List;

import de.d3web.core.session.values.AnswerNum;

/**
 * Insert the type's description here.
 * Creation date: (09.08.2001 18:00:32)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumSectionInterpolate extends QuestionComparatorNumSection {

	public double compare(List<?> ans1, List<?> ans2) {
		try {
			//       (g(x) / g(y))
			Double x1 = (Double) ((AnswerNum) ans1.get(0)).getValue(null);
			Double x2 = (Double) ((AnswerNum) ans2.get(0)).getValue(null);

			double gx = getInterpolatedFunctionValue(x1);
			double gy = getInterpolatedFunctionValue(x2);

			return Math.min(gx, gy) / Math.max(gx, gy);
		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}
	}

	public double getInterpolatedFunctionValue(Double val) {
		double a = 0, ga = 0;
		double b = 0, gb = 0;

		Iterator<Double> xiter = xValues.iterator();
		Iterator<Double> yiter = yValues.iterator();

		double m = 0;

		while (xiter.hasNext()) {
			Double d = xiter.next();
			Double gd = yiter.next();

			b = d.doubleValue();
			gb = gd.doubleValue();

			if (d.doubleValue() <= val.doubleValue()) {
				if (b != a) {
					m = ((gb - ga) / (b - a));
				}

				a = d.doubleValue();
				ga = gd.doubleValue();
			} else {
				break;
			}
		}

		if (b < val.doubleValue()) {
			gb += (val.doubleValue() - b) * m;
			b = val.doubleValue();

		}

		if (a == b) {
			return ga;
		}

		return interpolate(a, ga, b, gb, val.doubleValue());
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07.08.2001 00:44:13)
	 * @return double
	 * @param a double
	 * @param b double
	 */
	private double interpolate(double a, double ga, double b, double gb, double val) {

		return ((gb - ga) / (b - a)) * (val - a) + ga;
	}
}