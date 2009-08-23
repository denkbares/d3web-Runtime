/*
 * Created on 14.04.2004
 */
package de.d3web.caserepository.addons.fus;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.caserepository.addons.ISimpleTextFUSs;

/**
 * SimpleTextFUSs (in )
 * de.d3web.caserepository.addons.train
 * d3web-CaseRepository
 * @author hoernlein
 * @date 14.04.2004
 */
public class SimpleTextFUSs implements ISimpleTextFUSs {
    
    private List<SimpleTextFUS> stfs = new LinkedList<SimpleTextFUS>();
    
    public List<SimpleTextFUS> getSTFs() {
        return Collections.unmodifiableList(stfs);
    }
    
    public void addSTF(SimpleTextFUS stf) {
        if (!stfs.contains(stf))
            stfs.add(stf);
    }
    
    public void removeSTF(SimpleTextFUS stf) {
        stfs.remove(stf);
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
     */
    public String getXMLCode() {
        StringBuffer sb = new StringBuffer();
        sb.append("<SimpleTextFUSs>\n");
        Iterator<SimpleTextFUS> iter = getSTFs().iterator();
        while (iter.hasNext())
            sb.append(iter.next().getXMLCode());
        sb.append("</SimpleTextFUSs>\n");
        return sb.toString();
    }

}
