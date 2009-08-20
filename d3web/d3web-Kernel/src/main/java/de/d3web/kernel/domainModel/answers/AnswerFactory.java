package de.d3web.kernel.domainModel.answers;
/**
 * This is a factory class for Answer objects
 * It contains several static methods which create the most popular answer types
 * Creation date: (26.06.2001 12:41:20)
 * @author Joachim Baumeister
 */
public class AnswerFactory {

	public static AnswerChoice createAnswerChoice(
		String theId,
		String theValue) {
		AnswerChoice theAnswer = new AnswerChoice();
		theAnswer.setId(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}

	public static AnswerNo createAnswerNo(String theId, String theValue) {
		AnswerNo theAnswer = new AnswerNo();
		theAnswer.setId(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}

	public static AnswerYes createAnswerYes(String theId, String theValue) {
		AnswerYes theAnswer = new AnswerYes();
		theAnswer.setId(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}
}