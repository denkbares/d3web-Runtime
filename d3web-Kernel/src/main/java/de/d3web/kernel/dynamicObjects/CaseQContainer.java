package de.d3web.kernel.dynamicObjects;

import de.d3web.kernel.domainModel.qasets.QContainer;

/**
 * Stores the dynamic, user specific values for an QContainer
 * object. It corresponds to the static QContainer object.<br>
 * @author Christian Betz, joba
 * @see QContainer
 */
public class CaseQContainer extends CaseQASet {

	public CaseQContainer(QContainer qcontainer) {
		super(qcontainer);
	}
}
