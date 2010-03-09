/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments.conditions;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.IDObject;
/**
 * FragementHandler for CondDStates
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DStateConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "DState");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondDState);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String solutionID = element.getAttribute("ID");
		String value = element.getAttribute("value");
		if (solutionID!=null && value != null) {
			IDObject idObject = kb.search(solutionID);
			if (idObject instanceof Diagnosis) {
				Diagnosis diag = (Diagnosis) idObject;
				DiagnosisState diagnosisState = getDiagnosisState(value);
				return new CondDState(diag, diagnosisState);
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondDState cond = (CondDState) object;
		String status = "";
		if(cond.getStatus() != null) {
			status = cond.getStatus().toString();
		}
		return XMLUtil.writeCondition(doc, cond.getDiagnosis(), "DState", status);
	}
	
	private static DiagnosisState getDiagnosisState(String status) {

		if (status.equals("established"))
			return DiagnosisState.ESTABLISHED;

		if (status.equals("excluded"))
			return DiagnosisState.EXCLUDED;

		if (status.equals("suggested"))
			return DiagnosisState.SUGGESTED;

		if (status.equals("unclear"))
			return DiagnosisState.UNCLEAR;

		return null;
	}

}
