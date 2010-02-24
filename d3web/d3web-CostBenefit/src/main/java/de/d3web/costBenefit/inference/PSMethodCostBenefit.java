package de.d3web.costBenefit.inference;

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
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionOC;
import de.d3web.costBenefit.CostFunction;
import de.d3web.costBenefit.SearchAlgorithm;
import de.d3web.costBenefit.StateTransition;
import de.d3web.costBenefit.TargetFunction;
import de.d3web.costBenefit.model.Node;
import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;
import de.d3web.costBenefit.model.Target;
import de.d3web.indication.ActionIndication;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * The PSMethodCostBenefit indicates QContainer to establish a diagnosis as
 * cheap as possible. This is configurable with a TargetFunction, a CostFunction
 * and a SearchAlgorithm.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSMethodCostBenefit extends PSMethodAdapter {

	private QContainer[] currentSequence;
	private SearchModel cbm;
	private Rule rule;
	private TargetFunction targetFunction;
	private CostFunction costFunction;
	private SearchAlgorithm searchAlgorithm;
	private int currentPathIndex = -1;
	private boolean hasBegun = false;
	private Set<Diagnosis> diags = new HashSet<Diagnosis>();

	public PSMethodCostBenefit(TargetFunction targetFunction,
			CostFunction costFunction, SearchAlgorithm searchAlgorithm) {
		this.targetFunction = targetFunction;
		this.costFunction = costFunction;
		this.searchAlgorithm = searchAlgorithm;
	}

	@Override
	public void init(XPSCase theCase) {
		calculateNewPath(theCase);
		activateNextQContainer(theCase);
	}
	
	private void activateNextQContainer(XPSCase theCase) {
		if (currentSequence == null) {
			deactivateCurrentQContainer(theCase);
		} else {
			if (currentPathIndex == -1
					|| currentSequence[currentPathIndex].isDone(theCase, true)) {
				deactivateCurrentQContainer(theCase);
				this.currentPathIndex++;
				this.hasBegun = false;
				if (currentPathIndex>=currentSequence.length) {
					calculateNewPath(theCase);
					activateNextQContainer(theCase);
					return;
				}
				QContainer qc = currentSequence[currentPathIndex];
				if (new Node(qc, null).isApplicable(theCase)) {
					activateQContainer(theCase, qc);
				} else {
					calculateNewPath(theCase);
					activateNextQContainer(theCase);
				}
			}
		}
	}

	private void activateQContainer(XPSCase theCase, QContainer qc) {
		rule = new Rule("TempRule");
		rule.setActive(true);
		ActionIndication action = new ActionIndication();
		action.setRule(rule);
		List<QASet> list = new LinkedList<QASet>();
		list.add(qc);
		action.setQASets(list);
		AbstractCondition cond = new CondAnd(
				new LinkedList<AbstractCondition>());
		rule.setAction(action);
		rule.setCondition(cond);
		rule.check(theCase);
	}

	private void deactivateCurrentQContainer(XPSCase theCase) {
		if (rule == null)
			return;
		rule.setCondition(new CondOr(new LinkedList<AbstractCondition>()));
		rule.check(theCase);
		rule = null;
	}

	private void calculateNewPath(XPSCase theCase) {
		List<StrategicSupport> stratgicSupports = getStrategicSupports(theCase);
		diags = new HashSet<Diagnosis>();
		cbm = new SearchModel(theCase);
		for (StrategicSupport strategicSupport: stratgicSupports){
			Collection<Diagnosis> solutions = strategicSupport
					.getPossibleDiagnoses(theCase);
			
			for (Diagnosis d : solutions) {
				diags.add(d);
			}
			Collection<Question> discriminatingQuestions = strategicSupport
					.getDiscriminatingQuestions(solutions, theCase);
			Collection<Target> targets = targetFunction.getTargets(theCase,
					discriminatingQuestions, solutions, strategicSupport);
			for (Target target : targets) {
				double benefit = strategicSupport.getEntropy(target, solutions, theCase);
				if (benefit == 0)
					continue;
				cbm.addTarget(target);
				cbm.maximizeBenefit(target, benefit);
			}
		}
		if (cbm.getBestBenefit() == 0) {
			resetPath();
		} else {
			searchAlgorithm.search(theCase, cbm);
			Target bestTarget = cbm.getBestCostBenefitTarget();
			if (bestTarget == null || bestTarget.getMinPath() == null) {
				// es wurde kein bestes Ziel erreicht, bzw. dessen MinPath ist
				// nicht gefunden worden.
				resetPath();
			} else {
				Path minPath = bestTarget.getMinPath();
				Collection<Node> nodes = minPath.getNodes();
				currentSequence = new QContainer[nodes.size()];
				int i = 0;
				for (Node node : nodes) {
					currentSequence[i] = node.getQContainer();
					makeOKQuestionsUndone(currentSequence[i], theCase);
					i++;
				}
			}
			this.currentPathIndex = -1;
		}
	}

	private List<StrategicSupport> getStrategicSupports(XPSCase theCase) {
		List<StrategicSupport> ret = new ArrayList<StrategicSupport>();
		for (PSMethod psm: theCase.getUsedPSMethods()) {
			if (psm instanceof StrategicSupport) {
				ret.add((StrategicSupport) psm);
			}
		}
		return ret;
	}

	private void resetPath() {
		currentSequence = null;
		this.currentPathIndex = -1;
		hasBegun = false;
	}

	private void makeOKQuestionsUndone(NamedObject container, XPSCase theCase) {
		for (NamedObject nob : container.getChildren()) {
			// if ok-question
			if (nob instanceof QuestionOC) {
				QuestionOC qoc = (QuestionOC) nob;
				List<AnswerChoice> choices = qoc.getAllAlternatives();
				if (choices.size() == 1
						&& "OK".equals(choices.get(0).getText())) {
					qoc.setValue(theCase, new Answer[0]);
					theCase.getAnsweredQuestions().remove(qoc);
				}
			}
			makeOKQuestionsUndone(nob, theCase);
		}
	}

	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		Set<QContainer> qcons = new HashSet<QContainer>();
		for (PropagationEntry entry : changes) {
			NamedObject object = entry.getObject();
			if (object instanceof Question) {
				addParentContainers(qcons, object);
			}
		}
		for (QContainer qcon : qcons) {
			if (qcon.isDone(theCase, true)) {
				List<? extends KnowledgeSlice> knowledge = qcon.getKnowledge(
						PSMethodCostBenefit.class,
						StateTransition.STATE_TRANSITION);
				if (knowledge != null) {
					for (KnowledgeSlice ks : knowledge) {
						StateTransition st = (StateTransition) ks;
						st.fire(theCase);
					}
				}
			}
		}
		// determines if the user has begun our current qContainer
		if (!hasBegun && currentSequence != null) {
			hasBegun = qcons.contains(currentSequence[currentPathIndex]);
		}
		// returns if the actual QContainer is not done and has begun yet
		if (hasBegun && !currentSequence[currentPathIndex].isDone(theCase, true)) {
			return;
		}
		// if the possible Diagnosis have changed, a flag that a new path has to
		// be calculated is set
		boolean changeddiags = false;
		Collection<Diagnosis> possibleDiagnoses = PSMethodXCL.getInstance()
				.getPossibleDiagnoses(theCase);
		if (possibleDiagnoses.size() == diags.size()) {
			for (Diagnosis d : possibleDiagnoses) {
				if (!diags.contains(d)) {
					changeddiags = true;
					break;
				}
			}
		} else {
			changeddiags = true;
		}
		// if there is no sequence calculated, the possible diags have changed
		// or the next qcontainer is not applicable, a new branch is calculated
		if (currentSequence == null || changeddiags) {
			calculateNewPath(theCase);
		}
		activateNextQContainer(theCase);
	}

	public TargetFunction getTargetFunction() {
		return targetFunction;
	}

	public CostFunction getCostFunction() {
		return costFunction;
	}

	public QContainer[] getCurrentPath() {
		return currentSequence;
	}

	private static void addParentContainers(Set<QContainer> targets,
			NamedObject q) {
		for (NamedObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add((QContainer) qaset);
			} else {
				addParentContainers(targets, qaset);
			}
		}

	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// TODO Auto-generated method stub
		return null;
	}
}
