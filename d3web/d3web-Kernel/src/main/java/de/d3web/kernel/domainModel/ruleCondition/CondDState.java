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

package de.d3web.kernel.domainModel.ruleCondition;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;

/**
 * This condition checks, if a specified diagnosis is established or
 * is in a specified state.
 * The composite pattern is used for this. This class is "leaf".
 * 
 * @author Christian Betz
 */
public class CondDState extends TerminalCondition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5690604452964483692L;
	private Diagnosis diagnosis;
	private DiagnosisState solutionState;

	/**
	 * Creates a new CondDState Expression:
	 * @param diagnose diagnosis to check
	 * @param solutionState state of the diagnosis to check
	 * @param context the context in which the diagnosis has the state
	 */
	public CondDState(
		Diagnosis diagnosis,
		DiagnosisState solutionState) {
		super(diagnosis);
		this.diagnosis = diagnosis;
		this.solutionState = solutionState;
	}

	/**
	 * This method checks the condition
	 * 
	 * Problem: UNCLEAR is default state of diagnosis. 
	 * But we need to check for NoAnswerException,
	 * if no rule has ever changed the state of the diagnosis.
	 */
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		return solutionState.equals(diagnosis.getState(theCase));
	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public DiagnosisState getStatus() {
		return solutionState;
	}

	public void setDiagnosis(Diagnosis newDiagnosis) {
		diagnosis = newDiagnosis;
	}

	public void setStatus(DiagnosisState newStatus) {
		solutionState = newStatus;
	}

	@Override
	public String toString() {
		return "\u2190 CondDState diagnosis: "
			+ diagnosis.getId()
			+ " value: "
			+ this.getStatus();
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
			CondDState otherCDS = (CondDState)other;
			boolean test = true;
			if(this.getDiagnosis() != null)
				test = this.getDiagnosis().equals(otherCDS.getDiagnosis()) && test;
			else //== null
				test = (otherCDS.getDiagnosis() == null) && test;
				
			if(this.getStatus() != null)
				test = this.getStatus().equals(otherCDS.getStatus()) && test;
			else
				test = (otherCDS.getStatus() == null) && test;
			return test;
	}
	
	@Override
	public int hashCode() {
		
		String str = getClass().toString();
		
		if (getDiagnosis() != null)
			str+=getDiagnosis().toString();
			
		if (getStatus() != null)
			str+=getStatus().toString();
		
		if (getTerminalObjects() != null)
			str+=getTerminalObjects().toString();
			
		return str.hashCode();
	}

	@Override
	public AbstractCondition copy() {
		return new CondDState(getDiagnosis(), getStatus());
	}

}