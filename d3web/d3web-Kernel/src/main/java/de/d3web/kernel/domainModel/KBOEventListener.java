package de.d3web.kernel.domainModel;

import java.util.EventListener;

import de.d3web.kernel.XPSCase;

/** 
 * 
 * @author Markus Kiennen
 */
public interface KBOEventListener extends EventListener {

	public abstract void notify(IEventSource source, XPSCase xpsCase);

}
