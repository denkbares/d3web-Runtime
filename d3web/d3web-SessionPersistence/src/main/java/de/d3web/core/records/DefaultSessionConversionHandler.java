/*
 * Copyright (C) 2010 denkbares GmbH
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

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * The default ConversionHandler
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class DefaultSessionConversionHandler implements SessionConversionHandler {

	@Override
	public void copyToSession(SessionRecord source, Session target) {
		// Search psmethods of session (improves performance)
		Map<String, PSMethod> psMethods = new HashMap<String, PSMethod>();
		for (PSMethod psm : target.getPSMethods()) {
			psMethods.put(psm.getClass().toString(), psm);
		}
		for (FactRecord factRecord : source.getFacts()) {
			PSMethod psMethod = psMethods.get(factRecord.getPsm());
			if (psMethod != null) {
				target.getBlackboard().addValueFact(
						new DefaultFact(factRecord.getObject(), factRecord.getValue(), psMethod,
								psMethod));
			}
			else {
				// throw new IOException();
			}
		}
	}

	@Override
	public void copyToSessionRecord(Session source, SessionRecord target) {
		Blackboard blackboard = source.getBlackboard();
		for (Question q : blackboard.getValuedQuestions()) {
			Fact valueFact = blackboard.getValueFact(q);
			if (valueFact != null) {
				PSMethod psm = valueFact.getPSMethod();
				// TODO: check if ps is source
				if (psm instanceof PSMethodUserSelected) {
					target.addFact(new FactRecord(q, psm.getClass().toString(),
							valueFact.getValue()));
				}
			}
		}
		for (Solution s : blackboard.getValuedSolutions()) {
			Fact valueFact = blackboard.getValueFact(s);
			if (valueFact != null) {
				PSMethod psm = valueFact.getPSMethod();
				Value value = valueFact.getValue();
				if (value != null && value instanceof Rating) {
					Rating rating = (Rating) value;
					// TODO check if ps is source
					if ((psm instanceof PSMethodUserSelected)
							&& !rating.hasState(State.UNCLEAR)) {
						target.addFact(new FactRecord(s, psm.getClass().toString(), value));
					}
				}
			}
		}
		for (TerminologyObject object : blackboard.getInterviewObjects()) {
			Fact interviewFact = blackboard.getInterviewFact(object);
			if (interviewFact != null) {
				Value value = interviewFact.getValue();
				PSMethod psMethod = interviewFact.getPSMethod();
				// TODO check if ps is source
				if (psMethod instanceof PSMethodUserSelected) {
					target.addFact(new FactRecord(object, psMethod.getClass().toString(), value));
				}
			}
		}
	}

}
