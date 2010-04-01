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

package de.d3web.shared;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;

public abstract class AbstractAbnormality implements KnowledgeSlice {

	public static double A0 = 0;
	public static double A1 = 0.0625;
	public static double A2 = 0.125;
	public static double A3 = 0.25;
	public static double A4 = 0.5;
	public static double A5 = 1;

	public static double convertConstantStringToValue(String c) {
		if (c.equalsIgnoreCase("A0")) {
			return A0;
		} else if (c.equalsIgnoreCase("A1")) {
			return A1;
		} else if (c.equalsIgnoreCase("A2")) {
			return A2;
		} else if (c.equalsIgnoreCase("A3")) {
			return A3;
		} else if (c.equalsIgnoreCase("A4")) {
			return A4;
		} else if (c.equalsIgnoreCase("A5")) {
			return A5;
		} else return A0;
	}

	public static String convertValueToConstantString(double value) {
		if (value < A1) {
			return "A0";
		} else if (value < A2) {
			return "A1";
		} else if (value < A3) {
			return "A2";
		} else if (value < A4) {
			return "A3";
		} else if (value < A5) {
			return "A4";
		} else return "A5";
	}

	private Question question = null;

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which contains answerValue, A0 if answerValue is not contained in any AbnormalityInterval
	 */
	public abstract double getValue(Answer ans);

	/**
	 * Creation date: (06.08.2001 16:00:34)
	 * @return de.d3web.kernel.domainModel.Question
	 */
	public de.d3web.core.knowledge.terminology.Question getQuestion() {
		return question;
	}

	/**
	 * sets the question and adds this object as KnowledgeSlice
	 * to the Question
	 * Creation date: (06.08.2001 16:00:34)
	 * @param newQuestion de.d3web.kernel.domainModel.Question
	 */
	public void setQuestion(de.d3web.core.knowledge.terminology.Question newQuestion) {
		if (question != null) {
			question.removeKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_ABNORMALITY);
		}
		
		question = newQuestion;

		if (newQuestion != null) {
			question.addKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_ABNORMALITY);
		}
	}

	/**
	 * @see de.d3web.core.inference.KnowledgeSlice#getId()
	 */
	public java.lang.String getId() {
		if (question != null) {
			return "A_" + question.getId();
		} else return null;
	}

	/**
	 * @see de.d3web.core.inference.KnowledgeSlice#getProblemsolverContext()
	 */
	public java.lang.Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodShared.class;
	}

	/**
	 * @see de.d3web.core.inference.KnowledgeSlice#isUsed(de.d3web.core.session.XPSCase)
	 */
	public boolean isUsed(de.d3web.core.session.XPSCase theCase) {
		return true;
	}

	public void remove() {
		setQuestion(null);
	}
	
}
