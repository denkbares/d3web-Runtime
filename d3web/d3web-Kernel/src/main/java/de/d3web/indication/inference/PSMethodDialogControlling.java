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

package de.d3web.indication.inference;
import java.util.Collection;
import java.util.LinkedList;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.interviewmanager.PSMethodCombined;
import de.d3web.scoring.inference.PSMethodHeuristic;

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
	public DiagnosisState getState(Session theCase, Solution theDiagnosis) {
		return null;
	}

	/**
	 * Retrieves all dialog controlling PSMethods from the given XPSCase
	 * Creation date: (03.01.2002 16:17:28)
	 */
	public void init(Session theCase) {
		//List moved from d3webCase (because it wasn't modfied anyway
		//TODO: getAll PSMethods and screen them for the correct ones 
		PSMethodUserSelected psmUser = PSMethodUserSelected.getInstance();
		LinkedList<PSMethod> dialogControllingPSMethods = new LinkedList<PSMethod>();
		dialogControllingPSMethods.add(psmUser);
		dialogControllingPSMethods.add(PSMethodHeuristic.getInstance());
		setPSMethods(new LinkedList<PSMethod>(dialogControllingPSMethods));
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			KnowledgeSlice knowledgeSlices = change.getObject().getKnowledge(this.getClass(), MethodKind.FORWARD);
			if (knowledgeSlices == null) return;
			RuleSet rs = (RuleSet) knowledgeSlices;
			for (Rule rule: rs.getRules()) {
				rule.check(theCase);
			}
		}
	}

	public String toString() {
		return "PSMethodDialogControlling";
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeIndicationFacts(facts);
	}
}