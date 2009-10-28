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

package de.d3web.empiricalTesting.caseVisualization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.MQDialogController;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;

public class BotHelper {

	private static BotHelper instance;

	private Map<String, AnswerChoice> answerHash;

	private BotHelper() {
		answerHash = new HashMap<String, AnswerChoice>();
	}

	public static BotHelper getInstance() {
		if (instance == null)
			instance = new BotHelper();
		return instance;
	}

	public void setCaseValue(XPSCase theCase, String questionID, String answerID)
			throws Exception {
		QuestionChoice q = (QuestionChoice) theCase.getKnowledgeBase()
				.searchQuestions(questionID);
		if (answerID != null) {
			AnswerChoice a = findAnswer(q, answerID);
			setCaseValue(theCase, q, a);
		}
	}

	public void setCaseValue(XPSCase theCase, QuestionChoice q, AnswerChoice a)
			throws Exception {
		theCase.setValue(q, new Object[] { a });

	}

	public AnswerChoice findAnswer(QuestionChoice q, String answerId)
			throws Exception {
		for (AnswerChoice answer : q.getAllAlternatives()) {
			if (answer.getId().equalsIgnoreCase(answerId)
					|| answer.getText().equalsIgnoreCase(answerId))
				return answer;
		}
		throw new Exception("Not found id [" + answerId + "] for question ["
				+ q + "][" + q.getId() + "]");
	}

	public QuestionChoice getNextQuestion(XPSCase theCase) throws Exception {
		MQDialogController controller = (MQDialogController) theCase
				.getQASetManager();
		QASet next = controller.moveToNextRemainingQASet();
		if (next != null && next instanceof QuestionChoice) {
			return (QuestionChoice) next;
		} else if (next != null) {
			List<QASet> validQuestions = controller
					.getAllValidQuestionsOf((QContainer) next);
			return (QuestionChoice) validQuestions.get(0);
		} else {
			return null;
		}
	}

	public String pretty(String text) {
		if (text.isEmpty())
			return "";
		text = text.replaceAll("<", "kleiner");
		text = text.replaceAll(">", "groesser");
		return text;
	}

	public String veryPretty(String text) {
		String t = pretty(text);
		if (t.indexOf("?") > -1)
			t = t.replaceAll("\\?", "");
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
		for (Question q : k.getQuestions()) {
			if (q instanceof QuestionChoice) {
				for (AnswerChoice a : ((QuestionChoice) q).getAllAlternatives()) {
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
			KnowledgeBase kb) throws Exception {
		Question foundQuestion = null;
		for (Question q : kb.getQuestions()) {
			if (questionIDorText.equals(q.getId())
					|| questionIDorText.equals(q.getText()))
				foundQuestion = q;
		}
		if (foundQuestion == null)
			throw new Exception("Question not found for ID/Text: "
					+ questionIDorText);
		else
			return foundQuestion;
	}

	public Diagnosis getDiagnosisByIDorText(String diagnosisIDorText,
			KnowledgeBase kb) throws Exception {
		Diagnosis foundDiagnosis = null;
		for (Diagnosis d : kb.getDiagnoses()) {
			if (diagnosisIDorText.equals(d.getId())
					|| diagnosisIDorText.equals(d.getText()))
				foundDiagnosis = d;
		}
		if (foundDiagnosis == null)
			throw new Exception("Diagnosis not found for ID/Text: "
					+ diagnosisIDorText);
		else
			return foundDiagnosis;
	}
}
