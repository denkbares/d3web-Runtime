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
 * AbstractExplainTest.java
 *
 * Created on 25. März 2002, 16:57
 */

package de.d3web.explain.test;

import junit.framework.TestCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.QASet;

/**
 *
 * @author  betz
 */
public abstract class AbstractExplainTest  extends TestCase {
    
    /** Creates a new instance of AbstractExplainTest */
    public AbstractExplainTest(String name) {
        super(name);
    }
    
    protected void log(String log) {
    	System.out.println(log);
    }
    
    public static QASet findQ(String text, KnowledgeBase kb) {
        return kb.searchQASet(text);
    }
    public static Diagnosis findD(String text, KnowledgeBase kb) {
        return kb.searchDiagnosis(text);
    }
    
}
