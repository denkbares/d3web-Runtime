package de.d3web.dialog2.util;

public class CoveredFinding {

    private double score;
    private double cStrength;
    private double weight;
    private double possibleScore;
    private String text;
    private String textVerbalization;

    private SimilarFinding simFinding;

    public CoveredFinding(double cStrength, double weight, double score,
	    double possibleScore, String text, String textVerbalization) {
	this.cStrength = cStrength;
	this.weight = weight;
	this.score = score;
	this.possibleScore = possibleScore;
	this.text = text;
	this.textVerbalization = textVerbalization;
    }

    public double getCStrength() {
	return cStrength;
    }

    public double getPossibleScore() {
	return possibleScore;
    }

    public double getScore() {
	return score;
    }

    public SimilarFinding getSimFinding() {
	return simFinding;
    }

    public String getText() {
	return text;
    }

    public String getTextVerbalization() {
	return textVerbalization;
    }

    public double getWeight() {
	return weight;
    }

    public void setSimFinding(SimilarFinding simFinding) {
	this.simFinding = simFinding;
    }

}
