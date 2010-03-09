package de.d3web.xcl;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.XPSCase;

	
public class DefaultInferenceTrace implements InferenceTrace {
	private Collection<XCLRelation> posRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> negRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> contrRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> reqPosRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> reqNegRelations = new HashSet<XCLRelation>();
	private Collection<XCLRelation> suffRelations = new HashSet<XCLRelation>();
	
	private DiagnosisState state = DiagnosisState.UNCLEAR;
	
	private double score = -1.0;
	private double support = -1.0;
	
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
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	
	/**
	 * Recalculates the inference trace relations for this model and the given case.
	 * 
	 * @param theCase the current case
	 */
	public void refreshRelations(XCLModel xclModel, XPSCase xpsCase) {
		evalRelations(xpsCase, xclModel.getRelations(), posRelations, negRelations);
		evalRelations(xpsCase, xclModel.getNecessaryRelations(), reqPosRelations, reqNegRelations);
		evalRelations(xpsCase, xclModel.getContradictingRelations(), contrRelations, null);
		evalRelations(xpsCase, xclModel.getSufficientRelations(), suffRelations, null);
	}
	
	private void evalRelations(XPSCase xpsCase, Collection<XCLRelation> source, Collection<XCLRelation> trueSet, Collection<XCLRelation> falseSet) {
		// clear result sets
		trueSet.clear();
		if (falseSet != null) falseSet.clear();
		// eval all relations
		for (XCLRelation rel : source) {
			try {
				boolean b = rel.eval(xpsCase);
				if (b) {
					trueSet.add(rel);
				} 
				else {
					if (falseSet != null) falseSet.add(rel);
				}
			} 
			catch (NoAnswerException e) {
				// do nothing
			} 
			catch (UnknownAnswerException e) {
				// do nothing
			}
		}
	}
	
}
