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
import java.util.logging.Logger;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.diaFlux.inference.CallFlowAction;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionRepeatedIndication;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;

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
	public boolean canFireEdges(Session session) {
		if (action instanceof ActionRepeatedIndication) {

			// TODO check for IOBE, only works for Questions
			Question question = (Question) ((ActionRepeatedIndication) action).getQASets().get(0);
			PSMethod psMethod = session.getPSMethodInstance(FluxSolver.class);
			Fact interviewFact = session.getBlackboard().getInterviewFact(question, psMethod);
			Fact valueFact = session.getBlackboard().getValueFact(question);

			if (valueFact == null) {
				return true;
			}

			long indicationTime = interviewFact.getTime();
			long valueTime = valueFact.getTime();
			return valueTime > indicationTime;
		}
		else {
			return super.canFireEdges(session);
		}
	}

	@Override
	public List<? extends TerminologyObject> getForwardKnowledge() {
		List<? extends TerminologyObject> objects = action.getForwardObjects();
		objects.removeAll(action.getBackwardObjects());

		return objects;
	}

	@Override
	public void undoAction(Session session) {
		getAction().undo(session, this, session.getPSMethodInstance(FluxSolver.class));
	}

	@Override
	public boolean couldActivate(Session session) {
		// TODO repeated indication of Questions also without snapshots
		// not sure yet if this works
		if (action instanceof ActionRepeatedIndication) {
			return true;
		}
		else {
			return super.couldActivate(session);
		}
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {

		// createSnapshotFact(session, snapshotNode);

		super.takeSnapshot(session, snapshotNode, nodes);

		// TODO do this for all types of nodes?
		undoAction(session);

		getAction().doIt(session, snapshotNode, session.getPSMethodInstance(FluxSolver.class));

	}

	/**
	 * 
	 * @param session
	 * @param snapshotNode
	 */
	private void createSnapshotFact(Session session, SnapshotNode snapshotNode) {

		Blackboard blackboard = session.getBlackboard();

		PSMethod psMethod = session.getPSMethodInstance(FluxSolver.class);

		// TODO can these be made more general?
		if (action instanceof ActionNextQASet) {

			ActionNextQASet nextQASetaction = (ActionNextQASet) action;
			QASet qaSet = nextQASetaction.getQASets().get(0);

			State state = nextQASetaction.getIndication().getState();

			blackboard.addInterviewFact(FactFactory.createIndicationFact(session, qaSet,
					new Indication(state), snapshotNode, psMethod));

		}
		else if (action instanceof ActionHeuristicPS) {

			ActionHeuristicPS heuristicAction = (ActionHeuristicPS) action;
			Solution solution = heuristicAction.getSolution();
			Score score = heuristicAction.getScore();

			Value value = new HeuristicRating(score);

			blackboard.addValueFact(FactFactory.createFact(session, solution, value, snapshotNode,
					psMethod));

		}
		else if (action instanceof ActionSetValue) {

			Question question = ((ActionSetValue) action).getQuestion();
			Object valueObj = ((ActionSetValue) action).getValue();
			Value value;

			if (valueObj instanceof FormulaElement) {

				value = ((FormulaElement) valueObj).eval(session);

			}
			else {
				value = (Value) valueObj;
			}

			blackboard.addValueFact(FactFactory.createFact(session, question, value, snapshotNode,
					psMethod));

		}
		else if (action == NOOPAction.INSTANCE || action instanceof CallFlowAction) {
			// nothing to do here...
		}
		else {
			Logger.getLogger(getClass().getName()).severe(
					"Could not create SnapshotFact for action: " + action.toString());
		}

	}

}
