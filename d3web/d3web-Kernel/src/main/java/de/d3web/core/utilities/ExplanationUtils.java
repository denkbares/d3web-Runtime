package de.d3web.core.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * Provides utility methods to conveniently track down the facts involved in deriving other facts.
 * <p>
 * Created by Albrecht Striffler (denkbares GmbH) on 24.08.2014.
 */
public class ExplanationUtils {

	/**
	 * Provides all facts that were directly involved in deriving the current value of the given object in the given
	 * session.
	 *
	 * @param session the session where the facts should be looked up
	 * @param object  the object for which we want the predecessor facts
	 * @return the predecessor facts for the given object
	 */
	public static Collection<Fact> getPredecessorFacts(Session session, TerminologyObject object) {
		Fact valueFact = session.getBlackboard().getValueFact(object);
		Set<TerminologyObject> predecessorObjects = valueFact.getPSMethod().getActiveDerivationSources(object, session);
		Collection<Fact> predecessorFacts = new ArrayList<Fact>(predecessorObjects.size());
		for (TerminologyObject predecessorObject : predecessorObjects) {
			Fact predecessorFact = session.getBlackboard().getValueFact(predecessorObject);
			if (predecessorFact == null) continue;
			predecessorFacts.add(predecessorFact);
		}
		return predecessorFacts;
	}

	/**
	 * Provides all source facts that were involved in deriving the current value of the given object in the given
	 * session. Source facts don't have any other predecessors and are usually the ones entered by an user.
	 *
	 * @param session the session where the facts should be looked up
	 * @param object  the object for which we want the source facts
	 * @return the source facts for the given object
	 */
	public static Collection<Fact> getSourceFacts(Session session, TerminologyObject object) {
		Set<Fact> sources = new HashSet<Fact>();
		Fact valueFact = session.getBlackboard().getValueFact(object);
		getSourceFacts(session, valueFact, sources);
		return sources;
	}

	private static void getSourceFacts(Session session, Fact fact, Set<Fact> sources) {
		Collection<Fact> predecessors = getPredecessorFacts(session, fact.getTerminologyObject());
		if (predecessors.isEmpty()) {
			sources.add(fact);
			return;
		}
		for (Fact predecessor : predecessors) {
			getSourceFacts(session, predecessor, sources);
		}
	}
}
