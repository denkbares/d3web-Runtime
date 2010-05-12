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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.ajax.html.HtmlAjaxCommandButton;
import org.apache.log4j.Logger;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.dialog2.basics.layout.AnswerRegion;
import de.d3web.dialog2.basics.layout.MMInfo;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionImage;
import de.d3web.dialog2.basics.layout.QuestionLayout;
import de.d3web.dialog2.basics.layout.QuestionPageLayout;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.component.html.UIQuestionPage;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.QuestionDateUtils;

public class QuestionsRendererUtils {

	public static Logger logger = Logger
			.getLogger(QuestionsRendererUtils.class);

	public static final String QUESTION_BLOCK_ID_PREFIX = "qTableCell_";

	private static boolean currentAnswerIsSet(Value specifiedValue, Question q,
			Session session) {
		Value sessionValue = session.getBlackboard().getValue(q);
		return specifiedValue.equals(sessionValue);
	}

	public static boolean currentAnswersAreBad(List<Object> answeridList,
			List<String> badAnswerIDs) {
		// take every entry in badAnswerList list and compare it with
		// answerIdList
		Iterator<String> badAnswerIDsIterator = badAnswerIDs.iterator();
		while (badAnswerIDsIterator.hasNext()) {
			String badIdToCompare = badAnswerIDsIterator.next();
			boolean badIdIsInList = false;
			for (Object currentID : answeridList) {
				if (badIdToCompare.equals(currentID)) {
					badIdIsInList = true;
					break;
				}
			}
			if (!badIdIsInList) {
				return false;
			}
		}
		return true;
	}

	private static String getAnsNoId(List<Choice> ans) {
		for (int i = ans.size() - 1; i >= 0; i--) {
			if (ans.get(i) instanceof AnswerNo) {
				return ans.get(i).getId();
			}
		}
		return null;
	}

	// private static Choice getAnswerChoiceForAnswerID(String answerID,
	// QuestionChoice qCh) {
	// List<Choice> answerList = qCh.getAllAlternatives();
	// for (Choice ans : answerList) {
	// if (ans.getId().equals(answerID)) {
	// return ans;
	// }
	// }
	// return null;
	// }

	private static String getAnswerValue(Question q, Session session) {
		Value v = session.getBlackboard().getValue(q);
		if (UndefinedValue.isNotUndefinedValue(v)) {
			return v.toString();
		}
		else {
			return "";
		}
	}

	protected static String getBackgroundClass(Session theCase, Question q) {
		if (q instanceof QuestionZC) {
			return "infoQ";
		}
		Question first = DialogUtils.getQuestionPageBean().getFirstQToAsk();
		if (first != null && first.getId().equals(q.getId())) {
			return "currentQ";
		}
		else if (q.isDone(theCase)) {
			return "answeredQ";
		}
		else {
			return "unansweredQ";
		}
	}

	protected static String getBackgroundStyleString(Session theCase,
			Question q, QuestionPageLayout layoutDef) {
		Question first = DialogUtils.getQuestionPageBean().getFirstQToAsk();
		if (first != null && first.getId().equals(q.getId())) {
			return layoutDef.getCurrentQuestionBackground();
		}
		else if (q.isDone(theCase)) {
			return layoutDef.getAnsweredQuestionBackground();
		}
		else {
			return layoutDef.getUnansweredQuestionBackground();
		}
	}

	private static List<QuestionImage> getQuestionImages(
			QuestionPageLayout layoutDef) {
		if (layoutDef instanceof QuestionLayout) {
			QuestionLayout qLayout = (QuestionLayout) layoutDef;
			if (qLayout.getQuestionImageList() != null
					&& qLayout.getQuestionImageList().size() > 0) {
				return qLayout.getQuestionImageList();
			}
		}
		return null;
	}

	private static String getQuestionNumPrefixUnit(Question q) {
		return (String) q.getProperties().getProperty(Property.PREFIX_UNIT);
	}

	private static String getQuestionNumUnit(Question q) {
		return (String) q.getProperties().getProperty(Property.UNIT);
	}

	public static String getStyleStringForMainTableCell(
			QuestionPageLayout layout, String bgStyle, String additionalStyle,
			int cols, int colspan, int colDiff, boolean validationError) {
		StringBuffer styleString = new StringBuffer();
		styleString.append("padding: " + layout.getPadding() + "; ");
		if (bgStyle != null) {
			styleString.append("background-color: " + bgStyle + "; ");
		}
		styleString.append("border: " + layout.getQuestionBorder() + "; ");
		if (validationError) {
			styleString.append("border-color: #f00; ");
		}
		styleString.append("vertical-align: "
				+ layout.getQuestionVerticalAlign() + ";");
		if (colspan > 1) {
			styleString.append("width: "
					+ (colspan != 0 ? (getWidthOfColumn(cols) * colspan)
					: getWidthOfColumn(cols)) + "%;");
		}
		else {
			styleString.append("width: " + getWidthOfColumn(cols)
					* (colDiff + 1) + "%; ");
		}

		if (additionalStyle != null) {
			styleString.append(additionalStyle);
		}
		else if (layout.getAdditionalCSSStyle() != null) {
			styleString.append(layout.getAdditionalCSSStyle());
		}
		return styleString.toString();
	}

	private static int getWidthOfColumn(int cols) {
		return 100 / cols;
	}

