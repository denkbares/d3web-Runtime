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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.log4j.Logger;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.core.utilities.Utils;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionPageLayout;
import de.d3web.dialog2.component.html.UIQuestionPage;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.InvalidAnswerError;
import de.d3web.dialog2.util.QuestionDateUtils;
import de.d3web.indication.inference.PSMethodUserSelected;

public class QuestionPageRenderer extends Renderer {

	public static Logger logger = Logger.getLogger(QuestionPageRenderer.class);

	private static void addValidationFailedMessage(UIComponent component, String id, String value,
			String facesMsg) {
		((UIQuestionPage) component).getErrorIDsToSubmittedValues().put(id, value);

		FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(facesMsg));
		FacesContext.getCurrentInstance().renderResponse();
	}

	private static Value getAnswer(UIComponent component, Session session, Question q, String idOrValue) {
		if (idOrValue.equals(Unknown.UNKNOWN_ID)) {
			return Unknown.getInstance();
		}
		else if (q instanceof QuestionChoice) {
			Choice choice = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase()).findChoice((QuestionChoice) q, idOrValue);
			return new ChoiceValue(choice);
		}
		else if (q instanceof QuestionText) {
			if (!idOrValue.equals("")) {
				return new TextValue(idOrValue);
			}
		}
		else if (q instanceof QuestionNum) {
			if (!idOrValue.equals("")) {
				try {
					Double ans = new Double(idOrValue);
					NumericalInterval interval = (NumericalInterval) q.getProperties().getProperty(
							Property.QUESTION_NUM_RANGE);
					if (interval != null) {
						if (!interval.contains(ans)) {
							addValidationFailedMessage(component, q.getId(), idOrValue,
									DialogUtils
									.getMessageWithParamsFor("error.wrongrange",
									new Object[] {
									idOrValue,
									interval.toString() }));
						}
						else {
							return new NumValue(ans);
						}
					}
					else {
						return new NumValue(ans);
					}
				}
				catch (NumberFormatException e) {
					logger.info(idOrValue + " is not a number.");
					addValidationFailedMessage(component, q.getId(), idOrValue,
							DialogUtils
							.getMessageWithParamsFor("error.no_number",
							new Object[] { idOrValue }));
				}
			}
		}
		else if (q instanceof QuestionDate) {
			InvalidAnswerError error = QuestionDateUtils.parseAnswerDate(idOrValue,
					(QuestionDate) q);
			if (error.getErrorType().equals(InvalidAnswerError.NO_ERROR)) {
				return new DateValue((Date) error.getAnswer());
			}
			else {
				if (error.getErrorType().equals(
						InvalidAnswerError.INVALID_DATEFORMAT_DATE)) {
					addValidationFailedMessage(
							component,
							q.getId(),
							idOrValue,
							DialogUtils
							.getMessageWithParamsFor(
							"error.invalid_dateformat_date",
							new Object[] {
							idOrValue,
							DialogUtils.getMessageFor("questiondate.date_format") }));
				}
				else if (error.getErrorType().equals(
						InvalidAnswerError.INVALID_DATEFORMAT_TIME)) {
					addValidationFailedMessage(
							component,
							q.getId(),
							idOrValue,
							DialogUtils
							.getMessageWithParamsFor(
							"error.invalid_dateformat_time",
							new Object[] {
							idOrValue,
							DialogUtils.getMessageFor("questiondate.time_format") }));
				}
				else if (error.getErrorType().equals(
						InvalidAnswerError.INVALID_DATEFORMAT_FULL)) {
					addValidationFailedMessage(
							component,
							q.getId(),
							idOrValue,
							DialogUtils
							.getMessageWithParamsFor(
							"error.invalid_dateformat_full",
							new Object[] {
							idOrValue,
							DialogUtils.getMessageFor("questiondate.full_format") }));
				}
			}
		}
		return null;
	}

	private void answerQuestion(UIComponent component, Object[] answerids, Session session, Question q) {
		if (q == null || answerids == null || answerids.length == 0) {
			return;
		}
		List<Object> answeridList = Utils.createList(answerids);
		Value value = null;
		if (answeridList.contains(Unknown.UNKNOWN_ID)) {
			value = Unknown.getInstance();
		}
		// if QuestionMC -> check the bad answer-combinations
		else if (q instanceof QuestionMC) {
			// get badanswerLists and add an error if a bad answer
			// combination is found
			List<List<String>> badanswersLists = (List<List<String>>) q.getProperties().getProperty(
					Property.MC_CONSTRAINTS);
			if (badanswersLists != null && badanswersLists.size() > 0) {
				for (List<String> badanswersList : badanswersLists) {
					if (QuestionsRendererUtils.currentAnswersAreBad(answeridList,
							badanswersList)) {
						addValidationFailedMessage(component, q.getId(), null,
								DialogUtils
								.getMessageWithParamsFor(
								"error.badmccombination",
								new Object[] { getAnswerNameListFromIDList(
								badanswersList, q,
								session) }));
						return;
					}
				}

			}
			List<ChoiceValue> choices = new ArrayList<ChoiceValue>(answeridList.size());
			Iterator<Object> iter = answeridList.iterator();
			while (iter.hasNext()) {
				ChoiceValue v = (ChoiceValue) (getAnswer(component, session, q,
						iter.next().toString()));
				choices.add(v);
			}
			value = new MultipleChoiceValue(choices);
		}
		// OC and num and date and text
		else {
			value = getAnswer(component, session, q, answeridList.get(0).toString());
		}
		if ((value != null)) {
			setValueInCase(session, q, value);
		}
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		Session session = DialogUtils.getDialog().getSession();
		List<Question> qList = DialogUtils.getQuestionPageBean().getQuestionListToRender();

		Map<String, String[]> requestMap = context.getExternalContext().getRequestParameterValuesMap();

		String unknownString = "false";
		String[] unkField = requestMap.get("setunknownhiddenfield");
		if (unkField != null && unkField.length > 0) {
			unknownString = unkField[0];
		}

		// get the Question that was answered last
		// Question lastAnsweredQuestion = null;
		// String answerId = LastClickedAnswer.getInstance()
		// .getLastClickedAnswerID(session.getId());
		// for (Question aQuestion : qList) {
		// if (aQuestion instanceof QuestionChoice) {
		// for (Answer anAnswer : ((QuestionChoice) aQuestion)
		// .getAllAlternatives()) {
		// if (anAnswer.getId().equals(answerId)) {
		// lastAnsweredQuestion = aQuestion;
		// break;
		// }
		// }
		// }
		// }

		// Old solution was updating all Questions from html:
		for (Iterator<Question> iter = qList.iterator(); iter.hasNext();) {
			Question q = iter.next();
			if (q != null && !isAbstractQuestion(q)) {
				// answer question / update the case
				if (requestMap.containsKey(q.getId())
						&& !hasEmptyString(requestMap.get(q.getId()))) {
					answerQuestion(component, requestMap.get(q.getId()), session, q);
				}
				else {
					// if "set all unknown" button was clicked, all questions
					// which are valid and not yet answered are set to "unknown"
					if (unknownString.equals("true")
							&& UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(q))
							&& DialogUtils.isValidQASet(q, session)) { //q.isValid(session)) {
						setValueInCase(session, q, Unknown.getInstance());
					}
					else {
						// delete the answer(s) ...
						setValueInCase(session, q, UndefinedValue.getInstance());
					}
				}
			}
		}
	}

	private boolean isAbstractQuestion(Question q) {
		if (q == null) return false;
		Object o = q.getProperties().getProperty(Property.ABSTRACTION_QUESTION);
		boolean abstractQ = false;
		if (o instanceof Boolean) {
			abstractQ = ((Boolean) o).booleanValue();
		}
		return abstractQ;
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		// Render hidden input-element (necessary for "setunknown"-button)
		writer.startElement("input", component);
		writer.writeAttribute("type", "hidden", "type");
		writer.writeAttribute("id", "setunknownhiddenfield", "id");
		writer.writeAttribute("name", "setunknownhiddenfield", "name");
		writer.writeAttribute("value", "false", "value");
		writer.endElement("input");
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		List<Question> qList = DialogUtils.getQuestionPageBean().getQuestionListToRender();
		QuestionPageLayout layoutDef = getLayout();

		Session session = DialogUtils.getDialog().getSession();
		ResponseWriter writer = context.getResponseWriter();
		if (DialogUtils.getImageMapBean().hasImagesForQContainer(qList)) {
			QuestionImageMapRendererUtils.renderQuestionsImageMap(writer, component,
					session, qList,
					layoutDef);
		}
		else if (layoutDef instanceof QContainerLayout) {
			new QContainerRendererForDefinedLayout(writer, component, session, qList,
					(QContainerLayout) layoutDef).render();
			// (QContainerLayout) layoutDef);
		}
		else {
			new QContainerRendererForUndefinedLayout(writer, component, session, qList,
					layoutDef).render();
		}
	}

	private QuestionPageLayout getLayout() {
		String qContainerID = DialogUtils.getQuestionPageBean().getActualQContainer().getId();

		// if no QContainerlayout is defined, then take questionpagelayout
		if (DialogUtils.getDialogLayout().hasDefinitonsForQContainerID(qContainerID)) {
			return DialogUtils.getDialogLayout().getQContainerLayoutDefinitionForID(
					qContainerID);
		}
		else {
			return DialogUtils.getDialogLayout().getQuestionPageLayout();
		}
	}

	private Object getAnswerNameListFromIDList(List<String> answerIDs, Question q, Session session) {
		List<String> answerNameList = new ArrayList<String>();
		for (String answerID : answerIDs) {
			Choice a = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase()).findChoice((QuestionChoice) q, answerID);
			answerNameList.add(a.getName());
		}
		return answerNameList;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	private boolean hasEmptyString(String[] stringArray) {
		if (stringArray == null || stringArray.length == 0) {
			return true;
		}
		for (String toTest : stringArray) {
			if (toTest.length() == 0) {
				return true;
			}
		}
		return false;
	}

	private void setValueInCase(Session session, Question q, Value answers) {
		session.getBlackboard().addValueFact(
				new DefaultFact(q, answers, PSMethodUserSelected.getInstance(),
				PSMethodUserSelected.getInstance()));
	}

}
