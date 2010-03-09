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

package de.d3web.core.knowledge.terminology.info;

import java.util.Collection;
import java.util.Iterator;

import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;

/**
 * A Num2ChoiceSchema is a knowledge slice of QuestionOC, which facilitates the
 * question to receive numerical values (AnswerNum) and to convert it to an
 * appropriate choice answer.
 * 
 * @author baumeister
 */
public class Num2ChoiceSchema extends IDObject implements KnowledgeSlice {

	private static final long serialVersionUID = -3081963703740657373L;
	private Double[] schemaArray;
	private Question question;


	public Num2ChoiceSchema(String id) {
		super(id);
	}
	
	public void setSchemaArray(Double[] newArray) {
		schemaArray = newArray;
	}

	public Double[] getSchemaArray() {
		return schemaArray;
	}

	/**
	 * @return PSMethodQuestionSetter.class
	 * @see de.d3web.core.inference.KnowledgeSlice#getProblemsolverContext()
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodQuestionSetter.class;
	}
	/**
	 * @return true by default, not used in this context.
	 * @see de.d3web.core.inference.KnowledgeSlice#isUsed(de.d3web.core.session.XPSCase)
	 */
	public boolean isUsed(XPSCase theCase) {
		return true;
	}

	/**
	 * @return the answer selected from the given answer collection according to
	 *         the given numeric value
	 */
	public Answer getAnswerForNum(Double num, Collection<AnswerChoice> answers, XPSCase theCase) {
		boolean ascending = isAscending();
		for (int i = 0; i < schemaArray.length; i++) {
			if ((ascending && num.doubleValue() < schemaArray[i].doubleValue())
					|| (!ascending && num.doubleValue() > schemaArray[i].doubleValue())) {
				return nth(answers, i);
			}
		}
		return nth(answers, answers.size() - 1);
	}

	protected boolean isAscending() {
		if (schemaArray.length > 1) {
			// enough to check first values, because KnowME will inform the user
			// if schema array is neither ascending nor descending.
			return schemaArray[0] < schemaArray[1];
		}
		return true;
	}

	protected Answer nth(Collection<AnswerChoice> answers, int pos) {
		Iterator<AnswerChoice> iter = answers.iterator();
		int position = 0;
		while (iter.hasNext()) {
			Answer element = iter.next();
			if (position == pos) {
				return element;
			}
			position++;
		}
		return null;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void remove() {
		question.removeKnowledge(getProblemsolverContext(), this, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
	}

}
