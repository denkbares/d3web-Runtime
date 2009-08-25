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
public class DiagnosticValue implements TargetValue {
	

	private static DiagnosticValue instance = null;

	/** Creates a new instance of DiagnosticValue */
	private DiagnosticValue() {
	}

	public static DiagnosticValue getInstance() {
		if (instance == null) {
			instance = new DiagnosticValue();
		}
		return instance;
	}

}