package de.d3web.kernel.domainModel.answers;

/**
 * Represents an Answer that is the "no" answer for YesNo-questions
 * Creation date: (28.09.00 17:52:43)
 * @author Joachim Baumeister
 */
public class AnswerNo extends AnswerChoice {

	/**
	 * Creates a new AnswerNo object
	 */
	public AnswerNo() {
		super();
	}

	/**
	 * @return true
	 */
	public boolean isAnswerNo() {
		return true;
	}

	// 20030923 marty: delegate to AnswerChoice.equals(...)
	//	/**
	//	 * compares for equal reference first, then for equal class instance.
	//	 * <BR>
	//	 * 2002-05-29 joba: added for better comparisons
	//	 */
	//	public boolean equals(Object other) {
	//		if (this == other) {
	//			return true;
	//		} else if (other instanceof AnswerNo) {
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
}
