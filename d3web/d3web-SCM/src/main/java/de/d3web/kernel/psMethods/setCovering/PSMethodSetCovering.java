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

package de.d3web.kernel.psMethods.setCovering;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationEntry;
import de.d3web.kernel.psMethods.setCovering.pools.HypothesisPool;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.utils.FindingsByQuestionIdContainer;
import de.d3web.kernel.psMethods.setCovering.utils.SortedList;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.HypothesisComparator;
import de.d3web.kernel.supportknowledge.Property;

/**
 * This is the problem solving method for SetCoveringModels
 * 
 * @author bates
 */
public class PSMethodSetCovering implements PSMethod {

	private static class SCDiagnosisByScoreComparator implements Comparator {
		private XPSCase theCase = null;
		public SCDiagnosisByScoreComparator(XPSCase theCase) {
			this.theCase = theCase;
		}
		public int compare(Object o1, Object o2) {
			try {
				SCDiagnosis d1 = (SCDiagnosis) o1;
				SCDiagnosis d2 = (SCDiagnosis) o2;

				double score1 = d1.getCurrentScore(theCase);
				double score2 = d2.getCurrentScore(theCase);
				
				if (Double.isNaN(score1)) {
					score1 = 0;
				}
				if (Double.isNaN(score2)) {
					score2 = 0;
				}
				
				if (score1 < score2) {
					return 1;
				} else if (score2 < score1) {
					return -1;
				} else {
					double d1Covered = d1.getCoveredSymptomWeightSum(theCase) - d1.getPositiveRemainderCoveringStrengthSum(theCase);
					double d2Covered = d2.getCoveredSymptomWeightSum(theCase) - d2.getPositiveRemainderCoveringStrengthSum(theCase);
					return (int) ((d2Covered - d1Covered) * 1000);
				}

			} catch (Exception e) {
				return 0;
			}
		}
	}

	private ResourceBundle bundle = null;

	private Map observedFindingsByXPSCase = null;

	// *********************** new score calculation *************************
	private Map diagnosisQueueByXPSCase = null;
	private Map totalObservedFindingWeightSumByXPSCase = null;

	public double getTotalObservedFindingsWeightSum(XPSCase theCase) {
		Double score = (Double) totalObservedFindingWeightSumByXPSCase.get(theCase);
		if (score != null) {
			return score.doubleValue();
		}
		return 0;
	}

	private void updateObservedWeightSum(XPSCase theCase) {
		double weightSum = 0;
		Collection observedFindings = (Collection) observedFindingsByXPSCase.get(theCase);
		if (observedFindings != null) {
			Iterator iter = observedFindings.iterator();
			while (iter.hasNext()) {
				Finding f = (Finding) iter.next();
				double weight = getCurrentWeight(f, theCase);
				weightSum += weight;
			}
		}
		totalObservedFindingWeightSumByXPSCase.put(theCase, new Double(weightSum));
	}

	private double getCurrentWeight(Finding f, XPSCase theCase) {
		double weight = f.getWeight(theCase);
		if (f.getNamedObject() instanceof QuestionMC) {
			if (f.getAnswers() != null && f.getAnswers().length > 0) {
				weight *= f.getAnswers().length;
			}
		}
		return weight;
	}

	public void diagnosisScoreUpdated(XPSCase theCase, SCDiagnosis diagnosis) {
		SortedList pQueue = (SortedList) diagnosisQueueByXPSCase.get(theCase);
		if (pQueue == null) {
			pQueue = new SortedList(new SCDiagnosisByScoreComparator(theCase));
			diagnosisQueueByXPSCase.put(theCase, pQueue);
		}
		pQueue.remove(diagnosis);
		pQueue.add(diagnosis);
	}

	public SortedList getSortedSingleSolutions(XPSCase theCase) {
		return (SortedList) diagnosisQueueByXPSCase.get(theCase);
	}
	// **********************************************************************

	private Map transitiveClosureByKnowledgeBase = null;

