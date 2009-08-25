/*
 * DiagnosticValue.java
 *
 * Created on 27. März 2002, 13:00
 */

package de.d3web.explain.eNodes.values;

import java.util.Collection;

/**
 *
 * @author  betz
 */
public class QValue implements TargetValue {
    
    private Collection values = null;		// Aw, die erklärt werden sollen (aus AnswerChoice-Objekten)
    
    /** Creates a new instance of DiagnosticValue */
    public QValue() {
    }
    
}
