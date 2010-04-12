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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.dialog2.basics.layout.HtmlTextLayout;
import de.d3web.dialog2.basics.layout.MMInfo;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionLayout;
import de.d3web.dialog2.util.DialogUtils;

public class QContainerRendererForDefinedLayout extends QContainerRenderer {

    public QContainerRendererForDefinedLayout(ResponseWriter writer,
	    UIComponent component, Session theCase, List<Question> qList,
	    QContainerLayout layoutDef) {
	super(writer, component, theCase, qList, layoutDef);
    }

    @Override
    public void renderQuestions() throws IOException {

	QContainerLayout layoutDefinition = (QContainerLayout) layoutDef;
	// create map (Key: Q-ID; Value: Question)
	// necessary to get the remaining questions which are not listed in
	// dialoglayout

	List<Question> questionWithoutDefinition = getQuestionWithoutLayoutInfo(layoutDefinition);

	Map<String, Question> renderedQuestionMap = new LinkedHashMap<String, Question>();
	for (Question question : qList) {
	    renderedQuestionMap.put(question.getId(), question);
	}

	for (int y = 0; y < layoutDefinition.getRows(); y++) {
	    writer.startElement("tr", component);

	    for (int x = 0; x < layoutDefinition.getCols(); x++) {

		// Layout of a question/htmlText, that starts here
		QuestionLayout qDef = layoutDefinition
			.getQuestionDefinitionOnPosition(x, y);
		HtmlTextLayout htmlTextDef = layoutDefinition
			.getHtmlTextDefinitionOnPosition(x, y);

		if (qDef != null) {
		    Question q = DialogUtils.getQuestionFromQList(qList, qDef
			    .getQID());
		    if (q != null) {
			QuestionsRendererUtils.renderQuestion(writer,
				component, theCase, q, qDef, layoutDefinition
					.getCols(), 0);
			renderedQuestionMap.remove(q.getId());
		    }
		    // check if a HtmlTextDefinition can be found on this
		    // position...
		} else if (htmlTextDef != null) {
		    renderHtmlTextBox(writer, component, theCase,
			    layoutDefinition, htmlTextDef);
		} else {
		    if (!anyQuestionExtendsToCoordinate(layoutDefinition, x, y)) {
			// dynamically create LayoutDefinition for first
			// question
			// without layout:
			Question questionToRender = getNextQuestionWithoutDefinition(questionWithoutDefinition);

			if (questionToRender != null) {
			    qDef = layoutDefinition
				    .getQuestionLayoutForQuestionID(questionToRender
					    .getId());
			    int defaultColspan = layoutDefinition
				    .getDefaultColspan();

			    if (qDef == null) {
				qDef = new QuestionLayout(questionToRender
					.getId());
			    }

			    if (qDef.getPosXStart() < 0) {
				qDef.setPosX(x);
			    }
			    if (qDef.getPosYStart() < 0) {
				qDef.setPosY(y);
			    }
			    if (qDef.getColspan() < 0) {
				qDef.setColspan(defaultColspan);
			    }

			    qDef.setFastAnswer(layoutDef.getFastAnswer());

			    // if a defaultColspan was set;
			    if (qDef.getColspan() != -1) {
				x += (qDef.getColspan() - 1);
			    }

			    QuestionsRendererUtils.renderQuestion(writer,
				    component, theCase, questionToRender, qDef,
				    layoutDefinition.getCols(), 0);
			    renderedQuestionMap
				    .remove(questionToRender.getId());

			    // reset position
			    qDef.setPosY(-1);
			}
		    }
		}
	    }
	    writer.endElement("tr");
	}

	// // find and render remaining questions...
	// List<Question> remainingQuestions = new ArrayList<Question>();
	// for (Iterator<String> it = renderedQuestionMap.keySet().iterator();
	// it
	// .hasNext();) {
	// remainingQuestions.add(renderedQuestionMap.get(it.next()));
	// }
	// new QContainerRendererForUndefinedLayout(writer, component, theCase,
	// remainingQuestions, layoutDefinition).renderQuestions();

    }

    private Question getNextQuestionWithoutDefinition(
	    List<Question> questionWithoutDefinition) {
	while (!questionWithoutDefinition.isEmpty()) {
	    Question q = questionWithoutDefinition.remove(0);
	    if (q.isValid(theCase) && QuestionsRendererUtils.showAbstract(q)) {
		return q;
	    }
	}
	return null;
    }

