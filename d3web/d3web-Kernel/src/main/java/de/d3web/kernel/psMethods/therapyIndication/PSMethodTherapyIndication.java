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

package de.d3web.kernel.psMethods.therapyIndication;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.Rule;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PropagationEntry;

/**
 * Heuristic problem-solver which adds scores to diagnoses
 * on the basis of question values. If score of a diagnosis exceeds
 * a threshold this diagnosis will be established.
 * Creation date: (28.08.00 18:04:09)
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
	 * @return the one and only instance of this ps-method (Singleton)
	 */
	public static PSMethodTherapyIndication getInstance() {
		if (instance == null) {
			instance = new PSMethodTherapyIndication();
		}
		return instance;
	}

	/**
	 * Calculates the state by checking the score of the diagnosis
	 * against a threshold value.
	 * Creation date: (05.10.00 13:41:07)
	 * @return de.d3web.kernel.domainModel.DiagnosisState
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore diagnosisScore =
			diagnosis.getScore(theCase, this.getClass());
		if (diagnosisScore == null)
			return DiagnosisState.UNCLEAR;
		else
			return DiagnosisState.getState(diagnosisScore);
	}

	/**
	 * Check if NamedObject has nextQASet rules and check them, if available
	 */
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			List<? extends KnowledgeSlice> knowledgeSlices = change.getObject().getKnowledge(this.getClass());
			if (knowledgeSlices == null) return;
			for (KnowledgeSlice slice : knowledgeSlices) {
				try {
					Rule rule = (Rule) slice;
					rule.check(theCase);
				} 
				catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).throwing(
						this.getClass().getName(), "propagate", e);
				}
			}
		}
	}

	public String toString() {
		return "heuristic problem-solver";
	}
}