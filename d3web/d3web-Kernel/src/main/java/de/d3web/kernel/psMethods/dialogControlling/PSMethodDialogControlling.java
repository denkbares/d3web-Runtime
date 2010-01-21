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

package de.d3web.kernel.psMethods.dialogControlling;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.Rule;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationEntry;
import de.d3web.kernel.psMethods.combinied.PSMethodCombined;

/**
 * This is a combined PSMethod used for dialog controlling
 * @author Christian Betz
 */
public class PSMethodDialogControlling extends PSMethodCombined {

	public PSMethodDialogControlling() {
		super();
	}

	/**
	 * @return the maximum of scores as DiagnosisState.
	 * Creation date: (03.01.2002 16:17:28)
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {

		DiagnosisState diagnosisState = null;
		for (PSMethod psMethod : getPSMethods()) {
				DiagnosisState dState = psMethod.getState(theCase, theDiagnosis);

			if (dState != null && dState.compareTo(diagnosisState) > 0) {
				diagnosisState = dState;
			}
		}

		if (diagnosisState == null)
			return DiagnosisState.UNCLEAR;
		else
			return diagnosisState;
	}

	/**
	 * Retrieves all dialog controlling PSMethods from the given XPSCase
	 * Creation date: (03.01.2002 16:17:28)
	 */
	public void init(XPSCase theCase) {
		setPSMethods(new LinkedList<PSMethod>(theCase.getDialogControllingPSMethods()));
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			List<? extends KnowledgeSlice> knowledgeSlices = change.getObject().getKnowledge(this.getClass());
			if (knowledgeSlices == null) return;
			for (KnowledgeSlice slice : knowledgeSlices) {
				Rule rule = (Rule) slice;
				rule.check(theCase);
			}
		}
	}

	public String toString() {
		return "PSMethodDialogControlling";
	}
}