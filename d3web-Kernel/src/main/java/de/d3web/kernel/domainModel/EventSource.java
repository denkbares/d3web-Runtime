package de.d3web.kernel.domainModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;

/**
 * 
 *
 * @author baumeister, markus kiennen
 * 
 */
public class EventSource implements IEventSource {

	Collection listeners;

	public void addListener(KBOEventListener listener) {
		if (listeners == null)
			listeners = new LinkedList();
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(KBOEventListener listener) {
		if (listeners != null) {

			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
			if (listeners.size() == 0) {
				listeners = null;
			}
		}
	}
	public void notifyListeners(XPSCase xpsCase, IEventSource source) {
		if (listeners != null && xpsCase != null && source != null) {
			Iterator lIter = new ArrayList(listeners).iterator();
			while (lIter.hasNext()) {
				KBOEventListener cl = (KBOEventListener) lIter.next();
				cl.notify(source, xpsCase);
			}
		}
	}


	public Collection getListeners() {
		return listeners;
	}
}
