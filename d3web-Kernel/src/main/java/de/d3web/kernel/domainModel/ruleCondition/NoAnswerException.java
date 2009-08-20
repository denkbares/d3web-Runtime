package de.d3web.kernel.domainModel.ruleCondition;

/**
 * Exception that will be thrown if a question has no answer when it should have one.
 * Creation date: (20.11.2000 10:03:12)
 * @see java.lang.Exception
 * @author Christian Betz
 */
public class NoAnswerException extends Exception {

	private static NoAnswerException instance = new NoAnswerException();

	public void printStackTrace() {
		System.err.println("No answer in Condition");
	}

	public static NoAnswerException getInstance() {
		return instance;
	}

	/**
	 * Creates a new NoAnswerException without message
	 */
	private NoAnswerException() {
		super();
	}

}