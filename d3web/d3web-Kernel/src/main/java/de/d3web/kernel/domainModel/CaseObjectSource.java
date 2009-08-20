package de.d3web.kernel.domainModel;

import de.d3web.kernel.dynamicObjects.XPSCaseObject;

/**
 * States the ability that the implementing objects
 * are able to create a user case sensitive part
 * of itself.
 */
public interface CaseObjectSource {

	/**
	 * Create a user case sesitive part of itself.
	 */
	public XPSCaseObject createCaseObject();
}
