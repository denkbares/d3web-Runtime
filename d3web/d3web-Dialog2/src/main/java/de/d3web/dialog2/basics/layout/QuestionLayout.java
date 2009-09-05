package de.d3web.dialog2.basics.layout;

import java.util.List;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;

public class QuestionLayout extends QuestionPageLayout {

    private final String qID;

    private int posXStart = -1;

    private int posYStart = -1;

    private int colspan = -1;

    public void setColspan(int colspan) {
	this.colspan = colspan;
    }

    private int rowspan = 1;

    private List<QuestionImage> questionImageList;

    private List<QuestionPopup> followingPopupQuestionIDs;

    public List<QuestionPopup> getFollowingPopupQuestions() {
	return followingPopupQuestionIDs;
    }

    /**
     * Returns the id of the first popup Question for the given id of an answer
     * saved in this layout
     * 
     * @param answerId
     *            the id of the answer, that triggers a popup question
     * @return the id of the first popup Question for the given id of an answer
     *         saved in this layout
     */
    public String getFollowingPopupQuestionId(String answerId) {
	for (QuestionPopup qp : followingPopupQuestionIDs) {
	    if (qp.getFiringAnswerID().equals(answerId)) {
		return qp.getNextQuestionID();
	    }
	}
	return null;
    }

    public Question getFollowingPopupQuestion(String answerId, KnowledgeBase kb) {
	String qId = getFollowingPopupQuestionId(answerId);
	for (Question q : kb.getQuestions()) {
	    if (q.getId().equals(qId)) {
		return q;
	    }
	}
	return null;
    }

    public void setFollowingPopupQuestions(
	    List<QuestionPopup> followingPopupQuestionIDs) {
	this.followingPopupQuestionIDs = followingPopupQuestionIDs;
    }

    public QuestionLayout(String qID) {
	super();
	this.qID = qID;
    }

    public QuestionLayout(String qID, String posX, String posY) {
	this.qID = qID;

	setPosX(posX);
	setPosY(posY);
    }

    public void setPosY(String posY) {
	if (posY.indexOf("-") == -1) {
	    posYStart = Integer.parseInt(posY);
	    rowspan = 0;
	} else {
	    String[] positions = posY.split("-");
	    try {
		int yStart = Integer.parseInt(positions[0]);
		int yEnd = Integer.parseInt(positions[1]);
		posYStart = yStart;
		rowspan = yEnd - yStart + 1;
	    } catch (NumberFormatException e) {
		posYStart = -1;
		rowspan = 0;
	    }

	}
    }

    public void setPosX(int posX) {
	this.posXStart = posX;
    }

    public void setPosY(int posY) {
	this.posYStart = posY;
    }

    public void setPosX(String posX) {
	if (posX.indexOf("-") == -1) {
	    posXStart = Integer.parseInt(posX);
	    colspan = 1;
	} else {
	    String[] positions = posX.split("-");
	    int xStart = Integer.parseInt(positions[0]);
	    int xEnd = Integer.parseInt(positions[1]);
	    posXStart = xStart;
	    colspan = xEnd - xStart + 1;
	}
    }

    public int getColspan() {
	return colspan;
    }

    public int getPosXStart() {
	return posXStart;
    }

    public int getPosYStart() {
	return posYStart;
    }

    public String getQID() {
	return qID;
    }

    public List<QuestionImage> getQuestionImageList() {
	return questionImageList;
    }

    public int getRowspan() {
	return rowspan;
    }

    public void setQuestionImageList(List<QuestionImage> questionImageList) {
	this.questionImageList = questionImageList;
    }

    @Override
    public String toString() {
	return "\n  <Question id=" + qID + "; posXStart=" + posXStart
		+ "; posYStart=" + posYStart + "; colspan=" + colspan
		+ "; rowspan=" + rowspan + "; questionBorder="
		+ getQuestionBorder() + "; answerTextColor="
		+ getAnswerTextColor() + "; headlineTextColor="
		+ getHeadlineTextColor() + " />";

    }
}
