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
public class MultimediaWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_Multimedia";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		if (object.getMultimedia() == null)
			return null;
		else
			return object.getMultimedia().getXMLCode();
	}

}
