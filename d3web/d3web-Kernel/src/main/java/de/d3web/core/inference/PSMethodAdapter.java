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

package de.d3web.core.inference;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * An adapter class with some empty method bodies and some defulat
 * implementations. Creation date: (27.09.00 14:22:54)
 * 
 * @author Joachim Baumeister
 * @see PSMethod
 */
public abstract class PSMethodAdapter implements PSMethod {
	private boolean contributingToResult = false;

	protected PSMethodAdapter() {
		super();
	}

	/**
	 * By default the problem solver does not count for a diagnosis. :-) Every
	 * problem-solver has to decide how it calculates the state of a diagnosis.
	 * Creation date: (05.10.00 13:41:07)
	 * 
	 * @see DiagnosisState
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore diagnosisScore = diagnosis.getScore(theCase, PSMethodHeuristic.class);
		if (diagnosisScore == null)
			return DiagnosisState.UNCLEAR;
		else
			return DiagnosisState.getState(diagnosisScore);
	}

	/**
	 * Does nothing.
	 */
	public void init(XPSCase theCase) {
	}

	/**
	 * @see PSMethod
	 */
	public boolean isContributingToResult() {
		return contributingToResult;
	}

	/**
	 * @see PSMethod
	 */
	public void setContributingToResult(boolean newContributingToResult) {
		contributingToResult = newContributingToResult;
	}
}