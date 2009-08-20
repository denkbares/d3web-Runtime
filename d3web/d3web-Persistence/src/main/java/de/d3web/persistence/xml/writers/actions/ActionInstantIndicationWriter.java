package de.d3web.persistence.xml.writers.actions;

import de.d3web.kernel.psMethods.nextQASet.ActionInstantIndication;

public class ActionInstantIndicationWriter extends ActionNextQASetWriter {
	public static final Class ID = ActionInstantIndication.class;

	protected String getNextQASetType() {
		return "ActionInstantIndication";
	}
}
