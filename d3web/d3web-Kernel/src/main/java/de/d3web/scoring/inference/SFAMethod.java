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

package de.d3web.scoring.inference;

import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.info.Property;

public class SFAMethod extends PSSubMethod{

	/**
	 * Singleton
	 */
	private static SFAMethod instance = new SFAMethod();
	private SFAMethod() {
		super();
		setContributingToResult(true);
	}
	public static SFAMethod getInstance() {
		return instance;
	}
	
	Class<PSMethodHeuristic> PSCONTEXT = PSMethodHeuristic.class;
		
	
	/**
	 * initialization method for this PSMethod
	 */
	public void init(XPSCase theCase) {
	}
	
	/**
	 * propergates the new value of the given NamedObject for the given XPSCase
	 */
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Diagnosis) {
				Diagnosis diagnosis = (Diagnosis) change.getObject();
				if (canCaseBeReenabled(theCase)) {
					enableCase(theCase);
				}
				if (isEstablished(theCase, diagnosis)) {
					if (isLeafDiagnosis(diagnosis)) {
						disableCase(theCase);
					}
				}
			}
		}
	}

	
	/**
	 * @param theCase
	 */
	private void disableCase(XPSCase theCase) {
		theCase.getProperties().setProperty(Property.HDT_ABORT_CASE_SFA, Boolean.TRUE);
		
	}		
	
	/**
	 * @param theCase
	 */
	private void enableCase(XPSCase theCase) {
		theCase.getProperties().setProperty(Property.HDT_ABORT_CASE_SFA, Boolean.FALSE);	
		
	}
	/**
	 * @param theCase
	 * @param diagnosis
	 * @return
	 */
	private boolean canCaseBeReenabled(XPSCase theCase) {
		Boolean b = (Boolean)theCase.getProperties().getProperty(Property.HDT_ABORT_CASE_SFA);
		if(b != null 
			&& b.booleanValue()
			&& !containsLeafDianosis(theCase.getDiagnoses(DiagnosisState.ESTABLISHED)))
			return true;
		return false;
	}
	
	/**
	 * @param diagnoses
	 * @return
	 */
	private boolean containsLeafDianosis(List<Diagnosis> diagnoses) {
		for (Diagnosis diag : diagnoses) {
			if (isLeafDiagnosis(diag))
				return true;
		}
		return false;
	}
	private boolean isLeafDiagnosis(Diagnosis diagnosis) {
		return diagnosis.getChildren() == null || diagnosis.getChildren().isEmpty();
	}
	private boolean isEstablished(XPSCase theCase, Diagnosis diagnosis) {
		return diagnosis.getState(theCase, PSCONTEXT).equals(DiagnosisState.ESTABLISHED);
	}
	
	public boolean isActivated(XPSCase theCase) {
		Boolean b = (Boolean)theCase.getKnowledgeBase().getProperties().getProperty(Property.SINGLE_FAULT_ASSUMPTION);
		return  b != null && b.booleanValue(); 
	}
			
}
