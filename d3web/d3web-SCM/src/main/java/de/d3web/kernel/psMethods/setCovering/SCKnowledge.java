package de.d3web.kernel.psMethods.setCovering;

/**
 * This interface describes the kind of knowledge that will be saved in a
 * SCRelation (e.g. SCScore; SCProbability)
 * 
 * @author bates
 */
public interface SCKnowledge {
	/**
	 * @return the value of this knowledge
	 */
	public Object getValue();

	/**
	 * 
	 * @return the symbol
	 */
	public String getSymbol();

	/**
	 * String verbalization for persistence. This is needed for the attribute
	 * "type" of &lt;Knowledge&gt;.
	 * 
	 * @return a verbalization of this knowledge
	 */
	public String verbalize();
}
