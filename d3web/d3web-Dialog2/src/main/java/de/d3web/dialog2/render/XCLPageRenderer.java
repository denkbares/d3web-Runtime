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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.dialog2.component.html.UIXCLPage;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.verbalizer.VerbalizationManager;
import de.d3web.kernel.verbalizer.Verbalizer;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;
import de.d3web.xcl.InferenceTrace;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.inference.PSMethodXCL;

public class XCLPageRenderer extends Renderer {

	// properties and template

	private ResponseWriter writer;
	private UIComponent component;
	private HashMap<String, Object> parameterMap;
	private Session theCase;

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {

		this.component = component;
		this.writer = context.getResponseWriter();
		this.parameterMap = new HashMap<String, Object>();
		parameterMap.put(Verbalizer.IS_SINGLE_LINE, Boolean.TRUE);

		theCase = DialogUtils.getDialog().getTheCase();
		String diagnosis = ((UIXCLPage) component).getDiag();

		Solution solution =
				theCase.getKnowledgeBase().searchSolution(diagnosis);

		Collection<KnowledgeSlice> models =
				theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(PSMethodXCL.class);

		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof XCLModel) {
				if (((XCLModel) knowledgeSlice).getSolution()
						.equals(solution)) {

					InferenceTrace trace = ((XCLModel) knowledgeSlice)
							.getInferenceTrace(theCase);

					if (trace != null) {

						verbalizeTrace(trace, solution.getName());
					}

				}
			}
		}

	}

	private void verbalizeTrace(InferenceTrace trace, String solution) throws IOException {

		// Score and Support
		Double score = trace.getScore();
		Double support = trace.getSupport();
		String stateString = "no state found";

		if (trace.getState() != null) {
			stateString = trace.getState().toString();
		}

		renderState(stateString, roundDouble(score), roundDouble(support), solution);

		// sufficiently derived by
		Collection<XCLRelation> suff = trace.getSuffRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.sufficiently"), suff);

		// is contradicted by
		Collection<XCLRelation> contr = trace.getContrRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.contradicted"), contr);

		// is required by
		Collection<XCLRelation> reqPos = trace.getReqPosRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.required"), reqPos);

		// is required by NOT FULFILLED
		Collection<XCLRelation> reqNeg = trace.getReqNegRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.requiredfalse"), reqNeg);

		// explains
		Collection<XCLRelation> relPos = trace.getPosRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.explained"), relPos);

		// explains negative
		Collection<XCLRelation> relNeg = trace.getNegRelations();
		renderTable(DialogUtils.getMessageFor("xcl.explainD3.notexplained"), relNeg);

	}

	private Double roundDouble(Double d) {
		return d = Math.round(d * 100.) / 100.;
	}

	/**
	 * Renders the state of the explanation
	 * 
	 * @param state the state to be rendered
	 * @param score the explanation score
	 * @param support the explanation support
	 * @throws IOException
	 */
	private void renderState(String state, Double score, Double support, String solution)
			throws IOException {

		if (score != null && support != null) {

			writer.startElement("table", component);

			// render solution
			writer.startElement("thead", component);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.startElement("strong", component);
			writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.solution") + ": ", "value");
			writer.endElement("strong");
			writer.endElement("td");
			writer.startElement("td", component);
			writer.writeText(solution, "value");
			writer.endElement("td");
			writer.endElement("tr");
			writer.endElement("thead");

			// render state
			writer.startElement("tbody", component);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.startElement("strong", component);
			writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.state") + ": ", "value");
			writer.endElement("strong");
			writer.endElement("td");
			writer.startElement("td", component);
			writer.writeText(state, "value");
			writer.endElement("td");
			writer.endElement("tr");

			// render score
			writer.startElement("tbody", component);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.startElement("strong", component);
			writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.score") + ": ", "value");
			writer.endElement("strong");
			writer.endElement("td");
			writer.startElement("td", component);
			writer.writeText(score, "value");
			writer.endElement("td");
			writer.endElement("tr");

			// render support
			writer.startElement("tbody", component);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.startElement("strong", component);
			writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.support") + ": ", "value");
			writer.endElement("strong");
			writer.endElement("td");
			writer.startElement("td", component);
			writer.writeText(support, "value");
			writer.endElement("td");
			writer.endElement("tr");
			writer.endElement("tbody");
			writer.endElement("table");

		}
	}

	private void renderTable(String type, Collection<XCLRelation> content)
			throws IOException {

		if (content != null && content.size() > 0) {

			writer.startElement("table", component);
			writer.writeAttribute("class", "xclTable", "class");

			if (!type.equals(DialogUtils.getMessageFor("xcl.explainD3.notexplained"))) {

				// render thead
				writer.startElement("thead", component);
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.startElement("strong", component);
				writer.writeText(type, "value");
				writer.endElement("strong");
				writer.endElement("td");
				writer.endElement("tr");
				writer.endElement("thead");

				// render tbody
				writer.startElement("tbody", component);
				renderContent(content);
				writer.endElement("tbody");
				writer.endElement("table");

			}
			else {

				// render thead
				writer.startElement("thead", component);
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.writeAttribute("colspan", "3", "colspan");
				writer.startElement("strong", component);
				writer.writeText(type, "value");
				writer.endElement("strong");
				writer.endElement("td");
				writer.endElement("tr");
				writer.endElement("thead");

				// render tbody
				writer.startElement("tbody", component);
				renderContentNotExplained(content);
				writer.endElement("tbody");
				writer.endElement("table");

			}
		}

	}

	/**
	 * Renders the table content, new row for every answer
	 * 
	 * @param content the answers to be rendered
	 * @return a table representation of the content
	 * @throws IOException
	 */
	private void renderContent(Collection<XCLRelation> content) throws IOException {

		for (XCLRelation rel : content) {
			Condition cond = rel.getConditionedFinding();
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeText(
					VerbalizationManager.getInstance().verbalize(cond,
					RenderingFormat.HTML, parameterMap).replaceAll("<b>", "").replaceAll(
					"</b>", ""), "value");
			writer.endElement("td");
			writer.endElement("tr");
		}

	}

	/**
	 * Content for "Not Explained" has to be rendered differently since it
	 * contains the question, the answer given and the expected answer
	 * (verbalized condition)
	 * 
	 * @param content the content to get rendered
	 * @return a table representation of the content
	 * @throws IOException
	 */
	private void renderContentNotExplained(Collection<XCLRelation> content) throws IOException {

		writer.startElement("tr", component);
		writer.writeAttribute("class", "emphasized", "class");
		writer.startElement("td", component);
		writer.startElement("strong", component);
		writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.question"), "value");
		writer.endElement("strong");
		writer.endElement("td");
		writer.startElement("td", component);
		writer.startElement("strong", component);
		writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.givenanswer"), "value");
		writer.endElement("strong");
		writer.endElement("td");
		writer.startElement("td", component);
		writer.startElement("strong", component);
		writer.writeText(DialogUtils.getMessageFor("xcl.explainD3.expectedanswer"), "value");
		writer.endElement("strong");
		writer.endElement("td");
		writer.endElement("tr");

		for (XCLRelation rel : content) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			Condition cond = rel.getConditionedFinding();
			List<? extends TerminologyObject> questions = cond.getTerminalObjects();
			ListIterator<? extends TerminologyObject> condIt = questions.listIterator();
			List<Question> askedQuestions = new ArrayList<Question>();
			int count = 0;
			Question cq = null;
			while (condIt.hasNext()) {
				if (count > 0) {
					writer.startElement("br", component);
				}
				cq = (Question) condIt.next();
				if (!askedQuestions.contains(cq)) {
					writer.writeText(cq.getName(), "value");
					count = count + 1;
					askedQuestions.add(cq);
				}
			}
			writer.endElement("td");
			writer.startElement("td", component);
			Value answer = theCase.getBlackboard().getValue(cq);
			List<Value> answers = new ArrayList<Value>();
			if (cq instanceof QuestionMC) {
				answers.addAll((List<ChoiceValue>) ((MultipleChoiceValue) answer).getValue());
			}
			else {
				answers.add(answer);
			}

			ListIterator<Value> iterator = answers.listIterator();
			count = 0;
			while (iterator.hasNext()) {
				if (count > 0) {
					writer.startElement("br", component);
				}
				Value a = iterator.next();
				writer.writeText(a.getValue(), "value");
				count = count + 1;
			}
			writer.endElement("td");

			writer.startElement("td", component);
			writer.writeAttribute("class", "emphasized", "class");
			writer.writeText(
					VerbalizationManager.getInstance().verbalize(cond,
					RenderingFormat.HTML, parameterMap).replaceAll("<b>", "").replaceAll(
					"</b>", ""), "value");
			writer.endElement("td");
			writer.endElement("tr");
		}

	}

}
