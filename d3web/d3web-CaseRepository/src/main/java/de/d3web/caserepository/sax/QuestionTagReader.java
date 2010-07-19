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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.core.knowledge.terminology.AnswerNo;
import de.d3web.core.knowledge.terminology.AnswerYes;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * @author bates
 */
public class QuestionTagReader extends AbstractTagReader {

	private static AbstractTagReader instance = null;

	private Question currentQuestion = null;
	private Value currentValue = null;
	private Value finalQuestionValue = null;

	private List<String> tagNames = null;

	private QuestionTagReader(String id) {
		super(id);
		tagNames = new ArrayList<String>(3);
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

	@Override
	public List<String> getTagNames() {
		return tagNames;
	}

	@Override
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("Question")) {
			startQuestion(attributes);
		} else if (qName.equals("Answer")) {
			startAnswer(attributes);
		}
	}

	@Override
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
		currentQuestion = getKnowledgeBase().searchQuestion(id);
		finalQuestionValue = UndefinedValue.getInstance();
	}

	private void endQuestion() {
		// avoid a NullPointerException in the case that the question doesn't
		// exist in the knowledgebase
		if (currentQuestion != null)
			getCaseObject().addQuestionAndAnswers(currentQuestion, finalQuestionValue);

		currentQuestion = null;
		finalQuestionValue = null;
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

			Choice c = KnowledgeBaseManagement.createInstance(getKnowledgeBase()).findChoice((QuestionChoice) currentQuestion, id);
			currentValue = new ChoiceValue(c);
				

			// [MISC]:marty: legacy code, i.e. ultra downward compatibility for
			// QuestionYN
			if (currentValue == null && currentQuestion instanceof QuestionYN) {
				if ("MaYES".equals(id)) {
					AnswerYes yes = null;
					Iterator aiter = ((QuestionYN) currentQuestion).getAllAlternatives().iterator();
					while (aiter.hasNext()) {
						Object o = aiter.next();
						if (o instanceof AnswerYes)
							yes = (AnswerYes) o;
					}
					if (yes != null)
						currentValue = new ChoiceValue(yes);
				} else if ("MaNO".equals(id)) {
					AnswerNo no = null;
					Iterator aiter = ((QuestionYN) currentQuestion).getAllAlternatives().iterator();
					while (aiter.hasNext()) {
						Object o = aiter.next();
						if (o instanceof AnswerNo)
							no = (AnswerNo) o;
					}
					if (no != null)
						currentValue = new ChoiceValue(no);
				}
			}
		} else if (currentQuestion instanceof QuestionNum) {
			currentValue = new NumValue(Double.valueOf(value));

		} else if (currentQuestion instanceof QuestionText // [MISC]:aha:legacy
														   // code
				&& value != null) {
			currentValue = new TextValue(value);
		} else if (currentQuestion instanceof QuestionDate) {
			String date = attributes.getValue("date");
			if (date == null || "".equals(date)) {
				date = value;
			}
			currentValue = new DateValue(new Date(date));
		}
	}

	private void endAnswer() {
		if (currentQuestion == null)
			return;

		if (currentQuestion instanceof QuestionText && !"".equals(getTextBetweenCurrentTag())) {
			finalQuestionValue = new TextValue(getTextBetweenCurrentTag());
		}
		else if (currentValue != null) {
			finalQuestionValue = currentValue;
		} else {
			Logger.getLogger(this.getClass().getName()).warning(
					"QuestionTagReader: not adding <null> Answer to Question "
							+ currentQuestion.getId());
		}
		currentValue = null;
	}

	private void endUnknownAnswer() {
		if (currentQuestion == null)
			return;
		finalQuestionValue = Unknown.getInstance();
	}

}