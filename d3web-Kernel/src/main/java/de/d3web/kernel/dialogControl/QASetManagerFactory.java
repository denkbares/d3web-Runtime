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
public interface QASetManagerFactory {
	public QASetManager createQASetManager(XPSCase theCase);
}
