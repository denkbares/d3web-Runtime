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
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
/**
 * Insert the type's description here.
 * Creation date: (02.08.2001 16:07:44)
 * @author: Norman Brümmer
 */
public class QuestionComparatorYN extends QuestionComparator {

	/**
	 * Insert the method's description here.
	 * Creation date: (02.08.2001 18:04:08)
	 */
	public QuestionComparatorYN() {
	}

	public double compare(List ans1, List ans2) {
		try {

			boolean isNo1 = (ans1.get(0) instanceof AnswerNo);
			boolean isNo2 = (ans2.get(0) instanceof AnswerNo);

			return (isNo1 == isNo2) ? 1 : 0;

		} catch (Exception x) {
			System.err.println("Exception while comparing (YN): " + x);
			return 0;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 14:07:38)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorYN'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();

	}
}