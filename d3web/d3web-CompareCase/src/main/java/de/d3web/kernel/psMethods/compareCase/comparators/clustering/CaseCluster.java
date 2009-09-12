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

package de.d3web.kernel.psMethods.compareCase.comparators.clustering;

import java.util.*;

import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.psMethods.compareCase.comparators.*;
import de.d3web.utilities.caseLoaders.CaseRepository;

/**
 * Cluster for a set of cases that fulfill the specified minimum similarity to
 * the case defined as center.
 * 
 * @author bruemmer
 */
public class CaseCluster {

	public static int ___COMPARE_SUM = 0;

	private CompareMode compareMode = null;

	private double minSim = 0;

	private String kbid = null;

	private String centerId = null;
	private Set caseIds = null;

	public CaseCluster(String kbid, String centerId, double minSim, CompareMode compareMode) {
		this.kbid = kbid;
		this.centerId = centerId;
		this.minSim = minSim;
		this.compareMode = compareMode;
		this.caseIds = new HashSet();
	}

	public String getId() {
		return centerId;
	}

	/**
	 * 
	 * @return Stored cases
	 */
	public Set getCaseIds() {
		return caseIds;
	}

	/**
	 * 
	 * @param other
	 *            Cluster to check
	 * @return true iff all cases of given cluster fulfill minSim
	 */
	public boolean containsCluster(CaseCluster other) {
		boolean containCheckPositive = true;
		Iterator iter = other.getCaseIds().iterator();
		while (iter.hasNext() && containCheckPositive) {
			Object o = iter.next();
			containCheckPositive = (caseIds.contains(o) || centerId.equals(o));
		}

		CaseObject otherCenterCase = CaseRepository.getInstance().getCaseById(kbid, other.centerId);

		return fulfilsMinSim(otherCenterCase) && containCheckPositive;
	}

	/**
	 * @return true if similarity between center and case fulfils minSim and is
	 *         not greater than 1
	 */
	public boolean fulfilsMinSim(CaseObject queryCase) {
		CaseObject center = CaseRepository.getInstance().getCaseById(kbid, centerId);
		double sim = CaseComparator.calculateSimilarityBetweenCases(compareMode, queryCase, center);
		___COMPARE_SUM++;
		return (sim <= 1) && (sim >= minSim);
	}

	public String getCenterId() {
		return centerId;
	}

	public void addCaseId(String caseId) {
		caseIds.add(caseId);
	}

	public void removeCaseId(String caseId) {
		caseIds.remove(caseId);
	}

	/**
	 * Adds canter and all other cases from the other cluster
	 */
	public void union(CaseCluster other) {
		caseIds.add(other.getCenterId());
		Iterator iter = other.getCaseIds().iterator();
		while (iter.hasNext()) {
			String caseid = (String) iter.next();
			if (!centerId.equals(caseid)) {
				caseIds.add(caseid);
			}
		}
	}

	public boolean isEmpty() {
		return caseIds.isEmpty();
	}

	public int hashCode() {
		return centerId.hashCode();
	}

	public boolean equals(Object o) {
		return hashCode() == o.hashCode();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + centerId + ">:");
		sb.append(caseIds);
		return sb.toString();
	}
}
