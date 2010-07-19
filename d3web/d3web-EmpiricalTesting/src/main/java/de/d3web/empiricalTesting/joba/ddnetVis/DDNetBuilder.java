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

package de.d3web.empiricalTesting.joba.ddnetVis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.caseVisualization.dot.DDNode;
import de.d3web.empiricalTesting.caseVisualization.dot.DDBuilder.caseType;

public class DDNetBuilder {
	boolean cutQuationnaireSibling = false;
	List<QContainer> ingnoreQuestionnaires;
	List<QContainer> registeredContainers;

//	public enum caseType {
//		old_case, new_case, incorrect
//	};

	public DDNetBuilder() {
		cutQuationnaireSibling = false;
		ingnoreQuestionnaires = new ArrayList<QContainer>(1);
		registeredContainers = new ArrayList<QContainer>();
	}

	/**
	 * Set set of all cases to be rendered are (cases JOIN oldCases).
	 * "Old cases" are additionally flagged as "old" when generating the edges
	 * of these nodes
	 * 
	 * @param cases
	 *            cases to render normally
	 * @param oldCases
	 *            cases to render in a grayed-out manner
	 * @return a Map of nodes of the DDNet (key=label of node, value=node)
	 */
	public HashMap<String, DDNode> generateDDNet(
			List<SequentialTestCase> cases, List<SequentialTestCase> oldCases) {
		HashMap<String, DDNode> nodes = new HashMap<String, DDNode>();

		// fillNodeMap(nodes, cases, oldCases);
		if (oldCases != null)
			generateDDNet(oldCases, caseType.old_case, nodes);
		if (cases != null)
			generateDDNet(cases, caseType.new_case, nodes);

		return nodes;
	}

	private void generateDDNet(List<SequentialTestCase> cases,
			caseType sessionType, HashMap<String, DDNode> nodes) {

		for (SequentialTestCase sequentialTestCase : cases) {
			List<RatedTestCase> ratedCases = sequentialTestCase.getCases();
			DDNode prec = null;

			for (int i = 0; i < ratedCases.size(); i++) {
				RatedTestCase ratedTestCase = ratedCases.get(i);
				RatedTestCase nextRatedTestCase = getAt(ratedCases, i + 1);

				String name = ratedTestCase.getName();
				if (nodes.get(name) == null)
					nodes.put(name, new DDNode(ratedTestCase));
				DDNode node = nodes.get(name);
				node.setTheCaseType(sessionType);

				if (prec != null) {
					prec.addChild(node, node.getFindings().get(0), sessionType);
				}

				if (cutQuationnaireSibling
						&& nextRatedTestCase != null
						&& parentQuestionnairesDiffer(nextRatedTestCase,
								ratedTestCase)) {
					node.setCuttedNode(true);
					node.setCuttedQContainer(findRootQuestion(nextRatedTestCase
							.getFindings().get(0).getQuestion()));
					break;
				}

				prec = node;
			}
		}

	}

	public HashMap<String, DDNode> generateDDNet(List<SequentialTestCase> cases) {
		return generateDDNet(cases, null);
	}

	private RatedTestCase getAt(List<RatedTestCase> list, int i) {
		if (list.size() > i)
			return list.get(i);
		else
			return null;
	}

	private boolean parentQuestionnairesDiffer(RatedTestCase nextRatedTestCase,
			RatedTestCase ratedTestCase) {
		if (nextRatedTestCase == null || ratedTestCase == null)
			return false;
		QASet root1Question = findRootQuestion(nextRatedTestCase.getFindings()
				.get(0).getQuestion());
		QASet root2Question = findRootQuestion(ratedTestCase.getFindings().get(
				0).getQuestion());
		if (getIngnoreQuestionnaire().contains(root1Question)
				|| getIngnoreQuestionnaire().contains(root2Question))
			return false;
		if (root1Question.equals(root2Question))
			return false;
		else
			return true;
	}

	private QASet findRootQuestion(QASet q) {
		QASet root = q.getKnowledgeBase().getRootQASet();
		QASet parent = (QASet) q.getParents()[0];
		if (parent.equals(root) || getRegisteredContainers().contains(parent))
			return parent;
		else
			return findRootQuestion(parent);
	}

	public boolean isCutQuationnaireSibling() {
		return cutQuationnaireSibling;
	}

	public List<QContainer> getIngnoreQuestionnaire() {
		return ingnoreQuestionnaires;
	}

	public void setCutQuationnaireSibling(boolean cutQuationnaireSibling) {
		this.cutQuationnaireSibling = cutQuationnaireSibling;
	}

	public boolean addIngnoreQuestionnaire(QContainer ingnoreQuestionnaire) {
		return this.ingnoreQuestionnaires.add(ingnoreQuestionnaire);
	}

	public void addAllIngnoreQuestionnaire(List<QContainer> ingnoreQuestionnaire) {
		for (QContainer container : ingnoreQuestionnaire) {
			addIngnoreQuestionnaire(container);
		}
	}

	public List<QContainer> getRegisteredContainers() {
		if (registeredContainers == null)
			registeredContainers = new ArrayList<QContainer>();
		return registeredContainers;
	}

	public void setRegisteredContainers(List<QContainer> registeredContainers) {
		this.registeredContainers = registeredContainers;
	}
}
