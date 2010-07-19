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
 * ExplainationFactory.java
 *
 * Created on 25. März 2002, 16:49
 */

package de.d3web.explain;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.explain.eNodes.ENode;
import de.d3web.explain.eNodes.values.DiagnosticValue;
import de.d3web.explain.eNodes.values.QState;

/**
 *
 * @author  betz
 */
public class ExplanationFactory {
    
    private Session myCase;
    /** Creates a new instance of ExplainationFactory */
    public ExplanationFactory(Session session) {
        myCase = session;
    }
    
    /**
     * Explains the activation of a qaset under a given context.
     * 
     */
    public ENode explainActive(QASet target, Collection context) {
        return new ENode(this, target, QState.ACTIVE, context);
    }
    
    /**
     * Explains the reasons for a diagnostic value under a given context.
     * 
     */
    public ENode explain(Solution target, Collection context) {
        return new ENode(this, target, DiagnosticValue.getInstance(), context);
    }
    
    
	/**
	 * Gets the myCase.
	 * @return Returns a Session
	 */
	public Session getSession() {
		return myCase;
	}

	
}