	private static boolean hasQuestionImages(QuestionPageLayout layoutDef) {
		if (layoutDef instanceof QuestionLayout) {
			QuestionLayout qLayout = (QuestionLayout) layoutDef;
			if (qLayout.getQuestionImageList() != null
					&& qLayout.getQuestionImageList().size() > 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean isRenderUnknownAnswer(Question q) {
		Boolean unknownVisible = (Boolean) q.getProperties().getProperty(
				Property.UNKNOWN_VISIBLE);
		if ((unknownVisible != null) && (unknownVisible.booleanValue())) {
			return true;
		}
		return false;
	}

	private static void renderAdditionalInfo(ResponseWriter writer,
			UIComponent component, Question q, MMInfo info) throws IOException {
		writer.startElement("div", component);
		writer.writeAttribute("style", "text-align: " + info.getAlign()
				+ "; padding: " + info.getPadding() + ";", "style");
		DialogRenderUtils.renderAdditionalInfoWithReplacedExtraMarkup(writer,
				component, q, info.getText());
		writer.endElement("div");
	}

	private static void renderAnswerRegion(ResponseWriter writer,
			UIComponent component, AnswerRegion answerRegion,
			QuestionChoice qChoice, Session theCase,
			QuestionPageLayout layoutDef, boolean hover) throws IOException {

		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		Value answer = kbm.findValue(qChoice, answerRegion.getAnswerID());
		String answerID = ((Choice) (answer.getValue())).getId();

		if (answer == null) {
			// TODO fehlerbehandlung
			logger.info("The Answer ID was not found in the knowledge base.");
			return;

		}

		Choice followingPopupQuestionAnswer = getAnswerOfFollowingPopupQuestion(
				answerRegion.getAnswerID(), layoutDef, theCase);

		writer.startElement("a", component);
		writer.writeAttribute("id", "region_" + answerID, "id");
		writer.writeAttribute("href", "#", "href");

		StringBuffer buf = new StringBuffer();
		buf.append("answerRegion");
		// if region is aleady answered --> insert another styleclass
		if (currentAnswerIsSet(answer, qChoice, theCase)) {
			buf.append(" answerSet");
		}
		// if region should be hovered by mouseover -> insert another styleclass
		if (hover) {
			buf.append(" qImageHover");
		}

		if (followingPopupQuestionAnswer != null) {
			// casts must work, else the answer would be null
			QuestionChoice followingPopupQuestion = (QuestionChoice) ((QuestionLayout) layoutDef)
					.getFollowingPopupQuestion(answerRegion.getAnswerID(),
					theCase.getKnowledgeBase());
			if (followingPopupQuestionAnswer.equals(followingPopupQuestion
					.getAllAlternatives().get(0))) {
				buf.append(" popupQuestionAnswered1");
			}
			else if (followingPopupQuestionAnswer
					.equals(followingPopupQuestion.getAllAlternatives().get(1))) {
				buf.append(" popupQuestionAnswered2");
			}
			else if (followingPopupQuestionAnswer
					.equals(followingPopupQuestion.getAllAlternatives().get(2))) {
				buf.append(" popupQuestionAnswered3");
			}
			else if (followingPopupQuestionAnswer
					.equals(followingPopupQuestion.getAllAlternatives().get(3))) {
				buf.append(" popupQuestionAnswered4");
			}
			else if (followingPopupQuestionAnswer
					.equals(followingPopupQuestion.getAllAlternatives().get(4))) {
				buf.append(" popupQuestionAnswered5");
			}
			else {
				buf.append(" popupQuestionAnsweredOther");
			}

		}

		writer.writeAttribute("class", buf, "class");

		StringBuffer styleString = new StringBuffer();
		styleString.append("top: " + answerRegion.getYStart() + "px; left: "
				+ answerRegion.getXStart() + "px;");
		styleString.append("width: " + answerRegion.getWidth() + "px; height: "
				+ answerRegion.getHeight() + "px;");
		writer.writeAttribute("style", styleString.toString(), "style");

		writer.writeAttribute("onclick", "setAnswer('" + answerID
				+ "'); saveLastClickedAnswer('" + answerID + "', ' "
				+ theCase.getId() + "'); doSubmit(); return false;", "onclick");
		String imageName = DialogUtils.getMessageWithParamsFor(
				"dialog.questionImageAnswerTitle",
				new Object[] { answer.getValue().toString() });
		writer.writeAttribute("title", imageName, "title");
		writer.writeAttribute("alt", imageName, "alt");

		// insert a transparent gif (so that Internet Exlorer can be supported)
		writer.startElement("img", component);
		writer.writeAttribute("alt", "space", "alt");
		writer.writeAttribute("src", "images/spacer.gif", "src");
		writer.writeAttribute("width", answerRegion.getWidth(), "width");
		writer.writeAttribute("height", answerRegion.getHeight(), "height");
		writer.writeAttribute("title", imageName, "title");
		writer.writeAttribute("alt", imageName, "alt");
		writer.endElement("img");

		writer.endElement("a");
	}

	/**
	 * Identifies the popupQuestion triggered by the given answer and returns
	 * the actual value in the given case Returns null, if no popupQuestion is
	 * triggered or if the question is no QuestionChoice or the question is not
	 * answered in the case yet. For QuestionMC the first correct Answer is
	 * returned.
	 * 
	 * @param answerId
	 * @param layoutDef
	 * @param theCase
	 * @return
	 */
	private static Choice getAnswerOfFollowingPopupQuestion(
			String answerId, QuestionPageLayout layoutDef, Session theCase) {
		// there was no PopupQuestion
		if (!(layoutDef instanceof QuestionLayout)) {
			return null;
		}

		QuestionLayout questionLayoutDef = (QuestionLayout) layoutDef;
		String questionId = questionLayoutDef
				.getFollowingPopupQuestionId(answerId);
		for (Question q : theCase.getBlackboard().getAnsweredQuestions()) {
			if (q.getId().equals(questionId) && (q instanceof QuestionChoice)) {
				Value answer = theCase.getBlackboard().getValue(q);
				if (answer != null) {
					return (Choice) answer.getValue();
				}
			}
		}
		return null;
	}

	private static void renderErrors(ResponseWriter writer,
			UIComponent component, String qid) throws IOException {
		Iterator<FacesMessage> it = FacesContext.getCurrentInstance()
				.getMessages(qid);
		while (it.hasNext()) {
			FacesMessage msg = it.next();
			writer.startElement("div", component);
			writer.writeAttribute("id", "q_" + qid + "_error", "id");
			writer.writeAttribute("class", "validationerror", "class");
			writer.writeText(msg.getSummary(), "value");
			writer.endElement("div");
		}
	}

	protected static void renderMMInfoWithoutPopup(ResponseWriter writer,
			UIComponent component, Question q, QuestionPageLayout layoutDef,
			String position) throws IOException {
		MMInfo info = layoutDef.getMmInfo();
		if (position == null || info.getPosition().equals(position)) {
			String text = DialogRenderUtils.getMMInfoStringForQuestion(q);
			if (text != null && text.length() > 0) {
				info.setText(text);
				QuestionsRendererUtils.renderAdditionalInfo(writer, component,
						q, info);
			}
		}
	}

	public static void renderQuestion(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			QuestionPageLayout layoutDef, int cols, int colDiff)
			throws IOException {
		if (q.isValid(theCase) && showAbstract(q)) {

			renderValidQuestion(writer, component, theCase, q, layoutDef, cols,
					colDiff);
		}
		else {
			// render nothing if case is not valid...
			logger.info("Question " + q.getName()
					+ " is not valid or abstract!");
		}
	}

	protected static void renderValidQuestion(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			QuestionPageLayout layoutDef, int cols, int colDiff)
			throws IOException {
		writer.startElement("td", component);
		writer.writeAttribute("id", QUESTION_BLOCK_ID_PREFIX + q.getId(), "id");

		int colspan = 0;
		if (layoutDef instanceof QuestionLayout) {
			QuestionLayout qDef = (QuestionLayout) layoutDef;
			colspan = qDef.getColspan();
			if (colspan > 0) {
				writer.writeAttribute("colspan", colspan, "colspan");
			}
			if (qDef.getRowspan() > 0) {
				writer.writeAttribute("rowspan", qDef.getRowspan(), "rowspan");
			}
		}
		else if (layoutDef instanceof QContainerLayout) {
			colspan = ((QContainerLayout) layoutDef).getDefaultColspan();
			// if defaultColspan was set
			if (colspan != -1) {
				writer.writeAttribute("colspan", colspan, "colspan");
			}
		}
		else if (colDiff > 0) {
			// empty cell after this question -> we have to enlarge the
			// question
			writer.writeAttribute("colspan", colDiff + 1, "colspan");
		}
		boolean validationError = ((UIQuestionPage) component)
				.getErrorIDsToSubmittedValues().containsKey(q.getId());
		writer.writeAttribute("style", getStyleStringForMainTableCell(
				layoutDef, getBackgroundStyleString(theCase, q, layoutDef),
				null, cols, colspan, colDiff, validationError), "style");

		if (layoutDef.getAdditionalCSSClass() != null) {
			writer.writeAttribute("class", getBackgroundClass(theCase, q) + " "
					+ layoutDef.getAdditionalCSSClass(), "class");
		}
		else {
			writer.writeAttribute("class", getBackgroundClass(theCase, q),
					"class");
		}

		writer.startElement("div", component);
		writer.writeAttribute("class", "question", "");

		// Start outer table
		DialogRenderUtils.renderTableWithClass(writer, component, "qTable");
		writer.startElement("tr", component);
		writer.startElement("td", component);

		// render question headline
		if (layoutDef.getShowQuestionHeadline()) {
			renderQuestionHeadline(writer, component, q, layoutDef);
		}

		// render mminfo
		renderMMInfoWithoutPopup(writer, component, q, layoutDef,
				MMInfo.POSITION_TOP);

		renderQuestionContentTableStart(writer, component, layoutDef);
		if (q instanceof QuestionText) {
			QuestionsRendererUtils.renderQuestionText(writer, component,
					theCase, q, layoutDef);
		}
		else if (q instanceof QuestionNum) {
			QuestionsRendererUtils.renderQuestionNum(writer, component,
					theCase, q, layoutDef);
		}
		else if (q instanceof QuestionDate) {
			QuestionsRendererUtils.renderQuestionDate(writer, component,
					theCase, q, layoutDef);
		}
		else if (q instanceof QuestionChoice) {
			int minanswrap = Integer.parseInt(((UIQuestionPage) component)
					.getMinanswrap());
			QuestionsRendererUtils.renderQuestionChoice(writer, component,
					theCase, (QuestionChoice) q, minanswrap, layoutDef);
		}

		// End QuestionContent Table
		writer.endElement("table");

		// render mminfo
		renderMMInfoWithoutPopup(writer, component, q, layoutDef,
				MMInfo.POSITION_BOTTOM);

		// render errors (if available) and remove the value from the map
		// (because it will be set again if errors occur again
		renderErrors(writer, component, q.getId());
		((UIQuestionPage) component).getErrorIDsToSubmittedValues().remove(
				q.getId());

		// End Question Table
		writer.endElement("td");
		writer.endElement("tr");
		writer.endElement("table");

		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "questionInfo", "class");
		writer.write(DialogRenderUtils.getMMInfoStringForQuestion(q));
		writer.endElement("div");
		writer.endElement("td");
	}

	private static void renderQuestionChoice(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int minanswrap, QuestionPageLayout layoutDef) throws IOException {
		if (hasQuestionImages(layoutDef)) {
			renderQuestionChoiceAsImages(writer, component, theCase, qChoice,
					minanswrap, layoutDef, getQuestionImages(layoutDef));
		}
		else {
			renderQuestionChoiceWithoutImage(writer, component, theCase,
					qChoice, minanswrap, layoutDef);
		}
	}

	private static void renderQuestionChoiceWithoutImage(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int minanswrap, QuestionPageLayout layoutDef) throws IOException {
		if (layoutDef.getAnswerChoiceType().equals("dropdown")) {
			renderQuestionChoiceAsList(writer, component, theCase, qChoice,
					layoutDef, true);
		}
		else if (layoutDef.getAnswerChoiceType().equals("list")) {
			renderQuestionChoiceAsList(writer, component, theCase, qChoice,
					layoutDef, false);
		}
		else {
			renderQuestionChoiceAsCheckBox(writer, component, theCase, qChoice,
					minanswrap, layoutDef);
		}
	}

	private static void renderQuestionChoiceAsCheckBox(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int minanswrap, QuestionPageLayout layoutDef) throws IOException {
		List<Choice> ans = qChoice.getAllAlternatives();

		// Decide how much cols are used...
		int cols = 1;
		if (layoutDef.getAnswerColumns() > 0) {
			cols = layoutDef.getAnswerColumns();
		}
		else if (minanswrap > 0 && ans.size() >= minanswrap - 1) {
			cols = 2;
		}
		if (cols > 1) {

			int ansSum = isRenderUnknownAnswer(qChoice) ? ans.size() + 1 : ans
					.size(); // +
			// 1
			// because
			// of
			// unknown
			// answer

			int answerCounter = 0;

			writer.startElement("tr", component);
			for (int i = 0; i < cols; i++) {
				writer.startElement("td", component);
				writer.writeAttribute("style", "width: " + 100 / cols
						+ "%; vertical-align: top", "style");

				int answersToDisplay = 0;
				if (ansSum % (cols - i) == 0) {
					answersToDisplay = ansSum / (cols - i);
				}
				else {
					answersToDisplay = (ansSum / (cols - i) + 1);
				}
				ansSum = ansSum - answersToDisplay;

				DialogRenderUtils.renderTable(writer, component);
				renderQuestionChoiceColumn(writer, component, theCase, qChoice,
						answerCounter, answersToDisplay, layoutDef);
				writer.endElement("table");
				writer.endElement("td"); // End of column

				answerCounter = answerCounter + answersToDisplay;
			}
			writer.endElement("tr");
		}
		else {
			renderQuestionChoiceColumn(writer, component, theCase, qChoice, 0,
					ans.size(), layoutDef);

			boolean fastAnswer = layoutDef.getFastAnswer();
			// disallow always: fast answer for MC
			if (qChoice instanceof QuestionMC) {
				fastAnswer = false;
			}

			renderUnknownRadioButton(writer, component, theCase, qChoice,
					DialogUtils.unknownAnswerInValueList(qChoice, theCase),
					fastAnswer);
		}
	}

	private static void renderQuestionChoiceAsImage(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int minanswrap, QuestionPageLayout layoutDef, QuestionImage qImage)
			throws IOException {
		DialogRenderUtils.renderTableWithClass(writer, component, "width100p");
		writer.startElement("tr", component);
		writer.startElement("td", component);

		// render visible table on top
		if (qImage.getAnswersPosition().equals("top")) {
			DialogRenderUtils.renderTableWithClass(writer, component,
					"width100p");
			renderQuestionChoiceWithoutImage(writer, component, theCase,
					qChoice, minanswrap, layoutDef);
			writer.endElement("table");
		}

		if (qImage.getAnswersPosition().equals("left")
				|| qImage.getAnswersPosition().equals("right")) {
			DialogRenderUtils.renderTableWithClass(writer, component,
					"width100p");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("align", "center", "align");

			DialogRenderUtils.renderTable(writer, component);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			if (qImage.getAnswersPosition().equals("left")) {
				DialogRenderUtils.renderTableWithClass(writer, component,
						"width100p");
				renderQuestionChoiceWithoutImage(writer, component, theCase,
						qChoice, minanswrap, layoutDef);
				writer.endElement("table");
			}
			else {
				writer.write("&nbsp;");
			}
			writer.endElement("td");
			writer.startElement("td", component);
			renderQuestionImage(writer, component, theCase, qChoice, qImage,
					layoutDef);
			writer.endElement("td");
			writer.startElement("td", component);
			if (qImage.getAnswersPosition().equals("right")) {
				DialogRenderUtils.renderTableWithClass(writer, component,
						"width100p");
				renderQuestionChoiceWithoutImage(writer, component, theCase,
						qChoice, minanswrap, layoutDef);
				writer.endElement("table");
			}
			else {
				writer.write("&nbsp;");
			}
			writer.endElement("td");
			writer.endElement("tr");
			writer.endElement("table");

			writer.endElement("td");
			writer.endElement("tr");
			writer.endElement("table");
		}
		else {
			renderQuestionImage(writer, component, theCase, qChoice, qImage,
					layoutDef);
		}

		// render visible table on bottom
		if (qImage.getAnswersPosition().equals("bottom")) {
			DialogRenderUtils.renderTableWithClass(writer, component,
					"width100p");
			renderQuestionChoiceWithoutImage(writer, component, theCase,
					qChoice, minanswrap, layoutDef);
			writer.endElement("table");
		}

		// invisible table if position is null or "hidden
		if (qImage.getAnswersPosition().equals("hidden")) {
			renderQuestionImageColumn(writer, component, theCase, qChoice,
					layoutDef, true);
		}

		writer.endElement("td");
		writer.endElement("tr");
		writer.endElement("table");
	}

	private static void renderQuestionChoiceAsImages(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int minanswrap, QuestionPageLayout layoutDef,
			List<QuestionImage> qImageList) throws IOException {
		writer.startElement("tr", component);
		for (QuestionImage qImage : qImageList) {
			writer.startElement("td", component);
			writer.writeAttribute("align", "center", "align");
			renderQuestionChoiceAsImage(writer, component, theCase, qChoice,
					minanswrap, layoutDef, qImage);
			writer.endElement("td");
		}
		writer.endElement("tr");
	}

	private static void renderQuestionChoiceAsList(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			QuestionPageLayout layoutDef, boolean asDropdownList)
			throws IOException {
		List<Choice> ans = qChoice.getAllAlternatives();
		boolean unknownAnswer = DialogUtils.unknownAnswerInValueList(qChoice,
				theCase);
		writer.startElement("tr", component);
		writer.startElement("td", component);

		writer.startElement("select", component);

		// get width of select-Element...
		StringBuffer styleString = new StringBuffer();
		styleString.append("width: " + layoutDef.getInputWidth() + ";");
		writer.writeAttribute("style", styleString.toString(), "style");

		writer.writeAttribute("name", qChoice.getId(), "name");
		if (qChoice instanceof QuestionMC) {
			writer.writeAttribute("multiple", "multiple", "multiple");
		}
		if (!asDropdownList) {
			writer.writeAttribute("size", isRenderUnknownAnswer(qChoice) ? (ans
					.size() + 1) : ans.size(), "size");
		}

		// Empty option (selected if no option is already set)
		if (asDropdownList && !(qChoice instanceof QuestionMC)) {
			writer.startElement("option", component);
			writer.writeAttribute("value", "", "value");
			if (layoutDef.getFastAnswer()) {
				// set no AnswerID
				writer.writeAttribute("onclick", "saveLastClickedAnswer('" + ""
						+ "', '" + theCase.getId() + "'); doSubmit();",
						"onclick");
			}
			// writer.write("");
			writer.endElement("option");
		}

		for (Choice specAns : ans) {
			writer.startElement("option", component);
			writer.writeAttribute("value", specAns.getId(), "value");

			if (currentAnswerIsSet(new ChoiceValue(specAns), qChoice, theCase)) {
				writer.writeAttribute("selected", "selected", "selected");
			}

			// Write JavaScript
			if (qChoice instanceof QuestionMC) {
				if (specAns instanceof AnswerNo) {
					writeJsDeselectAllOptionsBut(writer, qChoice.getId(),
							getAnsNoId(ans), layoutDef.getFastAnswer(), theCase);
				}
				else {
					writeJsDeselectOptionsUnknownAnd(writer, qChoice.getId(),
							getAnsNoId(ans), layoutDef.getFastAnswer(), theCase);
				}
			}
			else {
				if (layoutDef.getFastAnswer()) {
					writer.writeAttribute("onclick", "saveLastClickedAnswer('"
							+ specAns.getId() + "', ' " + theCase.getId()
							+ "'); doSubmit();", "onclick");
				}
			}
			writer.writeText(specAns.getName(), "value");
			writer.endElement("option");
		}

		boolean fastAnswer = layoutDef.getFastAnswer();
		// disallow always: fast answer for MC
		if (qChoice instanceof QuestionMC) {
			fastAnswer = false;
		}

		renderUnknownOptionElement(writer, component, theCase, qChoice,
				unknownAnswer, fastAnswer);

		writer.endElement("select");

		writer.endElement("td");
		writer.endElement("tr");
	}

	private static void renderQuestionChoiceColumn(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			int startIndex, int qCount, QuestionPageLayout layoutDef)
			throws IOException {
		List<Choice> ans = qChoice.getAllAlternatives();
		for (int i = startIndex; i < startIndex + qCount; i++) {
			// rende unknown answer if i is out of answerlist or render
			// invisible unknown answer...
			if (i >= ans.size()
					|| (i == ans.size() - 1 && !isRenderUnknownAnswer(qChoice))) {

				boolean fastAnswer = layoutDef.getFastAnswer();
				// disallow always: fast answer for MC
				if (qChoice instanceof QuestionMC) {
					fastAnswer = false;
				}
				renderUnknownRadioButton(writer, component, theCase, qChoice,
						DialogUtils.unknownAnswerInValueList(qChoice, theCase),
						fastAnswer);
				if (i >= ans.size()) {
					continue;
				}
			}
			writer.startElement("tr", component);
			writer.startElement("td", component);

			DialogRenderUtils.renderTable(writer, component);
			writer.startElement("tr", component);

			Choice specAns = ans.get(i);

			// 1. column : Checkbox
			writer.startElement("td", component);
			writer.startElement("input", component);
			writer.writeAttribute("id", specAns.getId(), "id");

			// if AnswerNo OR QuestionOC render radiobutton
			if (qChoice instanceof QuestionOC || specAns instanceof AnswerNo) {
				writer.writeAttribute("type", "radio", "type");
			}
			else {
				writer.writeAttribute("type", "checkbox", "type");
			}
			writer.writeAttribute("value", specAns.getId(), "value");
			writer.writeAttribute("name", qChoice.getId(), "name");

			if (currentAnswerIsSet(new ChoiceValue(specAns), qChoice, theCase)) {
				writer.writeAttribute("checked", "checked", "checked");
			}

			boolean badAnswer = false;
			if (qChoice instanceof QuestionOC) {
				if (layoutDef.getFastAnswer()) {
					writer.writeAttribute("onclick", "saveLastClickedAnswer('"
							+ specAns.getId() + "', '" + theCase.getId()
							+ "'); doSubmit()", "onclick");
				}
			}
			else if (qChoice instanceof QuestionMC) {

				if (DialogUtils.getDialogSettings()
						.isMCConstraintsAutoGrayOut()) {
					// check if Question has MC constraints and disable the
					// button if another answer is already set and this answer
					// would result in a restricted combination
					List<List<String>> badanswersLists = (List<List<String>>) qChoice
							.getProperties().getProperty(
							Property.MC_CONSTRAINTS);

					Value mcans = theCase.getBlackboard().getValue(qChoice);
					List<Choice> alreadySetAnsList = new ArrayList<Choice>();

					if (mcans instanceof MultipleChoiceValue) {
						alreadySetAnsList = ((MultipleChoiceValue) mcans).asChoiceList();
					}

					// only check if both lists have entries
					if (badanswersLists != null && badanswersLists.size() > 0
							&& alreadySetAnsList.size() > 0) {
						// add the current answerID and the already set
						// answerIDs in a list and check if this combination is
						// bad...
						List<Object> extendedAnswersIDList = new ArrayList<Object>();
						extendedAnswersIDList.add(specAns.getId());
						for (Answer a : alreadySetAnsList) {
							extendedAnswersIDList.add(a.getId());
						}
						for (List<String> badansList : badanswersLists) {
							boolean combinationIsBad = currentAnswersAreBad(
									extendedAnswersIDList, badansList);
							if (combinationIsBad) {
								badAnswer = true;
								writer.writeAttribute("disabled", "disabled",
										"disabled");
								break;
							}
						}
					}
				}

				// if it is a radiobutton (-> AnswerNo) ...
				if (specAns instanceof AnswerNo) {
					writeJsDeselectAllBut(writer, qChoice.getId(),
							getAnsNoId(ans), layoutDef.getFastAnswer(), theCase);
				}
				// if it is a select-button (no AnswerNo)
				else {
					writeJsDeselectUnknownAnd(writer, qChoice, specAns,
							theCase, getAnsNoId(ans),
							layoutDef.getFastAnswer(), DialogUtils
							.getDialogSettings()
							.isMCConstraintsAutoGrayOut());
				}

			}
			writer.endElement("input");
			writer.endElement("td");

			// 2. column: label and text
			writer.startElement("td", component);
			writer.startElement("label", component);
			writer.writeAttribute("for", specAns.getId(), "for");
			if (badAnswer) {
				// kind of grayed out
				writer.writeAttribute("style", "color: #999;", "style");
			}
			writer.writeText(specAns.getName(), "value");
			writer.endElement("label");
			writer.endElement("td");

			writer.endElement("tr");
			writer.endElement("table");

			writer.endElement("td");
			writer.endElement("tr");

		}
	}

	public static void renderQuestionContentTableStart(ResponseWriter writer,
			UIComponent component, QuestionPageLayout layoutDef)
			throws IOException {
		// Start QuestionContent-Table
		DialogRenderUtils.renderTableWithClass(writer, component, "width100p");
		StringBuffer styleString = new StringBuffer();
		styleString.append("color: " + layoutDef.getAnswerTextColor() + "; ");
		styleString.append("margin: " + layoutDef.getQuestionAnswersMargin()
				+ "; ");
		if (layoutDef.getAnswerFont() != null) {
			styleString.append("font: " + layoutDef.getAnswerFont() + "; ");
		}
		writer.writeAttribute("style", styleString, "style");
	}

	private static void renderQuestionDate(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			QuestionPageLayout layoutDef) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);

		writer.startElement("input", component);
		writer.writeAttribute("id", q.getId() + "_TF", "id");
		writer.writeAttribute("style", "width: " + layoutDef.getInputWidth()
				+ ";", "style");
		writer.writeAttribute("type", "text", "type");
		writer.writeAttribute("name", q.getId(), "type");

		// check if one answer is an AnswerUnknown
		boolean unknownAnswer = DialogUtils
				.unknownAnswerInValueList(q, theCase);
		if (unknownAnswer) {
			writer.writeAttribute("value", "", "value");
		}
		else {
			Value answer = theCase.getBlackboard().getValue(q);
			// List<Answer> valueList = q.getValue(theCase);
			String errorValue = ((UIQuestionPage) component)
					.getErrorIDsToSubmittedValues().get(q.getId());
			if (answer != null) {
				String dateanswer = QuestionDateUtils.dateToString(
						(QuestionDate) q,
						(Date) answer.getValue(), theCase);
				writer.writeAttribute("value", dateanswer, "value");
			}
			else if (errorValue != null) {
				writer.writeAttribute("value", errorValue, "value");
			}
			else {
				writer.writeAttribute("value", "", "value");
			}
		}

		// Javascript for Date input field
		writeJsEnableAndDeselectUnknownAnd(writer, q.getId(),
				AnswerUnknown.UNKNOWN_ID);

		writer.endElement("input");

		writer.endElement("td");
		writer.endElement("tr");

		// render validation errors (if available)
		// renderErrors(writer, component, q.getId());

		renderUnknownRadioButton(writer, component, theCase, q, unknownAnswer,
				layoutDef.getFastAnswer());
	}

