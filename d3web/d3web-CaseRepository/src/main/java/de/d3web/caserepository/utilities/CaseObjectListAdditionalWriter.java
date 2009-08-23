/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.utilities;

import de.d3web.caserepository.CaseObject;

/**
 * 24.09.2003 15:02:40
 * @author hoernlein
 */
public interface CaseObjectListAdditionalWriter {

	public String getTag();
	public String getXMLCode(CaseObject object);

}
