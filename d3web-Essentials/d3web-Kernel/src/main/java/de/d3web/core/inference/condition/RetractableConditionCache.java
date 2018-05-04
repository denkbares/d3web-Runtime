/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition;

import java.util.Collection;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * A condition cache that also allows notify that some terminology objects' values have changed and selectively
 * re-evaluate the contents.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 04.05.2018
 */
public class RetractableConditionCache extends DefaultConditionCache {

	private final MultiMap<TerminologyObject, Condition> usedObjects = new DefaultMultiMap<>();

	public RetractableConditionCache(Session session) {
		super(session);
	}

	public void notifyChange(TerminologyObject changedObject) {
		for (Condition condition : usedObjects.getValues(changedObject)) {
			removeResult(condition);
		}
	}

	public void notifyChanges(Collection<? extends TerminologyObject> changedObjects) {
		for (TerminologyObject object : changedObjects) {
			notifyChange(object);
		}
	}

	@Override
	protected ConditionResult evalToResult(Condition condition) {
		// also remember what objects where used to evaluate the condition.
		// Unfortunately sometimes less objects are required/used, but we ignore that here
		// (e.g. if in an or condition the dirt term ist true, the further ones are not used)
		for (TerminologyObject object : condition.getTerminalObjects()) {
			usedObjects.put(object, condition);
		}
		return super.evalToResult(condition);
	}
}
