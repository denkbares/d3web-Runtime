/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.ids.IterativeDeepeningSearchAlgorithm;
import de.d3web.costbenefit.model.Node;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.costbenefit.session.interviewManager.CostBenefitAgendaSortingStrategy;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * The PSMethodCostBenefit indicates QContainer to establish a diagnosis as
 * cheap as possible. This is configurable with a TargetFunction, a CostFunction
 * and a SearchAlgorithm.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSMethodCostBenefit extends PSMethodAdapter implements CaseObjectSource {

	private TargetFunction targetFunction;
	private CostFunction costFunction;
	private SearchAlgorithm searchAlgorithm;

	public PSMethodCostBenefit(TargetFunction targetFunction,
			CostFunction costFunction, SearchAlgorithm searchAlgorithm) {
		this.targetFunction = targetFunction;
		this.costFunction = costFunction;
		this.searchAlgorithm = searchAlgorithm;
	}

	public PSMethodCostBenefit() {
		this.targetFunction = new DefaultTargetFunction();
		this.costFunction = new DefaultCostFunction();
		this.searchAlgorithm = new IterativeDeepeningSearchAlgorithm();
	}

	@Override
	public void init(Session session) {
		session.getInterview().getInterviewAgenda().setAgendaSortingStrategy(
				new CostBenefitAgendaSortingStrategy());
		CostBenefitCaseObject caseObject = (CostBenefitCaseObject) session.getCaseObject(this);
		calculateNewPath(caseObject);
		activateNextQContainer(caseObject);
	}

	private void activateNextQContainer(CostBenefitCaseObject caseObject) {
		QContainer[] currentSequence = caseObject.getCurrentSequence();
		Session session = caseObject.getSession();
		if (currentSequence != null) {
			if (caseObject.getCurrentPathIndex() == -1
					|| isDone(currentSequence[caseObject.getCurrentPathIndex()], session)) {
				caseObject.incCurrentPathIndex();
				caseObject.setHasBegun(false);
				if (caseObject.getCurrentPathIndex() >= currentSequence.length) {
					calculateNewPath(caseObject);
					activateNextQContainer(caseObject);
					return;
				}
				QContainer qc = currentSequence[caseObject.getCurrentPathIndex()];
				if (!new Node(qc, null).isApplicable(session)) {
					calculateNewPath(caseObject);
					activateNextQContainer(caseObject);
				}
			}
		}
	}

	private void calculateNewPath(CostBenefitCaseObject caseObject) {
		Session session = caseObject.getSession();
		// remove old indications
		Blackboard blackboard = session.getBlackboard();
		List<Fact> indicatedFacts = caseObject.getIndicatedFacts();
		if (indicatedFacts != null) {
			for (Fact fact : indicatedFacts) {
				blackboard.removeInterviewFact(fact);
			}
		}
		caseObject.resetPath();
		List<StrategicSupport> stratgicSupports = getStrategicSupports(session);
		HashSet<Solution> diags = new HashSet<Solution>();
		caseObject.setDiags(diags);
		SearchModel cbm = new SearchModel(session);
		caseObject.setCbm(cbm);
		for (StrategicSupport strategicSupport : stratgicSupports) {
			Collection<Solution> solutions = strategicSupport
					.getPossibleSolutions(session);
			diags.addAll(solutions);
			Collection<Question> discriminatingQuestions = strategicSupport
					.getDiscriminatingQuestions(solutions, session);
			Collection<Target> targets = targetFunction.getTargets(session,
					discriminatingQuestions, solutions, strategicSupport);
			for (Target target : targets) {
				double benefit = strategicSupport.getEntropy(target, solutions, session);
				if (benefit == 0) continue;
				cbm.addTarget(target);
				cbm.maximizeBenefit(target, benefit);
			}
		}
		if (cbm.getBestBenefit() != 0) {
			searchAlgorithm.search(session, cbm);
			Target bestTarget = cbm.getBestCostBenefitTarget();
			if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
				Path minPath = bestTarget.getMinPath();
				Collection<Node> nodes = minPath.getNodes();
				QContainer[] currentSequence = new QContainer[nodes.size()];
				int i = 0;
				List<Fact> facts = new LinkedList<Fact>();
				for (Node node : nodes) {
					QContainer qContainer = node.getQContainer();
					currentSequence[i] = qContainer;
					makeOKQuestionsUndone(currentSequence[i], session);
					i++;
					Fact fact = FactFactory.createFact(qContainer, new Indication(State.INDICATED),
							this, this);
					facts.add(fact);
					blackboard.addInterviewFact(fact);
				}
				caseObject.setCurrentSequence(currentSequence);
				caseObject.setIndicatedFacts(facts);
			}
			caseObject.setCurrentPathIndex(-1);
		}
	}

	private List<StrategicSupport> getStrategicSupports(Session session) {
		List<StrategicSupport> ret = new ArrayList<StrategicSupport>();
		for (PSMethod psm : session.getPSMethods()) {
			if (psm instanceof StrategicSupport) {
				ret.add((StrategicSupport) psm);
			}
		}
		return ret;
	}

	private void makeOKQuestionsUndone(TerminologyObject container, Session session) {
		for (TerminologyObject nob : container.getChildren()) {
			// if ok-question
			if (nob instanceof QuestionOC) {
				QuestionOC qoc = (QuestionOC) nob;
				List<Choice> choices = qoc.getAllAlternatives();
				if (choices.size() == 1
						&& "OK".equals(choices.get(0).getName())) {
					Blackboard blackboard = session.getBlackboard();
					if (UndefinedValue.isNotUndefinedValue(blackboard.getValue(qoc))) {
						blackboard.addValueFact(
								new DefaultFact(qoc, UndefinedValue.getInstance(),
										PSMethodUserSelected.getInstance(),
										PSMethodUserSelected.getInstance()));
					}
				}
			}
			makeOKQuestionsUndone(nob, session);
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		CostBenefitCaseObject caseObject = (CostBenefitCaseObject) session.getCaseObject(this);
		Set<QContainer> qcons = new HashSet<QContainer>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (object instanceof Question) {
				addParentContainers(qcons, object);
			}
		}
		for (QContainer qcon : qcons) {
			if (isDone(qcon, session)) {
				KnowledgeSlice ks = qcon.getKnowledge(
						PSMethodCostBenefit.class,
						StateTransition.STATE_TRANSITION);
				if (ks != null) {
					StateTransition st = (StateTransition) ks;
					st.fire(session);
				}
			}
		}
		// determines if the user has begun our current qContainer
		final QContainer[] currentSequence = caseObject.getCurrentSequence();
		if (!caseObject.isHasBegun() && currentSequence != null) {
			caseObject.setHasBegun(qcons.contains(currentSequence[caseObject.getCurrentPathIndex()]));
		}
		// returns if the actual QContainer is not done and has begun yet
		if (caseObject.isHasBegun()
				&& !isDone(currentSequence[caseObject.getCurrentPathIndex()], session)) {
			return;
		}
		// if the possible Diagnosis have changed, a flag that a new path has to
		// be calculated is set
		boolean changeddiags = false;
		List<StrategicSupport> strategicSupports = getStrategicSupports(session);
		HashSet<Solution> possibleDiagnoses = new HashSet<Solution>();
		for (StrategicSupport strategicSupport : strategicSupports) {
			possibleDiagnoses.addAll(strategicSupport.getPossibleSolutions(session));
		}
		final Set<Solution> diags = caseObject.getDiags();
		if (possibleDiagnoses.size() == diags.size()) {
			for (Solution d : possibleDiagnoses) {
				if (!diags.contains(d)) {
					changeddiags = true;
					break;
				}
			}
		}
		else {
			changeddiags = true;
		}
		// if there is no sequence calculated, the possible diags have changed
		// or the next qcontainer is not applicable, a new branch is calculated
		if (currentSequence == null || changeddiags) {
			calculateNewPath(caseObject);
		}
		activateNextQContainer(caseObject);
	}

	/**
	 * Checks, if all questions, contained in the specified {@link QASet} have a
	 * value assigned to them in the specified session.
	 * 
	 * @param qaset the specified quaset
	 * @param session the specified session
	 * @return
	 */
	private boolean isDone(QASet qaset, Session session) {
		if (qaset instanceof Question) {
			Value value = session.getBlackboard().getValue((Question) qaset);
			if (UndefinedValue.isNotUndefinedValue(value)) {
				boolean done = true;
				for (TerminologyObject object : qaset.getChildren()) {
					done = done && isDone((QASet) object, session);
				}
				return done;
			}
			else {
				return false;
			}
		}
		else if (qaset instanceof QContainer) {
			boolean done = true;
			for (TerminologyObject object : qaset.getChildren()) {
				done = done && isDone((QASet) object, session);
			}
			return done;
		}
		return true;
	}

	public TargetFunction getTargetFunction() {
		return targetFunction;
	}

	public CostFunction getCostFunction() {
		return costFunction;
	}

	public SearchAlgorithm getSearchAlgorithm() {
		return searchAlgorithm;
	}

	public void setSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
		this.searchAlgorithm = searchAlgorithm;
	}

	public void setTargetFunction(TargetFunction targetFunction) {
		this.targetFunction = targetFunction;
	}

	public void setCostFunction(CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	private static void addParentContainers(Set<QContainer> targets,
			TerminologyObject q) {
		for (TerminologyObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add((QContainer) qaset);
			}
			else {
				addParentContainers(targets, qaset);
			}
		}

	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeIndicationFacts(facts);
	}

	@Override
	public SessionObject createCaseObject(Session session) {
		return new CostBenefitCaseObject(this, session);
	}
}
