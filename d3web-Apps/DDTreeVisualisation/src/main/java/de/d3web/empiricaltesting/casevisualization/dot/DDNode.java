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
import java.util.List;

import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.casevisualization.dot.DDBuilder.Lifecycle;

public final class DDNode {

	enum Content {
		SOLUTIONS, QUESTIONS, BOTH
	}

	private static int idCounter = 0;
	private final String id = "node" + (idCounter++);

	private final RatedTestCase testCase;
	private final List<DDEdge> outgoing = new ArrayList<DDEdge>();
	private final List<DDEdge> incoming = new ArrayList<DDEdge>();

	private final Lifecycle sessionType;
	private final Content content;

	public DDNode(RatedTestCase testCase, Content content, Lifecycle sessionType) {
		this.testCase = testCase;
		this.content = content;
		this.sessionType = sessionType;
	}

	public boolean hasPredecessors() {
		return (!incoming.isEmpty());
	}

	public List<Finding> getFindings() {
		return testCase.getFindings();
	}

	public void addChild(DDNode targetNode) {
		DDEdge edge = new DDEdge(this, targetNode, sessionType);
		edge.getEnd().incoming.add(edge);
		this.outgoing.add(edge);
	}

	public RatedTestCase getTestCase() {
		return testCase;
	}

	public List<DDEdge> getOutgoing() {
		return Collections.unmodifiableList(outgoing);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((outgoing == null) ? 0 : outgoing.hashCode());
		result = prime * result
				+ ((testCase == null) ? 0 : testCase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DDNode)) return false;
		DDNode other = (DDNode) obj;
		if (outgoing == null) {
			if (other.outgoing != null) return false;
		}
		else if (!outgoing.equals(other.outgoing)) return false;
		if (testCase == null) {
			if (other.testCase != null) return false;
		}
		else if (!testCase.equals(other.testCase)) return false;
		return true;
	}

	public Lifecycle getTheCaseType() {
		return sessionType;
	}

	public List<DDEdge> getIncoming() {
		return Collections.unmodifiableList(incoming);
	}

	public String getID() {
		return id;
	}

	public boolean isQuestionNode() {
		return !content.equals(Content.SOLUTIONS);
	}

	public boolean isSolutionNode() {
		return !content.equals(Content.QUESTIONS);
	}

}
