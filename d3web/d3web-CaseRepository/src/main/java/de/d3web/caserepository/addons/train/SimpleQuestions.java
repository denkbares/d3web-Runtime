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
 * Created on 06.04.2004
 */
package de.d3web.caserepository.addons.train;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.caserepository.addons.ISimpleQuestions;

/**
 * MultimediaSimpleQuestions (in )
 * de.d3web.caserepository.addons
 * d3web-CaseRepository
 * @author hoernlein
 * @date 06.04.2004
 */
public class SimpleQuestions implements ISimpleQuestions {
    
    private static int number = 0; 
    
    private Map<String, Object> id2obj = new HashMap<String, Object>();
    private Map<Object, String> obj2id = new HashMap<Object, String>();

    public SimpleQuestion getSimpleQuestion(String id) {
        return (SimpleQuestion) id2obj.get(id);
    }
    
    public String getId(SimpleQuestion sq) {
        return obj2id.get(sq);
    }
    
    public SimpleQuestion.SimpleAnswer getSimpleAnswer(String id) {
        return (SimpleQuestion.SimpleAnswer) id2obj.get(id);
    }
    
    public String getId(SimpleQuestion.SimpleAnswer sa) {
        return obj2id.get(sa);
    }
    
    private List<SimpleQuestion> simplequestions = new LinkedList<SimpleQuestion>();
    
    public void addSimpleQuestion(SimpleQuestion sq) {
        simplequestions.add(sq);
        id2obj.put("sq_" + ++number, sq);
        obj2id.put(sq, "sq_" + number);
        for (int i = 0; i < sq.getNumberOfAnswers(); i++) {
            id2obj.put("sa_" + ++number, sq.getAnswer(i));
            obj2id.put(sq.getAnswer(i), "sa_" + number);
        }
    }
    
    public void deleteSimpleQuestion(SimpleQuestion sq) {
        String id = obj2id.get(sq);
        id2obj.remove(id);
        obj2id.remove(sq);
        for (int i = 0; i < sq.getNumberOfAnswers(); i++) {
            id = obj2id.get(sq.getAnswer(i));
            id2obj.remove(id);
            obj2id.remove(sq.getAnswer(i));
        }
        simplequestions.remove(sq);
    }
    
    public List<SimpleQuestion> getSimpleQuestions() {
        return Collections.unmodifiableList(simplequestions);
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
     */
    public String getXMLCode() {
        StringBuffer sb = new StringBuffer();
        sb.append("<MultimediaSimpleQuestions>\n");

        Iterator iter = getSimpleQuestions().iterator();
        while (iter.hasNext())
            sb.append(((SimpleQuestion) iter.next()).getXMLCode());
        
        sb.append("</MultimediaSimpleQuestions>\n");
        return sb.toString();
    }

}
