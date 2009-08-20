package de.d3web.kernel.domainModel.answers;

/**
 * Represents an Answer that is the "yes" answer for YesNo-questions
 * Creation date: (28.09.00 17:52:09)
 * @author Joachim Baumeister
 */
public class AnswerYes extends AnswerChoice {

	/**
	 * creates a new AnswerYes.
	 */
	public AnswerYes() {
		setId("YES");
		setText("YES");
	}

	/**
	 * @return true
	 */
	public boolean isAnswerYes() {
		return true;
	}

	// 20030923 marty: delegate to AnswerChoice.equals(...)
	//	/**
	//	 * compares for equal reference first and then for equal 
	//	 * class instance.
	//	 * <BR>
	//	 * 2002-05-29 joba: added for better comparisons
	//	 * */
	//	public boolean equals(Object other) {
	//		if (this == other) {
	//			return true;
	//		} else if (other instanceof AnswerYes) {
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
}
