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

package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.List;

import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.terminology.info.NumericalInterval;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;

/**
 * Creation date: (02.08.2001 16:07:34)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorNum extends QuestionComparator {

	private static final long serialVersionUID = -1992509726813677004L;

	public double compare(List<?> ans1, List<?> ans2) {
		Object o1 = convertToIntervalOrDouble(ans1);
		Object o2 = convertToIntervalOrDouble(ans2);

		if ((o1 == null) || (o2 == null)) {
			return 0;
		}
		if (o1 instanceof NumericalInterval) {
			if (o2 instanceof NumericalInterval) {
				return o1.equals(o2) ? 1 : 0;
			} else if (o2 instanceof Double) {
				return ((NumericalInterval) o1).contains(((Double) o2).doubleValue()) ? 1 : 0;
			}
		} else if (o1 instanceof Double) {
			if (o2 instanceof NumericalInterval) {
				return ((NumericalInterval) o2).contains(((Double) o1).doubleValue()) ? 1 : 0;
			} else if (o2 instanceof Double) {
				return o1.equals(o2) ? 1 : 0;
			}
		}
		return 0;
	}

	private Object convertToIntervalOrDouble(List<?> answers) {
		try {
			Object o = answers.get(0);
			if (o instanceof AnswerChoice) {
				return new Double(((AnswerChoice) o).getText());
			} else if (o instanceof AnswerNum) {
				return ((AnswerNum) o).getValue(null);
			} else {
				return o;
			}

		} catch (Exception e) {
			return null;
		}
	}
}