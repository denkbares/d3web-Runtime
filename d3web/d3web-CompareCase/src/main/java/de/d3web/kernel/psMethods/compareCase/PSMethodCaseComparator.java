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

package de.d3web.kernel.psMethods.compareCase;

import java.util.Collection;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * This Class represents a ProblemSolver Method which depends on comparing a
 * case with cases from a case repository and taking the solution of the most
 * similar case. Creation date: (02.08.2001 15:38:27)
 * 
 * @author: Norman Brümmer
 */
public class PSMethodCaseComparator implements PSMethod {

	private boolean contributingToResult = true;

	/**
	 * returns the problemsolver context (class object of psmethod) Creation
	 * date: (14.08.2001 14:38:48)
	 * 
	 * @return java.lang.Class
	 */
	public static Class<? extends PSMethod> getProblemSolverContext() {
		return PSMethodCaseComparator.class;
	}

	/**
	 * Some space for initial methods of a PSMethod. Creation date: (08.08.2001
	 * 09:10:11)
	 * 
	 * @param theCase
	 *            EasyXPS.domainModel.Session the current case
	 */
	public void init(Session theCase) {
	}

	/**
	 * propagate method comment.
	 */
	@Override
	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
	}

	/**
	 * Insert the method's description here. Creation date: (07.01.2002
	 * 15:48:36)
	 * 
	 * @return boolean
	 */
	public boolean isContributingToResult() {
		return contributingToResult;
	}

	/**
	 * Insert the method's description here. Creation date: (07.01.2002
	 * 15:48:36)
	 * 
	 * @param newContributionToResult
	 *            boolean
	 */
	public void setContributionToResult(boolean newContributingToResult) {
		contributingToResult = newContributingToResult;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

}