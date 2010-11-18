/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.indication;

import java.util.ArrayList;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;

/**
 * This action type indicates a list of {@link QASet} instances no matter,
 * whether this {@link InterviewObject} objects has been already answered or
 * not. That way, a question can be repeatedly presented in the interview.
 * 
 * @author Joachim Baumeister
 */
public class ActionRepeatedIndication extends ActionNextQASet {

	@Override
	protected void doItWithContext(Session session, Object source) {
		for (QASet qaset : getQASets()) {
			Fact fact = new DefaultFact(qaset, new Indication(State.REPEATED_INDICATED), this,
					getProblemsolver());
			session.getBlackboard().addInterviewFact(fact);
		}
	}

	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let
		// the blackboard do all the work
		for (QASet qaset : getQASets()) {
			session.getBlackboard().removeInterviewFact(qaset, this);
		}
	}

	@Override
	public PSAction copy() {
		ActionRepeatedIndication a = new ActionRepeatedIndication();
		a.setQASets(new ArrayList<QASet>(getQASets()));
		return a;
	}
}