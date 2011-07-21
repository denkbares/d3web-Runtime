/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.empiricaltesting.casevisualization.dot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;

public final class DDNode {

	// variables to create the node / edge net
	private static int idCounter = 0;
	private final String id = "node" + (idCounter++);
	private final List<DDEdge> outgoing = new ArrayList<DDEdge>();
	private final List<DDEdge> incoming = new ArrayList<DDEdge>();

	// variables to describe the content of this node
	private final String caseName;
	private final String caseNote;
	private final boolean testedBefore;
	private final List<Finding> findings;
	private final List<RatedSolution> expectedSolutions;
	private final List<RatedSolution> derivedSolutions;

	private DDNode(String caseName, String caseNote, List<Finding> findings, List<RatedSolution> expectedSolutions, List<RatedSolution> derivedSolutions, boolean testedBefore) {
		this.caseName = caseName;
		this.caseNote = caseNote;
		this.findings = Collections.unmodifiableList(findings);
		this.expectedSolutions = Collections.unmodifiableList(expectedSolutions);
		this.derivedSolutions = Collections.unmodifiableList(derivedSolutions);
		this.testedBefore = testedBefore;
	}

	public static DDNode createNode(String caseName, List<Finding> findings, List<RatedSolution> expectedSolutions, List<RatedSolution> derivedSolutions, boolean testedBefore) {
		return new DDNode(caseName, null, findings, expectedSolutions, derivedSolutions,
				testedBefore);
	}

	public static DDNode createCompleteNode(RatedTestCase testcase) {
		return new DDNode(
				testcase.getName(),
				testcase.getNote(),
				testcase.getFindings(),
				testcase.getExpectedSolutions(),
				testcase.getDerivedSolutions(),
				testcase.wasTestedBefore());
	}

	public static DDNode createSolutionNode(RatedTestCase testcase) {
		return createSolutionNode(
				testcase.getName(),
				testcase.getExpectedSolutions(),
				testcase.getDerivedSolutions(),
				testcase.wasTestedBefore());
	}

	public static DDNode createSolutionNode(String caseName, List<RatedSolution> expectedSolutions, List<RatedSolution> derivedSolutions, boolean testedBefore) {
		return new DDNode(
				caseName,
				null,
				Collections.<Finding> emptyList(),
				expectedSolutions,
				derivedSolutions,
				testedBefore);
	}

	public static DDNode createFindingNode(RatedTestCase testcase) {
		return new DDNode(
				testcase.getName(),
				testcase.getNote(),
				testcase.getFindings(),
				Collections.<RatedSolution> emptyList(),
				Collections.<RatedSolution> emptyList(),
				testcase.wasTestedBefore());
	}

	public static DDNode createFindingNode(String caseName, List<Finding> findings, boolean testedBefore) {
		return new DDNode(
				caseName,
				null,
				findings,
				Collections.<RatedSolution> emptyList(),
				Collections.<RatedSolution> emptyList(),
				testedBefore);
	}

	public static DDNode createCopyNode(DDNode originalNode) {
		return new DDNode(
				originalNode.caseName,
				originalNode.getCaseNote(),
				originalNode.findings,
				originalNode.expectedSolutions,
				originalNode.derivedSolutions,
				originalNode.testedBefore);
	}

	public boolean hasPredecessors() {
		return (!incoming.isEmpty());
	}

	public List<Finding> getFindings() {
		return this.findings;
	}

	/**
	 * Returns the set of questions of the child nodes, that are decisive. A
	 * decisive question is a question that has multiple occurrences with
	 * different values in this {@link DDNode}'s child nodes.
	 * 
	 * @created 19.07.2011
	 * @return the list of decisive questions
	 */
	public List<Question> getDecisiveQuestions() {
		List<Question> result = new LinkedList<Question>();
		Set<Question> checked = new HashSet<Question>();
		for (DDNode child : getChildNodes()) {
			for (Finding finding : child.getFindings()) {
				Question question = finding.getQuestion();
				if (checked.contains(question)) continue;
				checked.add(question);
				if (DDBuilder.hasMultipleOutgoingValues(question, this)) {
					result.add(question);
				}
			}
		}
		return result;
	}

	/**
	 * Returns the set of findings of the child nodes, that are non-decisive. A
	 * non-decisive finding is a finding that do not has multiple occurrences
	 * with different values in this {@link DDNode}'s child nodes.
	 * <p>
	 * If there are multiple findings with same values for one questions, only
	 * one of these findings will be returned.
	 * 
	 * @created 19.07.2011
	 * @return the list of non-decisive findings
	 */
	public List<Finding> getNonDecisiveFindings() {
		List<Finding> result = new LinkedList<Finding>();
		Set<Question> checked = new HashSet<Question>();
		for (DDNode child : getChildNodes()) {
			for (Finding finding : child.getFindings()) {
				Question question = finding.getQuestion();
				if (checked.contains(question)) continue;
				checked.add(question);
				if (!DDBuilder.hasMultipleOutgoingValues(question, this)) {
					result.add(finding);
				}
			}
		}
		return result;
	}

	public void addChild(DDNode targetNode) {
		DDEdge edge = new DDEdge(this, targetNode);
		edge.getEnd().incoming.add(edge);
		this.outgoing.add(edge);
	}

	public void removeChild(DDNode childNode) {
		Iterator<DDEdge> iterator = this.outgoing.iterator();
		while (iterator.hasNext()) {
			DDEdge edge = iterator.next();
			if (edge.getEnd().equals(childNode)) {
				// remove this outgoing edge
				iterator.remove();
				// also remove this edge from the incoming edges
				// of the child node
				edge.getEnd().incoming.remove(edge);
			}
		}
	}

	public Set<DDNode> getParentNodes() {
		Set<DDNode> result = new HashSet<DDNode>();
		for (DDEdge edge : this.incoming) {
			result.add(edge.getBegin());
		}
		return result;
	}

	public Set<DDNode> getChildNodes() {
		Set<DDNode> result = new HashSet<DDNode>();
		for (DDEdge edge : this.outgoing) {
			result.add(edge.getEnd());
		}
		return result;
	}

	public List<DDEdge> getOutgoing() {
		return Collections.unmodifiableList(outgoing);
	}

	public List<DDEdge> getIncoming() {
		return Collections.unmodifiableList(incoming);
	}

	public String getID() {
		return id;
	}

	public boolean isQuestionNode() {
		return !this.findings.isEmpty();
	}

	public boolean isSolutionNode() {
		return !this.expectedSolutions.isEmpty() || !this.derivedSolutions.isEmpty();
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DDNode other = (DDNode) obj;
		return id.equals(other.id);
	}

	public String getCaseName() {
		return caseName;
	}

	public String getCaseNote() {
		return caseNote;
	}

	public List<RatedSolution> getExpectedSolutions() {
		return expectedSolutions;
	}

	public List<RatedSolution> getDerivedSolutions() {
		return derivedSolutions;
	}

	public boolean isTestedBefore() {
		return testedBefore;
	}

}
