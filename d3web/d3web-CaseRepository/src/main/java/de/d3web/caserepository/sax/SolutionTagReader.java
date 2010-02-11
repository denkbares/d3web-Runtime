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

package de.d3web.caserepository.sax;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.addons.PSMethodAuthorSelected;
import de.d3web.caserepository.addons.PSMethodClassicD3;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * @author bates
 */
public class SolutionTagReader extends AbstractTagReader {

	private final static String DEFAULT_WEIGHT = "1";
	private final static Class DEFAULT_PSMETHOD_CLASS = PSMethodUserSelected.class;

	private static SolutionTagReader instance = null;

	private CaseObject.Solution currentSolution = null;
	private CaseObject.Solution currentSolutionCopy = null;


	public static AbstractTagReader getInstance() {
		if(instance == null){
			instance = new SolutionTagReader("Solution");			
		}
		return instance;
	}


	private SolutionTagReader(String id) {
		super(id);
	}

	protected void startElement(String uri,String localName, String qName, Attributes attributes) {
		if (qName.equals("Solution")) {
			startSolution(attributes);
		} else if (qName.equals("Ratings")) {
			// do nothing
		} else if (qName.equals("Rating")) {
			startRating(attributes);
		}
	}

	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("Solution")) {
			endSolution();
		} else if (qName.equals("Ratings")) {
			// do nothing
		} else if (qName.equals("Rating")) {
			endRating();
		}
	}

	private void startSolution(Attributes attributes) {

		currentSolution = new CaseObject.Solution();
		currentSolutionCopy = null;

		String weight =
			checkAttribute("weight", attributes.getValue("weight"), DEFAULT_WEIGHT);
		currentSolution.setWeight(Double.parseDouble(weight));

		String id = checkAttribute("id", attributes.getValue("id"), "<not set>");
		Diagnosis diag = getKnowledgeBase().searchDiagnosis(id);
		if (diag == null) {
		    Logger.getLogger(this.getClass().getName()).warning("no Diagnosis found for " + id + " - omitting");
		    currentSolution = null;
		    return;
		}
		
		currentSolution.setDiagnosis(diag);

		String psMethod = attributes.getValue("psmethod");
		if (psMethod != null) { // i.e. it's the new Solution format
			try {
				currentSolution.setPSMethodClass(Class.forName(psMethod));
			} catch (ClassNotFoundException e) {
				Logger.getLogger(this.getClass().getName()).warning("no Class found for " + psMethod);
				currentSolution = null;
				return;
			}
			String state = attributes.getValue("state");
			DiagnosisState ds = Utilities.string2stateNarrow(state);
			currentSolution.setState(ds);
		} else {
			// [MISC]:marty 20031014: legacy code: set a default psmethod, just in case
			// Rationale: if a psmethod occurs in a rating block later, currentSolutionCopy
			// will get psmethod/state anyway, and currentSolution (here!) is not used.
			currentSolution.setPSMethodClass(PSMethodUserSelected.class);
			currentSolution.setState(DiagnosisState.ESTABLISHED);
		}

	}

	private void endSolution() {
		if (currentSolutionCopy == null
			&& currentSolution != null)
			addSolution(currentSolution);
		currentSolution = null;
	}
	
	private void startRating(Attributes attributes) {
		currentSolutionCopy = new CaseObject.Solution();
		currentSolutionCopy.setDiagnosis(currentSolution.getDiagnosis());
		currentSolutionCopy.setWeight(currentSolution.getWeight());

		String psmethodAttr = attributes.getValue("psmethod");

		// [MISC]:aha:legacy code
		if ("D3Classic".equals(psmethodAttr) || "System".equals(psmethodAttr))
			psmethodAttr = PSMethodClassicD3.class.getName();
		else if (
			"de.d3web.Train.kernel.caseImportAddons.PSMethodWebTrain".equals(
				psmethodAttr)
				|| "Author".equals(psmethodAttr)
				|| "WebTrain".equals(psmethodAttr))
			psmethodAttr = PSMethodAuthorSelected.class.getName();
		else if ("User".equals(psmethodAttr))
			psmethodAttr = PSMethodUserSelected.class.getName();

		try {
			Class psMethodClass = Class.forName(psmethodAttr);
			currentSolutionCopy.setPSMethodClass(psMethodClass);
		} catch (Exception ex) {
			//[MISC]:marty:legacy code
			if (psmethodAttr.equals("de.d3web.caserepository.PSMethodClassicD3")) {
				currentSolutionCopy.setPSMethodClass(PSMethodClassicD3.class);
			} else {
				Logger.getLogger(this.getClass().getName()).warning(
					"no Class found for "
						+ psmethodAttr
						+ ", set to "
						+ DEFAULT_PSMETHOD_CLASS);
				currentSolutionCopy.setPSMethodClass(DEFAULT_PSMETHOD_CLASS);
			}
		}
	}

	private void endRating() {
		if (currentSolutionCopy != null) {
			currentSolutionCopy
				.setState(
					Utilities
						.string2stateBroad(
							getTextBetweenCurrentTag()));
			addSolution(currentSolutionCopy);
		}
	}

	public List getTagNames() {
		List ret = new LinkedList();
		ret.add("Solution");
		ret.add("Ratings");
		ret.add("Rating");
		return ret;
	}

}
