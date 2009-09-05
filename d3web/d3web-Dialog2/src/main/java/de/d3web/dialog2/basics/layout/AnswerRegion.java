package de.d3web.dialog2.basics.layout;

public class AnswerRegion {

    private String answerID;

    private int xStart;

    private int xEnd;

    private int yStart;

    private int yEnd;

    public AnswerRegion(String answerID, int xStart, int xEnd, int yStart,
	    int yEnd) {
	this.answerID = answerID;
	this.xStart = xStart;
	this.xEnd = xEnd;
	this.yStart = yStart;
	this.yEnd = yEnd;
    }

    public String getAnswerID() {
	return answerID;
    }

    public int getHeight() {
	return yEnd - yStart;
    }

    public int getWidth() {
	return xEnd - xStart;
    }

    // Getter and setter

    public int getXEnd() {
	return xEnd;
    }

    public int getXStart() {
	return xStart;
    }

    public int getYEnd() {
	return yEnd;
    }

    public int getYStart() {
	return yStart;
    }

    @Override
    public String toString() {
	return "<AnswerRegion answerID=" + answerID + " xStart=" + xStart
		+ " xEnd=" + xEnd + " yStart=" + yStart + " yEnd=" + yEnd
		+ " />";
    }

}
