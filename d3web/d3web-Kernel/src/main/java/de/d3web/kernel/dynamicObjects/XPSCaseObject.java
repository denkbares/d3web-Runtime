package de.d3web.kernel.dynamicObjects;
import de.d3web.kernel.domainModel.CaseObjectSource;
/**
 * Superclass for any dynamic, user specific object.<br>
 * @author Christian Betz, joba
 */
public abstract class XPSCaseObject {
	private CaseObjectSource sourceObject;

	public XPSCaseObject(CaseObjectSource theSourceObject) {
		super();
		setSourceObject(theSourceObject);
	}

	/**
	 * Creation date: (23.05.2001 15:35:43)
	 * @return the IDObject this dynamic XPSCaseObject has been created for
	 */
	protected CaseObjectSource getSourceObject() {
		return sourceObject;
	}

	private void setSourceObject(
		CaseObjectSource newSourceObject) {
		sourceObject = newSourceObject;
	}
}