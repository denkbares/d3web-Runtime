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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.MMInfoObject;
import de.d3web.core.knowledge.terminology.info.MMInfoStorage;
import de.d3web.core.knowledge.terminology.info.MMInfoSubject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.dialog2.basics.layout.MMInfo;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.scoring.inference.PSMethodHeuristic;

public class DialogRenderUtils {

	public static Logger logger = Logger.getLogger(DialogRenderUtils.class);

	private static String getDiagnosesLinkOnclickString(Solution diag) {
		StringBuffer onclickString = new StringBuffer();
		onclickString.append("Tip(initExpPopup('" + diag.getId() + "'");
		if (DialogUtils.getDialogSettings().isShowDiagReason()) {
			onclickString.append(", '"
					+ DialogUtils.getMessageFor("explain.popup.reason") + "'");
		} else {
			onclickString.append(", ''");
		}
		if (DialogUtils.getDialogSettings().isShowDiagConcreteDerivation()) {
			onclickString.append(", '"
					+ DialogUtils
							.getMessageFor("explain.popup.concrete_derivation")
					+ "'");
		} else {
			onclickString.append(", ''");
		}
		if (DialogUtils.getDialogSettings().isShowDiagDerivation()) {
			onclickString.append(", '"
					+ DialogUtils.getMessageFor("explain.popup.derivation")
					+ "'");
		} else {
			onclickString.append(", ''");
		}
		onclickString.append(")); return false;");
		return onclickString.toString();
	}

	public static String getEndTagFor(String tag) {
		return "</" + tag + ">";
	}

	public static List<MMInfoObject> getMMInfo(NamedObject diagOrQuestion,
			MMInfoSubject subj) {
		MMInfoStorage storage = (MMInfoStorage) diagOrQuestion.getProperties()
				.getProperty(Property.MMINFO);
		if (storage != null) {
			DCMarkup dcMarkup = new DCMarkup();
			dcMarkup.setContent(DCElement.SOURCE, diagOrQuestion.getId());
			dcMarkup.setContent(DCElement.SUBJECT, subj.getName());
			List<MMInfoObject> ret = new ArrayList<MMInfoObject>();
			Set<MMInfoObject> info = storage.getMMInfo(dcMarkup);
			if (info != null) {
				Iterator<MMInfoObject> iter = info.iterator();
				while (iter.hasNext()) {
					ret.add(iter.next());
				}
			}
			return ret;
		} else {
			return new ArrayList<MMInfoObject>();
		}
	}

	public static String getMMInfoStringForQuestion(Question q) {
		StringBuffer buf = new StringBuffer();
		List<MMInfoObject> mmInfoTextList = getMMInfo(q, MMInfoSubject.INFO);
		for (int i = 0; i < mmInfoTextList.size(); i++) {
			MMInfoObject obj = mmInfoTextList.get(i);
			buf.append(obj.getContent());
			if (i != mmInfoTextList.size() - 1) {
				buf.append("<br />");
			}
		}
		return buf.toString();
	}

	public static Object[] getPositionAndNameOfFirstExtraTag(String htmlText,
			String[] extraTags) {
		int lowestPos = Integer.MAX_VALUE;
		int arrayPos = Integer.MAX_VALUE;
		for (int i = 0; i < extraTags.length; i++) {
			String markup = extraTags[i];
			String foundTag = getStartTagFor(markup, htmlText);
			if (foundTag == null) {
				continue;
			}
			int foundPosition = htmlText.indexOf(foundTag);
			if (foundPosition > -1 && foundPosition < lowestPos) {
				lowestPos = foundPosition;
				arrayPos = i;
			}
		}
		if (lowestPos != Integer.MAX_VALUE) {
			return new Object[] { lowestPos, extraTags[arrayPos] };
		} else {
			return null;
		}
	}

	public static String getStartTagFor(String tag, String textToCheck) {
		int startpos = textToCheck.indexOf("<" + tag);
		if (startpos == -1) {
			return null;
		}
		return textToCheck.substring(startpos, textToCheck.indexOf(">",
				startpos) + 1);
	}

