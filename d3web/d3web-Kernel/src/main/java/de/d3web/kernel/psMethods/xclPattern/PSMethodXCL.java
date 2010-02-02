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

package de.d3web.kernel.psMethods.xclPattern;

import java.util.Collection;
import java.util.List;

import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PropagationEntry;

public class PSMethodXCL extends PSMethodAdapter {
	
	private static PSMethodXCL instance = null;

	private PSMethodXCL() {
		super();
	}
	
	public static PSMethodXCL getInstance() {
		if (instance  == null) {
			instance = new PSMethodXCL();
		}
		return instance;
	}
	
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		List<? extends KnowledgeSlice> models = diagnosis.getKnowledge(PSMethodXCL.class, XCLModel.XCLMODEL);
		if (models == null || models.size() == 0) return DiagnosisState.UNCLEAR; 
		XCLModel model = (XCLModel) models.get(0);
		return model.getState(theCase);
	}
	
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		// TODO: implement well, as defined below
//		Set<XCLModel> modelsToUpdate = new HashSet<XCLModel>();
//		for (PropagationEntry change : changes) {
//			NamedObject nob = change.getObject();
//			List<? extends KnowledgeSlice> models = nob.getKnowledge(PSMethodXCL.class, XCLModel.XCL_CONTRIBUTED_MODELS);
//			if (models != null)  {
//				for (KnowledgeSlice model : models) {
//					modelsToUpdate.add((XCLModel) model);
//				}
//			}
//		}
//		for (XCLModel model : modelsToUpdate) {
//			DiagnosisState state = model.getState(theCase);
//			theCase.setValue(model.getSolution(), new DiagnosisState[]{state}, this.getClass());
//			model.notifyListeners(theCase, model);
//		}

		// TODO: remove this hack
		//only update if there is at least one question
		boolean hasQuestion = false;
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Question) hasQuestion = true;
		}
		if (!hasQuestion) return;
		Collection<KnowledgeSlice> models = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(PSMethodXCL.class);
		for (KnowledgeSlice knowledgeSlice : models) {
			if(knowledgeSlice instanceof XCLModel) {
				XCLModel model = (XCLModel) knowledgeSlice;
				
				//Quick fix for ClassCastException:
				Object o =  ((CaseDiagnosis) theCase.getCaseObject(model.getSolution())).getValue(this.getClass());
				DiagnosisState oldState = null;
				if(o instanceof DiagnosisState) {
					oldState =(DiagnosisState)o;
				}
				if(o instanceof DiagnosisScore) {
					DiagnosisScore oldScore = (DiagnosisScore)o;
					oldState = DiagnosisState.getState(oldScore);
				}
				

				
				
				
				
				// TODO: split getState into getState and refreshState
				//DiagnosisState oldState = model.getState(theCase);
				//model.refreshState(theCase);
				DiagnosisState newState = model.getState(theCase);
				if (!oldState.equals(newState)) {
					theCase.setValue(model.getSolution(), new DiagnosisState[]{newState}, this.getClass());
				}
				model.notifyListeners(theCase, model);
			}
		} 
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeSolutionFacts(facts);
	}
	
}
