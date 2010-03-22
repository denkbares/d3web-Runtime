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

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionPageLayout;
import de.d3web.dialog2.imagemap.Image;
import de.d3web.dialog2.imagemap.ImageMapAnswerIcon;
import de.d3web.dialog2.imagemap.Region;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.QuestionDateUtils;

public class QuestionImageMapRendererUtils {

	private static boolean allQuestionsInImageAnswered(Image image, XPSCase theCase) {
		for (Region r : image.getRegions()) {
			Question q = theCase.getKnowledgeBase().searchQuestion(r.getQuestionID());
			if (q != null && !q.isDone(theCase)) {
				return false;
			}
		}
		return true;
	}

	private static Answer currentAnswerIDsForQuestionID(String questionID, XPSCase theCase,
			List<Question> qList) {
		for (Question question : qList) {
			if (question.getId().equals(questionID)) {
				return question.getValue(theCase);
			}
		}
		return null;
	}

	private static ImageMapAnswerIcon getActualAnswerImage(Image image, Answer answer) {
		for (ImageMapAnswerIcon ai : image.getAnswerImages()) {
			if (answer.getId().equals(ai.getId())) {
				return ai;
			}
		}
		return null;
	}

	private static String getAnswerText(Question q, XPSCase theCase, Answer answer) {
		String answerText = answer.toString();
		if (answerText.equals(AnswerUnknown.UNKNOWN_VALUE)) {
			return DialogUtils.getMessageFor("dialog.unknown");
		} else if (q != null && q instanceof QuestionDate) {
			Answer ans = q.getValue(theCase);
			return QuestionDateUtils.dateToString((AnswerDate) ans, theCase);
		}
		return answerText;
	}

	private static String getDimensionString(Region region, ImageMapAnswerIcon answerIcon) {
		StringBuffer sb = new StringBuffer();
		if (answerIcon == null) {
			String[] coordsSplit = region.getCoords().split(",");
			// no ImageMapAnswerIcon -> dimension from coords
			sb.append("width: " + (Integer.parseInt(coordsSplit[2]) - Integer.parseInt(coordsSplit[0]))
					+ "px; ");
			sb.append("height: " + (Integer.parseInt(coordsSplit[3]) - Integer.parseInt(coordsSplit[1]))
					+ "px; ");
		}
		return sb.toString();
	}

	private static String getImageMapBackgroundClass(Image image, XPSCase theCase) {
		Question firstToAsk = DialogUtils.getQuestionPageBean().getFirstQToAsk();
		if (firstToAsk != null && questionIdInImage(image, firstToAsk.getId())) {
			return "currentQ";
		} else if (allQuestionsInImageAnswered(image, theCase)) {
			return "answeredQ";
		} else {
			return "unansweredQ";
		}
	}

	private static String getImageMapBackgroundColorString(Image image, XPSCase theCase,
			QuestionPageLayout layoutDef) {
		Question firstToAsk = DialogUtils.getQuestionPageBean().getFirstQToAsk();
		if (firstToAsk != null && questionIdInImage(image, firstToAsk.getId())) {
			return layoutDef.getCurrentQuestionBackground();
		} else if (allQuestionsInImageAnswered(image, theCase)) {
			return layoutDef.getAnsweredQuestionBackground();
		} else {
			return layoutDef.getUnansweredQuestionBackground();
		}
	}

	private static String getImageSrc(ImageMapAnswerIcon answerImage, String srcDir) {
		String ret = null;
		if (answerImage != null) {
			ret = srcDir + answerImage.getSrc();
		}
		return ret;
	}

	private static String getPositionString(Region region, ImageMapAnswerIcon answerIcon) {
		StringBuffer sb = new StringBuffer();
		if (answerIcon != null) {
			// Region has an ImageMapAnswerIcon
			if (!answerIcon.getCoords().equals("")) {
				String[] coordsSplit = answerIcon.getCoords().split(",");
				sb.append("left: " + coordsSplit[0] + "px; top:" + coordsSplit[1] + "px; ");
			} else {
				String[] coordsSplit = region.getCoords().split(",");
				if (region.getShape().equals("rect")) {
					// centered
					sb.append("left: "
							+ ((Integer.parseInt(coordsSplit[2]) - Integer.parseInt(coordsSplit[0])) / 2
									+ Integer.parseInt(coordsSplit[0]) - 6) + "px; ");
					sb.append("top: "
							+ ((Integer.parseInt(coordsSplit[3]) - Integer.parseInt(coordsSplit[1])) / 2
									+ Integer.parseInt(coordsSplit[1]) - 10) + "px; ");
				} else if (region.getShape().equals("circle")) {
					sb.append("left: " + (Integer.parseInt(coordsSplit[0]) - 8) + "px; top: "
							+ (Integer.parseInt(coordsSplit[1]) - 7) + "px; ");
				}
			}
		} else {
			// region has no ImageMapAnswerIcon, so we start at position of
			// coords
			String[] coordsSplit = region.getCoords().split(",");
			sb.append("left: " + coordsSplit[0] + "px; top:" + coordsSplit[1] + "px; ");
		}
		return sb.toString();
	}