	private Map globalHypSetByXPSCase = null;

	private Set xpsCases = null;

	private StrengthCalculationStrategy strengthCalcStrategy = DefaultStrengthCalculationStrategy
			.getInstance();
	private StrengthSelectionStrategy strengthSelStrategy = DefaultStrengthSelectionStrategy
			.getInstance();

	private static PSMethodSetCovering instance = null;

	private PSMethodSetCovering() {
		//xpsCases = SetPool.getInstance().getEmptySet();
		xpsCases = new HashSet();
		observedFindingsByXPSCase = new HashMap();
		totalObservedFindingWeightSumByXPSCase = new HashMap();
		transitiveClosureByKnowledgeBase = new HashMap();
		globalHypSetByXPSCase = new HashMap();
		diagnosisQueueByXPSCase = new HashMap();
		bundle = ResourceBundle.getBundle("properties.SCM", new Locale("en", "US"), getClass()
				.getClassLoader());
	}

	public static PSMethodSetCovering getInstance() {
		if (instance == null) {
			instance = new PSMethodSetCovering();
		}
		return instance;
	}

	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	/**
	 * <b>Not implemented!</b> Returns NULL by default.
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {
		return null;
	}

	public void removeKnowledgeBase(KnowledgeBase knowledgeBase) {
		Iterator iter = xpsCases.iterator();
		while (iter.hasNext()) {
			XPSCase theCase = (XPSCase) iter.next();
			iter.remove();
			removeXPSCase(theCase);
		}
		transitiveClosureByKnowledgeBase.remove(knowledgeBase);
	}

	private void clearDiagnosesFromXPSCase(Collection diags, XPSCase theCase) {
		Iterator iter = diags.iterator();
		while (iter.hasNext()) {
			SCDiagnosis scDiag = (SCDiagnosis) iter.next();
			scDiag.removeXPSCase(theCase);
		}
	}

	public void removeXPSCase(XPSCase theCase) {
		observedFindingsByXPSCase.remove(theCase);
		totalObservedFindingWeightSumByXPSCase.remove(theCase);
		TransitiveClosure closure = ((TransitiveClosure) transitiveClosureByKnowledgeBase
				.get(theCase.getKnowledgeBase()));

		clearDiagnosesFromXPSCase(closure.getNodes(SCDiagnosis.class), theCase);

		Collection hypotheses = (Collection) globalHypSetByXPSCase.get(theCase);
		if (hypotheses != null) {
			Iterator iter = hypotheses.iterator();
			while (iter.hasNext()) {
				Hypothesis hyp = (Hypothesis) iter.next();
				HypothesisPool.getInstance().free(hyp);
			}
			hypotheses.clear();
			globalHypSetByXPSCase.remove(theCase);
		}
		diagnosisQueueByXPSCase.remove(theCase);
		xpsCases.remove(theCase);
	}

	/**
	 * initializes this PSMethod with the given XPSCase. Creates a transitive
	 * closure and initializes the diagnoses...
	 */
	public void init(XPSCase theCase) {
		xpsCases.add(theCase);
		Collection relations = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(
				PSMethodSetCovering.class);
		//Set scNodes = SetPool.getInstance().getEmptySet();
		Set scNodes = new HashSet();
		Iterator relationsIter = relations.iterator();
		while (relationsIter.hasNext()) {
			Object o = relationsIter.next();
			if (o instanceof SCRelation) {
				SCRelation relation = (SCRelation) o;
				scNodes.add(relation.getSourceNode());
				scNodes.add(relation.getTargetNode());
			}
		}

		TransitiveClosure transitiveClosure = new TransitiveClosure(scNodes);
		transitiveClosureByKnowledgeBase.put(theCase.getKnowledgeBase(), transitiveClosure);

		initializeSCDiagnoses(transitiveClosure, scNodes);
		observedFindingsByXPSCase.remove(theCase);
		totalObservedFindingWeightSumByXPSCase.remove(theCase);
	}

