/*
 * Created on 01.12.2003
 *
 */
package de.d3web.kernel.dialogControl;

import de.d3web.kernel.XPSCase;

/**
 * [DOC]
 * @author Christian Betz
 *
 */
public class DefaultQASetManagerFactory implements QASetManagerFactory {

	public DefaultQASetManagerFactory() {
		super();
	}

	/**
	 * @see de.d3web.kernel.dialogControl.QASetManagerFactory#createQASetManager(de.d3web.kernel.XPSCase)
	 */
	public QASetManager createQASetManager(XPSCase theCase) {
		return new OQDialogController(theCase);
	}

}
