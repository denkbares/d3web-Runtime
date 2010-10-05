/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.core.knowledge.terminology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;
import de.d3web.core.manage.KnowledgeBaseManagement;

/**
 * Storage for Questions with predefined answers (alternatives). Abstract
 * because you can choose from multiple/single choices (answers).<BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public abstract class QuestionChoice extends Question {

	private List<Choice> alternatives = new LinkedList<Choice>();

	public QuestionChoice(String id) {
		super(id);
	}

	/**
	 * Gives you all the answers (alternatives) and does not care about any
	 * rules which could possibly suppress an answer.
	 * 
	 * @param session currentCase
	 * @return a List of all alternatives that are not suppressed by any
	 *         RuleSuppress
	 **/
	public List<Choice> getAllAlternatives() {
		return alternatives;
	}

	/**
	 * @deprecated Use KnowledgeBaseManagement.findChoice(...)
	 */
	@Deprecated
	public Choice findChoice(String choiceID) {
		return KnowledgeBaseManagement.createInstance(getKnowledgeBase()).findChoice(this, choiceID);
	}

	/**
	 * sets the answer alternatives from which a user or rule can choose one or
	 * more to answer this question.
	 */
	public void setAlternatives(List<Choice> alternatives) {
		if (alternatives != null) {
			this.alternatives = alternatives;
			Iterator<Choice> iter = this.alternatives.iterator();
			while (iter.hasNext()) {
				iter.next().setQuestion(this);
			}
		}
		else {
			setAlternatives(new LinkedList<Choice>());
		}

	}

	public void addAlternative(Choice answer) {
		if ((answer != null) && (!getAllAlternatives().contains(answer))) {
			alternatives.add(answer);
			answer.setQuestion(this);
		}
	}

	public void addAlternative(Choice answer, int pos) {
		if ((answer != null) && (!getAllAlternatives().contains(answer))) {
			alternatives.add(pos, answer);
			answer.setQuestion(this);
		}
	}

	public boolean removeAlternative(Choice answer) {
		answer.setQuestion(null);
		return alternatives.remove(answer);
	}

	/**
	 * @return the Num2ChoiceSchema that has been set to this question, null, if
	 *         no such schema exists.
	 */
	public Num2ChoiceSchema getSchemaForQuestion() {
		KnowledgeSlice schemaCol =
				getKnowledge(PSMethodAbstraction.class, PSMethodAbstraction.NUM2CHOICE_SCHEMA);
		if (schemaCol != null) {
			return (Num2ChoiceSchema) schemaCol;
		}
		else {
			return null;
		}
	}
}