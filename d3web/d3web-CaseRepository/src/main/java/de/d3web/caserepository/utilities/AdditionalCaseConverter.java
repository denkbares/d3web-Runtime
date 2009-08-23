package de.d3web.caserepository.utilities;

import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.XPSCase;

/**
 * @author bruemmer
 */
public interface AdditionalCaseConverter {
	public void caseObject2XPSCase(CaseObject caseObject, XPSCase xpsCase);
	public void xpsCase2CaseObject(XPSCase xpsCase, CaseObject caseObject);
}