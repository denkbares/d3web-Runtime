/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons.shared;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * 24.09.2003 15:09:22
 * @author hoernlein
 */
public class AppliedQSetsWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_AppliedQSets";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getAppliedQSets() == null)
			return null;
		else
			return object.getAppliedQSets().getXMLCode();
	}

}
