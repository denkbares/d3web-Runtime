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

package de.d3web.indication.inference;

import java.util.Collection;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * Heuristic problem-solver which adds scores to diagnoses on the basis of
 * question values. If score of a diagnosis exceeds a threshold this diagnosis
 * will be established. Creation date: (28.08.00 18:04:09)
 * 
 * @author joba
 */
public class PSMethodTherapyIndication extends PSMethodAdapter {

	private static PSMethodTherapyIndication instance = null;

	private PSMethodTherapyIndication() {
		super();
		setContributingToResult(true);
	}

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * 
	 * @return the one and only instance of this ps-method (Singleton)
	 */
	public static PSMethodTherapyIndication getInstance() {
		if (instance == null) {
			instance = new PSMethodTherapyIndication();
		}
		return instance;
	}

	/**
	 * Check if NamedObject has nextQASet rules and check them, if available
	 */
	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			KnowledgeSlice knowledgeSlices = ((NamedObject) change.getObject()).getKnowledge(
					this.getClass(), MethodKind.FORWARD);
			if (knowledgeSlices == null) return;
			RuleSet rs = (RuleSet) knowledgeSlices;
			for (Rule rule : rs.getRules()) {
				rule.check(theCase);
			}
		}
	}

	@Override
	public String toString() {
		return "heuristic problem-solver";
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeIndicationFacts(facts);
	}
}