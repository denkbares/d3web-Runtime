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

package de.d3web.kernel.psMethods;

import java.util.Collection;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;

/**
 * This is a 'marker' psmethod to represent all the initial values.
 * Especially used to add the initQASets to the QASetManager
 * Creation date: (21.02.2002 16:51:10)
 * @author Christian Betz
 */
public class PSMethodInit implements PSMethod {
	private static PSMethodInit instance = null;

	public static PSMethodInit getInstance() {
		if (instance == null) {
			instance = new PSMethodInit();
		}
		return instance;
	}

	public PSMethodInit() {
		super();
	}

	/**
	 * @return null
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {
		return null;
	}

	/**
	 * Some space for initial methods of a PSMethod.
	 * Does nothing.
	 * Creation date: (21.02.2002 16:51:10)
	 */
	public void init(XPSCase theCase) {
	}

	/**
	 * Indicates whether the problemsolver contributes to XPSCase.getDiagnoses(DiangosisState)
	 * Creation date: (21.02.2002 16:51:10)
	 * @return false
	 */
	public boolean isContributingToResult() {
		return false;
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
	}
}