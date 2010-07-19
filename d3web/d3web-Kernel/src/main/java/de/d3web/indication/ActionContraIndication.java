/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.indication;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.indication.inference.PSMethodContraIndication;

/**
 * RuleAction that contra indicates a QASet, when the corresponding rule fires
 * Creation date: (19.06.2001 18:32:09)
 * @author Joachim Baumeister
 */
public class ActionContraIndication extends PSAction {
	// the indication fact will be initialized with the first activation of this action in "doIt()" 
	private List<QASet> qasets;

	/**
	 * Invoked, if rule fires (action)
	 * Creation date: (02.11.2000 14:38:26)
	 * @param session current case
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let the blackboard do all the work
		for (QASet qaset : getQASets()) {
			Fact fact = new DefaultFact(qaset, new Indication(State.CONTRA_INDICATED), source, getProblemsolver());
			session.getBlackboard().addInterviewFact(fact);
		}
	}

	private PSMethod getProblemsolver() {
		return PSMethodContraIndication.getInstance();
	}

	/**
	 * @return PSMethodContraIndication.class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodContraIndication.class;
	}

	/**
	 * @return List of QASets this action can contraindicate
	 */
	public java.util.List<QASet> getQASets() {
		return qasets;
	}

	/**
	 * @return all objects participating on the action.<BR>
	 * -> getQASets()
	 */
	public List<QASet> getTerminalObjects() {
		return getQASets();
	}

		/**
	 * sets the QASets for contraindication
	 */
	public void setQASets(List<QASet> theQasets) {
		qasets = theQasets;
	}

	/**
	 * Invoked, if rule is undone (undoing action)
	 * @param session current case
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		// New handling of indications: Notify blackboard of indication and let the blackboard do all the work
		if (getQASets().size() > 1) {
			// todo: how to create facts with more than one QASet?!
			System.err.println("Not implemented yet.");
		}
		Value oldValue = session.getBlackboard().getIndication(getQASets().get(0));;
		session.getBlackboard().removeInterviewFact(getQASets().get(0), source);
		Value newValue = session.getBlackboard().getIndication(getQASets().get(0));
		session.getInterview().notifyFactChange(new PropagationEntry(getQASets().get(0), 
				oldValue, newValue));
	}
	
	public int hashCode() {
		if(getQASets() != null)
			return (getQASets().hashCode());
		else return 0;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionContraIndication) {
			ActionContraIndication a = (ActionContraIndication)o;
			if(getQASets() != null && a.getQASets() != null) { 
				return a.getQASets().equals(getQASets());
			}
			else if(getQASets() == null && a.getQASets() == null) {
				return true;
			}
			else  {
				return false;
			}
		}
		else
			return false;
	}
	
	public PSAction copy() {
		ActionContraIndication newAction = new ActionContraIndication();
		newAction.setQASets(new LinkedList<QASet>(getQASets()));
		return newAction;
	}
}