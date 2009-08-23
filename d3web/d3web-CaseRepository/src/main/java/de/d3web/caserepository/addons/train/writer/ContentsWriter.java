/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons.train.writer;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * 24.09.2003 15:11:43
 * @author hoernlein
 */
public class ContentsWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_Contents";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getContents() == null)
			return null;
		else
			return object.getContents().getXMLCode();
	}

}
