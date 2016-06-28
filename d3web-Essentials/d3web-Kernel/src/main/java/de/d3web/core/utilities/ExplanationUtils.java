package de.d3web.core.utilities;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * Provides utility methods to conveniently track down the facts involved in deriving other facts.
 * <p/>
 *
 * @author Albrecht Striffler (denkbares GmbH) on 24.08.2014.
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
		Collection<Fact> predecessorFacts = new HashSet<>(predecessorObjects.size());
		for (TerminologyObject predecessorObject : predecessorObjects) {
			Fact predecessorFact = session.getBlackboard().getValueFact(predecessorObject);
			if (predecessorFact == null) continue;
			predecessorFacts.add(predecessorFact);
		}
		return predecessorFacts;
	}

	public static Collection<Fact> getPredecessorFactsNonBlocking(Session session, TerminologyObject object) {
		try {
			return getPredecessorFacts(session, object);
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}

	/**
	 * Use this method in multi threaded environments. If the kernel is propagating during the call of this method,
	 * exceptions will be caught and an empty collection returned.<p/>
	 * Provides all source facts that were involved in deriving the current value of the given object in the given
	 * session. Source facts don't have any other predecessors and are usually the ones entered by an user.
	 *
	 * @param session the session where the facts should be looked up
	 * @param object  the object for which we want the source facts
	 * @return the source facts for the given object
	 */
	public static Collection<Fact> getSourceFacts(Session session, TerminologyObject object) {
		Fact valueFact = session.getBlackboard().getValueFact(object);
		if (valueFact == null) return Collections.emptyList();

		Set<Fact> sources = new HashSet<>();
		Set<Fact> visited = new HashSet<>();
		getSourceFacts(session, valueFact, sources, visited);
		return sources;
	}

	private static void getSourceFacts(Session session, Fact fact, Set<Fact> sources, Set<Fact> visited) {
		Collection<Fact> predecessors = getPredecessorFacts(session, fact.getTerminologyObject());
		predecessors.removeAll(visited); // loops can happen with unfortunate rule design,
		visited.addAll(predecessors);    // so we track the visited facts

		PSMethod psMethod = fact.getPSMethod();
		// a source fact can either be a fact where the PSM has Type.source or a
		// heuristic fact without predecessors, in case the fact was added externally to be added to derived scores
		if (psMethod.hasType(PSMethod.Type.source) ||
				(psMethod instanceof PSMethodInit) ||
				(psMethod instanceof PSMethodHeuristic && predecessors.isEmpty())) {
			sources.add(fact);
			return;
		}
		for (Fact predecessor : predecessors) {
			getSourceFacts(session, predecessor, sources, visited);
		}
	}

	/**
	 * Use this method in multi threaded environments. If the kernel is propagating during the call of this method,
	 * exceptions will be caught and an empty collection returned.<p/>
	 * Provides all source facts that were involved in deriving the current value of the given object in the given
	 * session. Source facts don't have any other predecessors and are usually the ones entered by an user.
	 *
	 * @param session the session where the facts should be looked up
	 * @param object  the object for which we want the source facts
	 * @return the source facts for the given object
	 */
	public static Collection<Fact> getSourceFactsNonBlocking(Session session, TerminologyObject object) {
		try {
			return getSourceFacts(session, object);
		}
		catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
