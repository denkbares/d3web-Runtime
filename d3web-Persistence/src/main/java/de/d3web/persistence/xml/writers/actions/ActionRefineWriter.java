package de.d3web.persistence.xml.writers.actions;

import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.kernel.psMethods.nextQASet.ActionRefine;

/**
 * Generates the XML representation of a ActionRefine Object
 * @author Michael Scharvogel
 */
public class ActionRefineWriter extends ActionNextQASetWriter {

	public static final Class ID = ActionRefine.class;

	protected String getTarget(ActionNextQASet anqas) {
		if(((ActionRefine) anqas).getTarget() == null) {
			return "<targetDiagnosis ID='"
			+ "'/>\n";
		} else 
			return "<targetDiagnosis ID='"
				+ ((ActionRefine) anqas).getTarget().getId()
				+ "'/>\n";
	}

	protected String getNextQASetType() {
		return "ActionRefine";
	}
}
