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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;

/**
 * This container holds findings by question ids for each knowledge base
 * 
 * @author bruemmer
 */
public class FindingsByQuestionIdContainer {

	private Map findingsByQuestionIdsByKnowledgeBases = null;

	private static FindingsByQuestionIdContainer instance = null;

	private FindingsByQuestionIdContainer() {
		findingsByQuestionIdsByKnowledgeBases = new HashMap();
	}

	public static FindingsByQuestionIdContainer getInstance() {
		if (instance == null) {
			instance = new FindingsByQuestionIdContainer();
		}
		return instance;
	}

	public void initialize(KnowledgeBase knowledgeBase) {
		findingsByQuestionIdsByKnowledgeBases.remove(knowledgeBase);
	}

	public Map getFindingsByQuestionIdsFor(KnowledgeBase knowledgeBase) {
		Map ret = (Map) findingsByQuestionIdsByKnowledgeBases.get(knowledgeBase);
		if (ret == null) {
			Set findings = PSMethodSetCovering.getInstance().getTransitiveClosure(knowledgeBase)
					.getNodes(PredictedFinding.class);
			ret = createFindingsByQuestionIds(knowledgeBase, findings);
			findingsByQuestionIdsByKnowledgeBases.put(knowledgeBase, ret);
		}
		return ret;
	}

	private Map createFindingsByQuestionIds(KnowledgeBase knowledgeBase, Set findings) {
		Map findingsByQuestionIds = new HashMap();
		Iterator iter = findings.iterator();
		while (iter.hasNext()) {
			PredictedFinding finding = (PredictedFinding) iter.next();
			Set fset = (Set) findingsByQuestionIds.get(finding.getNamedObject().getId());
			if (fset == null) {
				fset = new HashSet();
				findingsByQuestionIds.put(finding.getNamedObject().getId(), fset);
			}
			fset.add(finding);
		}
		return findingsByQuestionIds;
	}

}
