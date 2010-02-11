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

package de.d3web.core.terminology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestionMC;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNum;

/**
 * Storage for Questions with predefined multiple answers (alternatives).
 * <BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public class QuestionMC extends QuestionChoice {

	/**
	 * 
	 */
	public QuestionMC() {
		super();
	}
	
	public QuestionMC(String id) {
		super(id);
	}

	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseQuestionMC(this);
	}

	public List<AnswerChoice> getAlternatives() {
		if (alternatives == null) {
			return new Vector<AnswerChoice>();
		} else
			return alternatives;
	}

	public List getValue(XPSCase theCase) {
		List values = ((CaseQuestionMC) theCase.getCaseObject(this)).getValue();
		if (values != null) {
			return values;
		} else {
			// System.err.println("Fehlerhafte initialisierung des Fall-Wertes von MC-Fragen");
			return new LinkedList();
		}
	}

	/**
	 * Sets the current values of this MC-question belonging to the
	 * specified XPSCase.<BR>
	 * <B>Caution:</B> It is possible to set numerical values to a MC-
	 * question. In this case, a Num2ChoiceSchema must be defined a KnowledgeSlice.
	 * @param theCase the belonging XPSCase
	 * @param antwort an array of Answer instances
	 */
	public void setValue(XPSCase theCase, Object[] values) {
		List newValues = new ArrayList(values.length);
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof AnswerNum) {
				values[i] = convertNumericalValue(theCase, (AnswerNum)values[i]);
			}
			newValues.add(values[i]);
		}
		((CaseQuestionMC) theCase.getCaseObject(this)).setValue(newValues);
		notifyListeners(theCase,this);
	}

	public String toString() {
		return super.toString();
	}
}
