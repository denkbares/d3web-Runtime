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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
import de.d3web.kernel.domainModel.ruleCondition.CondQuestion;
import de.d3web.kernel.domainModel.ruleCondition.NonTerminalCondition;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.DefaultSymbolVerbalizer;
import de.d3web.kernel.psMethods.setCovering.utils.FindingVerbalizer;
import de.d3web.kernel.psMethods.setCovering.utils.SymbolVerbalizer;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * This Class describes a predicted finding. That is a Wrapper for a question
 * and a condition the observed values may fulfill. Then the observed Finding is
 * called "explained".
 * 
 * @see de.d3web.kernel.psMethods.setCovering.ObservableFinding
 * @author bates
 */
public class PredictedFinding extends Finding {

	private Collection allCoveringDiagnoses = null;
	private AbstractCondition condition = null;

	private SymbolVerbalizer symbolVerbalizer = DefaultSymbolVerbalizer.getInstance();

	public PredictedFinding() {
		super();
	}

	public void setCondition(AbstractCondition condition) {
		this.condition = condition;
		super.setAnswers(FindingVerbalizer.getInstance().retrieveNamedObjectAndAnswers(this,
				condition).toArray());
	}

	public void setSymbolVerbalizer(SymbolVerbalizer symbolVerbalizer) {
		this.symbolVerbalizer = symbolVerbalizer;
	}

	public SymbolVerbalizer getSymbolVerbalizer() {
		return symbolVerbalizer;
	}

	public Set getParametricallyCoveringParents() {
		//Set ret = SetPool.getInstance().getEmptySet();
		Set ret = new HashSet();

		List relations = getNamedObject().getKnowledge(PSMethodSetCovering.class,
				MethodKind.BACKWARD);
		Iterator iter = relations.iterator();
		while (iter.hasNext()) {
			SCRelation relation = (SCRelation) iter.next();
			ret.add(relation.getSourceNode());
		}
		return ret;
	}

	public AbstractCondition getCondition() {
		return condition;
	}

	public boolean isLeaf() {
		return true;
	}

