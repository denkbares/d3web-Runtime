package de.d3web.kernel.psMethods.contraIndication;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSMethodRulebased;

/**
 * Problem solving method for contraindicating QASets
 * This Class is a singleton.
 * Creation date: (03.11.2000 01:08:25)
 * @author Norman Brümmer
 */
public class PSMethodContraIndication extends PSMethodRulebased {

	private static PSMethodContraIndication instance = null;

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * @return de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication
	 */
	public static PSMethodContraIndication getInstance() {
		if (instance == null) {
			instance = new PSMethodContraIndication();
		}
		return instance;
	}


}