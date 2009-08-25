package de.d3web.kernel.psMethods.setCovering;

/**
 * This factory produces knowledge for SCRelations
 * 
 * @author bates
 */
public class SCKnowledgeFactory {

	/**
	 * creates an SCKnowledge-Object from the given type and value. This method
	 * will primary be used in SCMLoader
	 * 
	 * @param type
	 *            the knowledge type (e.g. score)
	 * @param value
	 *            its value as String
	 * @return the generated SCKnowledge-Object
	 */
	public static SCKnowledge createSCKnowledge(String type, String value) {
		SCKnowledge ret = null;
		if (type.equalsIgnoreCase("score")) {
			ret = new SCScore(Integer.parseInt(value));
		} else if (type.equalsIgnoreCase("probability")) {
			ret = SCProbability.getProbabilityBySymbol(value);
		} else if (type.equalsIgnoreCase("confirmationCategory")) {
			ret = SCProbability.getProbabilityBySymbol(value);
		}
		return ret;
	}

}
