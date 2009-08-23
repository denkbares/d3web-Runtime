/*
 * Created on 09.10.2003
 */
package de.d3web.caserepository.addons.train.writer;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * 09.10.2003 14:53:23
 * @author hoernlein
 */
public class TemplateSessionWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_TemplateSessions";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getTemplateSession() == null)
			return null;
		else
			return object.getTemplateSession().getXMLCode();
	}

}
