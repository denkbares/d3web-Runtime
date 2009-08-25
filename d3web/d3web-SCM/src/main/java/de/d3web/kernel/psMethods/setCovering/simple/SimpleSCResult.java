package de.d3web.kernel.psMethods.setCovering.simple;

public class SimpleSCResult {
	
	private double positiveDefinedQuestionsInModel;
	private double answeredWithPosDefinition;
	private double coveringAnswers;
	
	public SimpleSCResult(double posDef, double answered, double covering) {
		positiveDefinedQuestionsInModel = posDef;
		answeredWithPosDefinition = answered;
		coveringAnswers = covering;
	}
	
	
	public double getPositiveDefinedQuestionsInModel() {
		return positiveDefinedQuestionsInModel;
	}


	public double getCoveringAnswers() {
		return coveringAnswers;
	}


	public double getPrecision() {
		return coveringAnswers / answeredWithPosDefinition;
	}
	
	public double getRecall() {
		return answeredWithPosDefinition / positiveDefinedQuestionsInModel;
	}


	public double getAnsweredWithPosDefinition() {
		return answeredWithPosDefinition;
	}


	public void setAnsweredWithPosDefinition(double answeredWithPosDefinition) {
		this.answeredWithPosDefinition = answeredWithPosDefinition;
	}


	
	

}
