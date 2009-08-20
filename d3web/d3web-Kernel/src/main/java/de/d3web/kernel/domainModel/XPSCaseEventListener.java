package de.d3web.kernel.domainModel;

import de.d3web.kernel.XPSCase;

/**
 * Interface for event listeners for XPSCase instances.
 * 
 * @author gbuscher
 */
public interface XPSCaseEventListener {

	public abstract void notify(XPSCase source, ValuedObject o, Object context);
	
}
