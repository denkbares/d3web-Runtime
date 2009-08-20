package de.d3web.kernel.domainModel;

/**
 * This Exception will be thrown if a KnowledgeBase has been set to a XPSCaseObject more than one time.
 * @author Norman Brümmer
 */
public class KnowledgeBaseObjectModificationException extends Exception {

	/**
	 * Creates a new KnowledgeBaseObjectModificationException.
	 */
	public KnowledgeBaseObjectModificationException() {
		super();
	}

	/**
	 * Creates a new KnowledgeBaseObjectModificationException.
	 * @param s exception text
	 */
	public KnowledgeBaseObjectModificationException(String s) {
		super(s);
	}
}
