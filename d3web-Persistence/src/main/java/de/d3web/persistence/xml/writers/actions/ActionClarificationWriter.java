package de.d3web.persistence.xml.writers.actions;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;

/**
 * Generates the XML representation of an ActionClarification Object
 * @author Michael Scharvogel
 */
public class ActionClarificationWriter extends ActionNextQASetWriter {

	public static final Class ID = ActionClarify.class;

	protected String getTarget(ActionNextQASet anqas) {
		if(((ActionClarify) anqas).getTarget() == null) {
			return "<targetDiagnosis ID='"
			+ "'/>\n";
		} else 
			return "<targetDiagnosis ID='"
				+ ((ActionClarify) anqas).getTarget().getId()
				+ "'/>\n";
	}

	protected String getNextQASetType() {
		return "ActionClarify";
	}
}
