package de.d3web.dialog2.basics.layout;

import java.util.List;

public class QuestionImage {

    private String file;

    private List<AnswerRegion> answerRegions;

    private String align = "center";

    private String answersPosition = "hidden";

    private boolean showRegionOnMouseOver = true;

    public QuestionImage(String file) {
	this.file = file;
    }

    public String getAlign() {
	return align;
    }

    public List<AnswerRegion> getAnswerRegions() {
	return answerRegions;
    }

    public String getAnswersPosition() {
	return answersPosition;
    }

    public String getFile() {
	return file;
    }

    public boolean isShowRegionOnMouseOver() {
	return showRegionOnMouseOver;
    }

    public void setAlign(String align) {
	this.align = align;
    }

    public void setAnswerRegions(List<AnswerRegion> answerRegions) {
	this.answerRegions = answerRegions;
    }

    public void setAnswersPosition(String answersPosition) {
	this.answersPosition = answersPosition;
    }

    public void setShowRegionOnMouseOver(boolean showRegionOnMouseOver) {
	this.showRegionOnMouseOver = showRegionOnMouseOver;
    }

    @Override
    public String toString() {
	StringBuffer ret = new StringBuffer();
	ret.append("<QuestionImage file=" + file + ">");
	for (AnswerRegion a : answerRegions) {
	    ret.append("\n  " + a);
	}
	ret.append("</QuestionImage>");
	return ret.toString();
    }

}
