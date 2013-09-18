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

import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;

/**
 * RuleAction that contra-indicates a {@link QASet}, when the corresponding rule
 * fires Creation date: (19.06.2001 18:32:09)
 * 
 * @author Joachim Baumeister
 */
public class ActionContraIndication extends PSAction {

	// the indication fact will be initialized with the first activation of this
	// action in "doIt()"
	private List<QASet> qasets;

	/**
	 * Invoked, if rule fires (action) Creation date: (02.11.2000 14:38:26)
	 * 
	 * @param session current case
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let
		// the blackboard do all the work
		for (QASet qaset : getQASets()) {
			Indication indication = new Indication(State.CONTRA_INDICATED,
					qaset.getKnowledgeBase().getManager().getTreeIndex(qaset));
			Fact fact = FactFactory.createFact(session, qaset, indication, source,
					psmethod);

			session.getBlackboard().addInterviewFact(fact);
		}
	}

	public List<QASet> getQASets() {
		return qasets;
	}

	@Override
	public List<QASet> getBackwardObjects() {
		return getQASets();
	}

	/**
	 * Sets the specified QASets for contra-indication.
	 */
	public void setQASets(List<QASet> theQasets) {
		qasets = theQasets;
	}

	/**
	 * Invoked, if rule is undone (undoing action)
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let
		// the blackboard do all the work
		if (getQASets().size() > 1) {
			// TODO: how to create facts with more than one QASet?!
			Logger.getLogger(this.getClass().getName()).warning(
					"QASets().size() > 1: Not implemented yet.");
		}
		session.getBlackboard().removeInterviewFact(getQASets().get(0), source);
	}

	@Override
	public int hashCode() {
		if (getQASets() != null) {
			return (getQASets().hashCode());
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ActionContraIndication) {
			ActionContraIndication a = (ActionContraIndication) o;
			if (getQASets() != null && a.getQASets() != null) {
				return a.getQASets().equals(getQASets());
			}
			else {
				return (getQASets() == null && a.getQASets() == null);
			}
		}
		else {
			return false;
		}
	}
}