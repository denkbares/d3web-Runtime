/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session.builder;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.FactProtocolEntry;

/**
 * Executes facts of a specific problem solver. This executer is usually instantiated for source solver, but may also be
 * used by other solvers if they want their factrs to be simply re-added on loading.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
public class FactProtocolExecutor<T extends PSMethod> implements ProtocolExecutor<FactProtocolEntry> {

	private final Class<T> solverClass;
	private T psm;

	public FactProtocolExecutor(Class<T> solverClass) {
		this.solverClass = solverClass;
	}

	public Class<T> getSolverClass() {
		return solverClass;
	}

	@Override
	public void prepare(SessionBuilder builder) {
		// prepare psmethod of session
		psm = builder.getSession().getPSMethodInstance(solverClass);
	}

	protected boolean canHandle(FactProtocolEntry entry) {
		return Objects.equals(solverClass.getName(), entry.getSolvingMethodClassName());
	}

	@Override
	public void handle(SessionBuilder builder, Date date, List<FactProtocolEntry> entries) {

		List<FactProtocolEntry> subset = entries.stream().filter(this::canHandle).collect(Collectors.toList());
		if (subset.isEmpty()) return;

		Session session = builder.getSession();
		TerminologyManager manager = session.getKnowledgeBase().getManager();

		session.getPropagationManager().openPropagation(date.getTime());
		try {
			for (FactProtocolEntry entry : subset) {
				// determine object to be set
				String name = entry.getTerminologyObjectName();
				TerminologyObject object = manager.search(name);
				if (object == null) {
					builder.warn("Object not available, ignore value: " + name + " = " + entry.getValue());
					continue;
				}

				// create and add fact to blackboard
				Fact fact = FactFactory.createFact(object, entry.getValue(), psm, psm);
				session.getBlackboard().addValueFact(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
	}
}
