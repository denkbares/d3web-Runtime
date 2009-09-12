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
