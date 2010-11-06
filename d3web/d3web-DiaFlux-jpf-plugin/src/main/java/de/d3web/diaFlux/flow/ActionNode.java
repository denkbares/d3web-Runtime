/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.diaFlux.flow;

import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.indication.ActionIndication;
import de.d3web.indication.inference.PSMethodUserSelected;

public class ActionNode extends Node {

	protected final PSAction action;

	public ActionNode(String id, String name, PSAction action) {
		super(id, name);

		if (action == null) throw new IllegalArgumentException("'action' must not be null.");

		this.action = action;
	}

	public PSAction getAction() {
		return action;
	}

	@Override
	public void doAction(Session session) {
		getAction().doIt(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public void undoAction(Session session) {
		getAction().undo(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode) {
		super.takeSnapshot(session, snapshotNode);

		if (action instanceof ActionIndication) {
			ActionIndication aind = (ActionIndication) action;
			List<QASet> backwardObjects = aind.getBackwardObjects();
			if (backwardObjects.size() != 1) {
				System.out.println("Check");
			}
			else {

				QASet set = backwardObjects.get(0);

				if (set instanceof Question) {
					Question question = (Question) set;
					Blackboard blackboard = session.getBlackboard();
					if (UndefinedValue.isNotUndefinedValue(blackboard.getValue(question))) {
						blackboard.addValueFact(
								new DefaultFact(question, UndefinedValue.getInstance(),
										PSMethodUserSelected.getInstance(),
										PSMethodUserSelected.getInstance()));
					}

					blackboard.removeInterviewFacts(question);

				}
				else {
					System.out.println("check");
				}

			}

		}

	}

}
