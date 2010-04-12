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

	public void refreshStates(Collection<XCLModel> updatedModels, Session xpsCase) {
		for (XCLModel model : updatedModels) {
			DefaultInferenceTrace trace = (DefaultInferenceTrace) model.getInferenceTrace(xpsCase);
			DiagnosisState oldState = model.getState(xpsCase);
			// calculate scores
			// and the calculate states (based on that scores)
			double currentScore = computeScore(model, trace, xpsCase);
			double currentSupport = computeSupport(model, trace, xpsCase);
			DiagnosisState currentState = computeState(model, trace, currentScore, currentSupport);
			trace.setScore(currentScore);
			trace.setSupport(currentSupport);
			trace.setState(currentState);
			if (!oldState.equals(currentState)) {
				xpsCase.setValue(model.getSolution(), currentState, PSMethodXCL.class);
			}
			model.notifyListeners(xpsCase, model);
		}
	}

	private DiagnosisState computeState(XCLModel model, InferenceTrace trace, double score, double support) {

		boolean hasContradiction = trace.getContrRelations().size() > 0;
		boolean hasSufficient = trace.getSuffRelations().size() > 0;
		boolean hasAllNecessary = trace.getReqPosRelations().size()  == model.getNecessaryRelations().size();
		
		if (hasContradiction) {
			return DiagnosisState.EXCLUDED;
		}

		if (hasSufficient) {
			return DiagnosisState.ESTABLISHED;
		}

		double minSupport = model.getMinSupport();
		if (minSupport <= support) {
			if (score >= model.getEstablishedThreshold()) {
				return hasAllNecessary ? DiagnosisState.ESTABLISHED : DiagnosisState.SUGGESTED;
			}
			if (score >= model.getSuggestedThreshold() && hasAllNecessary) {
				return DiagnosisState.SUGGESTED;
			}
		}

		return DiagnosisState.UNCLEAR;
	}

	public void update(XCLModel xclModel, Collection<PropagationEntry> entries, Session xpsCase) {
		InferenceTrace trace = xclModel.getInferenceTrace(xpsCase);
		trace.refreshRelations(xclModel, xpsCase);
	}

	private double computeScore(XCLModel model, InferenceTrace trace, Session theCase) {

		// score is the sum of matching relations compared to evaluated relations
		double posSum = weightedSumOf(trace.getPosRelations()) + weightedSumOf(trace.getReqPosRelations());
		if (posSum <= 0) return 0;
		double negSum = weightedSumOf(trace.getNegRelations()) + weightedSumOf(trace.getReqNegRelations());
		
		double result = posSum / (negSum + posSum);
		return result;
	}

	private double computeSupport(XCLModel model, InferenceTrace trace, Session theCase) {
		// support is the sum of evaluated relations compared to all relations
		double posSum = weightedSumOf(trace.getPosRelations()) + weightedSumOf(trace.getReqPosRelations());
		double negSum = weightedSumOf(trace.getNegRelations()) + weightedSumOf(trace.getReqNegRelations());
		double allSum = weightedSumOf(model.getRelations()) + weightedSumOf(model.getNecessaryRelations());
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