	/**
	 * initializes this PSMethod with the given KnowledgeBase. Creates a
	 * transitive closure and initializes the diagnoses...
	 */
	public void init(KnowledgeBase kb) {
		Collection relations = kb.getAllKnowledgeSlicesFor(PSMethodSetCovering.class);
		//Set scNodes = SetPool.getInstance().getEmptySet();
		Set scNodes = new HashSet();
		Iterator relationsIter = relations.iterator();
		while (relationsIter.hasNext()) {
			Object o = relationsIter.next();
			if (o instanceof SCRelation) {
				SCRelation relation = (SCRelation) o;
				scNodes.add(relation.getSourceNode());
				scNodes.add(relation.getTargetNode());
			}
		}
		TransitiveClosure transitiveClosure = new TransitiveClosure(scNodes);
		transitiveClosureByKnowledgeBase.put(kb, transitiveClosure);
		initializeSCDiagnoses(transitiveClosure, scNodes);
		FindingsByQuestionIdContainer.getInstance().initialize(kb);
	}

	private void initializeSCDiagnoses(TransitiveClosure transitiveClosure, Set scNodes) {
		Iterator iter = scNodes.iterator();
		while (iter.hasNext()) {
			SCNode node = (SCNode) iter.next();
			if (!node.isLeaf()) {
				((SCDiagnosis) node).initialize(transitiveClosure, strengthCalcStrategy,
						strengthSelStrategy);
			}
		}
	}

