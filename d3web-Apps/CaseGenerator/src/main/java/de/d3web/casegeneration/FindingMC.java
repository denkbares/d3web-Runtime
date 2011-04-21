/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.casegeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;

/**
 * A FindingMC encapsulates a QuestionMC and an AnswerChoice[]. Each answer has
 * to be a valid alternative of the question.
 * 
 * @author Sebastian Furth
 * 
 */
public class FindingMC {

	private final QuestionMC question;
	private final Choice[] answers;

	/**
	 * Creates a new FindingMC with the committed question and answers
	 * 
	 * @param question Question
	 * @param answers Answer[]
	 */
	public FindingMC(QuestionMC question, Choice[] answers) {
		this.question = question;
		this.answers = Arrays.copyOf(answers, answers.length);
	}

	/**
	 * Returns a new FindingMC based on the {@link QuestionMC} contained in the
	 * specified {@link KnowledgeBase} with the specified questionName and the
	 * specified answerNames.
	 * 
	 * @param k KnowledgeBase
	 * @param questionName String the name of the QuestionMC
	 * @param answerNames String the name of the Answers
	 * @return FindingMC the created Finding
	 * @throws Exception when null delivered in one of the arguments,
	 *         inappropriate Question type used or if the question is not in the
	 *         KnowledgeBase.
	 */
	public static FindingMC createFindingMC(KnowledgeBase k, String questionName, String[] answerNames) throws Exception {

		if (k == null || questionName == null || answerNames == null) throw new IllegalArgumentException(
				"Null delivered as argument.");

		for (Question q : k.getManager().getQuestions()) {
			if (q.getName().equals(questionName)) {
				if (q instanceof QuestionMC) {
					List<Choice> answers = new ArrayList<Choice>();
					for (Choice answer : ((QuestionMC) q).getAllAlternatives()) {
						for (String s : answerNames) {
							if (answer.getName().equals(s)) {
								answers.add(answer);
							}
						}
					}
					return new FindingMC(
							(QuestionMC) q,
							answers.toArray(new Choice[answers.size()]));
				}
				else {
					throw new Exception("Inappropriate question type.");
				}
			}
		}
		throw new Exception("Question not found.");
	}

	/**
	 * Returns the Question of the FindingMC
	 * 
	 * @return Question the Question of this FindingMC
	 */
	public QuestionMC getQuestion() {
		return question;
	}

	/**
	 * Returns a <b>COPY</b> of the Answer[] of this FindingMC
	 * 
	 * @return Answer[] the answers
	 */
	public Choice[] getAnswers() {
		// We have to clone the array for security reasons
		return answers.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Choice a : answers) {
			result = prime * result + ((a == null) ? 0 : a.hashCode());
		}
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof FindingMC)) return false;
		FindingMC other = (FindingMC) obj;
		if (answers == null) if (other.answers != null) return false;
		else if (answers.length != other.answers.length) return false;
		else {
			for (int i = 0; i < answers.length; i++) {
				if (!answers[i].equals(other.answers[i])) return false;
			}
		}
		if (question == null) {
			if (other.question != null) return false;
		}
		else if (!question.equals(other.question)) return false;
		return true;
	}

	/**
	 * Returns String representation of this FindingMC. question = [answer1,
	 * answer2]
	 * 
	 * @return String representation of this FindingMC.
	 */
	@Override
	public String toString() {
		return question + " = " + answers;
	}

}
