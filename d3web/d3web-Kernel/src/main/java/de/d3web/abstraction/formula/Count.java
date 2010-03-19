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

package de.d3web.abstraction.formula;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
/**
 * FormulaElement term that can count the answers of a QuestionMC
 * Creation date: (14.08.2000 16:33:00)
 * @author Christian
 */
public class Count implements FormulaNumberElement {

	private static final long serialVersionUID = 332901233837459047L;
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

	public Collection<Object> getTerminalObjects() {
		Collection<Object> ret = new LinkedList<Object>();
		ret.add(questionMC);

		return ret;
	}

	/**
	 * @return the number of active alternatives for a multiple-choice answer,
	 * 0, if the active answer is "No" or "unknown".
	 */
	public Double eval(XPSCase theCase) {
//		double count = 0;
		AnswerMultipleChoice answer = (AnswerMultipleChoice)(getQuestionMC().getValue(theCase));
		List<AnswerChoice> choices = answer.getChoices();
		
		// check, if AnswerNo oder AnswerUnknown is included
		for (AnswerChoice answerChoice : choices) {
			if (answerChoice.isAnswerNo() || answerChoice.isUnknown()) 
				return new Double(0);
		}
		return new Double(choices.size());
		
//		Iterator<?> iter = getQuestionMC().getValue(theCase).iterator();
//		while (iter.hasNext()) {
//			AnswerChoice answer = (AnswerChoice) iter.next();
//			if ((answer.isAnswerNo() || answer.isUnknown())) {
//				return new Double(0);
//			}
//			count++;
//		}
//		return new Double(count);
	}

	public QuestionMC getQuestionMC() {
		return questionMC;
	}

	/**
	 * Creation date: (20.06.2001 15:34:57)
	 * @return the XML-representation of this Count object
	 */
	public String getXMLString() {
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