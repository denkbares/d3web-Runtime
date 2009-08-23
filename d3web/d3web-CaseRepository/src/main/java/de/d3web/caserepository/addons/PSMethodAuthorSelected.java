
package de.d3web.caserepository.addons;

import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * @author gbuscher
 *
 * This PSMethod is used to determine, if a solution-rating comes from the trainer.
 * It is a marker class only.
 * Creation date: (29.04.2003 17:10:00)
 */
public class PSMethodAuthorSelected extends PSMethodAdapter {

 	private static PSMethodAuthorSelected instance = null;

	private PSMethodAuthorSelected() { /* hide empty constructor */ }

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodAuthorSelected getInstance() {
		if (instance == null) {
			instance = new PSMethodAuthorSelected();
		}
		return instance;
	}

}
