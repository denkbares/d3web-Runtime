package de.d3web.xcl;

import java.util.Collection;
import java.util.HashSet;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;

public class DefaultInferenceTrace implements InferenceTrace {

	private final Collection<XCLRelation> posRelations = new HashSet<XCLRelation>();
	private final Collection<XCLRelation> negRelations = new HashSet<XCLRelation>();
	private final Collection<XCLRelation> contrRelations = new HashSet<XCLRelation>();
	private final Collection<XCLRelation> reqPosRelations = new HashSet<XCLRelation>();
	private final Collection<XCLRelation> reqNegRelations = new HashSet<XCLRelation>();
	private final Collection<XCLRelation> suffRelations = new HashSet<XCLRelation>();

	private Rating state = new Rating(Rating.State.UNCLEAR);

	private double score = 0;
	private double support = 0;

	public Rating getState() {
		return state;
	}

	public void setState(Rating state) {
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
	 * Recalculates the inference trace relations for this model and the given
	 * case.
	 * 
	 * @param theCase the current case
	 */
	public void refreshRelations(XCLModel xclModel, Session session) {
		evalRelations(session, xclModel.getRelations(), posRelations, negRelations);
		evalRelations(session, xclModel.getNecessaryRelations(), reqPosRelations,
				reqNegRelations);
		evalRelations(session, xclModel.getContradictingRelations(), contrRelations, null);
		evalRelations(session, xclModel.getSufficientRelations(), suffRelations, null);
	}

	private void evalRelations(Session session, Collection<XCLRelation> source, Collection<XCLRelation> trueSet, Collection<XCLRelation> falseSet) {
		// clear result sets
		trueSet.clear();
		if (falseSet != null) falseSet.clear();
		// eval all relations
		for (XCLRelation rel : source) {
			try {
				boolean b = rel.eval(session);
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
