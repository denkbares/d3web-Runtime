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

package de.d3web.caserepository.sax;

import java.util.*;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.*;
import de.d3web.kernel.domainModel.qasets.*;

/**
 * @author bates
 */
public class QuestionTagReader extends AbstractTagReader {

	private static AbstractTagReader instance = null;

	private Question currentQuestion = null;
	private Answer currentAnswer = null;
	private Set currentAnswers = null;

	private List tagNames = null;

	private QuestionTagReader(String id) {
		super(id);
		tagNames = new LinkedList();
		tagNames.add("Question");
		tagNames.add("Answer");
		tagNames.add("UnknownAnswer");
	}

	public static AbstractTagReader getInstance() {
		if (instance == null) {
			instance = new QuestionTagReader("Question");
		}
		return instance;
	}

	public List getTagNames() {
		return tagNames;
	}

	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("Question")) {
			startQuestion(attributes);
		} else if (qName.equals("Answer")) {
			startAnswer(attributes);
		}
	}

	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("Question")) {
			endQuestion();
		} else if (qName.equals("Answer")) {
			endAnswer();
		} else if (qName.equals("UnknownAnswer")) {
			endUnknownAnswer();
		}
	}

	private void startQuestion(Attributes attributes) {
		String id = checkAttribute("id", attributes.getValue("id"), "<not set>");
		currentQuestion = getKnowledgeBase().searchQuestions(id);
		currentAnswers = new HashSet(2);
	}

	private void endQuestion() {
		// avoid a NullPointerException in the case that the question doesn't
		// exist in the knowledgebase
		if (currentQuestion != null)
			getCaseObject().addQuestionAndAnswers(currentQuestion, currentAnswers);

		currentQuestion = null;
		currentAnswers = null;
	}

	private void startAnswer(Attributes attributes) {
		if (currentQuestion == null)
			return;

		String value = attributes.getValue("value");

		if (currentQuestion instanceof QuestionChoice) {

			// [MISC]:aha:legacy code
			String _id = attributes.getValue("id");
			String id = _id;
			if (id == null || "".equals(id))
				id = value;

			currentAnswer = ((QuestionChoice) currentQuestion).getAnswer(null, id);

			// [MISC]:marty: legacy code, i.e. ultra downward compatibility for
			// QuestionYN
			if (currentAnswer == null && currentQuestion instanceof QuestionYN) {
				if ("MaYES".equals(id)) {
					AnswerYes yes = null;
					Iterator aiter = ((QuestionYN) currentQuestion).getAllAlternatives().iterator();
					while (aiter.hasNext()) {
						Object o = aiter.next();
						if (o instanceof AnswerYes)
							yes = (AnswerYes) o;
					}
					if (yes != null)
						currentAnswer = yes;
				} else if ("MaNO".equals(id)) {
					AnswerNo no = null;
					Iterator aiter = ((QuestionYN) currentQuestion).getAllAlternatives().iterator();
					while (aiter.hasNext()) {
						Object o = aiter.next();
						if (o instanceof AnswerNo)
							no = (AnswerNo) o;
					}
					if (no != null)
						currentAnswer = no;
				}
			}
		} else if (currentQuestion instanceof QuestionNum) {
			currentAnswer = ((QuestionNum) currentQuestion).getAnswer(null, Double.valueOf(value));

		} else if (currentQuestion instanceof QuestionText // [MISC]:aha:legacy
														   // code
				&& value != null) {
			currentAnswer = ((QuestionText) currentQuestion).getAnswer(null, value);
		} else if (currentQuestion instanceof QuestionDate) {
			String date = attributes.getValue("date");
			if (date == null || "".equals(date)) {
				date = value;
			}
			currentAnswer = ((QuestionDate) currentQuestion).getAnswer(null, date);
		}
	}

	private void endAnswer() {
		if (currentQuestion == null)
			return;

		if (currentQuestion instanceof QuestionText && !"".equals(getTextBetweenCurrentTag())) {
			Answer a = ((QuestionText) currentQuestion).getAnswer(null, getTextBetweenCurrentTag());
			currentAnswers.add(a);
		} else if (currentAnswer != null) {
			currentAnswers.add(currentAnswer);
		} else {
			Logger.getLogger(this.getClass().getName()).warning(
					"QuestionTagReader: not adding <null> Answer to Question "
							+ currentQuestion.getId());
		}
		currentAnswer = null;
	}

	private void endUnknownAnswer() {
		if (currentQuestion == null)
			return;

		AnswerUnknown unknown = currentQuestion.getUnknownAlternative();
		if (unknown != null)
			currentAnswers.add(unknown);
		else
			Logger.getLogger(this.getClass().getName()).warning(
					"QuestionTagReader: not adding AnswerUnknown because Question" + " ("
							+ currentQuestion.getId() + ") has no unknown answer");
	}

}