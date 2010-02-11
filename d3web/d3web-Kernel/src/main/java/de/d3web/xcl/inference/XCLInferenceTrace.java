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

package de.d3web.xcl.inference;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.core.terminology.DiagnosisState;
import de.d3web.xcl.XCLRelation;

public class XCLInferenceTrace {
	
	private Collection<XCLRelation> posRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> negRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> contrRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> reqPosRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> reqNegRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> suffRelations = new HashSet<XCLRelation>();
	
	private DiagnosisState state = null;
	
	private Double score = -1.0;
	private Double support = -1.0;
	
	public DiagnosisState getState() {
		return state;
	}
	public void setState(DiagnosisState state) {
		this.state = state;
	}
	public Collection<XCLRelation> getPosRelations() {
		return posRelations;
	}
	public Collection<XCLRelation> getNegRelations() {
		return negRelations;
	}
	public Collection<XCLRelation> getContrRelations() {
		return contrRelations;
	}
	public Collection<XCLRelation> getReqPosRelations() {
		return reqPosRelations;
	}
	public Collection<XCLRelation> getReqNegRelations() {
		return reqNegRelations;
	}
	public Collection<XCLRelation> getSuffRelations() {
		return suffRelations;
	}
	
	
	public void addPosRelation(XCLRelation r) {
		posRelations.add(r);
	}
	
	public void addNegRelation(XCLRelation r) {
		negRelations.add(r);
	}
	
	public void addContrRelation(XCLRelation r) {
		contrRelations.add(r);
	}
	
	public void addReqPosRelation(XCLRelation r) {
		reqPosRelations.add(r);
	}
	
	public void addReqPNegRelation(XCLRelation r) {
		reqNegRelations.add(r);
	}
	
	public void addSuffRelation(XCLRelation r) {
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
