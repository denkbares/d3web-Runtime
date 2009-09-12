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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.IEventSource;
import de.d3web.kernel.domainModel.KBOEventListener;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.simple.SimpleMCCoverage;
import de.d3web.kernel.psMethods.setCovering.simple.SimpleSCResult;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;
import de.d3web.kernel.supportknowledge.Property;

/**
 * This is a Diagnosis-wrapper for SCM with several accounts.
 * 
 * @author bates, georg
 */
public class SCDiagnosis extends SCNode implements IEventSource {

	private Map strengthByTransitivePredictedFindings = null;

	private Map transitiveObservedFindingsByXPSCase = null;
	private Map transitiveParametricPredictedFindingsByXPSCase = null;
	private Map transitiveExplainedFindingsByXPSCase = null;
	private Map transitiveNegativePredictedFindingsByXPSCase = null;

	private double aprioriProbability = 0.5;

	// *********************** new score calculation *************************
	private Map coveredSymptomWeightSumByXPSCase = null;
	private Map remainderPositiveCoveringStrengthsByXPSCase = null;
	private Map remainderNegativeCoveringStrengthsByXPSCase = null;
	private Map currentScoreByXPSCase = null;

	// *********************** simple set covering calculation
	// *************************
	private Map<XPSCase, SimpleSCResult> simpleCoverageResultsByXPSCase = null;

	public void updateScoreSimple(XPSCase theCase) {

		KnowledgeBase kb = theCase.getKnowledgeBase();

		Collection<? extends KnowledgeSlice> screlations = ((Diagnosis) this
				.getNamedObject()).getKnowledge(PSMethodSetCovering.class);
		Collection<Question> expectedQuestions = new HashSet<Question>();
		HashMap<Question, SimpleMCCoverage> coveredQuestions = new HashMap<Question, SimpleMCCoverage>();
		for (KnowledgeSlice slice : screlations) {
			if (slice instanceof SCRelation) {
				SCRelation screl = ((SCRelation) (slice));
				SCNode source = screl.getSourceNode();
				double p = screl.getProbability();
				if (source == this && p > 0) {
					SCNode target = screl.getTargetNode();

					if (target.getNamedObject() instanceof Question) {
						Question q = (Question) target.getNamedObject();
						expectedQuestions.add(q);

						List answers = q.getValue(theCase);
						if (target instanceof Finding) {
							Finding f = (Finding) target;
							if (q instanceof QuestionChoice) {
								Object[] definedAnswers = f.getAnswers();
								if (q instanceof QuestionOC) {
									for (int i = 0; i < definedAnswers.length; i++) {

										if (answers
												.contains((Answer) definedAnswers[i])) {

											coveredQuestions.put(q, new SimpleMCCoverage());
										}
									}
								}
							}
							if( q instanceof QuestionMC) {
								SimpleMCCoverage cover = new SimpleMCCoverage();
								if(coveredQuestions.containsKey(q)) {
									cover = coveredQuestions.get(q);
								}else {
									coveredQuestions.put(q,cover);
								}
								cover.setAnswerSet(answers);
								cover.addDefinedAnswer(f.getAnswers());
							}
							if (q instanceof QuestionNum) {
								boolean match = matchAnswerNum(f, answers,
										theCase);
								if (match) {
									coveredQuestions.put(q,new SimpleMCCoverage());
								}
							}
						}
					}

				}

			}
		}
		
		//total weight is count of defined Questions in model
		int positiveDefinitionCnt = expectedQuestions.size();
		
		//covered weight is sum of coverings
		double coveredQuestionCnt = 0; 
		for (Question question2 : coveredQuestions.keySet()) {
			coveredQuestionCnt += coveredQuestions.get(question2).calcIntersection();
		}
		
		//relevant Questions
		List<? extends Question> answered = theCase.getAnsweredQuestions();
		int answeredAndDefinedCnt = 0;
		for (Question question : answered) {
			if ((question.getValue(theCase).size() > 0)
					&& expectedQuestions.contains(question)) {
				if (question.getValue(theCase).get(0) instanceof AnswerUnknown) {
					// Unknown wird als unbeantwortet berechnet
				} else {
					answeredAndDefinedCnt++;
				}
			}
		}

		if (ruleHypbridExtension(theCase) && isExcludedByRule(theCase)) {
			simpleCoverageResultsByXPSCase.put(theCase, new SimpleSCResult(
					positiveDefinitionCnt, answeredAndDefinedCnt, 0));
			PSMethodSetCovering.getInstance().diagnosisScoreUpdated(theCase,
					this);
			notifyListeners(theCase, this);
			return;

		}

		simpleCoverageResultsByXPSCase.put(theCase, new SimpleSCResult(
				positiveDefinitionCnt, answeredAndDefinedCnt,
				coveredQuestionCnt));
		notifyListeners(theCase, this);
	}

