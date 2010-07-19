package de.d3web.kernel.psmethods2.scmcbr;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.core.knowledge.terminology.Rating;

public class SCMCBRInferenceTrace {
	
	private Collection<SCMCBRRelation> posRelations = new HashSet<SCMCBRRelation>();
	private Collection<SCMCBRRelation> negRelations = new HashSet<SCMCBRRelation>();
	private Collection<SCMCBRRelation> contrRelations = new HashSet<SCMCBRRelation>();
	private Collection<SCMCBRRelation> reqPosRelations = new HashSet<SCMCBRRelation>();
	private Collection<SCMCBRRelation> reqNegRelations = new HashSet<SCMCBRRelation>();
	private Collection<SCMCBRRelation> suffRelations = new HashSet<SCMCBRRelation>();
	
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
