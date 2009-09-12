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
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * This condition checks, if a specified diagnosis is established or
 * is in a specified state.
 * The composite pattern is used for this. This class is "leaf".
 * 
 * @author Christian Betz
 */
public class CondDState extends TerminalCondition {
	private Diagnosis diagnosis;
	private DiagnosisState solutionState;
	private Class context;

	/**
	 * Creates a new CondDState Expression:
	 * @param diagnose diagnosis to check
	 * @param solutionState state of the diagnosis to check
	 * @param context the context in which the diagnosis has the state
	 */
	public CondDState(
		Diagnosis diagnosis,
		DiagnosisState solutionState,
		Class context) {
		super(diagnosis);
		this.diagnosis = diagnosis;
		this.solutionState = solutionState;
		this.context = context;
	}

	/**
	 * This method checks the condition
	 * 
	 * Problem: UNCLEAR is default state of diagnosis. 
	 * But we need to check for NoAnswerException,
	 * if no rule has ever changed the state of the diagnosis.
	 */
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		if (diagnosis
			.getState(theCase, context)
			.equals(DiagnosisState.UNCLEAR)) {
			if (!hasHeuristicRulesFired(diagnosis, theCase, context))
				throw NoAnswerException.getInstance();
		}
		return solutionState.equals(diagnosis.getState(theCase, context));
	}

	/**
	 * @return true, if a knowledge slice of a given diagnosis has fired for a given context.
	 */
	private static boolean hasHeuristicRulesFired(
		Diagnosis diagnosis,
		XPSCase theCase,
		Class context) {

		List knowledge =
			diagnosis.getKnowledge(context, MethodKind.BACKWARD);
		if (knowledge != null) {
			Iterator slices = knowledge.iterator();
			while (slices.hasNext()) {
				KnowledgeSlice slice = (KnowledgeSlice) slices.next();
				if (slice.isUsed(theCase))
					return true;
			}
		}
		return false;
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
		return "<Condition type='DState' ID='"
			+ diagnosis.getId()
			+ "' value='"
			+ this.getStatus()
			+ "'>"
			+ "</Condition>\n";
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
			
			if(this.getContext() != null)
				test = this.getContext().equals(otherCDS.getContext()) && test;
			else
				test = (otherCDS.getContext() == null) && test;
					
			return test;
		
	}
	
	@Override
	public int hashCode() {
		
		String str = getClass().toString();
		
		if (getDiagnosis() != null)
			str+=getDiagnosis().toString();
			
		if (getStatus() != null)
			str+=getStatus().toString();
		
		if (getContext() != null)
			str+=getContext().toString();
			
		if (getTerminalObjects() != null)
			str+=getTerminalObjects().toString();
			
		return str.hashCode();
	}

	/**
	 * @return Class
	 */
	public Class getContext() {
		return context;
	}

	@Override
	public AbstractCondition copy() {
		return new CondDState(getDiagnosis(), getStatus(), getContext());
	}

}