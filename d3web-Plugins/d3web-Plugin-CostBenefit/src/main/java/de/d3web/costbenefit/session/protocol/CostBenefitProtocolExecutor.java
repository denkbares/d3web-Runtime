/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.session.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.builder.ProtocolExecutor;
import de.d3web.core.session.builder.SessionBuilder;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;

/**
 * Protocol executor to make sure that a path is calculated after the protocol as been executed.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
public class CostBenefitProtocolExecutor implements ProtocolExecutor<CalculatedPathEntry> {

	private Session session;
	private PSMethodCostBenefit psm;
	private CostBenefitCaseObject pso;

	@Override
	public void prepare(SessionBuilder builder) {
		this.session = builder.getSession();
		this.psm = session.getPSMethodInstance(PSMethodCostBenefit.class);
		if (psm != null) {
			this.pso = session.getSessionObject(psm);
			pso.setUndiscriminatedSolutions(null);
			pso.setReplayingSession(true);
		}
	}

	@Override
	public void handle(SessionBuilder builder, Date date, List<CalculatedPathEntry> entries) {
		if (psm == null) return;
		for (CalculatedPathEntry entry : entries) {
			try {
				session.getPropagationManager().openPropagation(date.getTime());
				handle(builder, entry);
			}
			finally {
				session.getPropagationManager().commitPropagation();
			}
		}
	}

	private void handle(SessionBuilder builder, CalculatedPathEntry entry) {
		// get the calculated path
		List<QContainer> path = new ArrayList<>();
		for (String name : entry.getPath()) {
			QContainer container = session.getKnowledgeBase().getManager().searchQContainer(name);
			if (container == null) {
				builder.warn("QContainer does not exists: " + name);
			}
			else {
				path.add(container);
			}
		}

		// and activate the path as usual
		pso.resetPath();
		pso.activatePath(path, psm);
		pso.activateNextQContainer();
	}

	@Override
	public void complete(SessionBuilder builder) {
		if (psm == null) return;
		pso.setReplayingSession(false);
		psm.checkPath(session);
	}
}
