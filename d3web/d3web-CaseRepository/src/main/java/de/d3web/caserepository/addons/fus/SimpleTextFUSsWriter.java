/*
 * Created on 14.04.2004
 */
package de.d3web.caserepository.addons.fus;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * SimpleTextFUSsWriter (in )
 * de.d3web.caserepository.addons.fus
 * d3web-CaseRepository
 * @author hoernlein
 * @date 14.04.2004
 */
public class SimpleTextFUSsWriter implements CaseObjectListAdditionalWriter {

    /* (non-Javadoc)
     * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getTag()
     */
    public String getTag() {
        return "_SimpleTextFUSs";
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter#getXMLCode(de.d3web.caserepository.CaseObject)
     */
    public String getXMLCode(CaseObject object) {
        if (object.getSimpleTextFUSs() == null)
            return null;
        else
            return object.getSimpleTextFUSs().getXMLCode();
    }

}
