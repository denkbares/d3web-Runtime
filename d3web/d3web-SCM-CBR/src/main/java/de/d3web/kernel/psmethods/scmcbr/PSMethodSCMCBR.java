package de.d3web.kernel.psmethods.scmcbr;

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

}