	public static String getUnknownAnswerString(Question q, XPSCase theCase) {
		String answer = (String) q.getUnknownAlternative().getValue(theCase);
		if (answer.equals(AnswerUnknown.UNKNOWN_VALUE)) {
			answer = DialogUtils.getMessageFor("dialog.unknown");
		}
		return answer;
	}

	public static boolean hasExtraTags(String htmlText, String[] extraTags) {
		for (String tag : extraTags) {
			String theTag = getStartTagFor(tag, htmlText);
			if (theTag == null) {
				continue;
			}
			if (htmlText.indexOf(theTag) > -1) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMMInfoAvailable(List<MMInfoObject>[] lists) {
		int counter = 0;
		for (int i = 0; i < lists.length; i++) {
			List<MMInfoObject> list = lists[i];
			if (list != null) {
				counter += list.size();
			}
		}
		return (counter == 0) ? false : true;
	}

	private static boolean moreTagAvailableInMMInfo(
			List<MMInfoObject> mmInfoTextList) {
		for (int i = 0; i < mmInfoTextList.size(); i++) {
			MMInfoObject infoObj = mmInfoTextList.get(i);
			if (moreTagAvailableInText(infoObj.getContent())) {
				return true;
			}
		}
		return false;
	}

	public static boolean moreTagAvailableInText(String text) {
		if (text.indexOf("<More>") != -1) {
			return true;
		}
		return false;
	}

	public static void renderAdditionalInfoWithReplacedExtraMarkup(
			ResponseWriter writer, UIComponent component, NamedObject qOrDiag,
			String textToCheck) throws IOException {
		String[] extraTags = MMInfo.getExtraTags();
		// when a extratag is found
		if (hasExtraTags(textToCheck, extraTags)) {
			while (hasExtraTags(textToCheck, extraTags)) {
				Object[] posAndName = getPositionAndNameOfFirstExtraTag(
						textToCheck, extraTags);
				int pos = (Integer) posAndName[0];
				String tag = (String) posAndName[1];
				// before
				// don't escape strings because html is allowed...
				writer.write(textToCheck.substring(0, pos));
				// content of extra Tag
				String starttag = getStartTagFor(tag, textToCheck);
				String content = textToCheck.substring(textToCheck
						.indexOf(starttag)
						+ starttag.length(), textToCheck
						.indexOf(getEndTagFor(tag)));

				// replace with ...
				// "more" button
				writer.startElement("a", component);
				writer.writeAttribute("style", "padding-left: 4px;", "style");
				writer.writeAttribute("id", "addinfo_morebutton_"
						+ qOrDiag.getId(), "id");
				writer.writeAttribute("title", DialogUtils
						.getMessageFor("mminfo.info.showMore.title"), "title");
				writer.writeAttribute("href", "#", "href");
				writer.writeAttribute("onclick", "makeInvisible('"
						+ "addinfo_morebutton_" + qOrDiag.getId()
						+ "'); makeVisible('" + "addinfo_" + qOrDiag.getId()
						+ "'); return false", "onclick");
				writer.writeText(DialogUtils
						.getMessageFor("mminfo.info.showMore"), "value");
				writer.endElement("a");

				// invisible span with more information...
				writer.startElement("span", component);
				writer.writeAttribute("class", "invis", "class");
				writer.writeAttribute("id", "addinfo_" + qOrDiag.getId(), "id");
				// don't escape strings because html is allowed...
				writer.write(content);

				// "less" link
				writer.startElement("a", component);
				writer.writeAttribute("id", "addinfo_lessbutton_"
						+ qOrDiag.getId(), "id");
				writer.writeAttribute("style", "padding-left: 4px;", "style");
				writer.writeAttribute("title", DialogUtils
						.getMessageFor("mminfo.info.showLess.title"), "title");
				writer.writeAttribute("href", "#", "href");
				writer.writeAttribute("onclick", "makeVisible('"
						+ "addinfo_morebutton_" + qOrDiag.getId()
						+ "'); makeInvisible('" + "addinfo_" + qOrDiag.getId()
						+ "'); return false", "onclick");
				writer.writeText(DialogUtils
						.getMessageFor("mminfo.info.showLess"), "value");
				writer.endElement("a");

				writer.endElement("span");

				// after
				String newText = textToCheck.substring(textToCheck
						.indexOf(getEndTagFor(tag))
						+ getEndTagFor(tag).length(), textToCheck.length());
				textToCheck = newText;
			}
			// render remaining text...
			// don't escape strings because html is allowed...
			writer.write(textToCheck);
		} else {
			// don't escape strings because html is allowed...
			writer.write(textToCheck);
		}
	}

	public static void renderChild(FacesContext facesContext, UIComponent child)
			throws IOException {
		if (!child.isRendered()) {
			return;
		}
		child.encodeBegin(facesContext);
		if (child.getRendersChildren()) {
			child.encodeChildren(facesContext);
		} else {
			renderChildren(facesContext, child);
		}
		child.encodeEnd(facesContext);
	}

	public static void renderChildren(FacesContext facesContext,
			UIComponent component) throws IOException {
		if (component.getChildCount() > 0) {
			for (Iterator<UIComponent> it = component.getChildren().iterator(); it
					.hasNext();) {
				UIComponent child = it.next();
				renderChild(facesContext, child);
			}
		}
	}

	public static void renderDiagnosesLink(ResponseWriter writer,
			UIComponent component, Solution diag, XPSCase theCase,
			String styleClass, String score, boolean showScore)
			throws IOException {

		if (DialogUtils.getDialogSettings().isShowDiagExplanation()) {
			writer.startElement("a", component);
			DiagnosisState diagState = diag.getState(theCase,
					PSMethodHeuristic.class);
			writer.writeAttribute("id", component.getId() + "_heur_"
					+ diagState.getName() + "_" + diag.getId(), "id");
			writer.writeAttribute("class", styleClass, "class");
			writer.writeAttribute("onclick",
					getDiagnosesLinkOnclickString(diag), "onclick");
			writer.writeAttribute("href", "#", "href");
			writer.writeAttribute("title", DialogUtils.getMessageWithParamsFor(
					"explain.button.text", new Object[] { diag.getName() }),
					"title");
			if (score == null || !showScore) {
				writer.writeText(diag.getName(), "value");
			} else {
				writer
						.writeText(diag.getName() + " (" + score.toString()
								+ ")", "value");
			}
			writer.endElement("a");
		} else {
			if (score == null || !showScore) {
				writer.writeText(diag.getName(), "value");
			} else {
				writer
						.writeText(diag.getName() + " (" + score.toString()
								+ ")", "value");
			}
		}
	}

	public static void renderEmptyTableCell(ResponseWriter writer,
			UIComponent component) throws IOException {
		writer.startElement("td", component);
		writer.write("&nbsp;");
		writer.endElement("td");
	}

	private static void renderHtmlForMMInfo(UIComponent component,
			ResponseWriter writer, List<MMInfoObject> list,
			NamedObject diagOrQuestion, int maxCharCount, boolean explicitWidth)
			throws IOException {
		for (int i = 0; i < list.size(); i++) {
			MMInfoObject infoObj = list.get(i);
			// if info-length longer than maxlength, then render a substring
			writer.startElement("li", component);
			if (explicitWidth || infoObj.getContent().length() <= maxCharCount) {
				renderAdditionalInfoWithReplacedExtraMarkup(writer, component,
						diagOrQuestion, infoObj.getContent());
			} else {
				writer.startElement("a", component);
				writer.writeAttribute("href", "#", "href");
				writer.writeAttribute("title", DialogUtils
						.getMessageFor("mminfo.info.title"), "title");
				writer.writeAttribute("onclick", "openMMInfoPopup('"
						+ diagOrQuestion.getId() + "'); return false;",
						"onclick");
				// don't escape strings
				writer.write(infoObj.getContent().substring(0, maxCharCount)
						+ DialogUtils.getMessageFor("mminfo.shortenedstring"));
				writer.endElement("a");
			}
			writer.endElement("li");
		}
	}

	private static void renderHtmlForMMMultimedia(UIComponent component,
			ResponseWriter writer, List<MMInfoObject> list, String kbid)
			throws IOException {
		for (int i = 0; i < list.size(); i++) {
			writer.startElement("li", component);
			writer.startElement("a", component);
			writer.writeAttribute("href", ResourceRepository
					.getMMPathForKB(kbid)
					+ list.get(i).getContent(), "href");
			writer.writeAttribute("target", "_blank", "target");
			writer.writeAttribute("title", DialogUtils
					.getMessageFor("mminfo.multimedialink.title"), "title");
			writer
					.writeText(DialogUtils
							.getMessageFor("mminfo.multimedialink"), "value");
			writer.endElement("a");
			writer.endElement("li");
		}
	}

	private static void renderHtmlForMMURL(UIComponent component,
			ResponseWriter writer, List<MMInfoObject> list, int maxCharCount,
			boolean explicitWidth) throws IOException {
		for (int i = 0; i < list.size(); i++) {
			MMInfoObject infoObj = list.get(i);

			writer.startElement("li", component);
			writer.startElement("a", component);
			writer.writeAttribute("href", list.get(i).getContent(), "href");
			writer.writeAttribute("target", "_blank", "target");
			writer.writeAttribute("title", DialogUtils
					.getMessageFor("mminfo.url.title"), "title");
			// if info-length longer than maxlength, then render a substring
			if (explicitWidth || infoObj.getContent().length() <= maxCharCount) {
				writer.writeText(infoObj.getContent(), "value");
			} else {
				writer.writeText(infoObj.getContent()
						.substring(0, maxCharCount)
						+ DialogUtils.getMessageFor("mminfo.shortenedstring"),
						"value");
			}

			writer.endElement("a");
			writer.endElement("li");
		}
	}

	public static void renderInfoImageForTooltip(ResponseWriter writer,
			UIComponent component, NamedObject diagOrQuestion, MMInfo info,
			boolean moreTagAvailable, String spanIDString) throws IOException {
		writer.startElement("img", component);
		writer.writeAttribute("id", spanIDString + "link_"
				+ diagOrQuestion.getId(), "id");
		writer.writeAttribute("src", "images/info_lo.gif", "src");
		writer.writeAttribute("alt", "info", "alt");
		writer.writeAttribute("class", "info", "class");
		if (info != null) {
			// popup for a question
			if (info.getTooltipWidth() != 0) {
				String tooltipString = "TagToTip('" + spanIDString + "_"
						+ diagOrQuestion.getId() + "', WIDTH, "
						+ info.getTooltipWidth() + "); return false;";
				// check if the info has a "More" button -> then the
				// popup should not close
				// on mouseclick because otherwise the click on "More"
				// will result on closing the tooltip
				if (moreTagAvailable) {
					tooltipString = "TagToTip('" + spanIDString + "_"
							+ diagOrQuestion.getId() + "', WIDTH, "
							+ info.getTooltipWidth()
							+ ", CLICKCLOSE, false); return false;";
				}
				writer.writeAttribute(info.getMouseEvent(), tooltipString, info
						.getMouseEvent());
			} else {
				writer.writeAttribute(info.getMouseEvent(), "TagToTip('"
						+ spanIDString + "_" + diagOrQuestion.getId()
						+ "'); return false;", info.getMouseEvent());
			}
		} else { // popup for a diagnosis
			writer.writeAttribute("onclick", "TagToTip('" + spanIDString + "_"
					+ diagOrQuestion.getId() + "'); return false;", "onclick");
		}
		writer.endElement("img");
	}

	public static void renderMMInfoPopupLink(ResponseWriter writer,
			UIComponent component, NamedObject diagOrQuestion,
			boolean inTableRow, MMInfo info) throws IOException {
		// this method renders the info popup for mminfo defined in knowledge
		// base only.
		if (DialogUtils.getDialogSettings().isShowMMInfo()) {
			List<MMInfoObject> mmInfoTextList = getMMInfo(diagOrQuestion,
					MMInfoSubject.INFO);
			List<MMInfoObject> mmInfoURLList = getMMInfo(diagOrQuestion,
					MMInfoSubject.URL);
			List<MMInfoObject> mmInfoMultimediaList = getMMInfo(diagOrQuestion,
					MMInfoSubject.MULTIMEDIA);
			// if MMInfo available
			if (isMMInfoAvailable(new List[] { mmInfoTextList, mmInfoURLList,
					mmInfoMultimediaList })) {
				if (inTableRow) {
					writer.startElement("td", component);
					writer.writeAttribute("align", "right", "align");
				}

				renderInfoImageForTooltip(writer, component, diagOrQuestion,
						info, moreTagAvailableInMMInfo(mmInfoTextList),
						"mminfo");

				// render invisible popup content...
				writer.startElement("div", component);
				writer.writeAttribute("id", "mminfo_" + diagOrQuestion.getId(),
						"id");
				writer.writeAttribute("class", "invis", "class");

				writer.startElement("ul", component);
				writer.writeAttribute("class", "tooltip", "class");
				if (info != null) {
					writer.writeAttribute("style", "padding: "
							+ info.getPadding() + "; text-align: "
							+ info.getAlign() + "; ", "style");
				}

				boolean explicitWidth = false;
				if (info != null && info.getTooltipWidth() != 0) {
					explicitWidth = true;
				}
				renderHtmlForMMInfo(component, writer, mmInfoTextList,
						diagOrQuestion, DialogUtils.getDialogSettings()
								.getMaxCharLengthInMMInfoPopup(), explicitWidth);
				renderHtmlForMMURL(component, writer, mmInfoURLList,
						DialogUtils.getDialogSettings()
								.getMaxCharLengthInMMInfoPopup(), explicitWidth);
				renderHtmlForMMMultimedia(component, writer,
						mmInfoMultimediaList, DialogUtils.getDialog()
								.getTheCase().getKnowledgeBase().getId());
				writer.endElement("ul");
				writer.endElement("div");

				if (inTableRow) {
					writer.endElement("td");
				}
			}
			// if no info available, but RenderInTable is true -> render empty
			// TD
			else {
				if (inTableRow) {
					writer.startElement("td", component);
					writer.write("&nbsp;");
					writer.endElement("td");
				}
			}
		}
	}

	public static void renderTable(ResponseWriter writer, UIComponent component)
			throws IOException {
		writer.startElement("table", component);
		writer.writeAttribute("summary", "-", "summary");
		writer.writeAttribute("cellspacing", "0", "cellspacing");
		writer.writeAttribute("cellpadding", "0", "cellpadding");
	}

	public static void renderTableHeadRow(ResponseWriter writer,
			UIComponent component, String[] headlines) throws IOException {
		writer.startElement("tr", component);
		for (int i = 0; i < headlines.length; i++) {
			writer.startElement("th", component);
			writer.writeText(headlines[i], "value");
			writer.endElement("th");
		}
		writer.endElement("tr");
	}

	public static void renderTableWithClass(ResponseWriter writer,
			UIComponent component, String styleClass) throws IOException {
		writer.startElement("table", component);
		writer.writeAttribute("summary", "-", "summary");
		writer.writeAttribute("class", styleClass, "class");
		writer.writeAttribute("cellspacing", "0", "cellspacing");
		writer.writeAttribute("cellpadding", "0", "cellpadding");
	}

	public static void renderTableWithClass(ResponseWriter writer,
			UIComponent component, String styleClass, int spacing, int padding)
			throws IOException {
		writer.startElement("table", component);
		writer.writeAttribute("summary", "-", "summary");
		writer.writeAttribute("class", styleClass, "class");
		writer.writeAttribute("cellspacing", spacing, "cellspacing");
		writer.writeAttribute("cellpadding", padding, "cellpadding");
	}

	public static void sortDiagnosisList(List<Solution> diagList,
			final XPSCase theCase) {
		Comparator<Solution> diagCompAsc = new Comparator<Solution>() {

			public int compare(Solution a, Solution b) {
				if (a.getScore(theCase, PSMethodHeuristic.class).getScore() < b
						.getScore(theCase, PSMethodHeuristic.class).getScore())
					return 1;
				else if (a.getScore(theCase, PSMethodHeuristic.class)
						.getScore() > b.getScore(theCase,
						PSMethodHeuristic.class).getScore())
					return -1;
				else
					return 0;
			}
		};
		Collections.sort(diagList, diagCompAsc);
	}

}
