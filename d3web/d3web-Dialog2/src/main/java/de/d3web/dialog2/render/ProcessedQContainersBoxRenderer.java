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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.ajax4jsf.ajax.html.HtmlAjaxCommandLink;
import org.apache.myfaces.component.html.ext.HtmlOutputText;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.dialog2.controller.ProcessedQContainersController;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.QuestionDateUtils;

public class ProcessedQContainersBoxRenderer extends Renderer {

	private static boolean checkDisplayability(Session theCase, List<QContainer> processedContainers) {
		for (Iterator<QContainer> iter = processedContainers.iterator(); iter.hasNext();) {
			QContainer cont = iter.next();
			List<Question> qList = DialogUtils.getQuestionPageBean().convertQuestionsToRender(cont);
			for (Iterator<Question> iter2 = qList.iterator(); iter2.hasNext();) {
				Question q = iter2.next();
				if (q.isDone(theCase)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean qInContainerAnswered(Session session, List<Question> qList, boolean showUnknown) {
		for (Iterator<Question> iter2 = qList.iterator(); iter2.hasNext();) {
			Question q = iter2.next();
			// if we only want to display known answers and the answer is
			// unknown -> continue
			if (!showUnknown && DialogUtils.unknownAnswerInValueList(q, session)) {
				continue;
			}

			// if we do not want to show abstract Questions, and q is abstract
			// => continue
			if (isAbstractQuestion(q)
					&& !DialogUtils.getDialogSettings().isShowAbstractQuestionsInResultPage()) {
				continue;
			}
			if (DialogUtils.hasValue(q, session)) {
				return true;
			}
		}
		return false;
	}

	private static void renderProcessedQContainer(ResponseWriter writer, UIComponent component,
			QContainer cont, String dialogMode) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeAttribute("colspan", "2", "colspan");
		writer.writeAttribute("style", "font-weight: normal;", "style");

		if (dialogMode.equals("MQ")) {
			// render link
			HtmlAjaxCommandLink facet = (HtmlAjaxCommandLink) component.getFacet("link");
			if (facet != null) {
				facet.setTitle(DialogUtils.getMessageWithParamsFor("processed.moveToContainer",
						new Object[] { cont.getName() }));
				facet.setOnclick("cursor_wait();setClickedQASet('" + cont.getId() + "')");
				HtmlOutputText comp = (HtmlOutputText) FacesContext.getCurrentInstance().getApplication()
						.createComponent(HtmlOutputText.COMPONENT_TYPE);
				comp.setValue(cont.getName());
				comp.setId(component.getId() + "_jump_cont_" + cont.getId());
				facet.encodeBegin(FacesContext.getCurrentInstance());
				comp.encodeAll(FacesContext.getCurrentInstance());
				facet.encodeEnd(FacesContext.getCurrentInstance());
			}
		}
		else {
			writer.writeText(cont.getName(), "value");
		}
		writer.endElement("th");
		writer.endElement("tr");
	}

	private static void renderProcessedQContainersBox(FacesContext context, UIComponent component,
			Session theCase, List<QContainer> processedContainers,
			ProcessedQContainersController processedBean) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		DialogRenderUtils.renderTableWithClass(writer, component, "panelBox processedBox");
		writer.writeAttribute("id", component.getClientId(context), "id");
		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeAttribute("colspan", "2", "colspan");

		UIComponent unknowncheckbox = component.getFacet("unknownbox");
		if (unknowncheckbox != null) {
			DialogRenderUtils.renderChild(FacesContext.getCurrentInstance(), unknowncheckbox);
		}

		UIComponent qContToggler = component.getFacet("qContNames");
		if (qContToggler != null) {
			DialogRenderUtils.renderChild(FacesContext.getCurrentInstance(), qContToggler);
		}

		writer.startElement("div", component);
		writer.writeAttribute("class", "proc_hl", "class");
		writer.writeText(DialogUtils.getMessageFor("processed.title"), "value");
		writer.endElement("div");

		writer.endElement("th");
		writer.endElement("tr");

		boolean renderMoreLessLink = false;
		boolean moreThanMaxShown = false;
		boolean finish = false;
		int counter = 0;
		List<Question> questionToRenderList = new ArrayList<Question>();
		for (Iterator<QContainer> iter = processedContainers.iterator(); iter.hasNext();) {
			QContainer cont = iter.next();
			List<Question> qList = DialogUtils.getQuestionPageBean().convertQuestionsToRender(cont);
			// if no answer is set in this QContainer -> take next QContainer
			if (!qInContainerAnswered(theCase, qList, processedBean.isShowUnknown())) {
				continue;
			}
			if (finish) {
				// we dont render the next question but we know now that another
				// question is answered and we need the "more/less" button
				renderMoreLessLink = true;
				break;
			}
			// if a question in this container is done, the QContainer will be
			// rendered...
			if (processedBean.isShowQContainerNames()) {
				renderProcessedQContainer(writer, component, cont, DialogUtils.getDialogSettings()
						.getDialogMode());
			}

			for (int j = 0; j < qList.size(); j++) {
				Question q = qList.get(j);
				if (UndefinedValue.isNotUndefinedValue(theCase.getBlackboard().getValue(q))) {
					if (!processedBean.isShowUnknown()
							&& DialogUtils.unknownAnswerInValueList(q, theCase)) {
						continue;
					}
					if (isAbstractQuestion(q)
							&& !DialogUtils.getDialogSettings().isShowAbstractQuestionsInResultPage()) {
						continue;
					}
					if (finish) {
						// we dont render the next question but we know now that
						// another question is aswerered and we need the
						// "more/less" button
						renderMoreLessLink = true;
						break;
					}
					if (moreThanMaxShown) {
						// more than max are shown, so we have to render the
						// "more/less"-link
						renderMoreLessLink = true;
					}
					if (processedBean.isShowQContainerNames()) {
						renderProcessedQuestion(writer, component, q, theCase,
								processedBean.getQTextMode());
					}
					else {
						questionToRenderList.add(q);
					}

					counter++;
					if (counter >= processedBean.getMaxInput()) {
						// if not all processed should be shown -> mark as
						// "finished"
						if (!processedBean.isShowAll()) {
							finish = true;
						}
						else {
							// all should be shown
							moreThanMaxShown = true;
						}
					}
				}
			}
		}
		if (!processedBean.isShowQContainerNames() && questionToRenderList.size() > 0) {
			// sort list alphabetical
			Comparator<Question> qCompAsc = new Comparator<Question>() {

				public int compare(Question a, Question b) {
					Collator collator = Collator.getInstance(DialogUtils.getLocaleBean().getLocale());
					if (collator.compare(a.getName(), b.getName()) < 0) {
						return -1;
					}
					else if (collator.compare(a.getName(), b.getName()) > 0) {
						return 1;
					}
					else
					return 0;
				}
			};
			Collections.sort(questionToRenderList, qCompAsc);
			for (Question q : questionToRenderList) {
				renderProcessedQuestion(writer, component, q, theCase, processedBean.getQTextMode());
			}
		}

		// render "showAll"-button if more than "showMax" are processed
		if (renderMoreLessLink) {
			writer.startElement("tr", component);
			writer.startElement("th", component);
			writer.writeAttribute("class", "procright", "class");
			writer.writeAttribute("colspan", "2", "colspan");
			UICommand facet = (UICommand) component.getFacet("toggleLink");
			if (facet != null) {
				if (processedBean.isShowAll()) {
					facet.setValue(DialogUtils.getMessageFor("processed.showless"));
				}
				else {
					facet.setValue(DialogUtils.getMessageFor("processed.showall"));
				}
				DialogRenderUtils.renderChild(FacesContext.getCurrentInstance(), facet);
			}
			writer.endElement("th");
			writer.endElement("tr");
		}
		writer.endElement("table"); // end panelBox table
	}

	private static void renderProcessedQuestion(ResponseWriter writer, UIComponent component, Question q,
			Session theCase, String qTextMode) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);

		// render link
		HtmlAjaxCommandLink facet = (HtmlAjaxCommandLink) component.getFacet("link");
		if (facet != null) {
			facet.setTitle(DialogUtils.getMessageWithParamsFor("processed.moveToQuestion",
					new Object[] { q
					.getName() }));
			facet.setOnclick("cursor_wait();setClickedQASet('" + q.getId() + "')");

			HtmlOutputText comp = (HtmlOutputText) FacesContext.getCurrentInstance().getApplication()
					.createComponent(HtmlOutputText.COMPONENT_TYPE);
			String abstractFlag = "";
			if (q.getProperties().getProperty(Property.ABSTRACTION_QUESTION) != null
					&& q.getProperties().getProperty(Property.ABSTRACTION_QUESTION) instanceof Boolean
					&& ((Boolean) q.getProperties().getProperty(Property.ABSTRACTION_QUESTION))
							.booleanValue()) {
				abstractFlag = " (abstr.)";
			}
			if (qTextMode.equals(ProcessedQContainersController.QTEXTMODE_PROMPT + abstractFlag)) {
				comp.setValue(DialogUtils.getQPrompt(q));
			}
			else {

				comp.setValue(q.getName() + abstractFlag);
			}
			comp.setId(component.getId() + "_jump_q_" + q.getId());
			facet.encodeBegin(FacesContext.getCurrentInstance());
			comp.encodeAll(FacesContext.getCurrentInstance());
			facet.encodeEnd(FacesContext.getCurrentInstance());
		}

		writer.endElement("td");
		writer.startElement("td", component);
		writer.startElement("span", component);
		writer.writeAttribute("id", component.getId() + "_ans_" + q.getId(), "id");

		Value ans = theCase.getBlackboard().getValue(q);

		if (ans instanceof ChoiceValue) {
			String valueID = ((Choice) ans.getValue()).getId();
			QuestionChoice qC = (QuestionChoice) q;
			List<Choice> alterList = qC.getAllAlternatives();
			for (Iterator<Choice> it = alterList.iterator(); it.hasNext();) {
				Choice alterAns = it.next();
				if (alterAns.getId().equals(valueID)) {
					writer.writeText(alterAns.getName(), "value");
					break;
				}
			}
		}
		else if (ans instanceof MultipleChoiceValue) {
			writer.writeText(((MultipleChoiceValue) ans).getName(), "value");
		}
		else if (ans instanceof Unknown) {
			writer.writeText(DialogRenderUtils.getUnknownAnswerString(q,
					theCase), "value");
		}
		else if (ans instanceof DateValue) {
			String dateanswer = QuestionDateUtils.dateToString(
					(QuestionDate) q,
					(Date) ans.getValue(), theCase);
			writer.writeText(dateanswer, "value");
		}
		else {
			writer.writeText(ans.toString(), "value");
		}

		writer.endElement("span");
		writer.endElement("td");
		writer.endElement("tr");
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Session theCase = DialogUtils.getDialog().getSession();
		ProcessedQContainersController processedBean = DialogUtils.getProcessedQContainersBean();
		// MQDialogController mqdc = DialogUtils.getMQDialogController(theCase);
		// List<QContainer> processedContainers = mqdc.getProcessedContainers();
		List<QContainer> processedContainers = theCase.getKnowledgeBase().getQContainers();
		if (checkDisplayability(theCase, processedContainers)) {
			renderProcessedQContainersBox(context, component, theCase, processedContainers,
					processedBean);
		}
	}

	private static boolean isAbstractQuestion(Question q) {
		Object qIsAbstract = q.getProperties().getProperty(Property.ABSTRACTION_QUESTION);
		if (qIsAbstract != null && Boolean.parseBoolean(qIsAbstract.toString()) == true) {
			// this question is abstract and we don't want to display it!
			return true;
		}
		return false;
	}

}