	private static List<Question> getQuestionsNotInImage(Image image, List<Question> qList) {
		List<Question> remainingQuestions = new ArrayList<Question>();
		for (Question q : qList) {
			boolean inRegionList = false;
			for (Region r : image.getRegions()) {
				String qID = r.getQuestionID();
				if (q.getId().equals(qID)) {
					inRegionList = true;
					break;
				}
			}
			if (!inRegionList) {
				remainingQuestions.add(q);
			}
		}
		return remainingQuestions;
	}

	private static Object getToolTipString(Question q, XPSCase theCase, Answer answer) {
		return "Tip('" + getAnswerText(q, theCase, answer) + "', CLOSEBTN, false, STICKY, false)";
	}

	private static boolean questionIdInImage(Image image, String id) {
		for (Region r : image.getRegions()) {
			if (r.getQuestionID().equals(id)) {
				return true;
			}
		}
		return false;
	}

	private static void renderClickableRegions(ResponseWriter writer, UIComponent component, Image image,
			XPSCase theCase, List<Question> qList, String srcDir, QuestionPageLayout layoutDef)
			throws IOException {
		// Draw clickable regions
		for (Region region : image.getRegions()) {

			Answer answer = currentAnswerIDsForQuestionID(region.getQuestionID(), theCase, qList);
			Question q = theCase.getKnowledgeBase().searchQuestion(region.getQuestionID());

			ImageMapAnswerIcon answerImage = getActualAnswerImage(image, answer);
			String imageSrc = getImageSrc(answerImage, srcDir);

			writer.startElement("a", component);

			StringBuffer buf = new StringBuffer();
			buf.append("clickable");
			if (layoutDef.isShowImageMapRegionOnMouseOver()) {
				buf.append(" imgmap");
			}
			writer.writeAttribute("class", buf, "class");
			writer.writeAttribute("id", "clickable_" + region.getQuestionID(), "id");
			writer.writeAttribute("href", "#", "href");
			writer.writeAttribute("title", DialogUtils.getMessageWithParamsFor("dialog.imagemapAnswerTitle",
					new Object[] { q.getName() }), "title");

			writer.writeAttribute("style", getPositionString(region, answerImage)
					+ getDimensionString(region, answerImage), "style");

			if (region.isRotate()) {
				QuestionChoice qc = (QuestionChoice) DialogUtils.getQuestionFromQList(qList, region
						.getQuestionID());

				boolean useNext = false;
				if (answer != null) {
					if (answer.getId().equals(AnswerUnknown.UNKNOWN_ID)) {
						useNext = true;
					}
				} else {
					useNext = true;
				}

				String nextAnswerID = "";
				for (Object object : qc.getAllAlternatives()) {
					AnswerChoice ac = (AnswerChoice) object;
					if (useNext) {
						nextAnswerID = ac.getId();
						useNext = false;
						break;
					}
					if (answer != null) {
						if (ac.getId().equals(answer.getId())) {
							// This is the current answer. The next one is to be
							// selected on clicking
							useNext = true;
						}
					}
				}
				if (nextAnswerID.equals("")) {
					nextAnswerID = AnswerUnknown.UNKNOWN_ID;
				}

				if (imageSrc != null) {
					writer.writeAttribute("onclick", "setAns('" + region.getQuestionID() + "','"
							+ nextAnswerID + "'); return false", "onclick");
					writer
							.writeAttribute("onmouseover", getToolTipString(q, theCase, answer),
									"onmouseover");
					renderImageMapAnswerIcon(writer, component, q, answer, imageSrc);
				} else {
					writer.writeAttribute("onclick", "setAns('" + region.getQuestionID() + "','"
							+ nextAnswerID + "'); return false;", "onclick");
					renderSpacerImage(writer, component, getDimensionString(region, answerImage));
				}
			} else if (!region.getTextCoords().equals("")) {
				// Open menu on questionable region
				writer.writeAttribute("onclick", "openQuestion(event,'" + region.getQuestionID()
						+ "'); return false;", "onclick");
				renderSpacerImage(writer, component, getDimensionString(region, answerImage));
				writer.endElement("a");

				// And also open menu on answer text region
				String[] textCoords = region.getTextCoords().split(",");

				writer.startElement("a", component);
				writer.writeAttribute("href", "#", "href");
				writer.writeAttribute("title", DialogUtils.getMessageWithParamsFor(
						"dialog.imagemapAnswerTitle", new Object[] { q.getName() }), "title");
				writer.writeAttribute("style", "position: absolute; cursor: pointer; left: " + textCoords[0]
						+ "px; top: " + textCoords[1] + "px;", "style");
				writer.writeAttribute("onclick", "openQuestion(event,'" + region.getQuestionID()
						+ "'); return false;", "onclick");
				writer.writeText(getAnswerText(q, theCase, answer), "value");
			} else {
				if (imageSrc != null) {
					if (!(region.isRotate() || region.isMC())) {
						writer.writeAttribute("onclick", "openQuestion(event,'" + region.getQuestionID()
								+ "'); return false;", "onclick");
						writer.writeAttribute("onmouseover", getToolTipString(q, theCase, answer),
								"onmouseover");
						renderImageMapAnswerIcon(writer, component, q, answer, imageSrc);
					}
				} else {
					writer.writeAttribute("onclick", "openQuestion(event,'" + region.getQuestionID()
							+ "'); return false;", "onclick");
					renderSpacerImage(writer, component, getDimensionString(region, answerImage));
				}
			}
			writer.endElement("a");
		}
	}

