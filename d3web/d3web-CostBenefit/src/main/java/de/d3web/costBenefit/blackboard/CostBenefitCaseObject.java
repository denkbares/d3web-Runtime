/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.costBenefit.blackboard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costBenefit.model.SearchModel;

/**
 * CaseObject for CostBenefit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class CostBenefitCaseObject extends SessionObject {

	private QContainer[] currentSequence;
	private SearchModel cbm;
	private List<Fact> indicatedFacts;
	private int currentPathIndex = -1;
	private boolean hasBegun = false;
	private Set<Solution> diags = new HashSet<Solution>();
	private Session session;

	public CostBenefitCaseObject(CaseObjectSource theSourceObject, Session session) {
		super(theSourceObject);
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public QContainer[] getCurrentSequence() {
		return currentSequence;
	}

	public void setCurrentSequence(QContainer[] currentSequence) {
		this.currentSequence = currentSequence;
	}

	public SearchModel getCbm() {
		return cbm;
	}

	public void setCbm(SearchModel cbm) {
		this.cbm = cbm;
	}

	public int getCurrentPathIndex() {
		return currentPathIndex;
	}

	public void setCurrentPathIndex(int currentPathIndex) {
		this.currentPathIndex = currentPathIndex;
	}

	public void incCurrentPathIndex() {
		currentPathIndex++;
	}

	public boolean isHasBegun() {
		return hasBegun;
	}

	/**
	 * Resets the path
	 */
	public void resetPath() {
		currentSequence = null;
		this.currentPathIndex = -1;
		hasBegun = false;
	}

	public void setHasBegun(boolean hasBegun) {
		this.hasBegun = hasBegun;
	}

	public void setIndicatedFacts(List<Fact> indicatedFacts) {
		this.indicatedFacts = indicatedFacts;
	}

	public List<Fact> getIndicatedFacts() {
		return indicatedFacts;
	}

	public Set<Solution> getDiags() {
		return diags;
	}

	public void setDiags(Set<Solution> diags) {
		this.diags = diags;
	}

}
