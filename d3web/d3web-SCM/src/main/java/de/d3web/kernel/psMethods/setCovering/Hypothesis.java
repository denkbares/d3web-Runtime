package de.d3web.kernel.psMethods.setCovering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.pools.HypothesisPool;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.FindingUtils;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * A Hypothesis is a set of Diagnoses. It provides a quality and false
 * prediction account for given set of observed findings
 * 
 * @author bates
 */
public class Hypothesis implements Cloneable {

	private Set scDiagnoses;
	private double aprioriProbability = 1.0;

	private Map explainedFindingsByXPSCase = null;
	private Map observedFindingsByXPSCase = null;
	private Map negativePredictedFindingsByXPSCase = null;

	public Hypothesis() {
		initialize();
	}

	public void setSCDiagnoses(Set scDiagnoses) {
		this.scDiagnoses = scDiagnoses;
		completelyComputeMaps();
		// Iterator iter = scDiagnoses.iterator();
		// while(iter.hasNext()) {
		// updateMaps((SCDiagnosis) iter.next());
		// }
	}

	public void addSCDiagnosis(SCDiagnosis diag) {
		scDiagnoses.add(diag);
		updateMaps(diag);
	}

	public Set getSCDiagnoses() {
		return this.scDiagnoses;
	}

	public void initialize() {
		if (scDiagnoses != null) {
			SetPool.getInstance().free(scDiagnoses);
		}
		//scDiagnoses = SetPool.getInstance().getEmptySet();
		scDiagnoses = new HashSet();
		aprioriProbability = 0.5;

		explainedFindingsByXPSCase = new HashMap();
		observedFindingsByXPSCase = new HashMap();
		negativePredictedFindingsByXPSCase = new HashMap();
	}