	private boolean matchAnswerNum(Finding f, List answers, XPSCase theCase) {
		boolean b = false;
		for (Object object : answers) {
			if (object instanceof AnswerNum) {
				// double num = (Double)((AnswerNum)object).getValue(theCase);
				if (f instanceof PredictedFinding) {
					AbstractCondition cond = ((PredictedFinding) f)
							.getCondition();

					try {
						b = cond.eval(theCase);
					} catch (NoAnswerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownAnswerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return b;
	}

	public double updateScore(XPSCase theCase) {
		/*
		 * before computing covering first check, if rule-based exclusion is
		 * activated: hybrid computation of score: if heuristic problem-solver
		 * excludes solution, then set score to "0" by default
		 */
		if (ruleHypbridExtension(theCase) && isExcludedByRule(theCase)) {
			currentScoreByXPSCase.put(theCase, new Double(0));
			PSMethodSetCovering.getInstance().diagnosisScoreUpdated(theCase,
					this);
			notifyListeners(theCase, this);
			return 0;

		}

		double coveredSymptomWeightSum = 0;
		double positiveRemainderSum = 0;
		double negativeRemainderSum = 0;
		double currentScore = 0;

		Set explained = getTransitiveExplainedFindings(theCase);
		if (explained != null) {
			Iterator iter = explained.iterator();
			while (iter.hasNext()) {
				PredictedFinding f = (PredictedFinding) iter.next();
				double cStrength = getCoveringStrength(f);
				if (cStrength >= 0) {
					// finding counts for the diagnosis
					coveredSymptomWeightSum += f.getWeight(theCase);
					positiveRemainderSum += (f.getWeight(theCase) * (1 - cStrength));
				} else {
					// finding counts against the diagnosis
					negativeRemainderSum += (f.getWeight(theCase) * (1 + cStrength));
				}
			}
		}

		Hashtable<Finding, PredictedFinding> observedWithSimilarity = getObservedFindingsWithSimilarity(theCase);
		if (observedWithSimilarity != null) {
			for (Finding f : observedWithSimilarity.keySet()) {
				PredictedFinding mostSimilar = observedWithSimilarity.get(f);
				double similarity = f
						.calculateSimilarity(Arrays.asList(f.getAnswers()),
								Arrays.asList(mostSimilar.getAnswers()));

				double cStrength = getCoveringStrength(mostSimilar);
				if (cStrength >= 0) {
					coveredSymptomWeightSum += f.getWeight(theCase)
							* similarity;
					positiveRemainderSum += (f.getWeight(theCase)
							* (1 - cStrength) * similarity);
				} else {
					negativeRemainderSum += (f.getWeight(theCase)
							* (1 + cStrength) * similarity);
				}
			}
		}

		coveredSymptomWeightSumByXPSCase.put(theCase, new Double(
				coveredSymptomWeightSum));
		remainderPositiveCoveringStrengthsByXPSCase.put(theCase, new Double(
				positiveRemainderSum));
		remainderNegativeCoveringStrengthsByXPSCase.put(theCase, new Double(
				negativeRemainderSum));

		double totalObsWeightSum = PSMethodSetCovering.getInstance()
				.getTotalObservedFindingsWeightSum(theCase);

		currentScore = (coveredSymptomWeightSum - positiveRemainderSum)
				/ (totalObsWeightSum - positiveRemainderSum - negativeRemainderSum);

		// here, the computed score is updated.
		currentScoreByXPSCase.put(theCase, new Double(currentScore));

		PSMethodSetCovering.getInstance().diagnosisScoreUpdated(theCase, this);
		notifyListeners(theCase, this);
		return currentScore;
	}

	private boolean ruleHypbridExtension(XPSCase theCase) {
		Boolean b = (Boolean) theCase.getKnowledgeBase().getProperties()
				.getProperty(Property.RULEBASED_EXCLUSION);
		return b != null && b.booleanValue();
	}

	/*
	 * Checks, if the corresponding Diagnosis object was excluded by a heuristic
	 * scoring rule.
	 */
	private boolean isExcludedByRule(XPSCase theCase) {
		Diagnosis d = (Diagnosis) getNamedObject();
		return (d.getState(theCase, PSMethodHeuristic.class))
				.equals(DiagnosisState.EXCLUDED);
	}

	/**
	 * Returns a Hashtable that maps observed (but not explained) findings,
	 * which have a similar PredictedFinding, to the most similar
	 * PredictedFinding.
	 */
	public Hashtable<Finding, PredictedFinding> getObservedFindingsWithSimilarity(
			XPSCase theCase) {
		Hashtable<Finding, PredictedFinding> simFindings = new Hashtable();

		Set<Finding> observedFindings = getObservedFindings(theCase);
		Set<Finding> explainedFindings = getTransitiveExplainedFindings(theCase);
		for (Finding observed : observedFindings) {
			if (!containsFinding(explainedFindings, observed)) {
				PredictedFinding mostSimilar = getMostSimilarPredictedFinding(observed);
				if (mostSimilar != null) {
					simFindings.put(observed, mostSimilar);
				}
			}
		}
		return simFindings;
	}

	/**
	 * Returns the PredictedFinding that is most similar to the given Finding.
	 */
	public PredictedFinding getMostSimilarPredictedFinding(Finding finding) {
		Set<PredictedFinding> predictedFindings = getTransitivePredictedFindings();
		List<PredictedFinding> predictedFindingsConcerningObserved = getPredictedFindingsForNamedObject(
				predictedFindings, finding.getNamedObject());
		PredictedFinding mostSimilar = getMostSimilarFinding(finding,
				predictedFindingsConcerningObserved);
		return mostSimilar;
	}

	/**
	 * Returns all findings of the given list of findings that concern the given
	 * NamedObject.
	 */
	private List<PredictedFinding> getPredictedFindingsForNamedObject(
			Set<PredictedFinding> predictedFindings, NamedObject o) {
		List<PredictedFinding> findings = new LinkedList();
		for (PredictedFinding finding : predictedFindings) {
			if (finding.getNamedObject() == o) {
				findings.add(finding);
			}
		}
		return findings;
	}

	/**
	 * Returns the PredictedFinding out of the given list, which is most similar
	 * to the given Finding. null will be returned, if all PredictedFindings
	 * have a 0 similarity to the given Finding.
	 */
	private PredictedFinding getMostSimilarFinding(Finding f,
			List<PredictedFinding> similarFindings) {
		PredictedFinding mostSimilar = null;
		double maxSim = 0;
		for (PredictedFinding finding : similarFindings) {
			// if not the same finding
			if (finding.getAnswers().length != f.getAnswers().length
					|| !Arrays.asList(finding.getAnswers()).containsAll(
							Arrays.asList(f.getAnswers()))) {
				double sim = finding.calculateSimilarity(Arrays.asList(f
						.getAnswers()), Arrays.asList(finding.getAnswers()));
				if (sim > maxSim) {
					maxSim = sim;
					mostSimilar = finding;
				}
			}
		}

		return mostSimilar;
	}

	public double getCoveredSymptomWeightSum(XPSCase theCase) {
		Double score = (Double) coveredSymptomWeightSumByXPSCase.get(theCase);
		if (score != null) {
			return score.doubleValue();
		} else {
			return 0;
		}
	}

	public double getPositiveRemainderCoveringStrengthSum(XPSCase theCase) {
		Double score = (Double) remainderPositiveCoveringStrengthsByXPSCase
				.get(theCase);
		if (score != null) {
			return score.doubleValue();
		} else {
			return 0;
		}
	}

	public SimpleSCResult getSimpleSymptomSum(XPSCase theCase) {
		return simpleCoverageResultsByXPSCase.get(theCase);
	}

	public double getNegativeRemainderCoveringStrengthSum(XPSCase theCase) {
		Double score = (Double) remainderNegativeCoveringStrengthsByXPSCase
				.get(theCase);
		if (score != null) {
			return score.doubleValue();
		} else {
			return 0;
		}
	}

	public double getCurrentScore(XPSCase theCase) {
		Double score = (Double) currentScoreByXPSCase.get(theCase);
		if (score != null) {
			return score.doubleValue();
		} else {
			return 0;
		}
	}

	/**
	 * Returns a set which contains all findings of the case that are observed
	 * but not explained (and that are not similar to a PredictedFinding).
	 */
	public Set getUnexplainedFindings(XPSCase theCase) {
		Set<Finding> explainedFindings = getTransitiveExplainedFindings(theCase);
		Set<Finding> similarFindings = getObservedFindingsWithSimilarity(
				theCase).keySet();

		Set<Finding> unexplained = SetPool.getInstance().getFilledSet(
				PSMethodSetCovering.getInstance().getObservedFindings(theCase)
						.toArray());

		Iterator<Finding> unexplIter = unexplained.iterator();
		while (unexplIter.hasNext()) {
			Finding unexplF = unexplIter.next();
			if (containsFinding(explainedFindings, unexplF)
					|| (containsFinding(similarFindings, unexplF))) {
				unexplIter.remove();
			}
		}

		return unexplained;
	}

	/**
	 * Returns true, if the given Collection contains a Finding with the same
	 * parameters as the given one. (Class of the finding is not relevant.)
	 */
	private boolean containsFinding(Collection<Finding> c, Finding f) {
		for (Finding finding : c) {
			if (findingsEqual(f, finding)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true, iff both findings describe the same NamedObject and the
	 * same answers.
	 */
	private boolean findingsEqual(Finding f1, Finding f2) {
		if (!f1.parametricallyEquals(f2)) {
			return false;
		}
		for (Object o1 : f1.getAnswers()) {
			boolean containedInF2 = false;
			for (Object o2 : f2.getAnswers()) {
				if (o1.equals(o2)) {
					containedInF2 = true;
					break;
				}
			}
			if (!containedInF2) {
				return false;
			}
		}
		return true;
	}

	// **********************************************************************

	public void setAprioriProbability(double aprioriProbability) {
		this.aprioriProbability = aprioriProbability;
	}

	public double getAprioriProbability() {
		return this.aprioriProbability;
	}

	public SCDiagnosis() {
		super();
		transitiveObservedFindingsByXPSCase = new HashMap();
		transitiveParametricPredictedFindingsByXPSCase = new HashMap();
		transitiveExplainedFindingsByXPSCase = new HashMap();
		transitiveNegativePredictedFindingsByXPSCase = new HashMap();

		coveredSymptomWeightSumByXPSCase = new HashMap();
		remainderPositiveCoveringStrengthsByXPSCase = new HashMap();
		remainderNegativeCoveringStrengthsByXPSCase = new HashMap();
		currentScoreByXPSCase = new HashMap();

		simpleCoverageResultsByXPSCase = new HashMap<XPSCase, SimpleSCResult>();
	}

	public String getId() {
		if (getNamedObject() != null) {
			return getNamedObject().getId();
		}
		return null;
	}

	public boolean isLeaf() {
		return false;
	}

	/**
	 * Initializes the SCDiagnosis. Determines the set of transitive predicted
	 * findings by using the given closure.
	 */
	public void initialize(TransitiveClosure closure,
			StrengthCalculationStrategy calcStrategy,
			StrengthSelectionStrategy selStrategy) {

		strengthByTransitivePredictedFindings = new HashMap();
		Collection nodes = closure.getNodes();
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			SCNode node = (SCNode) iter.next();
			if (node.isLeaf() && closure.existsPath(this, node)) {
				Double strength = selStrategy.selectStrength(calcStrategy
						.calculateTransitiveStrengths(closure, this,
								(PredictedFinding) node));
				strengthByTransitivePredictedFindings.put(node, strength);
			}
		}
	}

	public void removeXPSCase(XPSCase theCase) {
		transitiveExplainedFindingsByXPSCase.remove(theCase);
		transitiveNegativePredictedFindingsByXPSCase.remove(theCase);
		transitiveObservedFindingsByXPSCase.remove(theCase);
		transitiveParametricPredictedFindingsByXPSCase.remove(theCase);
		coveredSymptomWeightSumByXPSCase.remove(theCase);
		remainderPositiveCoveringStrengthsByXPSCase.remove(theCase);
		remainderNegativeCoveringStrengthsByXPSCase.remove(theCase);
		currentScoreByXPSCase.remove(theCase);
	}

	/**
	 * @param theCase
	 *            current case
	 * @return all findings that are predicted by this diagnosis in the current
	 *         case.
	 */
	public Set getObservedFindings(XPSCase theCase) {
		Set s = (Set) transitiveObservedFindingsByXPSCase.get(theCase);
		if (s != null) {
			return s;
		}
		return new HashSet();
	}

	/**
	 * @param theCase
	 *            current case
	 * @return all findings that are predicted by this diagnosis in the current
	 *         case.
	 */
	public Set getTransitiveParametricPredictedFindings(XPSCase theCase) {
		Set s = (Set) transitiveParametricPredictedFindingsByXPSCase
				.get(theCase);
		if (s != null) {
			return s;
		}
		return new HashSet();
	}

	/**
	 * @param theCase
	 *            current case
	 * @return all findings that are explained by this diagnosis in the current
	 *         case.
	 */
	public Set getTransitiveExplainedFindings(XPSCase theCase) {
		Set s = (Set) transitiveExplainedFindingsByXPSCase.get(theCase);
		if (s != null) {
			return s;
		}
		return new HashSet();
	}

	/**
	 * @param theCase
	 *            current case
	 * @return all findings that are negatively observed by this diagnosis in
	 *         the current case. I.e. findings that have been observed but with
	 *         wrong value.
	 */
	public Set getTransitiveNegativePredictedFindings(XPSCase theCase) {
		Set s = (Set) transitiveNegativePredictedFindingsByXPSCase.get(theCase);
		if (s != null) {
			return s;
		}
		return new HashSet();
	}

	/**
	 * 
	 * @param f
	 *            finding to consider
	 * @return the calculated transitive covering strength of this diagnosis for
	 *         the given finding.
	 */
	public double getCoveringStrength(PredictedFinding f) {
		Double strength = (Double) strengthByTransitivePredictedFindings.get(f);
		if (strength == null) {
			return 0;
		} else {
			return strength.doubleValue();
		}
	}

	public void removeObservedFindingsFor(XPSCase theCase, Question question) {
		Set obsFindings = getObservedFindings(theCase);
		if (obsFindings != null) {
			List toRemove = new LinkedList();
			Iterator iter = obsFindings.iterator();
			while (iter.hasNext()) {
				ObservableFinding f = (ObservableFinding) iter.next();
				if (f.getNamedObject().equals(question)) {
					toRemove.add(f);
				}
			}
			Iterator remIter = toRemove.iterator();
			while (remIter.hasNext()) {
				ObservableFinding f = (ObservableFinding) remIter.next();
				removeObservedFinding(theCase, f);
			}
		}
	}

	/**
	 * Adds a by this diagnosis transitively observed finding respecting the
	 * given XPSCase
	 */
	public void addObservedFinding(XPSCase theCase, ObservableFinding f) {
		Set observedFindings = (Set) transitiveObservedFindingsByXPSCase
				.get(theCase);
		if (observedFindings == null) {
			//observedFindings = SetPool.getInstance().getEmptySet();
			observedFindings = new HashSet();
			transitiveObservedFindingsByXPSCase.put(theCase, observedFindings);
		}
		observedFindings.add(f);
		updatePredictedFindings(theCase, f);
	}

	/**
	 * Removes a by this diagnosis transitively observed finding respecting the
	 * given XPSCase
	 */
	public void removeObservedFinding(XPSCase theCase, ObservableFinding f) {
		Set observedFindings = (Set) transitiveObservedFindingsByXPSCase
				.get(theCase);
		if (observedFindings != null) {
			observedFindings.remove(f);
		}
	}

	private void updatePredictedFindings(XPSCase theCase, ObservableFinding f) {
		Set parametricPredictedFindings = (Set) transitiveParametricPredictedFindingsByXPSCase
				.get(theCase);
		Set explainedFindings = (Set) transitiveExplainedFindingsByXPSCase
				.get(theCase);
		Set negativePredictedFindings = (Set) transitiveNegativePredictedFindingsByXPSCase
				.get(theCase);

		if (parametricPredictedFindings == null) {
			//parametricPredictedFindings = SetPool.getInstance().getEmptySet();
			parametricPredictedFindings = new HashSet();
			transitiveParametricPredictedFindingsByXPSCase.put(theCase,
					parametricPredictedFindings);
		}

		if (explainedFindings == null) {
			//explainedFindings = SetPool.getInstance().getEmptySet();
			explainedFindings = new HashSet();
			transitiveExplainedFindingsByXPSCase
					.put(theCase, explainedFindings);
		}

		if (negativePredictedFindings == null) {
			//negativePredictedFindings = SetPool.getInstance().getEmptySet();
			negativePredictedFindings = new HashSet();
			transitiveNegativePredictedFindingsByXPSCase.put(theCase,
					negativePredictedFindings);
		}

		boolean unknown = ((Answer) f.getAnswers()[0]).isUnknown();

		Iterator predictedIter = strengthByTransitivePredictedFindings.keySet()
				.iterator();
		while (predictedIter.hasNext()) {
			PredictedFinding cf = (PredictedFinding) predictedIter.next();
			if (cf.parametricallyEquals(f)) {
				parametricPredictedFindings.add(cf);
				if (unknown) {
					explainedFindings.remove(cf);
					negativePredictedFindings.remove(cf);
				} else if (cf.covers(theCase, f)) {
					explainedFindings.add(cf);
					negativePredictedFindings.remove(cf);
				} else {
					negativePredictedFindings.add(cf);
					explainedFindings.remove(cf);
				}
			}
		}

		// ***_special_MC_handling_***
		if (!unknown) {
			Iterator iter = PSMethodSetCovering.getInstance()
					.getObservedFindings(theCase).iterator();
			while (iter.hasNext()) {
				ObservableFinding obsF = (ObservableFinding) iter.next();
				if (obsF.getNamedObject() instanceof QuestionMC) {
					Iterator neIter = negativePredictedFindings.iterator();
					while (neIter.hasNext()) {
						PredictedFinding predF = (PredictedFinding) neIter
								.next();
						if (predF.covers(theCase, obsF)) {
							explainedFindings.add(predF);
							neIter.remove();
						}
					}
				}
			}
		}
		// ***************************
	}

	/**
	 * @return a set of all findings that are transitively predicted by this
	 *         diagnosis
	 */
	public Set getTransitivePredictedFindings() {
		return strengthByTransitivePredictedFindings.keySet();
	}

	/**
	 * 
	 * @param f
	 *            covered PredictedFinding
	 * @return the calculated transitive covering strength for the given finding
	 */
	public double getTransitiveCoveringStrength(PredictedFinding f) {
		double ret = 0;
		Double strength = (Double) strengthByTransitivePredictedFindings.get(f);
		if (strength != null) {
			ret = strength.doubleValue();
		}
		return ret;
	}

	public String toString() {
		return getNamedObject().getId() + " apriori= "
				+ getAprioriProbability();
	}

	public String verbalize() {
		return getNamedObject().getId() + "[" + getAprioriProbability() + "]";
	}

	public boolean equals(Object o) {
		try {
			SCDiagnosis other = (SCDiagnosis) o;
			return other.getNamedObject().equals(getNamedObject())
					&& (other.getNamedObject().getKnowledgeBase() == getNamedObject()
							.getKnowledgeBase());
		} catch (Exception e) {
			return false;
		}
	}

	public int hashCode() {
		return getNamedObject().hashCode();
	}

	// ************* IEventSource implementation *********************

	Collection listeners;

	public void addListener(KBOEventListener listener) {
		if (listeners == null)
			listeners = new LinkedList();
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(KBOEventListener listener) {
		if (listeners != null) {

			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}

	public void notifyListeners(XPSCase xpsCase, IEventSource source) {
		if (listeners != null && xpsCase != null && source != null) {
			Iterator lIter = new ArrayList(listeners).iterator();
			while (lIter.hasNext()) {
				KBOEventListener cl = (KBOEventListener) lIter.next();
				cl.notify(source, xpsCase);
			}
		}
	}

	public Collection getListeners() {
		return listeners;
	}

	// *****************************************************************

}
