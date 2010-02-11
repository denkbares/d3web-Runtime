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

package de.d3web.kernel.psMethods.shared;
/**
 * Insert the type's description here.
 * Creation date: (18.10.2001 19:03:52)
 * @author: Norman Brümmer
 */
public class DiagnosisWeightValue {
	private de.d3web.core.terminology.Diagnosis diagnosis = null;
	private int value = 0;



/**
 * DiagnosisWeightValue constructor comment.
 */
public DiagnosisWeightValue() {
	super();
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:19)
 * @return de.d3web.kernel.domainModel.Diagnosis
 */
public de.d3web.core.terminology.Diagnosis getDiagnosis() {
	return diagnosis;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:32)
 * @return int
 */
public int getValue() {
	return value;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:19)
 * @param newDiagnosis de.d3web.kernel.domainModel.Diagnosis
 */
public void setDiagnosis(de.d3web.core.terminology.Diagnosis newDiagnosis) {
	diagnosis = newDiagnosis;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:32)
 * @param newValue int
 */
public void setValue(int newValue) {
	value = newValue;
}
}