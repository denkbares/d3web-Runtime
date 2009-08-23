/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.train.writer;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * 21.10.2003 17:30:30
 * @author hoernlein
 */
public class TherapyConfigurationWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_TherapyConfiguration";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getTherapyConfiguration() == null)
			return null;
		else
			return object.getTherapyConfiguration().getXMLCode();
	}

}