	public boolean isContributingToResult() {
		return false;
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @return the global latest generated hypotheses set for the current
	 *         XPSCase
	 */
	public Set getGlobalHypothesesSet(XPSCase theCase) {
		Set hypSet = (Set) globalHypSetByXPSCase.get(theCase);
		if (hypSet == null) {
			//hypSet = SetPool.getInstance().getEmptySet();
			hypSet = new HashSet();
			globalHypSetByXPSCase.put(theCase, hypSet);
		}
		return hypSet;
	}

	public List getSortedSolutions(XPSCase theCase) {
		List result = new LinkedList(getGlobalHypothesesSet(theCase));
		Collections.sort(result, new HypothesisComparator(theCase));
		return result;
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @return the whole set of observed findings according the current case
	 */
	public Set getObservedFindings(XPSCase theCase) {
		return (Set) observedFindingsByXPSCase.get(theCase);
	}

	/**
	 * 
	 * @param kb
	 *            used KnowledgeBase
	 * @return the transitive closure over the set-covering model according to
	 *         the given KnowledgeBase
	 */
	public TransitiveClosure getTransitiveClosure(KnowledgeBase kb) {
		TransitiveClosure closure = (TransitiveClosure) transitiveClosureByKnowledgeBase.get(kb);
		if (closure == null) {
			init(kb);
			closure = (TransitiveClosure) transitiveClosureByKnowledgeBase.get(kb);
		}
		return closure;
	}

	/**
	 * propagates new values for the given NamedObject. If nob is a Question it
	 * will become a new PredictedFinding (with given value)
	 */
	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		boolean hasChange = false;
		for (PropagationEntry change : changes) {
			NamedObject nob = change.getObject();
			// only handle relevant changes
			if (!(nob instanceof Question)) continue;
			Object[] newValue = change.getNewValue();
			if (newValue == null || newValue.length == 0) continue;
			hasChange = true;

			TransitiveClosure transitiveClosure = (TransitiveClosure) transitiveClosureByKnowledgeBase
					.get(theCase.getKnowledgeBase());
	
			// remove previously observed findings with same parameter
			ObservableFinding paramF = SCNodeFactory.createObservableFinding((Question) nob,
					new Object[]{newValue[0]});
			removeParametricallyFromGlobalObservedHash(theCase, paramF);

			for (int i = 0; i < newValue.length; ++i) {
				ObservableFinding f = SCNodeFactory.createObservableFinding((Question) nob,
						new Object[]{newValue[i]});

				boolean unknown = ((Answer) newValue[i]).isUnknown();
				// add new finding, if value not empty
				if (!unknown) {
					addToGlobalObservedHash(theCase, f);
				}
				Collection diagnosesReachingF = retrieveDiagnosesCoveringParametrically(
						transitiveClosure, f);
				if (diagnosesReachingF != null) {
					Iterator ancestorsIter = diagnosesReachingF.iterator();
					while (ancestorsIter.hasNext()) {
						SCDiagnosis diagCoveringF = (SCDiagnosis) ancestorsIter.next();
						// update all diagnoses that cover f parametrically
						if (i == 0) {
							diagCoveringF.removeObservedFindingsFor(theCase, (Question) nob);
						}
						diagCoveringF.addObservedFinding(theCase, f);
					}
				}
			}
		}
		// new score calculation if there have been any changes
		if (hasChange) {
			Object val = theCase.getKnowledgeBase().getProperties().getProperty(Property.SC_PROBLEMSOLVER_SIMPLE);
			//DEFAULT: should be FALSE to use standard SC-ProblemSolver
			boolean simple = false;
			if(val != null && val instanceof Boolean) {
				simple = ((Boolean)val).booleanValue();
			}
			for (Diagnosis d : theCase.getKnowledgeBase().getDiagnoses()) {
				SCDiagnosis scDiag = getSCDiagnosisForDiagnosis(d);
				if (scDiag != null) {						
					if (simple) {
						scDiag.updateScoreSimple(theCase);
					} else { 
						scDiag.updateScore(theCase);
					}
				}
			}
		}
	}
	
	private SCDiagnosis getSCDiagnosisForDiagnosis(Diagnosis d) {
		Collection knowledgeList = d.getKnowledge(PSMethodSetCovering.class, MethodKind.FORWARD);
		if ((knowledgeList != null) && !knowledgeList.isEmpty()) {
			SCRelation r = (SCRelation) knowledgeList.iterator().next();
			SCDiagnosis scDiag = (SCDiagnosis) r.getSourceNode();
			return scDiag;
		}
		return null;
	}

	private Collection retrieveDiagnosesCoveringParametrically(TransitiveClosure closure,
			ObservableFinding obsF) {
		//Set ret = SetPool.getInstance().getEmptySet();
		Set ret = new HashSet();
		Iterator iter = closure.getNodes(PredictedFinding.class).iterator();
		while (iter.hasNext()) {
			PredictedFinding finding = (PredictedFinding) iter.next();
			if (obsF.parametricallyEquals(finding)) {
				Iterator ancestorIter = closure.getRelationsBySCNodesLeadingTo(finding).keySet()
						.iterator();
				while (ancestorIter.hasNext()) {
					SCNode ancestor = (SCNode) ancestorIter.next();
					if (!ancestor.isLeaf()) {
						ret.add(ancestor);
					}
				}
			}
		}
		return ret;
	}

	private void removeParametricallyFromGlobalObservedHash(XPSCase theCase, Finding f) {
		Set observed = (Set) observedFindingsByXPSCase.get(theCase);
		if (observed != null) {
			Iterator iter = observed.iterator();
			while (iter.hasNext()) {
				Finding obs = (Finding) iter.next();
				if (obs.parametricallyEquals(f)) {
					iter.remove();
				}
			}
		}
		updateObservedWeightSum(theCase);
	}

	private void addToGlobalObservedHash(XPSCase theCase, ObservableFinding f) {
		Set observed = (Set) observedFindingsByXPSCase.get(theCase);
		if (observed == null) {
			//observed = SetPool.getInstance().getEmptySet()
			observed = new HashSet();
			observedFindingsByXPSCase.put(theCase, observed);
		}
		observed.add(f);
		updateObservedWeightSum(theCase);
	}

	/**
	 * 
	 * @return all registered XPSCases
	 */
	public Set getXPSCases() {
		return xpsCases;
	}

}
