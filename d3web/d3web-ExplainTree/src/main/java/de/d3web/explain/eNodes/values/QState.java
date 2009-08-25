/*
 * DiagnosticValue.java
 *
 * Created on 27. März 2002, 13:00
 */

package de.d3web.explain.eNodes.values;

/**
 *
 * @author  betz
 */
public class QState implements TargetValue {
		//TargetValue für Fragen und Frageklassen
   
    public static final QState ACTIVE = new QState("active");

/*
    public static final QState INACTIVE = new QState("inactive");
    public static final QState CONTRA = new QState("supressed");
*/

    private String name = "";
    
    public QState(String newName) {
    	name = newName;
    }
    
    public String toString() {
    	return name;
    }
    
    
}
