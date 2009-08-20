package de.d3web.kernel.domainModel;

import de.d3web.kernel.XPSCase;
/**
 * Specifies that implementing classes are used as
 * explict knowledge. Each KnowledgeSlice needs to have
 * a problem-solver context in which it is relevant and
 * if it has already been used (e.g. rule = hasFired).
 * @author joba
 */
public interface KnowledgeSlice extends java.io.Serializable {

	/**
	 * Provide a unique id for each part of knowledge.
	 * @return java.lang.String
	 */
	String getId();

	/**
	 * Creation date: (30.08.00 17:23:04)
	 * @return the class of the PSMethod in which this KnowledgeSlice makes sense.
	 */
	public Class getProblemsolverContext();

	/**
	 * Has this knowledge already been used? (e.g. did a rule fire?)
	 */
	public boolean isUsed(XPSCase theCase);

	
	/**
	 * Prompts the knowledgeslice to remove itsself from all objects
	 *
	 */
	public void remove(); 
	
	/**
	 * generic verbalization method
	 * @return java.lang.String
	 */
	public String toString();
}