	public static void renderQuestionHeadline(ResponseWriter writer,
			UIComponent component, Question q, QuestionPageLayout layoutDef)
			throws IOException {

		DialogRenderUtils.renderTableWithClass(writer, component, "width100p");

		StringBuffer style = new StringBuffer();
		style.append("margin: " + layoutDef.getHeadlineMargin() + ";");
		style.append("background: " + layoutDef.getHeadlineBackground() + "; ");
		if (layoutDef.getHeadlineBorder() != null) {
			style.append("border: " + layoutDef.getHeadlineBorder() + "; ");
		}
		writer.writeAttribute("style", style.toString(), "style");

		writer.startElement("tr", component);

		// Render button if available and desirable.
		if (layoutDef.getShowButton() || !(q instanceof QuestionOC)) {
			writer.startElement("td", component);
			writer.writeAttribute("class", "qHeadlineButton", "class");
			HtmlAjaxCommandButton facet = (HtmlAjaxCommandButton) component
					.getFacet("button");
			if (facet != null) {
				DialogRenderUtils.renderChild(
						FacesContext.getCurrentInstance(), facet);
			}
			writer.endElement("td");
		}

		writer.startElement("td", component);
		writer.writeAttribute("class", "qHeadlineText", "class");
		// build style
		StringBuffer styleString = new StringBuffer();
		styleString.append("color: " + layoutDef.getHeadlineTextColor() + "; ");
		if (layoutDef.getHeadlineFont() != null) {
			styleString.append("font: " + layoutDef.getHeadlineFont() + "; ");
		}
		writer.writeAttribute("style", styleString, "style");

		// if question not null -> headline for question, else -> headline for
		// imagemap
		if (q != null) {
			writer.writeText(DialogUtils.getQPrompt(q), "value");
			writer.write("&nbsp;");

			// Render MMInfo from knowledge base...
			MMInfo mmInfo = layoutDef.getMmInfo();
			if (mmInfo.getPosition().equals(MMInfo.POSITION_HEADLINE)) {
				DialogRenderUtils.renderMMInfoPopupLink(writer, component, q,
						false, mmInfo);
			}
		}
		else {
			if (layoutDef.getImageMapHeadlineText() != null) {
				writer.writeText(layoutDef.getImageMapHeadlineText(), "value");
			}
			else {
				writer.writeText(DialogUtils
						.getMessageFor("dialog.imagemapHeadlineText"), "value");
			}
		}
		writer.endElement("td");
		writer.endElement("tr");
		writer.endElement("table"); // End HeadlineTable
	}

