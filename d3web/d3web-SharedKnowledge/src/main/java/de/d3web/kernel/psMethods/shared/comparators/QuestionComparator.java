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

package de.d3web.kernel.psMethods.shared.comparators;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.shared.PSMethodShared;

/**
 * superclass of all question qomparators
 * Creation date: (02.08.2001 16:08:28)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparator implements KnowledgeSlice {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7940425720868244820L;

	private de.d3web.core.terminology.Question question = null;

	private double unknownSimilarity = -1;

	
	/**
	 * compare method wihthout ComparableQuestions. just needs the answer-arrays.
	 */
	public abstract double compare(List<?> answers1, List<?> answers2);

	/**
	 * @return java.lang.String
	 */
	public String getId() {
		return "QCOMP_" + question.getId();
	}

	/**
	 * Returns the class of the PSMethod in which this
	 * KnowledgeSlice makes sense.
	 * Creation date: (02.08.2001 16:27:48)
	 * @return java.lang.Class PSMethod class
	 */
	public Class<? extends PSMethodShared> getProblemsolverContext() {
		return PSMethodShared.class;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (02.08.2001 16:15:24)
	 * @return de.d3web.kernel.domainModel.Question
	 */
	public de.d3web.core.terminology.Question getQuestion() {
		return question;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (16.08.01 12:16:13)
	 * @return double
	 */
	public double getUnknownSimilarity() {
		return unknownSimilarity;
	}

	/**
	 * Has this knowledge already been used? (e.g. did a rule fire?)
	 */
	public boolean isUsed(de.d3web.core.session.XPSCase theCase) {
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (02.08.2001 16:15:24)
	 * @param newQuestion de.d3web.kernel.domainModel.Question
	 */
	public void setQuestion(de.d3web.core.terminology.Question newQuestion) {
		question = newQuestion;
		if (question != null) {
			question.addKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_SIMILARITY);
		} else {
			System.err.println("trying to set a null Question to QuestionComparator!");
			System.err.println("class: " + this.getClass());
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (16.08.01 12:16:13)
	 * @param newUnknownSimilarity double
	 */
	public void setUnknownSimilarity(double newUnknownSimilarity) {
		unknownSimilarity = newUnknownSimilarity;
	}
	
	public void remove() {
		question.removeKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_SIMILARITY);
	}
	
}