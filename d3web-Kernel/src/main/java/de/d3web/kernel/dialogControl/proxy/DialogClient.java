package de.d3web.kernel.dialogControl.proxy;
import java.util.Collection;

import de.d3web.kernel.XPSCase;

/**
 * A DialogClient is an Object that can be handled by the DialogProxy.
 * If the Proxy is asked for Answers of a Question, it will first ask all registered
 * clients if they have such an answer.
 * Every Client gets a priority by which it can be compared by DialogClientComparator
 * A DialogClient may be a ShadowMemory (in RAM) or e.g. a Database-Client...
 * @see ShadowMemory
 * @author Norman Brümmer
 */
public abstract class DialogClient {
	private int priority = 0;

	public DialogClient() {
		super();
	}

	/**
	 * @return a List of Answers for the Question with the given ID, if such answers exist, otherwise null
	 */
	public abstract Collection getAnswers(String QuestionID);

	/**
	 * @return the Priority of this client. 1 is the highest.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * fills this Client with Question-IDs and answers from the given case
	 */
	public abstract void putCase(XPSCase theCase);

	/**
	 * @param newPriority Priority of this Client. Neccessary for Proxy. 1 is the highest...
	 */
	public void setPriority(int newPriority) {
		priority = newPriority;
	}
}