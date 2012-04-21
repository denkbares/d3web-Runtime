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

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.CondRepeatedAnswered;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.indication.ActionRepeatedIndication;

public class ActionNode extends AbstractNode {

	private final PSAction action;
	private final Condition edgePrecondition;

	public ActionNode(String id, PSAction action) {
		super(id, action.toString());

		this.action = action;

		if (this.action instanceof ActionRepeatedIndication) {
			Question question = (Question) ((ActionRepeatedIndication) action).getQASets().get(0);
			this.edgePrecondition = new CondRepeatedAnswered(question);
		}
		else {
			edgePrecondition = ConditionTrue.INSTANCE;
		}

	}

	public PSAction getAction() {
		return action;
	}

	@Override
	public void execute(Session session, FlowRun run) {
		getAction().doIt(session, this, session.getPSMethodInstance(FluxSolver.class));

	}

	@Override
	public Condition getEdgePrecondition() {
		return edgePrecondition;
	}

	@Override
	public List<? extends TerminologyObject> getHookedObjects() {
		List<? extends TerminologyObject> objects = new LinkedList<TerminologyObject>(
				action.getForwardObjects());
		objects.removeAll(action.getBackwardObjects());

		return objects;
	}

	@Override
	public void retract(Session session, FlowRun run) {
		getAction().undo(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public boolean isReevaluate(Session session) {
		return action.hasChangedValue(session);
	}

	@Override
	public void takeSnapshot(Session session) {

		super.takeSnapshot(session);

		// redo action with SSN as source
		retract(session, null);
		if (!(getAction() instanceof ActionRepeatedIndication)) {
			getAction().doIt(session, FluxSolver.SNAPSHOT_SOURCE,
					session.getPSMethodInstance(FluxSolver.class));
		}

	}

}
