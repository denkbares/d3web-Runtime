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

package de.d3web.kernel.domainModel.formula;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
/**
 * FormulaElement term that can count the answers of a QuestionMC
 * Creation date: (14.08.2000 16:33:00)
 * @author Christian
 */
public class Count implements FormulaNumberElement {

	private QuestionMC questionMC = null;

	/** 
	 * Creates a new Count with null-question.
	 */
	public Count() {
		this(null);
	}	
	
	/**
	 *	Creates a new Count object that counts the answers of questionMC
	 **/
	public Count(QuestionMC questionMC) {
		this.questionMC = questionMC;
	}

	public Collection getTerminalObjects() {
		Collection ret = new LinkedList();
		ret.add(questionMC);

		return ret;
	}

	/**
	 * @return the number of active alternatives for a multiple-choice answer,
	 * 0, if the active answer is "No" or "unknown".
	 */
	public Double eval(XPSCase theCase) {
		double count = 0;
		Iterator iter = getQuestionMC().getValue(theCase).iterator();
		while (iter.hasNext()) {
			AnswerChoice answer = (AnswerChoice) iter.next();
			if ((answer.isAnswerNo() || answer.isUnknown())) {
				return new Double(0);
			}
			count++;
		}
		return new Double(count);
	}

	private QuestionMC getQuestionMC() {
		return questionMC;
	}

	/**
	 * Creation date: (20.06.2001 15:34:57)
	 * @return the XML-representation of this Count object
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Count>\n");
		sb.append("<QuestionMC>" + getQuestionMC().getId() + "</QuestionMC>\n");
		sb.append("</Count>\n");
		return sb.toString();
	}

	public String toString() {
		return "#" + (getQuestionMC() != null ?  " "+getQuestionMC().toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}