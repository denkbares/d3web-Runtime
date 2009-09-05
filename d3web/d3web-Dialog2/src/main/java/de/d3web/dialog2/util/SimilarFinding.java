package de.d3web.dialog2.util;

public class SimilarFinding {

    private double similarity;
    private String text;
    private String textVerbalization;

    public SimilarFinding(double similarity, String text,
	    String textVerbalization) {
	this.similarity = similarity;
	this.text = text;
	this.textVerbalization = textVerbalization;
    }

    public double getSimilarity() {
	return similarity;
    }

    public String getText() {
	return text;
    }

    public String getTextVerbalization() {
	return textVerbalization;
    }

}