	/**
	 * @return true iff internal condition is true
	 */
	public boolean covers(XPSCase theCase) {
		try {
			return condition.eval(theCase);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @param observed
	 *            observed finding to consider
	 * @return true iff case value = observed.getAnswers() and covers(theCase)
	 */
	public boolean covers(XPSCase theCase, ObservableFinding observed) {
		if ((observed != null) && (observed.getNamedObject().equals(getNamedObject()))) {
			Set observedAnswers = SetPool.getInstance().getFilledSet(observed.getAnswers());
			List caseVal = ((Question) observed.getNamedObject()).getValue(theCase);
			//Set caseValue = SetPool.getInstance().getEmptySet();
			Set caseValue = new java.util.HashSet();
			if (caseVal != null) {
				Iterator iter = caseVal.iterator();
				while (iter.hasNext()) {
					Answer ans = (Answer) iter.next();
					if (ans.getQuestion() == null) { // workaround
						ans.setQuestion((Question) observed.getNamedObject());
					}
					caseValue.add(ans);
				}
			}
			if (caseVal.containsAll(observedAnswers)) {
				return covers(theCase);
			}
		}
		return false;
	}

	public Collection getAllCoveringDiagnoses() {
		if (allCoveringDiagnoses == null) {
			//allCoveringDiagnoses = SetPool.getInstance().getEmptySet();
			allCoveringDiagnoses = new HashSet();
			TransitiveClosure closure = PSMethodSetCovering.getInstance().getTransitiveClosure(
					getNamedObject().getKnowledgeBase());
			Map coveringNodesMap = closure.getRelationsBySCNodesLeadingTo(this);
			Iterator iter = coveringNodesMap.keySet().iterator();
			while (iter.hasNext()) {
				SCNode scNode = (SCNode) iter.next();
				if (!scNode.isLeaf()) {
					allCoveringDiagnoses.add(scNode);
				}
			}
		}
		return allCoveringDiagnoses;
	}

	/**
	 * Calculates the similarity of this finding and the other (given) Finding
	 * ATTENTION: only CondOr and CondXXXEquals are considered, yet! These
	 * should be at most medium complex! If the condition is OR, the maximum
	 * will be returned
	 * 
	 * @param otherFinding
	 *            PredictedFinding to compare to
	 * @return the similarity value of the comparison
	 */
	public double calculateSimilarity(Finding otherFinding) {
		// [TODO]: bates: consider all condition types!

		// if the NamedObjects are different, their
		// similarity is 0.
		if (!getNamedObject().getId().equals(otherFinding.getNamedObject().getId())) {
			return 0;
		}

		if (otherFinding instanceof PredictedFinding) {
			PredictedFinding otherFindingPred = (PredictedFinding) otherFinding;
			boolean otherHasCondAnd = (otherFindingPred.getCondition() instanceof NonTerminalCondition)
					&& !(otherFindingPred.getCondition() instanceof CondAnd);
			if (getCondition() instanceof CondAnd) {
				if (otherHasCondAnd) {
					List thisAnswers = Arrays.asList(getAnswers());
					List othersAnswers = Arrays.asList(otherFinding.getAnswers());
					return super.calculateSimilarity(thisAnswers, othersAnswers);
				} else {
					return 0;
				}
			}
		}

		// compute maximum of similarities if CondOr
		double sim = 0;
		List listOfAnswerLists = retrieveAnswerLists(otherFinding);
		Iterator iter = listOfAnswerLists.iterator();
		while (iter.hasNext()) {
			List otherAnswers = (List) iter.next();
			double tempSim = recursiveSimilarityComputation(getCondition(), otherAnswers);

			// die Fälle werden getrennt behandelt, weil evtl. noch was geändert
			// wird...
			if ((getCondition() instanceof CondOr) && tempSim > sim) {
				sim = tempSim;
			} else if ((getCondition() instanceof CondQuestion)) {
				sim = tempSim;
			}
		}

		return sim;

	}

	private double recursiveSimilarityComputation(AbstractCondition condition, List otherAnswers) {
		double ret = 0;
		if (condition instanceof CondQuestion) {
			List answers = FindingVerbalizer.getAnswersFromCondQuestion(this,
					(CondQuestion) condition);
			return super.calculateSimilarity(answers, otherAnswers);
		} else if (condition instanceof CondOr) {
			Iterator iter = ((CondOr) condition).getTerms().iterator();
			while (iter.hasNext()) {
				AbstractCondition term = (AbstractCondition) iter.next();
				double tempSim = recursiveSimilarityComputation(term, otherAnswers);
				if (tempSim > ret) {
					ret = tempSim;
				}
			}
		}
		return ret;
	}

	List retrieveAnswerLists(Finding finding) {
		List ret = null;
		if (finding instanceof ObservableFinding) {
			ret = new LinkedList();
			ret.add(Arrays.asList(finding.getAnswers()));
		} else {
			ret = recursiveAnswerRetrieval((PredictedFinding) finding, ((PredictedFinding) finding)
					.getCondition());
		}
		return ret;
	}

	private List recursiveAnswerRetrieval(PredictedFinding predF, AbstractCondition condition) {
		List ret = new LinkedList();
		if (condition instanceof CondQuestion) {
			ret.add(FindingVerbalizer.getAnswersFromCondQuestion(predF, (CondQuestion) condition));
		} else if (condition instanceof NonTerminalCondition) {
			Iterator termIter = ((NonTerminalCondition) condition).getTerms().iterator();
			while (termIter.hasNext()) {
				AbstractCondition term = (AbstractCondition) termIter.next();
				ret.addAll(recursiveAnswerRetrieval(predF, term));
			}
		}
		return ret;
	}

	public String toString() {
		if (getNamedObject() == null) {
			return "null";
		}
		return getNamedObject().getId() + "=" + verbalize();
	}

	public String verbalize() {
		return FindingVerbalizer.getInstance().verbalizeForDialog(this);
	}

	public boolean equals(Object o) {
		try {
			PredictedFinding pred = (PredictedFinding) o;
			return pred.getCondition().equals(getCondition());
		} catch (Exception e) {
			return false;
		}
	}

}
