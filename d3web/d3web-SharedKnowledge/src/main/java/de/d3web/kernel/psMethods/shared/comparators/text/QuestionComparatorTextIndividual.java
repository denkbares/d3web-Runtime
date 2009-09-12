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

package de.d3web.kernel.psMethods.shared.comparators.text;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;
/**
 * Insert the type's description here.
 * Creation date: (27.02.2002 13:46:53)
 * @author: Norman Brümmer
 */
public class QuestionComparatorTextIndividual
	extends de.d3web.kernel.psMethods.shared.comparators.QuestionComparator
	implements IndividualComparator {

	public double compare(List answers1, List answers2) {
		try {
			AnswerText ans1 = (AnswerText) answers1.get(0);
			AnswerText ans2 = (AnswerText) answers2.get(0);

			if (ans1.getValue(null).equals(ans2.getValue(null))) {
				return 1;
			}
		} catch (Exception x) {
			return 0;
		}
		return 0;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (27.02.2002 13:46:53)
	 * @return java.lang.String
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorTextIndividual'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}