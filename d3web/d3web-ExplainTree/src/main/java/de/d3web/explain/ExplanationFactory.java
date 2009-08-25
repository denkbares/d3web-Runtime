/*
 * ExplainationFactory.java
 *
 * Created on 25. März 2002, 16:49
 */

package de.d3web.explain;

import java.util.Collection;

import de.d3web.explain.eNodes.ENode;
import de.d3web.explain.eNodes.values.DiagnosticValue;
import de.d3web.explain.eNodes.values.QState;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.QASet;

/**
 *
 * @author  betz
 */
public class ExplanationFactory {
    
    private XPSCase myCase;
    /** Creates a new instance of ExplainationFactory */
    public ExplanationFactory(XPSCase theCase) {
        myCase = theCase;
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
    public ENode explain(Diagnosis target, Collection context) {
        return new ENode(this, target, DiagnosticValue.getInstance(), context);
    }
    
    
	/**
	 * Gets the myCase.
	 * @return Returns a XPSCase
	 */
	public XPSCase getXPSCase() {
		return myCase;
	}

	
}
