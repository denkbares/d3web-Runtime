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

import java.util.Collection;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;

/**
 * Interface for all problen-solver methods to implement. Each XPSCase has a
 * list of currently used problem-solvers. They are notified, if some value
 * (question or diagnosis) has changed. Creation date: (28.08.00 17:22:54)
 * 
 * @author joba
 */
public interface PSMethod {

	/**
	 * Every problem-solver has to decide how it calculates the state of a
	 * diagnosis.
	 * 
	 * @return the DiagnosisState of the given Diagnosis depending on the given
	 *         XPSCase
	 */
	// TODO: should be moved as blackboard functionality: get merged facts for a
	// specific problem solver
	DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis);

	/**
	 * initialization method for this PSMethod
	 */
	void init(XPSCase theCase);

	/**
	 * Indicates whether the problemsolver contributes to
	 * XPSCase.getDiagnoses(DiangosisState)
	 */
	boolean isContributingToResult();

	/**
	 * propergates the new value of the given NamedObject for the given XPSCase
	 */
	void propagate(XPSCase theCase, Collection<PropagationEntry> changes);

	/**
	 * Merges the facts created by this problem solver to the final value. The
	 * method will receive a non-empty set of facts created by this problem
	 * solver to merge it to the final value. The method may rely on that every
	 * fact has a unique source. The method may also rely on that all facts are
	 * created by their own. Therefore it may cast the facts to the
	 * implementation class it uses for creating facts.
	 * 
	 * @param facts
	 *            the facts to be merged
	 * 
	 * @return the merged fact
	 */
	Fact mergeFacts(Fact[] facts);
}