	/**
	 * @return the calculated apriori-probability
	 */
	public double getAprioriProbability() {
		return aprioriProbability;
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @return all findings that are explained by this hypothesis by the given
	 *         XPSCase
	 */
	public Set getExplainedFindings(XPSCase theCase) {
		return (Set) explainedFindingsByXPSCase.get(theCase);
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @return all findings that are negatively observed by this hypothesis by
	 *         the given XPSCase
	 */
	public Set getNegativePredictedFindings(XPSCase theCase) {
		return (Set) negativePredictedFindingsByXPSCase.get(theCase);
	}

	private void completelyComputeMaps() {
		if ((scDiagnoses != null) && !scDiagnoses.isEmpty()) {
			Set xpsCases = PSMethodSetCovering.getInstance().getXPSCases();
			Iterator casesIter = xpsCases.iterator();
			while (casesIter.hasNext()) {
				XPSCase aCase = (XPSCase) casesIter.next();

				// dispose old findings sets
				Set expOld = (Set) explainedFindingsByXPSCase.get(aCase);
				if (expOld != null) {
					SetPool.getInstance().free(expOld);
				}
				Set obsOld = (Set) observedFindingsByXPSCase.get(aCase);
				if (obsOld != null) {
					SetPool.getInstance().free(obsOld);
				}
				Set negObsOld = (Set) negativePredictedFindingsByXPSCase.get(aCase);
				if (negObsOld != null) {
					SetPool.getInstance().free(negObsOld);
				}

				// retrieve observed and explained findings
				//Set explainedFindings = SetPool.getInstance().getEmptySet();
				//Set observedFindings = SetPool.getInstance().getEmptySet();
				Set explainedFindings = new java.util.HashSet();
				Set observedFindings = new java.util.HashSet();
				Iterator diagnosesIter = scDiagnoses.iterator();
				while (diagnosesIter.hasNext()) {
					SCDiagnosis scd = (SCDiagnosis) diagnosesIter.next();
					Set scdExpSet = scd.getTransitiveExplainedFindings(aCase);
					if (scdExpSet != null) {
						explainedFindings.addAll(scdExpSet);
					}
					Set scdObsSet = scd.getObservedFindings(aCase);
					if (scdObsSet != null) {
						observedFindings.addAll(scdObsSet);
					}

				}
				explainedFindingsByXPSCase.put(aCase, explainedFindings);
				observedFindingsByXPSCase.put(aCase, observedFindings);

				// retrieve negative predicted findings
				//Set negativePredictedFindings = SetPool.getInstance().getEmptySet();
				Set negativePredictedFindings = new HashSet();
				diagnosesIter = scDiagnoses.iterator();
				while (diagnosesIter.hasNext()) {
					SCDiagnosis scd = (SCDiagnosis) diagnosesIter.next();
					Set negPredFindings = scd.getTransitiveNegativePredictedFindings(aCase);
					if (negPredFindings != null) {
						Iterator negIter = negPredFindings.iterator();
						while (negIter.hasNext()) {
							PredictedFinding negF = (PredictedFinding) negIter.next();
							if (!explainedFindings.contains(negF)) {
								negativePredictedFindings.add(negF);
							}
						}
					}
				}
				negativePredictedFindingsByXPSCase.put(aCase, negativePredictedFindings);
			}
		}
	}

	private void updateMaps(SCDiagnosis diag) {
		Iterator casesIter = PSMethodSetCovering.getInstance().getXPSCases().iterator();
		while (casesIter.hasNext()) {
			XPSCase aCase = (XPSCase) casesIter.next();

			Set observed = diag.getObservedFindings(aCase);
			Set explained = diag.getTransitiveExplainedFindings(aCase);
			Set negPred = diag.getTransitiveNegativePredictedFindings(aCase);

			// add observed findings of new diagnosis
			if (observed != null) {
				Set obsFindings = (Set) observedFindingsByXPSCase.get(aCase);
				if (obsFindings == null) {
					//obsFindings = SetPool.getInstance().getEmptySet();
					obsFindings = new HashSet();
					observedFindingsByXPSCase.put(aCase, obsFindings);
				}
				obsFindings.addAll(observed);
			}

			Set expFindings = (Set) explainedFindingsByXPSCase.get(aCase);
			if (expFindings == null) {
				//expFindings = SetPool.getInstance().getEmptySet();
				expFindings = new HashSet();
				explainedFindingsByXPSCase.put(aCase, expFindings);
			}

			// add explained findings of new diagnosis
			// compute coveringStrength for the explained findings
			if (explained != null) {
				expFindings.addAll(explained);
			}

			if (negPred != null) {
				Set negPredFindings = (Set) negativePredictedFindingsByXPSCase.get(aCase);
				if (negPredFindings == null) {
					//negPredFindings = SetPool.getInstance().getEmptySet();
					negPredFindings = new HashSet();
					negativePredictedFindingsByXPSCase.put(aCase, negPredFindings);
				}

				// add negative predicted findings that are not yet explained
				Iterator negPredIter = negPred.iterator();
				while (negPredIter.hasNext()) {
					PredictedFinding f = (PredictedFinding) negPredIter.next();
					if (!expFindings.contains(f)) {
						negPredFindings.add(f);
					}
				}

				// remove negative predicted findings that are explained now
				Iterator explainedFindingsIter = expFindings.iterator();
				while (explainedFindingsIter.hasNext()) {
					Object f = explainedFindingsIter.next();
					if (negPredFindings.contains(f)) {
						negPredFindings.remove(f);
					}
				}
			}
		}
	}

	public double getQuality(XPSCase theCase) {
		return getQuality(theCase, true);
	}

	public double getQuality(XPSCase theCase, boolean allObserved) {
		Set explained = (Set) explainedFindingsByXPSCase.get(theCase);
		double maxWeightedStrengthSum = getMaxWeightedStrengthSum(explained, theCase);
		double weightSum = FindingUtils.retrieveObservedWeightSum(theCase);
		if (!allObserved) {
			weightSum -= FindingUtils.retrieveObservedButNotModeledWeightSum(theCase);
		}
		double ret = maxWeightedStrengthSum / weightSum;
		return ret > 0 ? ret : 0;
	}

	/**
	 * 
	 * @param theCase
	 *            current case
	 * @return the false prediction account for this hypothesis
	 */
	public double getFalsePredictionAccount(XPSCase theCase) {
		Set negativePredicted = (Set) negativePredictedFindingsByXPSCase.get(theCase);
		double maxWeightedStrengthSum = getMaxWeightedStrengthSum(negativePredicted, theCase);
		double weightSum = FindingUtils.retrieveObservedWeightSum(theCase);

		return maxWeightedStrengthSum / weightSum;
	}

	private double getMaxWeightedStrengthSum(Set findingsToConsider, XPSCase theCase) {
		double ret = 0;
		if (findingsToConsider != null) {
			Iterator consIter = findingsToConsider.iterator();
			while (consIter.hasNext()) {
				PredictedFinding consF = (PredictedFinding) consIter.next();
				double maxCoveringStrength = computeMaxCoveringStrength(theCase, consF);
				ret += (maxCoveringStrength * consF.getWeight(theCase));
			}
		}
		return ret;
	}

	public double computeMaxCoveringStrength(XPSCase theCase, PredictedFinding f) {
		double maxCoveringStrength = Double.NEGATIVE_INFINITY;
		TransitiveClosure transitiveClosure = PSMethodSetCovering.getInstance()
				.getTransitiveClosure(theCase.getKnowledgeBase());

		Map coveringNodesMap = transitiveClosure.getRelationsBySCNodesLeadingTo(f);
		Set nodes = coveringNodesMap.keySet();
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			SCNode node = (SCNode) iter.next();
			if (!node.isLeaf() && scDiagnoses.contains(node)) {
				SCDiagnosis diag = (SCDiagnosis) node;
				double coveringStrength = diag.getTransitiveCoveringStrength(f);
				if (coveringStrength > maxCoveringStrength) {
					maxCoveringStrength = coveringStrength;
				}
			}
		}
		if (maxCoveringStrength == Double.NEGATIVE_INFINITY) {
			maxCoveringStrength = 0;
		}
		return maxCoveringStrength;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Iterator iter = scDiagnoses.iterator();
		while (iter.hasNext()) {
			SCDiagnosis diag = (SCDiagnosis) iter.next();
			sb.append(diag.getNamedObject().getId());
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public Object clone() {
		Hypothesis ret = HypothesisPool.getInstance().getEmptyHypothesis();
		ret.scDiagnoses = SetPool.getInstance().getFilledSet(scDiagnoses.toArray());
		// [MISC]: bates: maybe more efficient when not calculating in clone()
		ret.completelyComputeMaps();
		return ret;
	}

	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		Iterator iter = scDiagnoses.iterator();
		while (iter.hasNext()) {
			SCDiagnosis diag = (SCDiagnosis) iter.next();
			sb.append(diag.getId());
		}
		return sb.toString().hashCode();
	}

	public boolean equals(Object o) {
		try {
			Hypothesis other = (Hypothesis) o;
			return other.hashCode() == hashCode();
		} catch (Exception e) {
			return false;
		}
	}

}
