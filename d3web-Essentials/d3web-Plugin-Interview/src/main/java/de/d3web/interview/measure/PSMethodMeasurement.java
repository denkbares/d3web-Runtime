/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview.measure;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * Source PSMethod to signal that a value has been measured by some measurement.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 12.12.2016
 */
public class PSMethodMeasurement implements PSMethod {

	public PSMethodMeasurement() {
	}

	@Override
	public void init(Session session) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeUniqueFact(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.source;
	}

	@Override
	public double getPriority() {
		// little les priority than PSMethodUserSelected
		return 3;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		// do nothing
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		return Collections.emptySet();
	}
}