	private static void renderQuestionImage(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			QuestionImage questionImage, QuestionPageLayout layoutDef)
			throws IOException {
		writer.startElement("div", component);
		writer.writeAttribute("class", "questionImage", "class");

		Dimension d = DialogUtils.getImageDimension(theCase, questionImage
				.getFile());
		if (d != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("width: " + d.getWidth() + "px;");
			buffer.append("height: " + d.getHeight() + "px;");

			if (questionImage.getAlign().equals("right")) {
				buffer.append("margin-left: auto;");
			}
			else if (questionImage.getAlign().equals("center")) {
				buffer.append("margin-left: auto;");
				buffer.append("margin-right: auto;");
			}
			writer.writeAttribute("style", buffer.toString(), "style");
		}

		// render image
		writer.startElement("img", component);
		writer.writeAttribute("id", "img_" + qChoice.getId(), "id");
		String filename = ResourceRepository.getMMPathForKB(theCase
				.getKnowledgeBase().getId())
				+ questionImage.getFile();
		if (!new File(filename).exists()) {
			filename = ResourceRepository.getMMPathForKB(theCase
					.getKnowledgeBase().getId())
					+ questionImage.getFile();
		}
		writer.writeAttribute("src", ResourceRepository.getMMPathForKB(theCase
				.getKnowledgeBase().getId())
				+ questionImage.getFile(), "src");
		writer.writeAttribute("alt", "image", "alt");
		writer.endElement("img");

		// render AnswerRegions
		for (AnswerRegion answerRegion : questionImage.getAnswerRegions()) {
			renderAnswerRegion(writer, component, answerRegion, qChoice,
					theCase, layoutDef, questionImage.isShowRegionOnMouseOver());
		}
		writer.endElement("div");
	}

