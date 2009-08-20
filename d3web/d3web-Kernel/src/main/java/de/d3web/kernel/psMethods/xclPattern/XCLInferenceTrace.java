package de.d3web.kernel.psMethods.xclPattern;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.kernel.domainModel.DiagnosisState;

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
