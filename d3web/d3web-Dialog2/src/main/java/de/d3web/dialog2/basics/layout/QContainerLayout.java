package de.d3web.dialog2.basics.layout;

import java.util.List;

public class QContainerLayout extends QuestionPageLayout {

    private List<QuestionLayout> questionLayoutList;

    private List<HtmlTextLayout> htmlTextList;

    private String qContID;

    private int rows;

    private int cols;

    private int defaultColspan;

    // private boolean usePositioning;

    public int getDefaultColspan() {
	return defaultColspan;
    }

    public void setDefaultColspan(int defaultColspan) {
	this.defaultColspan = defaultColspan;
    }

    public void setDefaultColspan(String defaultColspanString) {
	try {
	    setDefaultColspan(Integer.parseInt(defaultColspanString));
	} catch (NumberFormatException e) {
	    // do nothing
	}
    }

    public QContainerLayout() {
    }

    public QContainerLayout(String qContID, String _rows, String _cols) {
	this.qContID = qContID;
	this.rows = Integer.parseInt(_rows);
	this.cols = Integer.parseInt(_cols);
	this.defaultColspan = -1;
    }

    public int getCols() {
	return cols;
    }

    public HtmlTextLayout getHtmlTextDefinitionOnPosition(int x, int y) {
	for (HtmlTextLayout layoutDef : htmlTextList) {
	    if (layoutDef.getPosXStart() == x && layoutDef.getPosYStart() == y) {
		return layoutDef;
	    }
	}
	return null;
    }

    public List<HtmlTextLayout> getHtmlTextList() {
	return htmlTextList;
    }

    public String getQContID() {
	return qContID;
    }

    public QuestionLayout getQuestionDefinitionOnPosition(int x, int y) {
	for (QuestionLayout questionDef : questionLayoutList) {
	    if (questionDef.getPosXStart() == x
		    && questionDef.getPosYStart() == y) {
		return questionDef;
	    }
	}
	return null;
    }

    public QuestionLayout getQuestionLayoutForQuestionID(String id) {
	for (QuestionLayout questionDef : questionLayoutList) {
	    if (questionDef.getQID().equals(id)) {
		return questionDef;
	    }
	}
	return null;
    }

    public List<QuestionLayout> getQuestionLayoutList() {
	return questionLayoutList;
    }

    public int getRows() {
	return rows;
    }

    public void setHtmlTextList(List<HtmlTextLayout> htmlTextList) {
	this.htmlTextList = htmlTextList;
    }

    public void setQuestionList(List<QuestionLayout> questionList) {
	this.questionLayoutList = questionList;
    }

    @Override
    public String toString() {
	return "\nQContainerLayoutDefinition id=" + qContID + "; gridgap="
		+ getGridgap() + "; padding=" + getPadding() + "; rows=" + rows
		+ "; cols=" + cols + "; containerBorder="
		+ getQContainerBorder() + "; " + questionLayoutList;
    }
}
