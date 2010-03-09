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

package de.d3web.core.inference.condition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerUnknown;
/**
 * This condition checks, whether a specified value is assigned to a question 
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author Christian Betz
 */
public class CondEqual extends CondQuestion {
	
	private static final long serialVersionUID = -974336518850100085L;
	private List<Answer> values;

	/**
	 * Creates a new equal-condition constraining a specified 
	 * question to the unknown answer. 
	 * @param question the specified question to check
	 * @param value the UnknownAnswer that have to be fulfilled
	 */
	public CondEqual(Question question, AnswerUnknown value) {
		super(question);
		values = new ArrayList<Answer>(1);
		values.add(value);
	}

	/**
	 * Creates a new equal-condition constraining a specified 
	 * question to a specified list of answers. 
	 * @param question the question to check
	 * @param values the values the question needs to be assigned to 
	 */
	public CondEqual(Question question, List<Answer> values) {
		super(question);
		this.values = values;
	}

	/**
	 * Creates a new equal-condition constraining a specified 
	 * question to a specified answer. 
	 * @param question the question to check
	 * @param value the value the question needs to have to fulfill the condition
	 */
	public CondEqual(QuestionChoice question, Answer value) {
		super(question);
		values = new ArrayList<Answer>(1);
		values.add(value);
	}
	
	/**
	 * Creates a new equal-condition constraining a specified 
	 * question to a specified answer. 
	 * @param question the question to check
	 * @param value the value the question needs to have to fulfill the condition
	 */
	public CondEqual(Question question, Answer value) {
		super(question);
		values = new ArrayList<Answer>(1);
		values.add(value);
	}
	
	/**
	 * Creates a new equal-condition, where {@link AnswerUnknown} needs to be assigned
	 * to the question.<br/>
	 * THIS METHOD ONLY EXISTS TO SOLVE THE AMBIGUITY<br/>
	 * @param question the question to check
	 * @param value the UnknownAnswer that have to be fulfilled
	 */
	public CondEqual(QuestionChoice question, AnswerUnknown value) {
		super(question);
		values = new ArrayList<Answer>(1);
		values.add(value);
	}

	@Override
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		List<?> value = question.getValue(theCase);
		return value.containsAll(values);
	}

	private String getId(Object answer) {
		if (answer instanceof AnswerChoice)
			return ((AnswerChoice) answer).getId();
		else if (answer instanceof AnswerUnknown)
			return ((AnswerUnknown) answer).getId();
		else {
			Logger.getLogger(this.getClass().getName()).warning(
				"Could not convert "
					+ answer
					+ ". Check "
					+ this.getClass()
					+ ".getId(Object)!");
			return answer.toString();
		}
	}

	/**
	 * Returns the values that have to be assigned to the question
	 * to fulfill the condition.
	 * @return the constrained values of this condition 
	 */
	public List<Answer> getValues() {
		return values;
	}

	/**
	 * Sets the values that have to be assigned to the question
	 * to fulfill the condition.
	 * @param newValues the constrained values of this condition 
	 */
	public void setValues(List<Answer> newValues) {
		values = newValues;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondEqual question: "
		+ question.getId()
		+ " value: ";
		for (Object o: values) {
			ret += getId(o)+",";
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
		
		if (this.getValues() != null && ((CondEqual)other).getValues() != null)
			return this.getValues().containsAll(((CondEqual)other).getValues()) && ((CondEqual)other).getValues().containsAll((this).getValues());
		else return this.getValues() == ((CondEqual)other).getValues();
	}

	@Override
	public AbstractCondition copy() {
		return new CondEqual(getQuestion(), getValues());
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode()*31;
		for (Object o : this.getValues()) {
			// hash code ignores order of answers (according to equals)
			hash += o.hashCode();
		}
		return hash;
	}
}