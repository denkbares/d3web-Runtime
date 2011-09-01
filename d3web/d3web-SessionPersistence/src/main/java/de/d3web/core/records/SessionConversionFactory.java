/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethod.Type;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Factory to create a Session out of a SessionRecord and vice versa.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public final class SessionConversionFactory {

	/**
	 * Avoids the creation of an instance for this class.
	 */
	private SessionConversionFactory() {
	}

	/**
	 * Converts a SessionRecord to a Session
	 * 
	 * @created 05.08.2011
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @param source {@link SessionRecord}
	 * @return {@link Session}
	 * @throws IOException
	 */
	public static Session copyToSession(KnowledgeBase knowledgeBase, SessionRecord source) throws IOException {
		DefaultSession target = SessionFactory.createSession(source.getId(),
				knowledgeBase, source.getCreationDate());
		target.setName(source.getName());
		InfoStoreUtil.copyEntries(source.getInfoStore(), target.getInfoStore());

		// Search psmethods of session
		Map<String, PSMethod> psMethods = new HashMap<String, PSMethod>();
		for (PSMethod psm : target.getPSMethods()) {
			psMethods.put(psm.getClass().getName(), psm);
		}
		target.getPropagationManager().openPropagation();
		try {
			List<Fact> valueFacts = getFacts(knowledgeBase, source.getValueFacts(), psMethods);
			List<Fact> interviewFacts =
					getFacts(knowledgeBase, source.getInterviewFacts(), psMethods);
			for (Fact fact : valueFacts) {
				target.getBlackboard().addValueFact(fact);
			}
			for (Fact fact : interviewFacts) {
				target.getBlackboard().addInterviewFact(fact);
			}
		}
		finally {
			target.getPropagationManager().commitPropagation();
		}

		// restore protocol from source
		target.getProtocol().clear();
		for (ProtocolEntry entry : source.getProtocol().getProtocolHistory()) {
			target.getProtocol().addEntry(entry);
		}

		// this must be the last operation to overwrite all touches within
		// propagation
		target.touch(source.getLastChangeDate());
		return target;
	}

	private static List<Fact> getFacts(KnowledgeBase kb, List<FactRecord> factRecords, Map<String, PSMethod> psMethods) throws IOException {
		List<Fact> resultFacts = new LinkedList<Fact>();
		for (FactRecord factRecord : factRecords) {
			// ignore merged facts
			String psm = factRecord.getPsm();
			if (psm == null) continue;
			PSMethod psMethod = psMethods.get(psm);

			// ensure that problem solver exists
			if (psMethod == null) {
				throw new IOException("Problemsolver " + psm + " not found in Session.");
			}

			// ignore all non-source facts
			if (!psMethod.hasType(Type.source)) continue;

			// otherwise, create the fact and add it to our results
			Value value = factRecord.getValue();
			String objectName = factRecord.getObjectName();
			TerminologyObject object = kb.getManager().search(objectName);
			if (object == null) {
				throw new IOException("Object " + objectName + " not found in knowledge base");
			}
			resultFacts.add(new DefaultFact(object, value, psMethod, psMethod));
		}
		return resultFacts;
	}

	/**
	 * Converts a {@link Session} to a {@link SessionRecord}.
	 * 
	 * @created 05.08.2011
	 * @param source the session to be converted
	 * @return the created session record
	 */
	public static SessionRecord copyToSessionRecord(Session source) {
		return copyToSessionRecord(source, false);
	}

	/**
	 * Converts a {@link Session} to a {@link SessionRecord}.
	 * 
	 * @created 05.08.2011
	 * @param source the session to be converted
	 * @param createNewID specified if the record should be decoupled from the
	 *        session be creating a new (unique) id
	 * @return the created session record
	 */
	public static SessionRecord copyToSessionRecord(Session source, boolean createNewID) {
		DefaultSessionRecord target =
				new DefaultSessionRecord(
						createNewID ? UUID.randomUUID().toString() : source.getId(),
						source.getCreationDate(), source.getLastChangeDate());
		target.setName(source.getName());
		InfoStoreUtil.copyEntries(source.getInfoStore(), target.getInfoStore());
		target.getProtocol().addEntries(source.getProtocol().getProtocolHistory());
		Blackboard blackboard = source.getBlackboard();
		for (Question q : blackboard.getValuedQuestions()) {
			List<PSMethod> contributingPSMethods = blackboard.getContributingPSMethods(q);
			for (PSMethod psm : contributingPSMethods) {
				Value value = blackboard.getValue(q, psm);
				if (UndefinedValue.isNotUndefinedValue(value)) {
					target.addValueFact(new FactRecord(q, psm.getClass().getName(), value));
				}
			}
			// if more than one psm has set a value, add the globally merged
			// fact
			if (contributingPSMethods.size() > 1) {
				Value valueFact = blackboard.getValue(q);
				target.addValueFact(new FactRecord(q, null, valueFact));
			}
		}
		for (Solution s : blackboard.getValuedSolutions()) {
			List<PSMethod> contributingPSMethods = blackboard.getContributingPSMethods(s);
			for (PSMethod psm : contributingPSMethods) {
				Rating rating = blackboard.getRating(s, psm);
				if (!rating.hasState(State.UNCLEAR)) {
					target.addValueFact(new FactRecord(s, psm.getClass().getName(), rating));
				}
			}
			if (contributingPSMethods.size() > 1) {
				Value value = blackboard.getValue(s);
				target.addValueFact(new FactRecord(s, null, value));
			}
		}
		for (TerminologyObject object : blackboard.getInterviewObjects()) {
			List<PSMethod> indicatingPSMethods = blackboard.getIndicatingPSMethods(object);
			for (PSMethod psm : indicatingPSMethods) {
				Indication indication = blackboard.getIndication((InterviewObject) object, psm);
				if (!indication.hasState(Indication.State.NEUTRAL)) {
					target.addInterviewFact(new FactRecord(object, psm.getClass().getName(),
							indication));
				}
			}
			if (indicatingPSMethods.size() > 1) {
				Fact interviewFact = blackboard.getInterviewFact(object);
				Value value = interviewFact.getValue();
				target.addInterviewFact(new FactRecord(object, null, value));
			}

		}
		target.touch(source.getLastChangeDate());
		return target;
	}

}
