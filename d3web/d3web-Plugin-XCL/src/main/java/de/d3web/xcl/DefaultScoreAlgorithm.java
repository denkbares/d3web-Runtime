/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.xcl;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.xcl.inference.PSMethodXCL;

public class DefaultScoreAlgorithm implements ScoreAlgorithm {

	private double defaultEstablishedThreshold = 0.8;
	private double defaultSuggestedThreshold = 0.3;
	private double defaultMinSupport = 0.01;

	public void setDefaultEstablishedThreshold(double defaultEstablishedThreshold) {
		this.defaultEstablishedThreshold = defaultEstablishedThreshold;
	}

	public void setDefaultSuggestedThreshold(double defaultSuggestedThreshold) {
		this.defaultSuggestedThreshold = defaultSuggestedThreshold;
	}

	public void setDefaultMinSupport(double defaultMinSupport) {
		this.defaultMinSupport = defaultMinSupport;
	}

	public double getDefaultEstablishedThreshold() {
		return defaultEstablishedThreshold;
	}

	public double getDefaultSuggestedThreshold() {
		return defaultSuggestedThreshold;
	}

	public double getDefaultMinSupport() {
		return defaultMinSupport;
	}

	@Override
	public InferenceTrace createInferenceTrace(XCLModel xclModel) {
		return new DefaultInferenceTrace();
	}

	@Override
	public void refreshStates(Collection<XCLModel> updatedModels, Session session) {
		for (XCLModel model : updatedModels) {
			DefaultInferenceTrace trace = (DefaultInferenceTrace) model.getInferenceTrace(session);
			Rating oldState = model.getState(session);
			// calculate scores
			// and the calculate states (based on that scores)
			double currentScore = computeScore(trace);
			double currentSupport = computeSupport(model, trace);
			Rating currentState = computeState(model, trace, currentScore,
					currentSupport);
			trace.setScore(currentScore);
			trace.setSupport(currentSupport);
			trace.setState(currentState);
			if (!oldState.equals(currentState)) {
				session.getBlackboard().addValueFact(
						FactFactory.createFact(model.getSolution(), currentState,
								model, session.getPSMethodInstance(PSMethodXCL.class)));
			}
		}
	}

	private Rating computeState(XCLModel model, InferenceTrace trace, double score, double support) {

		boolean hasContradiction = trace.getContrRelations().size() > 0;
		boolean hasSufficient = trace.getSuffRelations().size() > 0;
		boolean hasAllNecessary = trace.getReqPosRelations().size() == model.getNecessaryRelations().size();

		if (hasContradiction) {
			return new Rating(Rating.State.EXCLUDED);
		}

		if (hasSufficient) {
			return new Rating(Rating.State.ESTABLISHED);
		}

		double minSupport = getMinSupport(model);
		if (minSupport <= support) {
			if (score >= getEstablishedThreshold(model)) {
				return hasAllNecessary
						? new Rating(Rating.State.ESTABLISHED)
						: new Rating(Rating.State.SUGGESTED);
			}
			if (score >= getSuggestedThreshold(model)) {
				return new Rating(Rating.State.SUGGESTED);
			}
		}

		return new Rating(Rating.State.UNCLEAR);
	}

	@Override
	public void update(XCLModel xclModel, Collection<PropagationEntry> entries, Session session) {
		InferenceTrace trace = xclModel.getInferenceTrace(session);
		trace.refreshRelations(xclModel, session);
	}

	private double computeScore(InferenceTrace trace) {

		// score is the sum of matching relations compared to evaluated
		// relations
		double posSum = weightedSumOf(trace.getPosRelations())
				+ weightedSumOf(trace.getReqPosRelations());
		if (posSum <= 0) {
			return 0;
		}
		double negSum = weightedSumOf(trace.getNegRelations())
				+ weightedSumOf(trace.getReqNegRelations());

		return posSum / (negSum + posSum);
	}

	private double computeSupport(XCLModel model, InferenceTrace trace) {
		// support is the sum of evaluated relations compared to all relations
		double posSum = weightedSumOf(trace.getPosRelations())
				+ weightedSumOf(trace.getReqPosRelations());
		double negSum = weightedSumOf(trace.getNegRelations())
				+ weightedSumOf(trace.getReqNegRelations());
		double allSum = weightedSumOf(model.getRelations())
				+ weightedSumOf(model.getNecessaryRelations());

		if (allSum == 0) { // happens if there are only sufficient relations
			return 0; // then the formula below would return NaN
		}
		else {
			return (posSum + negSum) / (allSum);
		}
	}

	private double weightedSumOf(Collection<XCLRelation> relations) {
		double sum = 0;
		for (XCLRelation relation : relations) {
			sum += relation.getWeight();
		}
		return sum;
	}

	@Override
	public double getEstablishedThreshold(XCLModel model) {
		Double establishedThreshold = model.getEstablishedThreshold();
		if (establishedThreshold == null) {
			establishedThreshold = defaultEstablishedThreshold;
		}
		return establishedThreshold;
	}

	@Override
	public double getMinSupport(XCLModel model) {
		Double minSupport = model.getMinSupport();
		if (minSupport == null) {
			minSupport = defaultMinSupport;
		}
		return minSupport;
	}

	@Override
	public double getSuggestedThreshold(XCLModel model) {
		Double suggestedThreshold = model.getSuggestedThreshold();
		if (suggestedThreshold == null) {
			suggestedThreshold = defaultSuggestedThreshold;
		}
		return suggestedThreshold;
	}

}
