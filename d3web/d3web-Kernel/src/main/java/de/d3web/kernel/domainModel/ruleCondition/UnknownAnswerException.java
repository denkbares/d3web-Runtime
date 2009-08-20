package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.domainModel.D3WebCase;

/**
 * Exception that will be thrown if a question has an "unknown" answer when it should have a known one.
 * This is a singleton class.
 * Creation date: (06.12.2000 11:10:41)
 * @author Norman Brümmer
 */
public class UnknownAnswerException extends Exception {

	private static UnknownAnswerException instance =
		new UnknownAnswerException();

	/**
	 * @return the only instance of this Exception
	 */
	public static UnknownAnswerException getInstance() {
		return instance;
	}

	public void printStackTrace() {
		D3WebCase.strace(
			"Unknown answer in Condition");
	}
}
