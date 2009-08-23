/*
 * Created on 24.11.2003
 */
package de.d3web.caserepository.addons.shared;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;
import de.d3web.config.Config;

/**
 * 24.11.2003 11:40:42
 * @author hoernlein
 */
public class ConfigWriter implements CaseObjectListAdditionalWriter {

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
	 */
	public String getTag() {
		return "_Config";
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
	 */
	public String getXMLCode(CaseObject object) {
		StringBuffer sb = new StringBuffer();
		Config c = object.getConfig();
		if (c != null)
			de.d3web.config.persistence.ConfigWriter.write(c, sb);
		return sb.toString();
	}

}