	private static void renderErrors(ResponseWriter writer, UIComponent component, List<String> idsInImage,
			XPSCase theCase) throws IOException {
		for (String qID : idsInImage) {
			Iterator<FacesMessage> it = FacesContext.getCurrentInstance().getMessages(qID);
			while (it.hasNext()) {
				FacesMessage msg = it.next();
				writer.startElement("div", component);
				writer.writeAttribute("id", "imgmap_q_" + idsInImage + "_error", "id");
				writer.writeAttribute("class", "validationerror", "class");
				Question q = theCase.getKnowledgeBase().searchQuestion(qID);
				if (q != null) {
					writer.writeText(q.getName() + ": " + msg.getSummary(), "value");
				} else {
					writer.writeText(msg.getSummary(), "value");
				}
				writer.endElement("div");
			}
		}
	}

	private static void renderImageMapAnswerIcon(ResponseWriter writer, UIComponent component, Question q,
			Answer answer, String imageSrc) throws IOException {
		writer.startElement("img", component);
		if (q != null && answer != null) {
			writer.writeAttribute("id", "answerImg_q_" + q.getId() + "_" + answer.getId(), "id");
		}
		writer.writeAttribute("src", imageSrc, "src");
		writer.writeAttribute("alt", "ans", "alt");
		writer.endElement("img");
	}

	private static void renderPopupMenus(ResponseWriter writer, UIComponent component, Image image,
			XPSCase theCase, List<Question> qList, QuestionPageLayout layoutDef) throws IOException {
		for (Region region : image.getRegions()) {
			writer.startElement("div", component);
			writer.writeAttribute("class", "popupmenu", "class");
			writer.writeAttribute("id", "pm_" + region.getQuestionID(), "id");
			// JS so that the popup will not disappear when the mouse is IN the
			// popup
			writer.writeAttribute("onmouseover", "disableHide()", "onmouseover");
			writer.writeAttribute("onmouseout", "enableHide()", "onmouseout");

			// get the question for the region
			Question q = null;
			for (Question object : qList) {
				if (object.getId().equals(region.getQuestionID())) {
					q = object;
					break;
				}
			}

			if (q == null) {
				return;
			}

			QuestionPageLayout layout = null;
			// get question style (if available) ...
			if (layoutDef instanceof QContainerLayout) {
				QContainerLayout qContL = (QContainerLayout) layoutDef;
				if (qContL.getQuestionLayoutForQuestionID(q.getId()) != null) {
					layout = qContL.getQuestionLayoutForQuestionID(q.getId());
				}
			}
			if (layout == null) {
				layout = layoutDef;
			}

			DialogRenderUtils.renderTable(writer, component);
			writer.startElement("tr", component);
			QuestionsRendererUtils.renderQuestion(writer, component, theCase, q, layout, 1, 0);
			writer.endElement("tr");
			writer.endElement("table");

			writer.endElement("div");
		}
	}

