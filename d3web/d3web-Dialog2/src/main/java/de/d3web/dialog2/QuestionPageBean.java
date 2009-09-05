package de.d3web.dialog2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.component.html.ext.HtmlInputHidden;

import de.d3web.dialog2.basics.settings.DialogSettings;
import de.d3web.dialog2.render.QuestionsRendererUtils;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.DialogController;
import de.d3web.kernel.dialogControl.MQDialogController;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;

public class QuestionPageBean {

	private List<Question> questionListToRender;

	private Question firstQToAsk;

	private QContainer actualQContainer;

	private List<Question> oqHistoryList = new ArrayList<Question>();

	private int oqListPointer = -1;

	private QASet userIndicatedQASet;

	public static Logger logger = Logger.getLogger(QuestionPageBean.class);

	public List<Question> convertQuestionsToRender(QContainer qContainer) {
		List<List<Object>> tempList = DialogUtils.getMQDialogController(
				DialogUtils.getDialog().getTheCase()).getAllQuestionsToRender(
				qContainer, DialogSettings.INCLUDING_INACTIVE_QUESTIONS,
				DialogSettings.INACTIVE_HIERARCHICAL_CHILDREN_ONLY);

		List<Question> qListToRender = new ArrayList<Question>();
		for (Iterator<List<Object>> iter = tempList.iterator(); iter.hasNext();) {
			List<Object> temp = iter.next();
			Question tempQ = (Question) temp.get(0);
			qListToRender.add(tempQ);

		}
		return qListToRender;
	}

	public QContainer getActualQContainer() {
		return actualQContainer;
	}

	public Question getFirstQToAsk() {
		return firstQToAsk;
	}

	private Question getFirstQuestionToAskFromList(XPSCase theCase,
			List<Question> qList, String dialogMode) {
		for (int i = 0; i < qList.size(); i++) {
			Question currentQuestion = qList.get(i);
			if (currentQuestion.isValid(theCase)) {
				if (!currentQuestion.isDone(theCase)) {
					return currentQuestion;
				}
			}
		}
		if (dialogMode.equals("MQ")) {
			return null;
		} else {
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
		if (q == null)
			return (null);

		Iterator<? extends NamedObject> iter = q.getParents().iterator();
		if (iter.hasNext()) {
			QASet qaSet = (QASet) iter.next();
			if (qaSet instanceof Question)
				return (getQContainerForQuestion((Question) qaSet));
			else
				return ((QContainer) qaSet);
		}
		return (null);
	}

	public QContainer getQContainerToRender() {
		MQDialogController controller = DialogUtils
				.getMQDialogController(DialogUtils.getDialog().getTheCase());
		QContainer lastContainer = (QContainer) controller.getCurrentQASet();
		QContainer qContainer = (QContainer) controller.moveToNewestQASet();

		if (qContainer == null
				&& !DialogUtils.getDialogSettings().isAutoMoveToResultpage()) {
			controller.moveToPreviousQASet();
			if (lastContainer != null) {
				qContainer = lastContainer;
			} else {
				qContainer = (QContainer) controller.getCurrentQASet();
			}
		}
		return qContainer;
	}

	public List<Question> getQuestionListToRender() {
		return questionListToRender;
	}

	public void init() {
		QContainer container = getQContainerToRender();
		if (container != null) {
			setQuestionListToRender(container);
		} else {
			// we move to result page because all questions are answered..
			// but we set the actual qcontainer to the previous qcontainer
			DialogController cont = DialogUtils
					.getMQDialogController(DialogUtils.getDialog().getTheCase());
			cont.moveToPreviousQASet();
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
		QASet qaSet = DialogUtils.getDialog().getTheCase().getKnowledgeBase()
				.searchQASet(qaSetID);
		DialogUtils.getMQDialogController(DialogUtils.getDialog().getTheCase())
				.moveToQASet(qaSet);

		userIndicatedQASet = getQContainerToRender();
		setQuestionListToRender((QContainer) userIndicatedQASet);

		if (qaSet instanceof Question) {
			userIndicatedQASet = qaSet;
		}

		if (qaSet instanceof Question) {
			setFirstQToAsk((Question) qaSet);
		}
		refreshTreeStyles(DialogUtils.getDialog().getTheCase());
		DialogUtils.getPageDisplay().moveToQuestionPage();
	}

	public String moveToResultPage() {
		// if a QContainer is indicated -> set styles but show resultpage
		QContainer nextContainer = getQContainerToRender();
		if (nextContainer != null) {
			setQuestionListToRender(nextContainer);
			refreshTreeStyles(DialogUtils.getDialog().getTheCase());
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

	private void refreshTreeStyles(XPSCase theCase) {
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
			} else if (userIndicatedQASet instanceof QContainer) {
				List<Question> temp = convertQuestionsToRender((QContainer) userIndicatedQASet);
				listToSet.add(getFirstQuestionToAskFromList(DialogUtils
						.getDialog().getTheCase(), temp, "OQ"));
			}
			userIndicatedQASet = null;
		}

		// if pointer is on the last position of oQList
		else if (oqListPointer == oqHistoryList.size() - 1) {
			listToSet.add(getFirstQuestionToAskFromList(DialogUtils.getDialog()
					.getTheCase(), convertQuestionsToRender(container), "OQ"));
		} else {
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
		} else {
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
					.getDialog().getTheCase(), questionListToRender, "MQ"));
		} else {
			setQuestionListOQToRender(container);
		}
	}

	public String clearLastClickandSubmitAction() {
		XPSCase theCase = DialogUtils.getDialog().getTheCase();
		LastClickedAnswer.getInstance().setLastClickedAnswerID(null,
				theCase.getId());
		return submitAction();
	}

	public String getCaseId() {
		return DialogUtils.getDialog().getTheCase().getId();
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
		refreshTreeStyles(DialogUtils.getDialog().getTheCase());
		return "";
	}

	public String getProgressBarStyle() {
		StringBuffer result = new StringBuffer();

		XPSCase theCase = DialogUtils.getDialog().getTheCase();
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
		} else {
			colorString = "rgb("
					+ (int) (Math
							.floor(255 - (((double) fractionOfAnsweredQuestionsPercentage - (double) 50) / 50 * 255)))
					+ ", 255, 0)";
		}

		result.append("background-color: " + colorString + ";");

		return result.toString();
	}

	private int computeAnsweredQuestionsFractionPercentage(XPSCase theCase,
			int answeredQuestionsCount) {
		double fractionOfAnsweredQuestions = 0.0;
		int validQuestionsCounter = 0;
		List<Question> qList = questionListToRender;

		if (qList != null) {
			// count the number of unanswered questions in this container;
			for (Question q : qList) {
				if (q.isValid(theCase)) {
					validQuestionsCounter++;
					if (theCase.getAnsweredQuestions().contains(q)) {
						answeredQuestionsCount++;
					}
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