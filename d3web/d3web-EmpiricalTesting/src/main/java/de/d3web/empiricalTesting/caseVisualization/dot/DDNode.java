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

package de.d3web.empiricalTesting.caseVisualization.dot;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.caseVisualization.dot.DDBuilder.caseType;


public class DDNode {
	private RatedTestCase testCase;
	private List<DDEdge> outgoing;
	private List<DDEdge> incoming;
	
	private boolean isCuttedNode;
	private caseType theCaseType;
	private QASet cuttedQContainer;
	
	public DDNode(RatedTestCase testCase, caseType theCasetype) {
		this.testCase = testCase;
		this.outgoing= new ArrayList<DDEdge>();
		this.setIncoming(new ArrayList<DDEdge>());
		isCuttedNode = false;
		setTheCaseType(theCasetype);
	}
	
	public DDNode(RatedTestCase testCase) {
		this(testCase, caseType.new_case);
	}
		
		
	public boolean hasPredecessors() {
		return (getIncoming() != null && !getIncoming().isEmpty()); 
	}
	
	public List<Finding> getFindings() {
		return testCase.getFindings();
	}
	
	public boolean addChild(DDEdge edge, boolean oldEdge) {
		edge.end.getIncoming().add(edge);
		return outgoing.add(edge);
	}

	public boolean addChild(DDEdge edge) {
		return addChild(edge, false);
	}

	public boolean addChild(DDNode targetNode, Finding label, caseType theCasetype) {
		return addChild(new DDEdge(this, targetNode, label, theCasetype));
	}	
	
	public boolean addChild(DDNode targetNode, Finding label) {
		return addChild(new DDEdge(this, targetNode, label), false);
	}

	public RatedTestCase getTestCase() {
		return testCase;
	}

	public List<DDEdge> getOutgoing() {
		return outgoing;
	}

	public void setTestCase(RatedTestCase testCase) {
		this.testCase = testCase;
	}

	public void setOutgoing(List<DDEdge> outgoing) {
		this.outgoing = outgoing;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DDNode))
			return false;
		DDNode other = (DDNode) obj;
		if (outgoing == null) {
			if (other.outgoing != null)
				return false;
		} else if (!outgoing.equals(other.outgoing))
			return false;
		if (testCase == null) {
			if (other.testCase != null)
				return false;
		} else if (!testCase.equals(other.testCase))
			return false;
		return true;
	}

	public boolean isCuttedNode() {
		return isCuttedNode;
	}

	public void setCuttedNode(boolean isCuttedNode) {
		this.isCuttedNode = isCuttedNode;
	}

	public QASet getCuttedQContainer() {
		return cuttedQContainer;
	}

	public void setCuttedQContainer(QASet cuttedQContainer) {
		this.cuttedQContainer = cuttedQContainer;
	}

	public caseType getTheCaseType() {
		return theCaseType;
	}

	public void setTheCaseType(caseType theCaseType) {
		this.theCaseType = theCaseType;
	}

	public void setIncoming(List<DDEdge> incoming) {
		this.incoming = incoming;
	}

	public List<DDEdge> getIncoming() {
		return incoming;
	}


	
}
