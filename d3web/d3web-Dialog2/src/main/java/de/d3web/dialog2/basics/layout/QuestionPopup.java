package de.d3web.dialog2.basics.layout;

public class QuestionPopup {
	
	private final String nextQuestionID;
	private final String firingAnswerID;
	
	public QuestionPopup(String nextQuestionID, String firingAnswerID) {
		super();
		this.nextQuestionID = nextQuestionID;
		this.firingAnswerID = firingAnswerID;
	}
	public String getNextQuestionID() {
		return nextQuestionID;
	}
	public String getFiringAnswerID() {
		return firingAnswerID;
	}
}
