/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.kernel.psmethods.scmcbr;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.core.knowledge.terminology.Rating;

public class SCMCBRInferenceTrace {

	private final Collection<SCMCBRRelation> posRelations = new HashSet<SCMCBRRelation>();
	private final Collection<SCMCBRRelation> negRelations = new HashSet<SCMCBRRelation>();
	private final Collection<SCMCBRRelation> contrRelations = new HashSet<SCMCBRRelation>();
	private final Collection<SCMCBRRelation> reqPosRelations = new HashSet<SCMCBRRelation>();
	private final Collection<SCMCBRRelation> reqNegRelations = new HashSet<SCMCBRRelation>();
	private final Collection<SCMCBRRelation> suffRelations = new HashSet<SCMCBRRelation>();

	private Rating state = null;

	private Double score = -1.0;
	private Double support = -1.0;

	public Rating getState() {
		return state;
	}

	public void setState(Rating state) {
		this.state = state;
	}

	public Collection<SCMCBRRelation> getPosRelations() {
		return posRelations;
	}

	public Collection<SCMCBRRelation> getNegRelations() {
		return negRelations;
	}

	public Collection<SCMCBRRelation> getContrRelations() {
		return contrRelations;
	}

	public Collection<SCMCBRRelation> getReqPosRelations() {
		return reqPosRelations;
	}

	public Collection<SCMCBRRelation> getReqNegRelations() {
		return reqNegRelations;
	}

	public Collection<SCMCBRRelation> getSuffRelations() {
		return suffRelations;
	}

	public void addPosRelation(SCMCBRRelation r) {
		posRelations.add(r);
	}

	public void addNegRelation(SCMCBRRelation r) {
		negRelations.add(r);
	}

	public void addContrRelation(SCMCBRRelation r) {
		contrRelations.add(r);
	}

	public void addReqPosRelation(SCMCBRRelation r) {
		reqPosRelations.add(r);
	}

	public void addReqPNegRelation(SCMCBRRelation r) {
		reqNegRelations.add(r);
	}

	public void addSuffRelation(SCMCBRRelation r) {
		suffRelations.add(r);
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Double getSupport() {
		return support;
	}

	public void setSupport(Double support) {
		this.support = support;
	}

}
