package de.d3web.kernel.psMethods.combinied;
import java.util.Collection;
import java.util.LinkedList;

import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * A generic class for combining different problem solving methods.
 * Can be used for information retrieval based upon multiple problem solving methods states.
 * Creation date: (03.01.2002 16:08:27)
 * @author Christian Betz
 */
public abstract class PSMethodCombined extends PSMethodAdapter {
	private Collection psmethods;

	public void addPSMethod(PSMethod newPSMethod) {
		psmethods.add(newPSMethod);
	}

	public PSMethodCombined() {
		super();
		setPSMethods(new LinkedList());
	}

	/**
	 * @return a List of all PSMethods this PSM combines
	 */
	public Collection getPSMethods() {
		return psmethods;
	}

	/**
	 * @param a List of all PSMethods this PSM should combine
	 */
	public void setPSMethods(Collection newPSMethods) {
		psmethods = newPSMethods;
	}
}