    private boolean anyQuestionExtendsToCoordinate(
	    QContainerLayout layoutDefinition, int x, int y) {
	for (QuestionLayout qLayout : layoutDefinition.getQuestionLayoutList()) {
	    if ((qLayout.getPosXStart() <= x)
		    && ((qLayout.getPosXStart() + qLayout.getColspan()) >= x)
		    && (qLayout.getPosXStart() > -1)
		    && (qLayout.getPosYStart() <= y)
		    && ((qLayout.getPosXStart() + qLayout.getRowspan()) >= y)
		    && (qLayout.getPosYStart() > -1)) {
		return true;
	    }
	}

	return false;
    }

    private static boolean rowHasContent(int y,
	    QContainerLayout layoutDefinition, Session theCase,
	    List<Question> qList) {
	for (int x = 0; x < layoutDefinition.getCols(); x++) {
	    QuestionLayout qDef = layoutDefinition
		    .getQuestionDefinitionOnPosition(x, y);
	    if (qDef != null) {
		Question q = DialogUtils.getQuestionFromQList(qList, qDef
			.getQID());
		if (q != null && q.isValid(theCase)
			&& QuestionsRendererUtils.showAbstract(q)) {
		    return true;
		}
	    }
	    HtmlTextLayout htmlTextDef = layoutDefinition
		    .getHtmlTextDefinitionOnPosition(x, y);
	    if (htmlTextDef != null) {
		if (htmlTextDef.getQuestionBinding() != null) {
		    Question q = theCase.getKnowledgeBase().searchQuestion(
			    htmlTextDef.getQuestionBinding());
		    if (q != null && q.isValid(theCase)) {
			return true;
		    }
		} else {
		    return true;
		}
	    }
	}
	return false;
    }

    private static void renderHtmlTextBox(ResponseWriter writer,
	    UIComponent component, Session theCase,
	    QContainerLayout layoutDefinition, HtmlTextLayout htmlTextDef)
	    throws IOException {
	writer.startElement("td", component);
	if (htmlTextDef.getColspan() != 0) {
	    writer.writeAttribute("colspan", htmlTextDef.getColspan(),
		    "colspan");
	}
	if (htmlTextDef.getRowspan() != 0) {
	    writer.writeAttribute("rowspan", htmlTextDef.getRowspan(),
		    "rowspan");
	}

	// get additional class string
	String additionalClass = null;

	// TODO what else to do when questionbinding is on??
	if (htmlTextDef.getQuestionBinding() != null) {
	    QuestionLayout qLayout = layoutDefinition
		    .getQuestionLayoutForQuestionID(htmlTextDef
			    .getQuestionBinding());
	    Question q = theCase.getKnowledgeBase().searchQuestion(
		    qLayout.getQID());

	    if (qLayout.getAdditionalCSSClass() != null) {
		additionalClass = qLayout.getAdditionalCSSClass();
	    } else if (layoutDefinition.getAdditionalCSSClass() != null) {
		additionalClass = layoutDefinition.getAdditionalCSSClass();
	    }
	    if (additionalClass != null) {
		writer.writeAttribute("class", "htmlbox "
			+ QuestionsRendererUtils.getBackgroundClass(theCase, q)
			+ " " + additionalClass, "class");
	    } else {
		writer.writeAttribute("class",
			"htmlbox "
				+ QuestionsRendererUtils.getBackgroundClass(
					theCase, q), "class");
	    }
	    writer.writeAttribute("style", QuestionsRendererUtils
		    .getStyleStringForMainTableCell(layoutDefinition,
			    QuestionsRendererUtils.getBackgroundStyleString(
				    theCase, q, qLayout), htmlTextDef
				    .getAdditionalCSSStyle(), layoutDefinition
				    .getCols(), htmlTextDef.getColspan(), 0,
			    false), "style");
	} else {
	    additionalClass = htmlTextDef.getAdditionalCSSClass();
	    if (additionalClass == null) {
		additionalClass = layoutDefinition.getAdditionalCSSClass();
	    }
	    if (additionalClass != null) {
		writer.writeAttribute("class", "htmlbox " + additionalClass,
			"class");
	    } else {
		writer.writeAttribute("class", "htmlbox", "class");
	    }
	    writer.writeAttribute("style", QuestionsRendererUtils
		    .getStyleStringForMainTableCell(layoutDefinition, null,
			    htmlTextDef.getAdditionalCSSStyle(),
			    layoutDefinition.getCols(), htmlTextDef
				    .getColspan(), 0, false), "style");
	}

	renderHtmlText(writer, component, theCase, layoutDefinition,
		htmlTextDef.getText());

	writer.endElement("td");
    }

