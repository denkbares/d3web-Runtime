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
import java.util.Arrays;
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
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
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
import de.d3web.costbenefit.session.interviewmanager.CostBenefitAgendaSortingStrategy;

/**
 * The PSMethodCostBenefit indicates QContainer to establish a diagnosis as
 * cheap as possible. This is configurable with a TargetFunction, a CostFunction
 * and a SearchAlgorithm.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSMethodCostBenefit extends PSMethodAdapter implements SessionObjectSource {

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
		CostBenefitCaseObject caseObject = (CostBenefitCaseObject) session.getSessionObject(this);
		session.getInterview().getInterviewAgenda().setAgendaSortingStrategy(
				new CostBenefitAgendaSortingStrategy(caseObject));
		// calculateNewPath(caseObject);
		// activateNextQContainer(caseObject);
	}

	/**
	 * This method is just for refreshing the path of the CaseObject, the Facts
	 * get pushed into the blackboard by calculateNewPath
	 * 
	 * @param caseObject {@link SessionObjectSource}
	 */
	void activateNextQContainer(CostBenefitCaseObject caseObject) {
		QContainer[] currentSequence = caseObject.getCurrentSequence();
		Session session = caseObject.getSession();
		if (currentSequence == null) return;
		// only check if the current one is done
		// (or no current one has been activated yet)
		if (caseObject.getCurrentPathIndex() == -1
					|| isDone(currentSequence[caseObject.getCurrentPathIndex()], session)) {
			caseObject.incCurrentPathIndex();
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

	/**
	 * Calculates a new path to a specific target. The method also selects the
	 * calculated path into the interview. The method ensures that the path will
	 * be followed as long as possible.
	 * <p>
	 * Due to changing indications, this method should only be called inside a
	 * propagation frame.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object to select the path for
	 * @param target the target for the path to be calculated
	 * @throws AbortException if no path could been established towards the
	 *         specified target
	 */
	void calculateNewPathTo(CostBenefitCaseObject caseObject, Target target) throws AbortException {
		// first reset the search path
		caseObject.resetPath();

		// in contrast to the "usual" path creation we do *not* consider if
		// there are already any other indicated qasets.

		// searching for the best cost/benefit result
		// (only if there is any benefitual target
		initializeSearchModelTo(caseObject, target);
		SearchModel searchModel = caseObject.getSearchModel();
		searchAlgorithm.search(caseObject.getSession(), searchModel);

		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
			Path minPath = bestTarget.getMinPath();
			activatePath(caseObject, minPath);
		}
		else {
			throw new AbortException();
		}
	}

	private void initializeSearchModelTo(CostBenefitCaseObject caseObject, Target target) {
		Session session = caseObject.getSession();
		SearchModel searchModel = new SearchModel(session);

		searchModel.addTarget(target);
		// initialize benefit in search model to a positive value
		// (use 1 if there is no benefit inside the target)
		double benefit = target.getBenefit();
		if (benefit <= 0) benefit = 1.0;
		searchModel.maximizeBenefit(target, benefit);

		// set the undiscriminated solution to "null" to indicate that we will
		// not consider them for checking to execute a new search
		// we also leave the "discriminating targets" of the case object
		// untouched because they are still valid.
		caseObject.setUndiscriminatedSolutions(null);
		caseObject.setSearchModel(searchModel);
	}

	void calculateNewPath(CostBenefitCaseObject caseObject) {
		// first reset the search path
		caseObject.resetPath();

		// if there are any other interview objects left (e.g. from other
		// problem solvers), we first are going to answer these before creating
		// a new path
		Session session = caseObject.getSession();
		if (hasUnansweredQuestions(session)) return;

		// searching for the best cost/benefit result
		// (only if there is any benefit target)
		initializeSearchModel(caseObject);
		SearchModel searchModel = caseObject.getSearchModel();
		if (searchModel.getBestBenefit() != 0) {
			searchAlgorithm.search(session, searchModel);
		}

		// sets the new path based on the result stored in the search model
		// inside the specified case object.
		Target bestTarget = searchModel.getBestCostBenefitTarget();
		if (!(bestTarget == null || bestTarget.getMinPath() == null)) {
			Path minPath = bestTarget.getMinPath();
			activatePath(caseObject, minPath);
		}
	}

	private boolean hasUnansweredQuestions(Session session) {
		// return ! session.getInterview().getInterviewAgenda().isEmpty();
		Blackboard blackboard = session.getBlackboard();
		for (InterviewObject interviewObject : blackboard.getInterviewObjects()) {
			if (blackboard.getIndication(interviewObject).isRelevant()) {
				if (!isDone(interviewObject, session)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Initializes the search model of this cost benefit case object to search
	 * for the best cost benefit target. All possible questions to discriminate
	 * are taken into account. Questions (or questionnaires) being
	 * contra-indicated are left out from this search.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object to be initialized for searching
	 */
	private void initializeSearchModel(CostBenefitCaseObject caseObject) {
		Session session = caseObject.getSession();
		HashSet<Solution> allSolutions = new HashSet<Solution>();
		HashSet<Target> allTargets = new HashSet<Target>();
		SearchModel searchModel = new SearchModel(session);
		List<StrategicSupport> strategicSupports = getStrategicSupports(session);
		for (StrategicSupport strategicSupport : strategicSupports) {
			// calculate the targets from the strategic support items
			Collection<Solution> solutions = strategicSupport
					.getUndiscriminatedSolutions(session);
			Collection<Question> discriminatingQuestions = strategicSupport
					.getDiscriminatingQuestions(solutions, session);
			Collection<Target> targets = targetFunction.getTargets(session,
					discriminatingQuestions, solutions, strategicSupport);
			// TODO: remove targets of contra-indicated questions/qcontainers
			// and rate the targets into the cost/benefit search model
			for (Target target : targets) {
				double benefit = strategicSupport.getInformationGain(
						target.getQContainers(), solutions, session);
				if (benefit == 0) continue;
				searchModel.addTarget(target);
				searchModel.maximizeBenefit(target, benefit);
			}
			// recall them for storing into the case object
			allSolutions.addAll(solutions);
			allTargets.addAll(targets);
		}
		caseObject.setUndiscriminatedSolutions(allSolutions);
		caseObject.setSearchModel(searchModel);
		caseObject.setDiscriminatingTargets(allTargets);
	}

	/**
	 * Activates a ready-made path by indicating its questionnaires and storing
	 * it into the specified case object
	 * 
	 * @created 08.03.2011
	 * @param caseObject the case object for this cost benefit session
	 * @param path the path to be activated
	 */
	private void activatePath(CostBenefitCaseObject caseObject, Path path) {
		Session session = caseObject.getSession();
		Collection<Node> nodes = path.getNodes();
		QContainer[] currentSequence = new QContainer[nodes.size()];
		int i = 0;
		List<Fact> facts = new LinkedList<Fact>();
		for (Node node : nodes) {
			QContainer qContainer = node.getQContainer();
			currentSequence[i] = qContainer;
			makeOKQuestionsUndone(currentSequence[i], session);
			i++;
			Fact fact = FactFactory.createFact(session, qContainer,
					new Indication(State.INDICATED), this, this);
			facts.add(fact);
			session.getBlackboard().addInterviewFact(fact);
		}
		caseObject.setCurrentSequence(currentSequence);
		caseObject.setCurrentPathIndex(-1);
		caseObject.setIndicatedFacts(facts);
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

						Fact fact = FactFactory.createUserEnteredFact(qoc,
								UndefinedValue.getInstance());

						blackboard.addValueFact(fact);
					}
				}
			}
			makeOKQuestionsUndone(nob, session);
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		CostBenefitCaseObject caseObject = (CostBenefitCaseObject) session.getSessionObject(this);
		Set<QContainer> answeredQuestionnaires = new HashSet<QContainer>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (!entry.isStrategic() && entry.hasChanged() && object instanceof Question) {
				addParentContainers(answeredQuestionnaires, object);
			}
		}
		// only proceed if we have received reals answers
		if (answeredQuestionnaires.isEmpty()) return;

		// for every finished QContainter fire its post transitions
		boolean isAnyQuesionnaireDone = false;
		List<QContainer> sequence = caseObject.getCurrentSequence() != null
				? Arrays.asList(caseObject.getCurrentSequence())
				: new LinkedList<QContainer>();
		for (QContainer qcon : answeredQuestionnaires) {
			if (isDone(qcon, session)) {
				// mark is the questionnaire is either indicated or in our
				// current sequence
				isAnyQuesionnaireDone |= session.getBlackboard().getIndication(qcon).isRelevant();
				isAnyQuesionnaireDone |= sequence.contains(qcon);
				KnowledgeSlice ks = qcon.getKnowledgeStore().getKnowledge(
						StateTransition.KNOWLEDGE_KIND);
				if (ks != null) {
					StateTransition st = (StateTransition) ks;
					st.fire(session);
				}
			}
		}

		// only check if the current path entry is done
		// this is important, because we have to complete a questionnaire to
		// fire its transitions
		// 1. we do not have any sequence yet
		// 2. solutions have changed
		// 3. next questionnaire is no longer applicable

		// 1. we do not have any sequence yet
		// and no other indication is unanswered
		if (!caseObject.hasCurrentSequence() && !hasUnansweredQuestions(session)) {
			calculateNewPath(caseObject);
			activateNextQContainer(caseObject);
			return;
		}

		// cost benefit only requires to act after at least one indicated
		// questionnaire has been completed
		if (!isAnyQuesionnaireDone) return;

		// 2. check if there are any changed to our remembered solutions
		if (hasChangedUndiscriminatedSolutions(caseObject)) {
			calculateNewPath(caseObject);
			activateNextQContainer(caseObject);
			return;
		}

		// continue with our current sequence
		// if current qcontainer is done
		// recalculate if next step of sequence is not applicable
		activateNextQContainer(caseObject);
	}

	/**
	 * Returns if the undiscriminated solutions have changed since the last use
	 * of the search algorithm. This indicates that a new search should be
	 * performed to adapt to the new diagnostic situation.
	 * 
	 * @created 08.03.2011
	 * @param caseObject the cost benefit case object to be checked for changed
	 *        solutions
	 * @return if the undiscriminated solutions have been changed
	 */
	private boolean hasChangedUndiscriminatedSolutions(CostBenefitCaseObject caseObject) {
		// if the current set of undiscriminated solutions is null
		// this indicated that we will not check for undiscriminated solutions
		// at all
		if (caseObject.getUndiscriminatedSolutions() == null) return false;

		// otherwise calculate the current solution sto be discriminated and
		// compare them to the previous ones
		Session session = caseObject.getSession();
		HashSet<Solution> currentSolutions = new HashSet<Solution>();
		for (StrategicSupport strategicSupport : getStrategicSupports(session)) {
			currentSolutions.addAll(strategicSupport.getUndiscriminatedSolutions(session));
		}
		final Set<Solution> previousSolutions = caseObject.getUndiscriminatedSolutions();
		return !previousSolutions.equals(currentSolutions);
	}

	/**
	 * Checks, if all questions, contained in the specified {@link QASet} have a
	 * value assigned to them in the specified session.
	 * 
	 * @param qaset the qaset to be checked
	 * @param session the specified session
	 * @return if the qaset is fully answered
	 */
	private boolean isDone(InterviewObject qaset, Session session) {
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
	public SessionObject createSessionObject(Session session) {
		return new CostBenefitCaseObject(this, session);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.strategic;
	}

	@Override
	public double getPriority() {
		return 6;
	}
}
