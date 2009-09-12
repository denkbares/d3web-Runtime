/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
