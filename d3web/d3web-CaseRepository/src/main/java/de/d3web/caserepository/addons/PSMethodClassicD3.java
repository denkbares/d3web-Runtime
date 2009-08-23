
package de.d3web.caserepository.addons;

import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * @author betz
 *
 * This PSMethod is used to determine, if a solution-rating comes from the trainer.
 * It is a marker class only.
 * Creation date: (29.04.2003 17:10:00)
 */
public class PSMethodClassicD3 extends PSMethodAdapter {

	private static PSMethodClassicD3 instance = null;

	private PSMethodClassicD3() { /* hide empty constructor */ }

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodClassicD3 getInstance() {
		if (instance == null) {
			instance = new PSMethodClassicD3();
		}
		return instance;
	}

}
