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
 * Created on 16.08.2004 by Chris
 * 
 */
package de.d3web.caserepository.addons.train.findings;

import de.d3web.core.knowledge.terminology.Diagnosis;

/**
 * DiagnosisRefRelation
 * 
 * @author Chris 16.08.2004
 */
public class DiagnosisRelation extends FindingDiagnosisRelation {

	private Diagnosis diag;

	public DiagnosisRelation() {
		super();
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.train.findings.FindingDiagnosisRelation#getDiagnosis()
	 */
	public Diagnosis getDiagnosis() {
		return diag;
	}

	public void setDiagnosis(Diagnosis diag) {
		this.diag = diag;
	}
	
	/**
	 * overridden method
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + diag.getId() + ", " + (getScore()==null?null:getScore().getSymbol()) + ")";
	}

}