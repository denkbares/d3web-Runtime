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

package de.d3web.empiricalTesting;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;


/**
 * A finding is a tuple of a {@link Question} and an {@link Answer}.
 * @author joba
 *
 */
public class Finding implements Comparable<Finding> {

	private Question question;
	private Value value;

	/**
	 * Constructs a new Finding by searching.
	 * @param question Underlying question
	 * @param answerIDorText Text or ID of the searched answer.
	 * @throws Exception
	 * @return new Finding consisting of committed question and searched answer.
	 */
	public Finding(QuestionChoice question, String answerIDorText) throws Exception {
		Choice foundAnswer = null;
		for (Choice ac : question.getAllAlternatives()) {
			if (answerIDorText.equals(ac.getId())
					|| answerIDorText.equals(ac.getName()))
				foundAnswer = ac;
		}
		if (foundAnswer == null)
			throw new Exception("Answer not found for ID/Text: "
					+ answerIDorText);
		else {
			setup(question, new ChoiceValue(foundAnswer));
		}
	}
	
	/**
	 * Constructs a new Finding by searching the KnowledgeBase
	 * for numeric Answer with committed value.
	 * @param question the question
	 * @param value value of the numeric Answer
	 * @throws Exception
	 * @return new Finding consisting of committed question and searched answer.
	 */
	public Finding(QuestionNum question, String value) throws Exception {
		setup(question, new NumValue(Double.parseDouble(value)));
	}

	/**
	 * Creates new Finding with committed question and answer.
	 * @param question The committed question
	 * @param answer The committed answer
	 * @return new Finding consisting of committed question and answer
	 */
	public Finding(Question question, Value value) {
		setup(question, value);
	}


	/**
	 * Creates a new {@link Finding} based on the {@link QuestionChoice}
	 * contained in the specified {@link KnowledgeBase} with
	 * the specified questionName and the specified answerName.
	 * @param questionName the specified questionName
	 * @param answerName the specified answerName
	 * @return a created Finding based on the specified names
	 * @throws Exception when null delivered in one of the arguments
	 *         or inappropriate Question type used
	 */
	public static Finding createFinding(KnowledgeBase k, String questionName, String answerName) throws Exception  {
		if (k == null || questionName == null || answerName == null)
			throw new Exception("Null delivered as argument.");
		for (Question question : k.getQuestions()) {
			if (questionName.equals(question.getName())) {
				if (question instanceof QuestionChoice) {
					return new Finding((QuestionChoice)question, answerName);
				} else if (question instanceof QuestionNum) {
					return new Finding((QuestionNum)question, answerName);
				} else {
					throw new Exception("Inappropriate question type.");
				}
			}
		}
		throw new Exception("Question not found.");
	}
	
	
	private void setup(Question question, Value value) {
		this.question = question;
		this.value = value;
	}

	/**
	 * Returns String representation of this Finding. question = value
	 * 
	 * @return String representation of this Finding.
	 */
	@Override
	public String toString() {
		return question + " = " + value;
	}

	/**
	 * Returns value of this Finding
	 * 
	 * @return value
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * Sets value of this Finding to v
	 * 
	 * @param v
	 *            the value
	 */
	public void setValue(Value v) {
		this.value = v;
	}

	/**
	 * Returns question of this finding
	 * @return question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets question of this finding to q
	 * @param q question
	 */
	public void setQuestion(Question q) {
		this.question = q;
	}

	@Override
	public int compareTo(Finding o) {
		int comp = question.getId().compareTo(o.getQuestion().getId());
		if (comp != 0)
			return comp;
		comp = question.getName().compareTo(o.getQuestion().getName());
		if (comp != 0)
			return comp;
		comp = value.compareTo(o.value);
		return comp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Finding))
			return false;
		Finding other = (Finding) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		return true;
	}

}
