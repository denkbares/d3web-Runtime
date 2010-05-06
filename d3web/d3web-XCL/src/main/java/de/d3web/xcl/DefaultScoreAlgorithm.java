package de.d3web.xcl;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.Session;
import de.d3web.xcl.inference.PSMethodXCL;

public class DefaultScoreAlgorithm implements ScoreAlgorithm {

	public InferenceTrace createInferenceTrace(XCLModel xclModel) {
		return new DefaultInferenceTrace();
	}

	public void refreshStates(Collection<XCLModel> updatedModels, Session session) {
		for (XCLModel model : updatedModels) {
			DefaultInferenceTrace trace = (DefaultInferenceTrace) model.getInferenceTrace(session);
			DiagnosisState oldState = model.getState(session);
			// calculate scores
			// and the calculate states (based on that scores)
			double currentScore = computeScore(model, trace, session);
			double currentSupport = computeSupport(model, trace, session);
			DiagnosisState currentState = computeState(model, trace, currentScore,
					currentSupport);
			trace.setScore(currentScore);
			trace.setSupport(currentSupport);
			trace.setState(currentState);
			if (!oldState.equals(currentState)) {
				session.setValue(model.getSolution(), currentState, PSMethodXCL.class);
			}
			model.notifyListeners(session, model);
		}
	}

	private DiagnosisState computeState(XCLModel model, InferenceTrace trace, double score, double support) {

		boolean hasContradiction = trace.getContrRelations().size() > 0;
		boolean hasSufficient = trace.getSuffRelations().size() > 0;
		boolean hasAllNecessary = trace.getReqPosRelations().size() == model.getNecessaryRelations().size();

		if (hasContradiction) {
			return new DiagnosisState(DiagnosisState.State.EXCLUDED);
		}

		if (hasSufficient) {
			return new DiagnosisState(DiagnosisState.State.ESTABLISHED);
		}

		double minSupport = model.getMinSupport();
		if (minSupport <= support) {
			if (score >= model.getEstablishedThreshold()) {
				return hasAllNecessary
						? new DiagnosisState(DiagnosisState.State.ESTABLISHED)
						: new DiagnosisState(DiagnosisState.State.SUGGESTED);
			}
			if (score >= model.getSuggestedThreshold() && hasAllNecessary) {
				return new DiagnosisState(DiagnosisState.State.SUGGESTED);
			}
		}

		return new DiagnosisState(DiagnosisState.State.UNCLEAR);
	}

	public void update(XCLModel xclModel, Collection<PropagationEntry> entries, Session session) {
		InferenceTrace trace = xclModel.getInferenceTrace(session);
		trace.refreshRelations(xclModel, session);
	}

	private double computeScore(XCLModel model, InferenceTrace trace, Session theCase) {

		// score is the sum of matching relations compared to evaluated
		// relations
		double posSum = weightedSumOf(trace.getPosRelations())
				+ weightedSumOf(trace.getReqPosRelations());
		if (posSum <= 0) return 0;
		double negSum = weightedSumOf(trace.getNegRelations())
				+ weightedSumOf(trace.getReqNegRelations());

		double result = posSum / (negSum + posSum);
		return result;
	}

	private double computeSupport(XCLModel model, InferenceTrace trace, Session theCase) {
		// support is the sum of evaluated relations compared to all relations
		double posSum = weightedSumOf(trace.getPosRelations())
				+ weightedSumOf(trace.getReqPosRelations());
		double negSum = weightedSumOf(trace.getNegRelations())
				+ weightedSumOf(trace.getReqNegRelations());
		double allSum = weightedSumOf(model.getRelations())
				+ weightedSumOf(model.getNecessaryRelations());
		return (posSum + negSum) / (allSum);
	}

	private double weightedSumOf(Collection<XCLRelation> relations) {
		double sum = 0;
		for (XCLRelation relation : relations) {
			sum += relation.getWeight();
		}
		return sum;
	}

}
