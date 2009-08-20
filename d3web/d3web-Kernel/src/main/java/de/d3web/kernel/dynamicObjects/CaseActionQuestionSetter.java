/*
 * Created on 16.05.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.kernel.dynamicObjects;

import java.util.Hashtable;

import de.d3web.kernel.domainModel.CaseObjectSource;

/**
 * @author Atzmueller
 */
public class CaseActionQuestionSetter extends XPSCaseObject {

	private Hashtable actionValues;
	Double lastSetValue;

	/**
	 * @param theSourceObject
	 */
	public CaseActionQuestionSetter(CaseObjectSource theSourceObject) {
		super(theSourceObject);
	}

	/**
	 * @return
	 */
	public Hashtable getActionValues() {
		return actionValues;
	}

	/**
	 * @param hashtable
	 */
	public void setActionValues(Hashtable hashtable) {
		actionValues = hashtable;
	}
	
	
	/**
	 * @return
	 */
	public Double getLastSetValue() {
		return lastSetValue;
	}


	/**
	 * @param double1
	 */
	public void setLastSetValue(Double double1) {
		lastSetValue = double1;
	}

}
