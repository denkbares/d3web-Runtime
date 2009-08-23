/*
 * Created on 20.02.2004
 */
package de.d3web.caserepository.addons;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.XMLCodeGenerator;

/**
 * ITemplateSession (in )
 * de.d3web.caserepository.addons.train
 * d3web-CaseRepository
 * @author hoernlein
 * @date 20.02.2004
 */
public interface ITemplateSession extends XMLCodeGenerator {
    public CaseObject getCaseObject();
}
