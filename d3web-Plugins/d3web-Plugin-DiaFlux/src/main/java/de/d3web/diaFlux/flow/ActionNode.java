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

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.ForcingSetQuestionAction;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.interview.inference.condition.CondRepeatedAnswered;

public class ActionNode extends AbstractNode {

	private final PSAction action;
	private final Condition edgePrecondition;

	public ActionNode(String id, PSAction action) {
		super(id, action.toString());

		Condition preCondition = ConditionTrue.INSTANCE;
		if (action instanceof ActionRepeatedIndication) {
			Question question = (Question) ((ActionRepeatedIndication) action).getQASets().get(0);
			preCondition = new CondRepeatedAnswered(question);
		}
		else if (action instanceof ActionSetQuestion) {
			action = new ForcingSetQuestionAction((ActionSetQuestion) action);
		}

		this.action = action;
		this.edgePrecondition = preCondition;
	}

	// do not call this method internally, as it unwraps a
	// ForcingSetQuestionAction
	public PSAction getAction() {
		if (this.action instanceof ForcingSetQuestionAction) {
			return ((ForcingSetQuestionAction) this.action).getDelegate();
		}
		else {
			return this.action;
		}
	}

	@Override
	public void execute(Session session, FlowRun run) {
		this.action.doIt(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public Condition getEdgePrecondition() {
		return edgePrecondition;
	}

	@Override
	public List<? extends TerminologyObject> getHookedObjects() {
		List<? extends TerminologyObject> objects = new LinkedList<TerminologyObject>(
				this.action.getForwardObjects());
		objects.removeAll(this.action.getBackwardObjects());

		return objects;
	}

	@Override
	public void retract(Session session, FlowRun run) {
		this.action.undo(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public void takeSnapshot(Session session) {
		super.takeSnapshot(session);

		// redo action with SSN as source
		retract(session, null);
		if (!(this.action instanceof ActionRepeatedIndication)) {
			this.action.doIt(session, FluxSolver.SNAPSHOT_SOURCE,
					session.getPSMethodInstance(FluxSolver.class));
		}
	}

	@Override
	public void update(Session session, FlowRun run) {
		this.action.update(session, this, session.getPSMethodInstance(FluxSolver.class));
	}
}