	private static void renderQuestionImageColumn(ResponseWriter writer,
			UIComponent component, Session theCase, QuestionChoice qChoice,
			QuestionPageLayout layoutDef, boolean invisible) throws IOException {
		if (invisible) {
			DialogRenderUtils.renderTableWithClass(writer, component, "invis");
		}
		else {
			DialogRenderUtils.renderTable(writer, component);
		}
		renderQuestionChoiceColumn(writer, component, theCase, qChoice, 0,
				qChoice.getAllAlternatives().size(), layoutDef);
		writer.endElement("table");
	}

	private static void renderQuestionNum(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			QuestionPageLayout layoutDef) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);

		// render prefix unit
		String unit = getQuestionNumPrefixUnit(q);
		if (unit != null) {
			writer.writeText(unit + " ", "value");
		}

		writer.startElement("input", component);
		writer.writeAttribute("style", "width: " + layoutDef.getInputWidth()
				+ ";", "style");
		writer.writeAttribute("type", "text", "type");
		writer.writeAttribute("name", q.getId(), "name");
		writer.writeAttribute("id", q.getId() + "_TF", "id");
		writer.writeAttribute("class", "textfieldEnabled", "class");

		String ans = getAnswerValue(q, theCase);
		;
		String errorValue = ((UIQuestionPage) component)
				.getErrorIDsToSubmittedValues().get(q.getId());
		if (DialogUtils.unknownAnswerInValueList(q, theCase)) {
			writer.writeAttribute("value", "", "value");
		}
		else if (errorValue != null) {
			writer.writeAttribute("value", errorValue, "value");
		}
		else {
			writer.writeAttribute("value", ans, "value");
		}

		// Javascript on textfield
		writeJsEnableAndDeselectUnknownAnd(writer, q.getId(),
				AnswerUnknown.UNKNOWN_ID);

		writer.endElement("input");

		// render unit
		unit = getQuestionNumUnit(q);
		if (unit != null) {
			writer.writeText(" " + unit, "value");
		}

		writer.endElement("td");
		writer.endElement("tr");

		// render validation errors (if available)
		// renderErrors(writer, component, q.getId());

		// the last false disallows fast answers generally
		renderUnknownRadioButton(writer, component, theCase, q, DialogUtils
				.unknownAnswerInValueList(q, theCase), false);
	}

	private static void renderQuestionText(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			QuestionPageLayout layoutDef) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);

		boolean unknownAnswer = DialogUtils
				.unknownAnswerInValueList(q, theCase);
		if (layoutDef.getQTextDisplayMode().equals("textarea")) {
			writer.startElement("textarea", component);
			StringBuffer styleString = new StringBuffer();
			styleString.append("width: " + layoutDef.getInputWidth() + "; ");
			styleString.append("height: " + layoutDef.getInputHeight() + "; ");
			writer.writeAttribute("style", styleString, "style");

			writer.writeAttribute("id", q.getId() + "_TF", "id");
			writer.writeAttribute("class", "textfieldEnabled", "class");
			writer.writeAttribute("name", q.getId(), "name");

			// Javascript on textfield
			writeJsEnableAndDeselectUnknownAnd(writer, q.getId(),
					AnswerUnknown.UNKNOWN_ID);

			if (unknownAnswer) {
				writer.write("");
			}
			else {
				writer.writeText(getAnswerValue(q, theCase), "value");
			}
			writer.endElement("textarea");
		}
		else if (layoutDef.getQTextDisplayMode().equals("textfield")) {
			writer.startElement("input", component);
			writer.writeAttribute("style", "width: "
					+ layoutDef.getInputWidth() + ";", "style");
			writer.writeAttribute("type", "text", "type");
			writer.writeAttribute("name", q.getId(), "name");
			writer.writeAttribute("id", q.getId(), "id");
			writer.writeAttribute("class", "textfieldEnabled", "class");

			// Javascript on textfield
			writeJsEnableAndDeselectUnknownAnd(writer, q.getId(),
					AnswerUnknown.UNKNOWN_ID);

			if (unknownAnswer) {
				writer.writeAttribute("value", "", "value");
			}
			else {
				writer.writeAttribute("value", getAnswerValue(q, theCase),
						"value");
				// writer.write(getAnswerValue(q, theCase));
			}
			writer.endElement("input");
		}

		writer.endElement("td");
		writer.endElement("tr");

		// the last false disallows fast answers generally
		renderUnknownRadioButton(writer, component, theCase, q, unknownAnswer,
				false);
	}

	private static void renderUnknownOptionElement(ResponseWriter writer,
			UIComponent component, Session theCase, Question q,
			boolean qHasUnknownAnswer, Boolean fastAnswer) throws IOException {
		writer.startElement("option", component);
		writer.writeAttribute("value", AnswerUnknown.UNKNOWN_ID, "value");
		if (qHasUnknownAnswer) {
			writer.writeAttribute("selected", "selected", "selected");
		}
		if (!isRenderUnknownAnswer(q)) {
			writer.writeAttribute("class", "invis", "class");
		}
		if (q instanceof QuestionMC) {
			writeJsDeselectAllOptionsBut(writer, q.getId(),
					AnswerUnknown.UNKNOWN_ID, fastAnswer, theCase);
		}
		else {
			if (fastAnswer) {
				// no AnswerID
				writer.writeAttribute("onclick", "saveLastClickedAnswer('" + ""
						+ "', '" + theCase.getId() + "');doSubmit();",
						"onclick");
			}
		}
		writer.writeText(DialogRenderUtils.getUnknownAnswerString(q, theCase),
				"value");
		writer.endElement("option");
	}

	private static void renderUnknownRadioButton(ResponseWriter writer,
			UIComponent component, Session session, Question q,
			boolean qHasUnknownAnswer, boolean fastAnswer) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);

		if (!isRenderUnknownAnswer(q)) {
			DialogRenderUtils.renderTableWithClass(writer, component, "invis");
		}
		else {
			DialogRenderUtils.renderTable(writer, component);
		}
		writer.startElement("tr", component);

		// first column: checkbox
		writer.startElement("td", component);
		writer.startElement("input", component);

		writer.writeAttribute("id", q.getId() + Unknown.UNKNOWN_ID, "id");
		writer.writeAttribute("type", "radio", "type");
		writer.writeAttribute("value", Unknown.UNKNOWN_ID, "value");
		writer.writeAttribute("name", q.getId(), "name");

		if (qHasUnknownAnswer) {
			writer.writeAttribute("checked", "checked", "checked");
		}

		// Javascript...
		if (q instanceof QuestionNum || q instanceof QuestionText
				|| q instanceof QuestionDate) {
			writeJsDisableTextField(writer, q.getId(), fastAnswer, session);
		}
		else if (q instanceof QuestionMC) {
			writeJsDeselectAllBut(writer, q.getId(), AnswerUnknown.UNKNOWN_ID,
					fastAnswer, session);
		}
		else if (q instanceof QuestionOC) {
			if (fastAnswer) {
				writer.writeAttribute("onclick", "saveLastClickedAnswer('" + ""
						+ "', '" + session.getId() + "'); doSubmit()",
						"onclick");
			}

		}

		writer.endElement("input");

		writer.endElement("td");

		// second column: text with label
		writer.startElement("td", component);

		writer.startElement("label", component);
		writer.writeAttribute("for", q.getId() + Unknown.UNKNOWN_ID,
				"for");

		writer.writeText(DialogRenderUtils.getUnknownAnswerString(q, session),
				"value");
		writer.endElement("label");

		writer.endElement("td");
		writer.endElement("tr");
		writer.endElement("table");

		writer.endElement("td");
		writer.endElement("tr");
	}

	private static void writeJsDeselectAllBut(ResponseWriter writer,
			String questionID, String excludedID, boolean fastAnswer,
			Session theCase) throws IOException {
		StringBuffer jsBuf = new StringBuffer();
		jsBuf.append("deselectAllBut(document.dialogForm." + questionID + ", '"
				+ excludedID + "'); ");
		if (fastAnswer) {
			jsBuf.append("saveLastClickedAnswer('" + "" + "', '"
					+ theCase.getId() + "'); doSubmit(); ");
		}
		writer.writeAttribute("onclick", jsBuf, "onclick");
	}

	private static void writeJsDeselectAllOptionsBut(ResponseWriter writer,
			String questionID, String excludedID, boolean fastAnswer,
			Session theCase) throws IOException {
		StringBuffer jsBuf = new StringBuffer();
		jsBuf.append("deselectAllOptionsBut(document.dialogForm." + questionID
				+ ", '" + excludedID + "'); ");
		if (fastAnswer) {
			jsBuf.append("saveLastClickedAnswer('" + "" + "', '"
					+ theCase.getId() + "'); doSubmit(); ");
		}
		writer.writeAttribute("onclick", jsBuf, "onclick");
	}

	private static void writeJsDeselectOptionsUnknownAnd(ResponseWriter writer,
			String questionID, String andID, Boolean fastAnswer, Session theCase)
			throws IOException {
		StringBuffer jsBuf = new StringBuffer();
		jsBuf.append("deselectOptionsUnknownAnd(document.dialogForm."
				+ questionID + ", '" + andID + "'); ");
		if (fastAnswer) {
			jsBuf.append("saveLastClickedAnswer('" + "" + "', '"
					+ theCase.getId() + "'); doSubmit(); ");
		}
		writer.writeAttribute("onclick", jsBuf, "onclick");
	}

	private static void writeJsDeselectUnknownAnd(ResponseWriter writer,
			QuestionChoice q, Choice specAns, Session theCase,
			String andID, boolean fastAnswer, boolean mCConstraintsAutoGrayOut)
			throws IOException {
		StringBuffer jsBuf = new StringBuffer();
		jsBuf.append("deselectUnknownAnd(document.dialogForm." + q.getId()
				+ ", '" + andID + "'); ");
		jsBuf.append("saveLastClickedAnswer('" + specAns.getId() + "', '"
				+ theCase.getId() + "'); ");
		if (fastAnswer) {
			// we dont need the autogreyout-javascript because question will be
			// answered immediately
			jsBuf.append("doSubmit(); ");
		}
		else if (mCConstraintsAutoGrayOut) {
			// get contraints and all already set answers
			List<List<String>> badanswersLists = (List<List<String>>) q
					.getProperties().getProperty(Property.MC_CONSTRAINTS);

			// only do something if both lists have entries
			if (badanswersLists != null && badanswersLists.size() > 0) {
				// get all answers which could be a bad combination
				List<List<String>> badAnswersForActualAnswer = new ArrayList<List<String>>();

				for (List<String> badansList : badanswersLists) {
					if (badansList.contains(specAns.getId())) {
						// actual answer is in list -> all other answers in this
						// list are bad in combination
						List<String> oneBadList = new ArrayList<String>();
						for (String badAns : badansList) {
							if (!badAns.equals(specAns.getId())) {
								oneBadList.add(badAns);
							}
						}
						badAnswersForActualAnswer.add(oneBadList);
					}
				}
				String jsString = "";
				for (List<String> oneList : badAnswersForActualAnswer) {
					for (String s : oneList) {
						jsString += s + ";";
					}
					jsString = jsString.substring(0, jsString.length() - 1); // remove
					// the
					// last
					// ";"
					jsString += "|";
				}
				if (jsString.length() > 0) {
					jsString = jsString.substring(0, jsString.length() - 1); // remove
					// the
					// last
					// "|"
				}
				// add the lists (if available) to javascript onclick
				if (jsString.length() > 0) {
					jsBuf
							.append("checkMCConstraints(this, document.dialogForm."
							+ q.getId() + ", '" + jsString + "'); ");
				}
			}
		}
		writer.writeAttribute("onclick", jsBuf, "onclick");
	}

	private static void writeJsDisableTextField(ResponseWriter writer,
			String questionID, boolean fastAnswer, Session theCase)
			throws IOException {
		StringBuffer jsBuf = new StringBuffer();
		jsBuf.append("disableTF(document.dialogForm." + questionID + "); ");
		if (fastAnswer) {
			jsBuf.append("saveLastClickedAnswer('" + "" + "', '"
					+ theCase.getId() + "'); doSubmit(); ");
		}
		writer.writeAttribute("onclick", jsBuf, "onclick");
	}

	private static void writeJsEnableAndDeselectUnknownAnd(
			ResponseWriter writer, String questionID, String unknownid)
			throws IOException {
		String jsString = "enableTF(this); deselectUnknownAnd(document.dialogForm."
				+ questionID + ", '" + unknownid + "')";
		writer.writeAttribute("onclick", jsString, "onclick");
	}

	protected static boolean showAbstract(Question q) {
		if (!DialogUtils.getDialogSettings().isShowAbstractQuestions()) {
			Object qIsAbstract = q.getProperties().getProperty(
					Property.ABSTRACTION_QUESTION);
			if (qIsAbstract != null
					&& Boolean.parseBoolean(qIsAbstract.toString()) == true) {
				// this question is abstract and we don't want to display it!
				return false;
			}
		}
		return true;
	}
}
