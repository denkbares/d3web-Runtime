package de.d3web.kernel.psMethods.SCMCBR;

import java.util.Collection;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Question;
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

	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
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
		// DiagnosisState state = model.getState(theCase);
		// theCase.setValue(model.getSolution(), new DiagnosisState[]{state},
		// this.getClass());
		// model.notifyListeners(theCase, model);
		// }

		// TODO: remove this hack
		// only update if there is at least one question
		boolean hasQuestion = false;
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Question) hasQuestion = true;
		}
		if (!hasQuestion) return;
		Collection<KnowledgeSlice> models = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(
				PSMethodSCMCBR.class);
		for (KnowledgeSlice knowledgeSlice : models) {
			if (knowledgeSlice instanceof SCMCBRModel) {
				SCMCBRModel model = (SCMCBRModel) knowledgeSlice;

				// Quick fix for ClassCastException:
				Rating oldState = theCase.getBlackboard().getState(model.getSolution());

				// TODO: split getState into getState and refreshState
				// DiagnosisState oldState = model.getState(theCase);
				// model.refreshState(theCase);
				Rating newState = model.getState(theCase);
				if (!oldState.equals(newState)) {
					theCase.getBlackboard().addValueFact(
							FactFactory.createFact(model.getSolution(), newState, model, this));
				}
				model.notifyListeners(theCase, model);
			}
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

}
