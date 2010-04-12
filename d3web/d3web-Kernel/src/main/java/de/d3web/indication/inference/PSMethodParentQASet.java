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

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * This is a psmethod to mark QContainers (QASets) which are (contra-)indicated
 * due to the (contra-)indication of a parent-QContainer (QASet).
 * @author Georg
 */
public class PSMethodParentQASet extends PSMethodAdapter {

	private static PSMethodParentQASet instance = null;

	private PSMethodParentQASet() {
		super();
		setContributingToResult(false);
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodParentQASet getInstance() {
		if (instance == null) {
			instance = new PSMethodParentQASet();
		}
		return instance;
	}


	public DiagnosisState getState(Session theCase, Solution diagnosis) {
		return null;
	}

	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
	}
	
	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeIndicationFacts(facts);
	}

}
