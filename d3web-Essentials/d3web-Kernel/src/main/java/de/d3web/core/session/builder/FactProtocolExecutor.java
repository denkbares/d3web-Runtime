/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session.builder;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.denkbares.collections.MultiMap;
import com.denkbares.collections.MultiMaps;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.FactProtocolEntry;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
public class FactProtocolExecutor implements ProtocolExecutor<FactProtocolEntry> {

	private final Map<String, PSMethod> solvers = new HashMap<>();
	private SessionBuilder builder;

	@Override
	public void prepare(SessionBuilder builder) {
		this.builder = builder;
		// prepare psmethods of session
		for (PSMethod psm : builder.getSession().getPSMethods()) {
			solvers.put(psm.getClass().getName(), psm);
		}
	}

	private PSMethod getSolver(FactProtocolEntry entry) {
		String psmName = entry.getSolvingMethodClassName();
		PSMethod psm = solvers.get(psmName);
		if (psm == null) {
			builder.warn("PSMethod is not available: " + psmName);
		}
		return psm;
	}

	@Override
	public void handle(SessionBuilder builder, Date date, List<FactProtocolEntry> entries) {
		Session session = builder.getSession();
		TerminologyManager manager = session.getKnowledgeBase().getManager();

		// group facts by problem solvers and execute them in order of their priority
		MultiMap<PSMethod, FactProtocolEntry> bySolvers = entries.stream().collect(MultiMaps.toMultiMap(
				this::getSolver, Function.identity(), MultiMaps.linkedFactory(), MultiMaps.linkedFactory()));
		bySolvers.keySet().stream().filter(Objects::nonNull)
				.sorted(Comparator.comparing(PSMethod::getPriority)).forEach(psm -> {

			session.getPropagationManager().openPropagation(date.getTime());
			try {
				for (FactProtocolEntry entry : bySolvers.getValues(psm)) {
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
		});
	}
}
