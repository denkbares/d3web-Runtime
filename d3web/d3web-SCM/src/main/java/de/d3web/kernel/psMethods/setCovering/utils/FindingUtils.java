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

package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.setCovering.Finding;
import de.d3web.kernel.psMethods.setCovering.Hypothesis;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;

/**
 * Utilities for anything that has to do with findings
 * 
 * @author bruemmer
 */
public class FindingUtils {

	public static double retrieveObservedWeightSum(XPSCase theCase) {
		Set observed = PSMethodSetCovering.getInstance().getObservedFindings(theCase);
		if (observed != null) {
			return calculateWeightSum(observed, theCase);
		}
		return 0;
	}

	public static double retrieveObservedButNotModeledWeightSum(XPSCase theCase) {
		Set observed = PSMethodSetCovering.getInstance().getObservedFindings(theCase);
		if (observed != null) {
			Set observedNotModeled = new java.util.HashSet();
			//Set observedNotModeled = SetPool.getInstance().getEmptySet();
			Iterator iter = observed.iterator();
			while (iter.hasNext()) {
				Finding finding = (Finding) iter.next();
				Collection knowledge = finding.getNamedObject().getKnowledge(
						PSMethodSetCovering.class, MethodKind.BACKWARD);
				if ((knowledge == null) || knowledge.isEmpty()) {
					observedNotModeled.add(finding);
				}
			}
			double ret = calculateWeightSum(observedNotModeled, theCase);
			SetPool.getInstance().free(observedNotModeled);
			return ret;
		}
		return 0;
	}

	public static double retrieveObservedButNotExplainedWeightSum(XPSCase theCase,
			Hypothesis hypothesis) {
		Set obsNotExp = retrieveObservedButNotExplainedFindings(theCase, hypothesis);
		if (obsNotExp != null) {
			return calculateWeightSum(obsNotExp, theCase);
		}
		return 0;
	}

	public static Set retrieveObservedButNotExplainedFindings(XPSCase theCase, Hypothesis hypothesis) {
		Set explainedFindings = hypothesis.getExplainedFindings(theCase);

		Set ret = SetPool.getInstance().getFilledSet(
				PSMethodSetCovering.getInstance().getObservedFindings(theCase).toArray());
		Hashtable observedByQuestionId = new Hashtable();

		Iterator iter = ret.iterator();
		while (iter.hasNext()) {
			ObservableFinding obs = (ObservableFinding) iter.next();
			observedByQuestionId.put(obs.getNamedObject().getId(), obs);
		}

		iter = explainedFindings.iterator();
		while (iter.hasNext()) {
			PredictedFinding explained = (PredictedFinding) iter.next();
			ObservableFinding observedWithSameQuestionId = (ObservableFinding) observedByQuestionId
					.get(explained.getNamedObject().getId());
			ret.remove(observedWithSameQuestionId);
		}

		Set negativePredictedFindings = hypothesis.getNegativePredictedFindings(theCase);
		if (negativePredictedFindings != null) {
			iter = negativePredictedFindings.iterator();
			while (iter.hasNext()) {
				PredictedFinding negPred = (PredictedFinding) iter.next();
				ObservableFinding observedWithSameQuestionId = (ObservableFinding) observedByQuestionId
						.get(negPred.getNamedObject().getId());
				ret.remove(observedWithSameQuestionId);
			}
		}

		return ret;
	}

	public static double calculateWeightSum(Set findings, XPSCase theCase) {
		double sum = 0;
		Iterator iter = findings.iterator();
		while (iter.hasNext()) {
			Finding f = (Finding) iter.next();
			sum += f.getWeight(theCase);
		}
		return sum;
	}
}
