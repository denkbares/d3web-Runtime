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

package de.d3web.empiricaltesting.casevisualization;

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;

public final class BotHelper {

	private static BotHelper instance;

	private final Map<String, Choice> answerHash;

	private BotHelper() {
		answerHash = new HashMap<String, Choice>();
	}

	public static BotHelper getInstance() {
		if (instance == null) instance = new BotHelper();
		return instance;
	}

	public void setCaseValue(Session session, String questionID, String answerID)
			throws Exception {
		QuestionChoice q = (QuestionChoice) session.getKnowledgeBase().getManager()
				.searchQuestion(questionID);
		if (answerID != null) {
			Choice a = findAnswer(q, answerID);
			setCaseValue(session, q, new ChoiceValue(a));
		}
	}

	public void setCaseValue(Session session, QuestionChoice q, ChoiceValue a) {
		Fact fact = FactFactory.createUserEnteredFact(q, a);

		session.getBlackboard().addValueFact(fact);
	}

	public Choice findAnswer(QuestionChoice q, String answerId)
			throws Exception {
		for (Choice answer : q.getAllAlternatives()) {
			if (answer.getId().equalsIgnoreCase(answerId)
					|| answer.getName().equalsIgnoreCase(answerId)) return answer;
		}
		throw new Exception("Not found id [" + answerId + "] for question ["
				+ q + "][" + q.getId() + "]");
	}

	public String pretty(String text) {
		if (text.isEmpty()) return "";
		text = text.replaceAll("<", "kleiner");
		text = text.replaceAll(">", "groesser");
		return text;
	}

	public String veryPretty(String text) {
		String t = pretty(text);
		if (t.indexOf('?') > -1) {
			t = t.replaceAll("\\?", "");
		}
		return t;
	}

	public String prettyAnswer(String answer) {
		return pretty(answer);
	}

	public String removeBadChars(String text) {
		String badChars = ": =()[]{}.?/\\-";
		for (int i = 0; i < badChars.length(); i++) {
			text = text.replace(badChars.charAt(i), '_');
			text = text.replace("_", "");
		}
		return text;
	}

	public void buildAnswerHashFor(KnowledgeBase k) {
		for (Question q : k.getManager().getQuestions()) {
			if (q instanceof QuestionChoice) {
				for (Choice a : ((QuestionChoice) q).getAllAlternatives()) {
					answerHash.put(a.getId(), a);
				}
			}
		}
	}

	public String prettyLabel(String label) {
		String l = pretty(label);
		l = l.replaceAll("-", "");
		l = l.replaceAll("\\?", "");
		l = l.replaceAll(" ", "_");
		l = l.replaceAll("u\\.\\/o\\.", "_uo_");
		l = l.replaceAll("\\(", "_");
		l = l.replaceAll("\\)", "_");
		l = l.replaceAll("\\.", "");
		l = l.replaceAll("\\+", "_");
		l = l.replaceAll("\\/", "_");
		l = l.replaceAll(",", "_");
		l = l.replaceAll("\\¡", "");
		l = l.replaceAll(":", "");
		return l;
	}

	public Question getQuestionByIDorText(String questionIDorText,
			String questionnaireText, KnowledgeBase kb) throws Exception {

		Question foundQuestion = null;

		if (questionnaireText == null || questionnaireText.equals("")) {
			for (TerminologyObject q : kb.getManager().getQuestions()) {
				if (questionIDorText.equals(q.getName())) foundQuestion = (Question) q;
			}
		}
		else {
			for (TerminologyObject q : kb.getManager().getQuestions()) {
				if (questionIDorText.equals(q.getName())
						&& checkQuestionnaire(q, questionnaireText)) foundQuestion = (Question) q;
			}
		}

		if (foundQuestion == null) throw new Exception("Question not found for ID/Text: "
				+ questionIDorText);
		else return foundQuestion;
	}

	public Solution getSolutionByIDorText(String diagnosisIDorText,
			KnowledgeBase kb) throws Exception {
		Solution foundDiagnosis = null;
		for (Solution d : kb.getManager().getSolutions()) {
			if (diagnosisIDorText.equals(d.getId())
					|| diagnosisIDorText.equals(d.getName())) foundDiagnosis = d;
		}
		if (foundDiagnosis == null) throw new Exception("Diagnosis not found for ID/Text: "
				+ diagnosisIDorText);
		else return foundDiagnosis;
	}

	private boolean checkQuestionnaire(TerminologyObject q, String questionnaireText) {
		TerminologyObject question = q;
		while (!(question.getParents()[0] instanceof QContainer)) {
			if (question.getParents()[0] instanceof Question) question = question.getParents()[0];
			else return false;
		}

		if (question.getParents()[0].getName().equals(questionnaireText)) return true;

		return false;
	}
}
