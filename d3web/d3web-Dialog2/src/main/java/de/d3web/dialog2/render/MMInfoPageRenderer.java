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
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.info.MMInfoObject;
import de.d3web.core.knowledge.terminology.info.MMInfoSubject;
import de.d3web.core.session.XPSCase;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.component.html.UIMMInfoPage;
import de.d3web.dialog2.util.DialogUtils;

public class MMInfoPageRenderer extends Renderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	XPSCase theCase = DialogUtils.getDialog().getTheCase();

	String kbid = theCase.getKnowledgeBase().getId();

	// TODO show not all infos?
	// String infoValue = (String) ((UIMMInfoPage) component).getValue();

	String qOrDiagID = ((UIMMInfoPage) component).getDiag();

	NamedObject diagOrQuestion = theCase.getKnowledgeBase()
		.searchDiagnosis(qOrDiagID);
	// diagnosis or qaset
	if (diagOrQuestion == null) {
	    diagOrQuestion = theCase.getKnowledgeBase().searchQASet(qOrDiagID);
	}

	DialogRenderUtils
		.renderTableWithClass(writer, component, "mminfotable");
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageWithParamsFor("mminfo.title",
		new Object[] { diagOrQuestion.getName() }), "value");
	writer.endElement("th");

	List<MMInfoObject> mmInfoTextList = DialogRenderUtils.getMMInfo(
		diagOrQuestion, MMInfoSubject.INFO);
	List<MMInfoObject> mmInfoURLList = DialogRenderUtils.getMMInfo(
		diagOrQuestion, MMInfoSubject.URL);
	List<MMInfoObject> mmInfomultimediaList = DialogRenderUtils.getMMInfo(
		diagOrQuestion, MMInfoSubject.MULTIMEDIA);

	if (DialogRenderUtils.isMMInfoAvailable(new List[] { mmInfoTextList,
		mmInfoURLList, mmInfomultimediaList })) {

	    for (Iterator<MMInfoObject> iter = mmInfoTextList.iterator(); iter
		    .hasNext();) {
		MMInfoObject obj = iter.next();

		writer.startElement("tr", component);
		writer.startElement("td", component);

		DialogRenderUtils.renderAdditionalInfoWithReplacedExtraMarkup(
			writer, component, diagOrQuestion, obj.getContent());

		writer.endElement("td");
		writer.endElement("tr");
	    }
	    for (Iterator<MMInfoObject> iter = mmInfoURLList.iterator(); iter
		    .hasNext();) {
		MMInfoObject obj = iter.next();

		writer.startElement("tr", component);
		writer.startElement("td", component);
		writer.startElement("a", component);
		writer.writeAttribute("href", obj.getContent(), "href");
		writer.writeAttribute("title", DialogUtils
			.getMessageFor("mminfo.url.title"), "title");
		writer.writeAttribute("target", "_blank", "target");
		writer.writeText(obj.getContent(), "value");
		writer.endElement("a");

		writer.endElement("td");
		writer.endElement("tr");
	    }
	    for (Iterator<MMInfoObject> iter = mmInfomultimediaList.iterator(); iter
		    .hasNext();) {
		MMInfoObject obj = iter.next();
		writer.startElement("tr", component);
		writer.startElement("td", component);

		writer.startElement("a", component);
		writer.writeAttribute("href", ResourceRepository
			.getMMPathForKB(kbid)
			+ obj.getContent(), "href");
		writer.writeAttribute("target", "_blank", "target");
		writer.writeAttribute("title", DialogUtils
			.getMessageFor("mminfo.multimedialink.title"), "title");
		writer.startElement("img", component);
		writer.writeAttribute("src", ResourceRepository
			.getMMPathForKB(kbid)
			+ obj.getContent(), "src");
		writer.writeAttribute("alt", obj.getContent(), "alt");
		writer.endElement("img");
		writer.endElement("a");

		writer.endElement("td");
		writer.endElement("tr");
	    }

	}
	writer.startElement("tr", component);
	writer.startElement("td", component);
	writer.writeAttribute("align", "right", "align");

	writer.startElement("a", component);
	writer.writeAttribute("class", "close", "class");
	writer.writeAttribute("title", DialogUtils
		.getMessageFor("mminfo.closebutton.text"), "title");
	writer.writeAttribute("href", "javascript:window.close()", "href");
	writer.writeText(DialogUtils.getMessageFor("mminfo.closebutton.text"),
		"value");
	writer.endElement("a");

	writer.endElement("td");
	writer.endElement("tr");
	writer.endElement("table");
    }
}
