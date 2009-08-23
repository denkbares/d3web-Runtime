/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * 21.10.2003 17:30:30
 * @author hoernlein
 */
public class FUSConfigurationWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_FUSConfiguration";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getFUSConfiguration() == null)
			return null;
		else
			return object.getFUSConfiguration().getXMLCode();
	}

}
