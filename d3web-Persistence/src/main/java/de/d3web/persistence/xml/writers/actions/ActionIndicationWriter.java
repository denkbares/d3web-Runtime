package de.d3web.persistence.xml.writers.actions;
import de.d3web.kernel.psMethods.nextQASet.ActionIndication;

/**
 * Generates the XML representation of a ActionNextQASet Object
 * @author Michael Scharvogel
 */
public class ActionIndicationWriter extends ActionNextQASetWriter {

	public static final Class ID = ActionIndication.class;

	protected String getNextQASetType() {
		return "ActionIndication";
	}
}
