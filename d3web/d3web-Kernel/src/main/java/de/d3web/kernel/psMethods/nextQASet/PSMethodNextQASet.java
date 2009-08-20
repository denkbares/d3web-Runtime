package de.d3web.kernel.psMethods.nextQASet;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSMethodRulebased;

/**
 * This PSMethod handles the indication of
 * QASets.
 * Creation date: (28.08.00 18:04:09)
 * @author joba
 */
public class PSMethodNextQASet extends PSMethodRulebased {
	private static PSMethodNextQASet instance = null;

	/**
	 * @return the one and only instance of this PSMethodContraIndication (Singleton)
	 */
	public static PSMethodNextQASet getInstance() {
		if (instance == null) {
			instance = new PSMethodNextQASet();
		}
		return instance;
	}


}