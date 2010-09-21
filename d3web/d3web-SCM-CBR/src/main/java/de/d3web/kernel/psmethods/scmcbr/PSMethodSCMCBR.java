/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.kernel.psmethods.scmcbr;

import java.util.Collection;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.blackboard.Facts;

public class PSMethodSCMCBR extends PSMethodAdapter {

	private static PSMethodSCMCBR instance = null;

	private PSMethodSCMCBR() {
	}

	public static PSMethodSCMCBR getInstance() {
		if (instance == null) {
			instance = new PSMethodSCMCBR();
		}
		return instance;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		// TODO: implement well, as defined below
		// Set<XCLModel> modelsToUpdate = new HashSet<XCLModel>();
		// for (PropagationEntry change : changes) {
		// NamedObject nob = change.getObject();
		// List<? extends KnowledgeSlice> models =
		// nob.getKnowledge(PSMethodXCL.class, XCLModel.XCL_CONTRIBUTED_MODELS);
		// if (models != null) {
		// for (KnowledgeSlice model : models) {
		// modelsToUpdate.add((XCLModel) model);
		// }
		// }
		// }
		// for (XCLModel model : modelsToUpdate) {
		// DiagnosisState state = model.getState(session);
		// session.setValue(model.getSolution(), new DiagnosisState[]{state},
		// this.getClass());
		// model.notifyListeners(session, model);
		// }

		// TODO: remove this hack
		// only update if there is at least one question
		boolean hasQuestion = false;
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Question) hasQuestion = true;
		}
		if (!hasQuestion) return;
		Collection<KnowledgeSlice> models = session.getKnowledgeBase().getAllKnowledgeSlicesFor(
				PSMethodSCMCBR.class);
		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof SCMCBRModel) {
				SCMCBRModel model = (SCMCBRModel) knowledgeSlice;

				// Quick fix for ClassCastException:
				Rating oldState = session.getBlackboard().getRating(model.getSolution());

				// TODO: split getState into getState and refreshState
				// DiagnosisState oldState = model.getState(session);
				// model.refreshState(session);
				Rating newState = model.getState(session);
				if (!oldState.equals(newState)) {
					session.getBlackboard().addValueFact(
							FactFactory.createFact(model.getSolution(), newState, model, this));
				}
				model.notifyListeners(session, model);
			}
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

}
