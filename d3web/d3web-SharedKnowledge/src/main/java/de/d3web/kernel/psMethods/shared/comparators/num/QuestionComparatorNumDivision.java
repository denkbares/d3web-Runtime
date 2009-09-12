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

import de.d3web.kernel.domainModel.answers.AnswerNum;

/**
 * Creation date: (07.08.2001 02:02:18)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumDivision extends QuestionComparatorNum {

	public double compare(List ans1, List ans2) {

		try {

			double x1 = ((Double) ((AnswerNum) ans1.get(0)).getValue(null)).doubleValue();
			double x2 = ((Double) ((AnswerNum) ans2.get(0)).getValue(null)).doubleValue();

			double max = Math.max(x1, x2);
			double min = Math.min(x1, x2);

			if (max != 0) {
				double quotient = min / max;
				if (quotient < 0) {
					// one of the numbers was negative!
					return 0;
				} else if(quotient > 1) {
					// both of the numbers were negative!
					return max / min;					
				} else
					return quotient;
			} else {
				return (min == 0) ? 1 : 0;
			}
		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}

	}

	/**
	 * Creation date: (09.08.2001 18:07:13)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorNumDivision'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}