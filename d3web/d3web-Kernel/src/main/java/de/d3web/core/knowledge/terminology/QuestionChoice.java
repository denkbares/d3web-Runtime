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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;

/**
 * Storage for Questions with predefined answers (alternatives). Abstract
 * because you can choose from multiple/single choices (answers).<BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public abstract class QuestionChoice extends Question {

	public QuestionChoice(KnowledgeBase kb, String name) {
		super(kb, name);
	}

	public QuestionChoice(KnowledgeBase kb, String name, String... choices) {
		super(kb, name);
		for (String c : choices) {
			alternatives.add(new Choice(c));
		}
	}

	public QuestionChoice(KnowledgeBase kb, String name, Choice... choices) {
		super(kb, name);
		for (Choice c : choices) {
			alternatives.add(c);
		}
	}

	private final List<Choice> alternatives = new LinkedList<Choice>();

	/**
	 * Gives you all the answers (alternatives) and does not care about any
	 * rules which could possibly suppress an answer.
	 * 
	 * @return a List of all alternatives that are not suppressed by any
	 *         RuleSuppress
	 **/
	public List<Choice> getAllAlternatives() {
		return Collections.unmodifiableList(alternatives);
	}

	/**
	 * @deprecated Use KnowledgeBaseUtils.findChoice(...)
	 */
	@Deprecated
	public Choice findChoice(String choiceID) {
		return KnowledgeBaseUtils.findChoice(this, choiceID);
	}

	/**
	 * sets the answer alternatives from which a user or rule can choose one or
	 * more to answer this question.
	 */
	public void setAlternatives(List<Choice> newChoices) {
		this.alternatives.clear();
		if (newChoices != null) {
			for (Choice choice : newChoices) {
				choice.setQuestion(this);
				this.alternatives.add(choice);
			}
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
			alternatives.add(pos > alternatives.size() ? alternatives.size() : pos, answer);
			answer.setQuestion(this);
		}
	}

	public boolean removeAlternative(Choice answer) {
		return alternatives.remove(answer);
	}
}