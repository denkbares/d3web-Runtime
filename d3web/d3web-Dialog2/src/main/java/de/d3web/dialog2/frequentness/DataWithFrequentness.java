package de.d3web.dialog2.frequentness;

public class DataWithFrequentness {

    private String text;

    private int absoluteFrequency;

    private double relativeFrequency;

    public DataWithFrequentness(String text) {
	this.text = text;
	this.absoluteFrequency = 0;
	this.relativeFrequency = 0.0;
    }

    public DataWithFrequentness(String text, int abs, double rel) {
	this.text = text;
	this.absoluteFrequency = abs;
	this.relativeFrequency = rel;
    }

    public int getAbsoluteFrequency() {
	return absoluteFrequency;
    }

    public double getRelativeFrequency() {
	return relativeFrequency;
    }

    public String getText() {
	return text;
    }

}
