package de.d3web.kernel.domainModel;

import java.util.Collection;

import de.d3web.kernel.XPSCase;

/**
 * 
 * @author gbuscher
 */
public interface IEventSource {

	public void addListener(KBOEventListener listener);

	public void removeListener(KBOEventListener listener);
	
	public void notifyListeners(XPSCase xpsCase, IEventSource source);

	public Collection getListeners();
	
}
