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

package de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.FindingsByQuestionIdContainer;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.DiagnosisByStrengthComparator;

/**
 * Selects the k best diagnoses for the given finding. Criterion is their
 * covering strength
 * 
 * @author bruemmer
 */
public class DefaultBestDiagnosesSelectionStrategy implements BestDiagnosesSelectionStrategy {

	private static DefaultBestDiagnosesSelectionStrategy instance = null;
	private DefaultBestDiagnosesSelectionStrategy() {
	}
	public static DefaultBestDiagnosesSelectionStrategy getInstance() {
		if (instance == null) {
			instance = new DefaultBestDiagnosesSelectionStrategy();
		}
		return instance;
	}

	public List selectBestKDiagnosesFor(XPSCase theCase, ObservableFinding obsF, int k) {
		Map findingsByQuestionId = FindingsByQuestionIdContainer.getInstance()
				.getFindingsByQuestionIdsFor(obsF.getNamedObject().getKnowledgeBase());
		Collection parametricEq = (Collection) findingsByQuestionId.get(obsF.getNamedObject()
				.getId());

		Iterator fIter = parametricEq.iterator();
		// alle diagnosen sammeln (ohne doppelte)
		Set tempDiagnoses = new HashSet();
		//Set tempDiagnoses = SetPool.getInstance().getEmptySet();
		while (fIter.hasNext()) {
			PredictedFinding predF = (PredictedFinding) fIter.next();
			tempDiagnoses.addAll(predF.getAllCoveringDiagnoses());
		}
		LinkedList ret = new LinkedList(tempDiagnoses);
		SetPool.getInstance().free(tempDiagnoses);
		// nach Überdeckungsstärke && sim sortieren
		Collections.sort(ret, new DiagnosisByStrengthComparator(theCase, obsF));

		// evtl liste abschneiden
		if (ret.size() >= k) {
			return ret.subList(0, k);
		}

		return ret;
	}

}