	public static void renderQuestionsImageMap(ResponseWriter writer, UIComponent component, XPSCase theCase,
			List<Question> qList, QuestionPageLayout layoutDef) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);
		writer.writeAttribute("id", "qTableCell_imgmap", "id");
		Image image = DialogUtils.getImageMapBean().getImageForQList(qList);
		int cols = layoutDef.getQuestionColumns();

		writer.writeAttribute("style", QuestionsRendererUtils.getStyleStringForMainTableCell(layoutDef,
				getImageMapBackgroundColorString(image, theCase, layoutDef), null, cols, cols, 0, false),
				"style");

		// check out colspan...

		if (layoutDef instanceof QContainerLayout) {
			QContainerLayout qContLayout = (QContainerLayout) layoutDef;
			writer.writeAttribute("colspan", qContLayout.getCols(), "colspan");
			cols = qContLayout.getCols();
		} else {
			writer.writeAttribute("colspan", cols, "colspan");
		}

		StringBuffer classString = new StringBuffer();
		classString.append("qTable " + getImageMapBackgroundClass(image, theCase));
		if (layoutDef.getAdditionalCSSClass() != null) {
			classString.append(" " + layoutDef.getAdditionalCSSClass());
		}
		writer.writeAttribute("class", classString, "class");

		// render question headline
		if (layoutDef.getShowQuestionHeadline()) {
			QuestionsRendererUtils.renderQuestionHeadline(writer, component, null, layoutDef);
		}

		QuestionsRendererUtils.renderQuestionContentTableStart(writer, component, layoutDef);
		writer.startElement("tr", component);
		writer.startElement("td", component);

		String src = DialogUtils.getImageMapBean().getSrcDir();

		// Draw image
		writer.startElement("div", component);
		writer.writeAttribute("id", "imagemap_image", "id");
		// get width and height so that the pic can be centered...
		Dimension d = DialogUtils.getImageDimension(theCase, image.getSrc());
		if (d != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("width: " + d.getWidth() + "px;");
			buffer.append("margin-left: auto;");
			buffer.append("margin-right: auto;");
			writer.writeAttribute("style", buffer.toString(), "style");
		}

		writer.startElement("img", component);
		writer.writeAttribute("src", src + image.getSrc(), "src");
		writer.writeAttribute("alt", "image", "alt");
		writer.endElement("img");

		renderClickableRegions(writer, component, image, theCase, qList, src, layoutDef);
		writer.endElement("div");

		renderPopupMenus(writer, component, image, theCase, qList, layoutDef);

		writer.endElement("td"); // end question-content cell
		writer.endElement("tr");
		writer.endElement("table");

		// render errors
		// generate a list with all ids in the current image
		List<String> idsInImage = new ArrayList<String>();
		for (Region r : image.getRegions()) {
			idsInImage.add(r.getQuestionID());
		}
		renderErrors(writer, component, idsInImage, theCase);

		writer.endElement("td"); // end main question cell
		writer.endElement("tr");

		// if some questions are not listed in "imagemap.xml" -> render them...
		List<Question> questionsNotInImage = getQuestionsNotInImage(image, qList);
		if (questionsNotInImage.size() > 0) {
			if (layoutDef instanceof QContainerLayout) {
				new QContainerRendererForDefinedLayout(writer, component, theCase, questionsNotInImage,
						(QContainerLayout) layoutDef).render();
			} else {
				new QContainerRendererForUndefinedLayout(writer, component, theCase, questionsNotInImage,
						layoutDef).render();
			}
		}
	}

	private static void renderSpacerImage(ResponseWriter writer, UIComponent component, String dimensionString)
			throws IOException {
		writer.startElement("img", component);
		writer.writeAttribute("src", "images/spacer.gif", "src");
		writer.writeAttribute("alt", "space", "alt");
		writer.writeAttribute("style", dimensionString, "style");
		writer.endElement("img");

	}
}
