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

/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Set;

import de.d3web.core.terminology.Diagnosis;


/**
 * 21.10.2003 15:44:51
 * @author hoernlein
 */
public class Configuration {
	
	private ProbabilityList caseObjectIDProbabilityList;
	private AbstractCNode node;
	
	public boolean matches(Set<Diagnosis> diagnoses) {
		return getNode().matches(diagnoses);
	}

	/**
	 * @return
	 */
	public ProbabilityList getCaseObjectIDProbabilityList() {
		return caseObjectIDProbabilityList;
	}

	/**
	 * @param object
	 */
	public void setCaseObjectIDProbabilityList(ProbabilityList caseObjectIDProbabilityList) {
		this.caseObjectIDProbabilityList = caseObjectIDProbabilityList;
	}

	/**
	 * @return
	 */
	public AbstractCNode getNode() {
		return node;
	}

	/**
	 * @param node
	 */
	public void setNode(AbstractCNode node) {
		this.node = node;
	}

	public Object clone() {
		Configuration temp = new Configuration();
		if (node!=null) {
			temp.setNode((AbstractCNode)node.clone());
		}
		temp.setCaseObjectIDProbabilityList(new ProbabilityList());
		return temp;
	}

}
