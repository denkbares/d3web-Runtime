package de.d3web.kernel.psMethods.suppressAnswer;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSMethodRulebased;
/**
 * Mechanism to suppress answers of questions via rules
 * Creation date: (28.08.00 18:04:09)
 * @author joba, norman
 */
public class PSMethodSuppressAnswer extends PSMethodRulebased {
	private static PSMethodSuppressAnswer instance = null;

	/**
	 * @return the one and only instance of this PSMethod
	 * Creation date: (04.12.2001 12:36:25)
	 */
	public static PSMethodSuppressAnswer getInstance() {
		if (instance == null) {
			instance = new PSMethodSuppressAnswer();
		}
		return instance;
	}

}