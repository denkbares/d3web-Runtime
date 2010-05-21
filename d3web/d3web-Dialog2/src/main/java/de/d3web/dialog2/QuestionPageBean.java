/*
 * x * Copyright (C) 2009 Chair of Artificial Intelligence and Applied
 * Informatics Computer Science VI, University of Wuerzburg
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

package de.d3web.dialog2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.component.html.ext.HtmlInputHidden;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.dialog2.basics.settings.DialogSettings;
import de.d3web.dialog2.render.QuestionsRendererUtils;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.indication.inference.PSMethodUserSelected;

public class QuestionPageBean {

	private List<Question> questionListToRender;

	private Question firstQToAsk;

	private QContainer actualQContainer;

	private List<Question> oqHistoryList = new ArrayList<Question>();

	private int oqListPointer = -1;

	private QASet userIndicatedQASet;

	public static Logger logger = Logger.getLogger(QuestionPageBean.class);

	public List<Question> convertQuestionsToRender(QContainer qContainer) {
		List<Question> qListToRender = new ArrayList<Question>();
		for (TerminologyObject terminologyObject : qContainer.getChildren()) {
			if (terminologyObject instanceof QContainer) {
				qListToRender.addAll(convertQuestionsToRender((QContainer) terminologyObject));
			}
			else if (terminologyObject instanceof Question) {
				Question question = (Question) terminologyObject;
				qListToRender.add(question);
				qListToRender.addAll(followUpQuestionOf(question,
						DialogSettings.INCLUDING_INACTIVE_QUESTIONS,
						DialogSettings.INACTIVE_HIERARCHICAL_CHILDREN_ONLY));
			}
			else {
				System.err.println("Unidentified TerminologyObject type: " + getClass());
			}
		}

		// List<List<Object>> tempList = DialogUtils.getMQDialogController(
		// DialogUtils.getDialog().getSession()).getAllQuestionsToRender(
		// qContainer, DialogSettings.INCLUDING_INACTIVE_QUESTIONS,
		// DialogSettings.INACTIVE_HIERARCHICAL_CHILDREN_ONLY);
		//
		// for (Iterator<List<Object>> iter = tempList.iterator();
		// iter.hasNext();) {
		// List<Object> temp = iter.next();
		// Question tempQ = (Question) temp.get(0);
		// qListToRender.add(tempQ);
		//
		// }
		return qListToRender;
	}

	private Collection<Question> followUpQuestionOf(Question question,
			boolean includingInactiveQuestions,
			boolean inactiveHierarchicalChildrenOnly) {
		Session session = DialogUtils.getDialog().getSession();
		List<Question> questions = new ArrayList<Question>();
		for (TerminologyObject followup : question.getChildren()) {
			if (followup instanceof Question) {
				boolean isActive = DialogUtils.isValidQASet((Question) followup, session);
				if (isActive || (includingInactiveQuestions || inactiveHierarchicalChildrenOnly)) {
					questions.add((Question) followup);
				}
				// recursively investigate follow-up questions of this follow-up
				// question
				if (followup.getChildren().length > 0) {
					questions.addAll(followUpQuestionOf((Question) followup,
							includingInactiveQuestions,
							inactiveHierarchicalChildrenOnly));
				}
			}
		}
		return questions;
	}

	public QContainer getActualQContainer() {
		return actualQContainer;
	}

	public Question getFirstQToAsk() {
		return firstQToAsk;
	}

	private Question getFirstQuestionToAskFromList(Session session,
			List<Question> qList, String dialogMode) {
		for (int i = 0; i < qList.size(); i++) {
			Question currentQuestion = qList.get(i);

			if (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(currentQuestion))) {
				return currentQuestion;
			}
			// OLD CODE:
			// if (currentQuestion.isValid(session)) {
			// if (!currentQuestion.isDone(session)) {
			// return currentQuestion;
			// }
			// }
		}
		if (dialogMode.equals("MQ")) {
			return null;
		}
		else {
			return qList.get(0);
		}
	}

	public List<Question> getOqList() {
		return oqHistoryList;
	}

	public int getOqListPointer() {
		return oqListPointer;
	}

	private int getPosInOQHistoryList(Question question) {
		if (!oqHistoryList.contains(question)) {
			return -1;
		}
		for (int i = 0; i < oqHistoryList.size(); i++) {
			if (question.getId().equals(oqHistoryList.get(i).getId())) {
				return i;
			}
		}
		return -1;
	}

	private QContainer getQContainerForQuestion(Question q) {
		if (q == null) return (null);
		for (TerminologyObject qaSet : q.getParents()) {
			if (qaSet instanceof Question) return (getQContainerForQuestion((Question) qaSet));
			else return ((QContainer) qaSet);
		}
		return (null);
	}

	public QContainer getQContainerToRender() {
		// MQDialogController controller = DialogUtils
		// .getMQDialogController(DialogUtils.getDialog().getSession());

		Session session = DialogUtils.getDialog().getSession();
		session.getInterview().setFormStrategy(new CurrentQContainerFormStrategy());
		// QContainer nextInterviewObject = (QContainer) controller
		// .getCurrentQASet();

		QContainer qContainer = (QContainer) session.getInterview().nextForm().getInterviewObject();
		return qContainer;
		// (QContainer)
		// controller.moveToNewestQASet();

		// QContainer qContainer = null;
		// if (nextInterviewObject instanceof QContainer) {
		// qContainer = (QContainer) nextInterviewObject;
		// }
		//
		// if (qContainer == null
		// && !DialogUtils.getDialogSettings().isAutoMoveToResultpage()) {
		// controller.moveToPreviousQASet();
		// if (nextInterviewObject != null) {
		// qContainer = nextInterviewObject;
		// } else {
		// qContainer = (QContainer) controller.getCurrentQASet();
		// }
		// }
		// return qContainer;
	}

	public List<Question> getQuestionListToRender() {
		return questionListToRender;
	}

	public void init() {
		QContainer container = getQContainerToRender();
		if (container != null) {
			setQuestionListToRender(container);
		}
		else {
			// we move to result page because all questions are answered..
			// but we set the actual qcontainer to the previous qcontainer
			// DialogController cont = DialogUtils
			// .getMQDialogController(DialogUtils.getDialog().getSession());
			// cont.moveToPreviousQASet();
			DialogUtils.getSaveCaseBean().autoSaveCase();
			DialogUtils.getPageDisplay().moveToResultPage();
		}
		initTrees();
	}

	private void initTrees() {
		DialogUtils.getQASetTreeBean().init();
		DialogUtils.getDiagnosesTreeBean().init();
	}

	public boolean isCanMoveBack() {
		if (oqHistoryList.size() > 0 && oqListPointer > 0) {
			return true;
		}
		return false;
	}

	public boolean isCanMoveForward() {
		if (oqHistoryList.size() > 0
				&& oqListPointer < oqHistoryList.size() - 1) {
			return true;
		}
		return false;
	}

	public String jumpToContainer() {
		HtmlInputHidden x = (HtmlInputHidden) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("dialogForm:clickedQASet");
		if (x != null) {
			moveToQASet((String) x.getValue());
		}
		return "";
	}

	public void moveToNewestQ(ActionEvent event) {
		oqListPointer = oqHistoryList.size() - 1;
		setQuestionListToRender(getQContainerForQuestion(oqHistoryList
				.get(oqListPointer)));
	}

	public void moveToQASet(String qaSetID) {
		Session session = DialogUtils.getDialog().getSession();
		QASet qaSet = session.getKnowledgeBase().searchQASet(qaSetID);
		if (qaSet != null) {
			// TODO: here we would need an instance indication
			session.getBlackboard()
					.addInterviewFact(
					FactFactory.createFact(qaSet, new Indication(
					State.INSTANT_INDICATED),
					PSMethodUserSelected.getInstance(),
					PSMethodUserSelected.getInstance()));
		}
		// DialogUtils.getMQDialogController(DialogUtils.getDialog().getSession())
		// .moveToQASet(qaSet);

		userIndicatedQASet = getQContainerToRender();
		setQuestionListToRender((QContainer) userIndicatedQASet);

		if (qaSet instanceof Question) {
			userIndicatedQASet = qaSet;
		}

		if (qaSet instanceof Question) {
			setFirstQToAsk((Question) qaSet);
		}
		refreshTreeStyles(DialogUtils.getDialog().getSession());
		DialogUtils.getPageDisplay().moveToQuestionPage();
	}

	public String moveToResultPage() {
		// if a QContainer is indicated -> set styles but show resultpage
		QContainer nextContainer = getQContainerToRender();
		if (nextContainer != null) {
			setQuestionListToRender(nextContainer);
			refreshTreeStyles(DialogUtils.getDialog().getSession());
		}
		// autosave-case
		DialogUtils.getSaveCaseBean().autoSaveCase();
		return DialogUtils.getPageDisplay().moveToResultPage();
	}

	public void oneQBack(ActionEvent event) {
		oqListPointer--;
		setQuestionListToRender(getQContainerForQuestion(oqHistoryList
				.get(oqListPointer)));
	}

	public void oneQForward(ActionEvent event) {
		oqListPointer++;
		setQuestionListToRender(getQContainerForQuestion(oqHistoryList
				.get(oqListPointer)));
	}

	private void refreshTreeStyles(Session theCase) {
		if (theCase != null) {
			DialogUtils.getQASetTreeBean().checkNodeStyles(theCase);
			DialogUtils.getDiagnosesTreeBean().checkNodeStyles(theCase);
		}
	}

	public void setActualQContainer(QContainer actualQContainer) {
		this.actualQContainer = actualQContainer;
	}

	public void setFirstQToAsk(Question firstQToAsk) {
		this.firstQToAsk = firstQToAsk;
	}

	public void setOqListPointer(int oqListPointer) {
		this.oqListPointer = oqListPointer;
	}

	private void setQuestionListOQToRender(QContainer container) {
		List<Question> listToSet = new ArrayList<Question>();
		// if the pointer is on the last listentry -> get a new question

		// if a user wants this qaset -> render it.
		if (userIndicatedQASet != null) {
			if (userIndicatedQASet instanceof Question) {
				listToSet.add((Question) userIndicatedQASet);
			}
			else if (userIndicatedQASet instanceof QContainer) {
				List<Question> temp = convertQuestionsToRender((QContainer) userIndicatedQASet);
				listToSet.add(getFirstQuestionToAskFromList(DialogUtils
						.getDialog().getSession(), temp, "OQ"));
			}
			userIndicatedQASet = null;
		}

		// if pointer is on the last position of oQList
		else if (oqListPointer == oqHistoryList.size() - 1) {
			listToSet.add(getFirstQuestionToAskFromList(DialogUtils.getDialog()
					.getSession(), convertQuestionsToRender(container), "OQ"));
		}
		else {
			listToSet.add(oqHistoryList.get(oqListPointer));
		}
		this.questionListToRender = listToSet;
		setFirstQToAsk(listToSet.get(0));
		QContainer qcont = getQContainerForQuestion(listToSet.get(0));
		if (qcont != null) {
			// System.out.println("setting as actual qContainer: " +
			// qcont.getText());
			setActualQContainer(qcont);
		}

		// ok, we found the question to render, so lets check the pointer...
		if (getPosInOQHistoryList(listToSet.get(0)) == -1) {
			// the question is not yet listed -> add it to queue
			// System.out.println("notyetlisted -> add it to queue");
			oqHistoryList.add(listToSet.get(0));
			oqListPointer = oqHistoryList.size() - 1;
		}
		else {
			// TODO the question is listed.. what to do here?
			// actual implementation: pointer is not changed
		}

		// DEBUG INFOS
		// System.out.println("I am asking -> " + questionListToRender.get(0));
		// System.out.println("Queue: " + oqHistoryList);
	}

	public void setQuestionListToRender(QContainer container) {
		if (DialogUtils.getDialogSettings().getDialogMode().equals("MQ")) {
			this.questionListToRender = convertQuestionsToRender(container);
			// mark container as "actual" (needed for qaTree)
			setActualQContainer(container);

			// mark "firstToAsk" question (will be overwritten if necessary)
			setFirstQToAsk(getFirstQuestionToAskFromList(DialogUtils
					.getDialog().getSession(), questionListToRender, "MQ"));
		}
		else {
			setQuestionListOQToRender(container);
		}
	}

	public String clearLastClickandSubmitAction() {
		Session theCase = DialogUtils.getDialog().getSession();
		LastClickedAnswer.getInstance().setLastClickedAnswerID(null,
				theCase.getId());
		return submitAction();
	}

	public String getCaseId() {
		return DialogUtils.getDialog().getSession().getId();
	}

	public String submitAction() {
		// find next qContainer and set it up for rendering
		QContainer nextContainer = getQContainerToRender();
		if (nextContainer == null) {
			// autosave-case
			DialogUtils.getSaveCaseBean().autoSaveCase();
			DialogUtils.getPageDisplay().moveToResultPage();
			return "";
		}
		setQuestionListToRender(nextContainer);
		// refresh styles...
		refreshTreeStyles(DialogUtils.getDialog().getSession());
		return "";
	}

	public String getProgressBarStyle() {
		StringBuffer result = new StringBuffer();

		Session theCase = DialogUtils.getDialog().getSession();
		int answeredQuestionsCount = 0;

		// 0 is used as default
		int fractionOfAnsweredQuestionsPercentage = computeAnsweredQuestionsFractionPercentage(
				theCase, answeredQuestionsCount);

		// append the width of the bar:
		result.append("width: " + fractionOfAnsweredQuestionsPercentage + "%;");

		// determine the color of the bar:
		String colorString;
		if (fractionOfAnsweredQuestionsPercentage < 51) {
			colorString = "rgb(255, "
					+ (int) (Math
					.floor((double) fractionOfAnsweredQuestionsPercentage
					/ (double) 50 * 255)) + ", 0)";
		}
		else {
			colorString = "rgb("
					+ (int) (Math
							.floor(255 - (((double) fractionOfAnsweredQuestionsPercentage - (double) 50) / 50 * 255)))
					+ ", 255, 0)";
		}

		result.append("background-color: " + colorString + ";");

		return result.toString();
	}

	private int computeAnsweredQuestionsFractionPercentage(Session theCase,
			int answeredQuestionsCount) {
		double fractionOfAnsweredQuestions = 0.0;
		int validQuestionsCounter = 0;
		List<Question> qList = questionListToRender;

		if (qList != null) {
			// count the number of unanswered questions in this container;
			for (Question q : qList) {
				validQuestionsCounter++;
				if (theCase.getBlackboard().getAnsweredQuestions().contains(q)) {
					answeredQuestionsCount++;
				}
			}
			fractionOfAnsweredQuestions = (double) answeredQuestionsCount
					/ (double) validQuestionsCounter;

		}

		// compute the percentage from the double
		int fractionOfAnsweredQuestionsPercentage = (int) Math
				.round(fractionOfAnsweredQuestions * 100);
		return fractionOfAnsweredQuestionsPercentage;
	}

	public String getCurrentQuestionCSSId() {
		return QuestionsRendererUtils.QUESTION_BLOCK_ID_PREFIX
				+ getFirstQToAsk().getId();
	}
}