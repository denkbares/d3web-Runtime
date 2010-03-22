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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.XPSCase;
import de.d3web.dialog2.LastClickedAnswer;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionLayout;
import de.d3web.dialog2.basics.layout.QuestionPageLayout;
import de.d3web.dialog2.basics.layout.QuestionPopup;
import de.d3web.dialog2.util.DialogUtils;

public abstract class QContainerRenderer {

	protected XPSCase theCase;
	protected ResponseWriter writer;
	protected UIComponent component;
	protected List<Question> qList;
	protected String qContainerID;
	protected QuestionPageLayout layoutDef;

	public QContainerRenderer(ResponseWriter writer, UIComponent component, XPSCase theCase,
			List<Question> qList, QuestionPageLayout layoutDef) {
		super();
		this.component = component;
		this.theCase = theCase;
		this.writer = writer;
		this.qList = qList;
		this.layoutDef = layoutDef;

		this.qContainerID = DialogUtils.getQuestionPageBean().getActualQContainer().getId();
	}

	public void render() throws IOException {

		// start the table
		DialogRenderUtils.renderTableWithClass(writer, component, "qWrapTable", layoutDef.getGridgap(), 0);
		writer.writeAttribute("id", component.getId(), "id");
		// render border
		writer.writeAttribute("style", "border:" + layoutDef.getQContainerBorder() + ";", "style");

		// render QContainer Headline (1 row)
		renderQContainerHeadline(layoutDef);

		// render the questions (multiple rows)
		renderQuestions();

		// end the table
		writer.endElement("table");

		// render popups
		renderPopupQuestions(layoutDef);
	}

	protected abstract void renderQuestions() throws IOException;

	/**
	 * renders a complete div block with the questions marked as popup for the
	 * last clicked answer in the dialoglayout.xml
	 * 
	 * @param qList
	 * @param layoutDef
	 * @throws IOException
	 */
	private void renderPopupQuestions(QuestionPageLayout layoutDef) throws IOException {

		if (layoutDef instanceof QContainerLayout) {
			for (Question question : qList) {
				QuestionLayout questionLayout = ((QContainerLayout) layoutDef)
						.getQuestionLayoutForQuestionID(question.getId());
				if (questionLayout != null && questionLayout.getFollowingPopupQuestions() != null) {
					for (QuestionPopup questionPopup : questionLayout.getFollowingPopupQuestions()) {
						if (questionPopup.getFiringAnswerID().equals(
								LastClickedAnswer.getInstance().getLastClickedAnswerID(theCase.getId()))) {

							writer.startElement("div", component);
							// String divID = "popup" +
							// questionPopup.getFiringAnswerID() + "_"
							// + questionPopup.getNextQuestionID();
							String divID = "questionPopup";
							writer.writeAttribute("id", divID, "id");
							writer.writeAttribute("class", "questionPopup", "class");

							Question popupQuestion = theCase.getKnowledgeBase().searchQuestion(
									questionPopup.getNextQuestionID());

							DialogRenderUtils.renderTable(writer, component);
							writer.writeAttribute("style", "width: 100%; height: 100%;", "style");
							writer.startElement("tr", component);
							QuestionsRendererUtils.renderValidQuestion(writer, component, theCase,
									popupQuestion, DialogUtils.getDialogLayout().getQuestionPageLayout(), 1,
									0);
							writer.endElement("tr");
							writer.endElement("table");
							writer.endElement("div");
						}
					}
				}
			}
		}
	}

	/**
	 * This method renders the headline of the QContainer in one table row
	 * 
	 * @throws IOException
	 */
	private void renderQContainerHeadline(QuestionPageLayout layoutDef) throws IOException {
		if (!(layoutDef.getShowQContainerHeadline() != null && !layoutDef.getShowQContainerHeadline())) {
			writer.startElement("tr", component);
			writer.startElement("th", component);
			writer.writeAttribute("class", "qContainerHeadline", "class");
			if (layoutDef instanceof QContainerLayout) {
				QContainerLayout qContLayout = (QContainerLayout) layoutDef;
				writer.writeAttribute("colspan", qContLayout.getCols(), "colspan");
			} else {
				writer.writeAttribute("colspan", layoutDef.getQuestionColumns(), "colspan");
			}

			// Stylestring
			StringBuffer buf = new StringBuffer();
			buf.append("border: " + layoutDef.getQContainerHeadlineBorder() + "; ");
			buf.append("background: " + layoutDef.getQContainerHeadlineBackground() + "; ");
			buf.append("padding: " + layoutDef.getQContainerHeadlinePadding() + "; ");
			buf.append("color: " + layoutDef.getQContainerHeadlineTextColor() + "; ");
			if (layoutDef.getQContainerHeadlineFont() != null) {
				buf.append("font: " + layoutDef.getQContainerHeadlineFont() + "; ");
			}
			writer.writeAttribute("style", buf, "style");

			if (layoutDef.getQContainerHeadlineAltText() != null) {
				writer.writeText(layoutDef.getQContainerHeadlineAltText(), "value");
			} else {
				writer.writeText(DialogUtils.getQuestionPageBean().getActualQContainer().getName(), "value");
			}

			writer.endElement("th");
			writer.endElement("tr");
		}
	}

	protected List<Question> getValidQuestionsFromQList() {
		List<Question> validQuestions = new ArrayList<Question>();
		for (Iterator<Question> iter = qList.iterator(); iter.hasNext();) {
			Question q = iter.next();
			if (q.isValid(theCase) && QuestionsRendererUtils.showAbstract(q)) {
				validQuestions.add(q);
			}
		}
		return validQuestions;
	}
}