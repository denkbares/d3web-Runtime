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

import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseDiagnosis;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.scoring.DiagnosisScore;

/**
 * This PSMethod is for user selections (e.g. in a Dialog) Creation date:
 * (03.01.2002 16:17:28)
 * 
 * @author Christian Betz
 */
public class PSMethodUserSelected extends PSMethodRulebased {
	private static PSMethodUserSelected instance = null;

	private PSMethodUserSelected() {
		super();
		setContributingToResult(true);
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodUserSelected getInstance() {
		if (instance == null) {
			instance = new PSMethodUserSelected();
		}
		return instance;
	}

	/**
	 * @return the (calculated) state of the given Diagnosis for the current
	 *         (given) case. Creation date: (03.01.2002 17:32:38)
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		Object value = ((CaseDiagnosis) (theCase.getCaseObject(diagnosis))).getValue(this.getClass());
		if (value instanceof DiagnosisScore) {
			return DiagnosisState.getState((DiagnosisScore) value);
		}
		else if (value instanceof DiagnosisState) {
			return (DiagnosisState) value;
		}
		else {
			return DiagnosisState.UNCLEAR;
		}
	}

	/**
	 * @see de.d3web.core.inference.PSMethod
	 */
	public void init(de.d3web.core.session.XPSCase theCase) {
	}

	public String toString() {
		return "User selections";
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}
}