    private static void renderHtmlText(ResponseWriter writer,
	    UIComponent component, Session theCase,
	    QContainerLayout layoutDefinition, String textToCheck)
	    throws IOException {
	String[] extraTags = HtmlTextLayout.getAllExtraTags();

	// extra markup found
	while (DialogRenderUtils.hasExtraTags(textToCheck, extraTags)) {
	    Object[] posAndName = DialogRenderUtils
		    .getPositionAndNameOfFirstExtraTag(textToCheck, extraTags);
	    int pos = (Integer) posAndName[0];
	    String tag = (String) posAndName[1];
	    // before
	    // don't escape additional info stuff
	    writer.write(textToCheck.substring(0, pos));
	    // content of extra Tag
	    String starttag = DialogRenderUtils
		    .getStartTagFor(tag, textToCheck);
	    String endtag = DialogRenderUtils.getEndTagFor(tag);
	    if (endtag == null) {
		continue;
	    }
	    String content = textToCheck.substring(textToCheck
		    .indexOf(starttag)
		    + starttag.length(), textToCheck.indexOf(endtag));

	    // replace with ...
	    renderExtraTagContent(writer, component, theCase, layoutDefinition,
		    starttag, content);

	    // after
	    String newText = textToCheck.substring(textToCheck
		    .indexOf(DialogRenderUtils.getEndTagFor(tag))
		    + DialogRenderUtils.getEndTagFor(tag).length(), textToCheck
		    .length());
	    textToCheck = newText;
	}
	// render remaining text...
	// don't escape additional info stuff
	writer.write(textToCheck);
    }

    private static void renderExtraTagContent(ResponseWriter writer,
	    UIComponent component, Session theCase,
	    QContainerLayout layoutDefinition, String starttag, String content)
	    throws IOException {
	if (starttag.startsWith("<QPrompt")) {
	    writer.writeText(DialogUtils.getQPrompt(theCase.getKnowledgeBase()
		    .searchQuestion(content)), "value");
	} else if (starttag.startsWith("<QText")) {
	    writer.writeText(theCase.getKnowledgeBase()
		    .searchQuestion(content).getName(), "value");
	} else if (starttag.startsWith("<MMInfo")) {
	    QuestionLayout qLayout = layoutDefinition
		    .getQuestionLayoutForQuestionID(content);
	    MMInfo info = qLayout.getMmInfo();
	    if (info.getPosition().equals(MMInfo.POSITION_HEADLINE)) {
		DialogRenderUtils.renderMMInfoPopupLink(writer, component,
			theCase.getKnowledgeBase().searchQuestion(content),
			false, info);
	    } else {
		QuestionsRendererUtils.renderMMInfoWithoutPopup(writer,
			component, theCase.getKnowledgeBase().searchQuestion(
				content), layoutDefinition, null);
	    }
	} else if (starttag.startsWith("<Image")) {
	    // get width attribute (if available)
	    String width = null;
	    int startpos = starttag.indexOf("width=\"");
	    int startposlength = startpos + "width=\"".length();
	    if (startpos > -1) {
		width = starttag.substring(startposlength, starttag.indexOf(
			"\"", startposlength + 1));
	    }
	    starttag.substring(startposlength);
	    writer.startElement("img", component);
	    writer.writeAttribute("alt", content, "alt");
	    writer.writeAttribute("src", "kbResources/"
		    + theCase.getKnowledgeBase().getId() + "/multimedia/"
		    + content, "src");
	    if (width != null) {

		writer.writeAttribute("style", "width: " + width + "; ",
			"style");
	    }
	    writer.endElement("img");
	}
    }

    private List<Question> getQuestionWithoutLayoutInfo(
	    QContainerLayout qContLayout) {
	List<Question> questions = new ArrayList<Question>();

	for (Question q : qList) {
	    QuestionLayout questionLayoutForQuestionID = qContLayout
		    .getQuestionLayoutForQuestionID(q.getId());
	    if (questionLayoutForQuestionID == null
		    || questionLayoutForQuestionID.getPosYStart() < 0) {
		questions.add(q);
	    }
	}
	return questions;
    }
}
