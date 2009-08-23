/*
 * Created on 23.02.2004
 */
package de.d3web.caserepository.sax;

import de.d3web.caserepository.CaseObject;

/**
 * ID2CaseMapper (in )
 * de.d3web.caserepository.sax
 * d3web-CaseRepository
 * @author hoernlein
 * @date 23.02.2004
 */
public interface ID2CaseMapper {
    public CaseObject getCaseObject(String id);
}
